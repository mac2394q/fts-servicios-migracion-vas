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

import scriptella.expression.LineIterator;
import scriptella.expression.PropertiesSubstitutor;
import scriptella.spi.AbstractConnection;
import scriptella.spi.ParametersCallback;
import scriptella.spi.QueryCallback;
import scriptella.util.ExceptionUtils;
import scriptella.util.IOUtils;
import scriptella.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class executes a regex query over a text file content.
 * <p>The query has a standard regex syntax.
 * Matching is performed for each line of the text file.
 * <p><u>Example:</u></p>
 * <b>Query:</b> <code>.*;(2\d+);.*</code><br>
 * <b>Text File:</b>
 * <code>
 * <pre>
 * 1;100;record 1
 * 2;200;record 2
 * 3;250;record 3
 * </pre>
 * </code>
 * As the result of the query execution the following result set is produced:
 * <table border=1>
 * <tr>
 * <th>Column name/<br>row number</th>
 * <th>0</th>
 * <th>1</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>2;200;record 2</td>
 * <td>200</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>3;250;record 3</td>
 * <td>250</td>
 * </tr>
 * </table>
 * Where column name corresponds to the matched regex group name.
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
class TextQueryExecutor implements ParametersCallback {
    private static final Logger LOG = Logger.getLogger(TextQueryExecutor.class.getName());
    private final PropertiesSubstitutor ps;
    private Pattern[] query;
    private Matcher result;
    private boolean trim;
    private int skipLines;
    private String nullString;
    private static final String COLUMN_PREFIX = "column";

    public TextQueryExecutor(final Reader queryReader, final PropertiesSubstitutor substitutor,
                             final boolean trim, final int skipLines, final String nullString) {
        this.trim = trim;
        this.skipLines = skipLines;
        this.nullString = nullString;
        ps = substitutor;
        //Compiles patterns loaded from specified reader.
        //Patterns are read line-by-line.
        BufferedReader r = IOUtils.asBuffered(queryReader);
        List<Pattern> result = new ArrayList<Pattern>();
        try {
            for (String s; (s = r.readLine()) != null;) {
                if (trim) {
                    s = s.trim();
                }
                s = ps.substitute(s);
                if (s.length() > 0) { //Not empty string
                    try {
                        result.add(Pattern.compile(s, Pattern.CASE_INSENSITIVE));
                    } catch (Exception e) {
                        throw new TextProviderException("Specified query is not a valid regex: " + s, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new TextProviderException("Unable to read query content", e);
        } finally {
            IOUtils.closeSilently(r);
        }
        if (result.isEmpty()) {
            LOG.fine("Empty query matches all lines");
            result.add(Pattern.compile(".*"));
        }
        query = result.toArray(new Pattern[result.size()]);
    }

    /**
     * Executes a query and iterates the resultset using the callback.
     *
     * @param qc      callback to notify on each row.
     * @param counter statements counter.
     */
    public void execute(Reader reader, final QueryCallback qc, AbstractConnection.StatementCounter counter) {
        int qCount = query.length;
        Matcher[] matchers = new Matcher[qCount];
        LineIterator it = new LineIterator(reader, ps, trim);
        //Skip a specified number of lines
        for (int i = 0; i < skipLines && it.hasNext(); i++) {
            it.next();
        }
        while (it.hasNext()) {
            String line = it.next();
            for (int i = 0; i < qCount; i++) {
                Matcher m = matchers[i];
                if (m == null) { //First time initialization
                    m = query[i].matcher(line);
                    matchers[i] = m;
                } else { //Reuse matcher for better performance
                    m.reset(line);
                }
                if (m.find()) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.info("Pattern matched: " + m);
                    }
                    result = m;
                    qc.processRow(this);
                }
            }
        }
        counter.statements += qCount;
    }


    /**
     * Returns the value of the named parameter.
     * <p>Use index of the captured group to obtain the value of the matched substring.
     *
     * @param name parameter name.
     * @return parameter value.
     */
    public Object getParameter(final String name) {
        String str = name;
        if (str != null && str.startsWith(COLUMN_PREFIX)) {
            str = name.substring(COLUMN_PREFIX.length());
        }
        if (StringUtils.isDecimalInt(str)) {
            try {
                int ind = Integer.parseInt(str);
                if (ind >= 0 && ind <= result.groupCount()) {
                    final String s = result.group(ind);
                    return (nullString != null && nullString.equals(s)) ? null : s;
                }
            } catch (NumberFormatException e) {
                ExceptionUtils.ignoreThrowable(e);
            }
        }
        return ps.getParameters().getParameter(name);
    }
}
