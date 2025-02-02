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
package scriptella.driver.text;

import scriptella.spi.AbstractConnection;
import scriptella.spi.ConnectionParameters;
import scriptella.spi.DialectIdentifier;
import scriptella.util.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

/**
 * Base class for Text/CSV connections.
 * <p>TODO: Move this class to a spi.support.text package.
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public abstract class AbstractTextConnection extends AbstractConnection {
    protected final String encoding;
    protected final boolean trim;
    protected final boolean flush;
    protected final URL url; //if null - use console
    protected final String eol;
    protected final int skipLines;
    protected final String nullString;


    /**
     * Name of the <code>encoding</code> connection property.
     * Specifies charset encoding in text files.
     */
    public static final String ENCODING = "encoding";
    /**
     * Name of the <code>eol</code> connection property.
     * EOL suffix. Default value is \n.
     */
    public static final String EOL = "eol";

    /**
     * Name of the <code>trim</code> connection property.
     * Value of <code>true</code> specifies that the leading and trailing
     * whitespaces should be omitted.
     */
    public static final String TRIM = "trim";

    /**
     * Name of the <code>flush</code> connection property.
     * Value of <code>true</code> specifies that the outputted content should flushed immediately after
     * the &lt;script&gt; element completes.
     */
    public static final String FLUSH = "flush";

    /**
     * Name of the <code>skip_lines</code> connection property.
     * The number of lines to skip before start reading.
     * Default value is 0 (no lines are skipped).
     * <p>Only valid for &lt;query&gt; elements.
     */
    public static final String SKIP_LINES = "skip_lines";

    /**
     * Name of the <code>null_string</code> connection property.
     * If set, specifies value of a string token to be parsed as Java <code>null</code> literal.
     */
    public static final String NULL_STRING = "null_string";


    /**
     * For testing only.
     */
    protected AbstractTextConnection() {
        encoding = null;
        trim = false;
        flush = false;
        url = null;
        eol = "\n";
        skipLines = 0;
        nullString = null;
    }

    /**
     * Initializes a text connection using specified properties.
     *
     * @param dialectIdentifier
     * @param parameters
     */
    protected AbstractTextConnection(DialectIdentifier dialectIdentifier, ConnectionParameters parameters) {
        super(dialectIdentifier, parameters);
        //URL can be set null, in this case console is used for reading/writing
        url = parameters.getUrl() == null ? null : parameters.getResolvedUrl();
        encoding = parameters.getCharsetProperty(ENCODING);
        trim = parameters.getBooleanProperty(TRIM, true);
        //When printing to console - flushing is enabled
        flush = url == null || parameters.getBooleanProperty(FLUSH, false);
        String eolStr = parameters.getStringProperty(TextConnection.EOL);
        eol = eolStr != null ? eolStr : "\n";//Default value
        skipLines = parameters.getIntegerProperty(SKIP_LINES, 0);
        nullString = parameters.getStringProperty(NULL_STRING);
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean isTrim() {
        return trim;
    }

    /**
     * Returns resolved URL for this connection.
     * <p>If null, the console is used for reading/writing.
     *
     * @return resolved URL or null.
     */
    public URL getUrl() {
        return url;
    }

    public String getEol() {
        return eol;
    }

    /**
     * Creates a new writer to send output to.
     *
     * @return writer for output.
     * @throws IOException if IO error occured.
     */
    protected Writer newOutputWriter() throws IOException {
        return url == null ? ConsoleAdapters.getConsoleWriter(encoding) :
                IOUtils.getWriter(IOUtils.getOutputStream(url), encoding);
    }

    /**
     * Creates a new reader for input.
     *
     * @return reader for input.
     * @throws IOException if IO error occured.
     */
    protected Reader newInputReader() throws IOException {
        return url == null ? ConsoleAdapters.getConsoleReader(encoding) :
                IOUtils.getReader(url.openStream(), encoding);
    }

}
