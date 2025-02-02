/*
 * Copyright 2006-2009 The Scriptella Project Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scriptella.driver.ldap;

import scriptella.core.EtlCancelledException;
import scriptella.driver.ldap.ldif.Entry;
import scriptella.driver.ldap.ldif.LdifParseException;
import scriptella.driver.ldap.ldif.LdifReader;
import scriptella.driver.ldap.ldif.TrackingLineIterator;
import scriptella.expression.LineIterator;
import scriptella.spi.AbstractConnection;
import scriptella.spi.ParametersCallback;

import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executor for LDIF script.
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class LdifScript {
    private static final Logger LOG = Logger.getLogger(LdifScript.class.getName());
    //Parsing options for DN, TODO maybe replace with a DirCtx name parser?
    static final Properties DN_SYNTAX = new Properties();

    static {
        DN_SYNTAX.setProperty("jndi.syntax.direction", "right_to_left");
        DN_SYNTAX.setProperty("jndi.syntax.separator", ",");
        DN_SYNTAX.setProperty("jndi.syntax.ignorecase", "true");
        DN_SYNTAX.setProperty("jndi.syntax.trimblanks", "true");
    }

    private final LdapConnection connection;

    public LdifScript(LdapConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        this.connection = connection;
    }


    /**
     * Executes an LDIF content from the specified reader.
     *
     * @param reader reader with LDIF content.
     * @throws LdapProviderException if directory access failed.
     */
    public void execute(Reader reader, DirContext ctx, ParametersCallback parameters) throws LdapProviderException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        if (ctx == null) {
            throw new IllegalArgumentException("DirContext cannot be null");
        }
        if (parameters == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        TrackingLineIterator in = new TrackingLineIterator(reader, parameters);
        AbstractConnection.StatementCounter counter = connection.getStatementCounter();
        try {
            in.trackLines();
            for (LdifIterator it = newLdifIterator(in); it.hasNext(); in.trackLines()) {
                EtlCancelledException.checkEtlCancelled();
                Entry e = it.next();
                if (isReadonly()) {
                    LOG.info("Readonly Mode - " + e + " has been skipped.");
                } else {
                    modify(ctx, e);
                }
                counter.statements++;
            }
        } catch (LdifParseException e) {
            if (e.getErrorStatement() == null) {
                e.setErrorStatement(in.getTrackedLines());
            }
            throw e;
        } catch (NamingException e) {
            LdapProviderException ex = new LdapProviderException("Failed to execute LDIF entry", e);
            ex.setErrorStatement(in.getTrackedLines());
            throw ex;
        }
    }

    /**
     * Adds/modifies ctx using entry information.
     *
     * @param ctx directory context to use for change.
     * @param e   entry with change description.
     * @throws NamingException if operation with directory failed.
     */
    static void modify(DirContext ctx, final Entry e) throws NamingException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Processing " + e);
        }
        Attributes atts = e.getAttributes();
        final String rootDn = ctx.getNameInNamespace();
        if (atts != null) { //If add entry
            ctx.createSubcontext(getRelativeDN(rootDn, e.getDn()), e.getAttributes());
        } else if (e.isChangeDelete()) {
            ctx.destroySubcontext(getRelativeDN(rootDn, e.getDn()));
        } else if (e.isChangeModDn() || e.isChangeModRdn()) {
            Name newRdn;
            if (e.getNewSuperior() != null) { //If new superior
                newRdn = getRelativeDN(rootDn, e.getNewSuperior());
            } else { //otherwise use DN as a base
                newRdn = getRelativeDN(rootDn, e.getDn());
                newRdn.remove(newRdn.size() - 1);
            }
            newRdn.add(e.getNewRdn());
            ctx.addToEnvironment("java.naming.ldap.deleteRDN", String.valueOf(e.isDeleteOldRdn()));
            ctx.rename(getRelativeDN(rootDn, e.getDn()), newRdn);
            ctx.removeFromEnvironment("java.naming.ldap.deleteRDN");//a better solution to use the previous value

        } else {
            List<ModificationItem> items = e.getModificationItems();
            ctx.modifyAttributes(getRelativeDN(rootDn, e.getDn()),
                    items.toArray(new ModificationItem[items.size()]));
        }
    }


    /**
     * @param rootDn root context DN.
     * @param dn     DN to compute a relative name. DN must starts with rootDn.
     * @return name relative to a root context DN.
     */
    static Name getRelativeDN(final String rootDn, final String dn) throws NamingException {
        CompoundName root = new CompoundName(rootDn, DN_SYNTAX);
        CompoundName entry = new CompoundName(dn, DN_SYNTAX);
        if (!entry.startsWith(root)) {
            throw new NamingException("Dn " + dn + " is not from root DN " + rootDn);
        }
        return entry.getSuffix(root.size());
    }

    /**
     * Accessor for testing purposes
     */
    protected Long getMaxFileLength() {
        return connection == null ? null : connection.getMaxFileLength();
    }

    protected boolean isReadonly() {
        return connection != null && connection.isReadonly();
    }

    private LdifIterator newLdifIterator(LineIterator in) {
        Long mx = getMaxFileLength();
        return mx == null ? new LdifIterator(in) : new LdifIterator(in, mx);

    }

    private class LdifIterator extends LdifReader {

        public LdifIterator(LineIterator in, long sizeLimit) {
            super(in, sizeLimit);
        }

        public LdifIterator(LineIterator in) {
            super(in);
        }

        protected InputStream getUriStream(String uri) throws IOException {
            return connection.getDriversContext().resolve(uri).openStream();
        }
    }

}
