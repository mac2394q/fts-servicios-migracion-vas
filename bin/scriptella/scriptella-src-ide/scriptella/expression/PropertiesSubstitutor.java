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
package scriptella.expression;

import scriptella.spi.ParametersCallback;
import scriptella.spi.support.MapParametersCallback;
import scriptella.util.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Substitutes properties(or expressions) in strings.
 * <p>$ symbol indicate  property or expression to evaluate and substitute.
 * <p>The following properties/expression syntax is used:
 * <h3>Property reference</h3>
 * References named property.
 * <br>Examples:
 * <pre><code>
 * $foo
 * </code></pre>
 * <h3>Expression</h3>.
 * Expression is wrapped by braces and evaluated by {@link Expression} engine.
 * Examples:
 * <pre><code>
 * ${name+' '+surname} etc.
 * </code></pre>
 * </ul>
 * <p>This class is not thread safe
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class PropertiesSubstitutor {
    /**
     * Simple property patterns, e.g. $property
     */
    public static final Pattern PROP_PTR = Pattern.compile("([a-zA-Z_0-9\\.]+)");

    /**
     * Expression pattern, e.g. ${property} etc.
     */
    public static final Pattern EXPR_PTR = Pattern.compile("\\{([^\\}]+)\\}");

    final Matcher m1 = PROP_PTR.matcher("");
    final Matcher m2 = EXPR_PTR.matcher("");

    private ParametersCallback parameters;
    private String nullString;

    /**
     * Creates a properties substitutor.
     * <p>This constructor is used for performance critical places where multiple instantiation
     * via {@link #PropertiesSubstitutor(scriptella.spi.ParametersCallback)} is expensive.
     * <p><b>Note:</b> {@link #setParameters(scriptella.spi.ParametersCallback)} must be called before
     * {@link #substitute(String)}.
     */
    public PropertiesSubstitutor() {
    }

    /**
     * Creates a properties substitutor.
     *
     * @param parameters parameters callback to use for substitution.
     */
    public PropertiesSubstitutor(ParametersCallback parameters) {
        this.parameters = parameters;
    }


    /**
     * Creates a properties substitutor based on specified properties map.
     *
     * @param map parameters to substitute.
     */
    public PropertiesSubstitutor(Map<String, ?> map) {
        this(new MapParametersCallback(map));
    }

    /**
     * Substitutes properties/expressions in s and returns the result string.
     * <p>If result of evaluation is null or the property being substitued doesn't have value in callback - the whole
     * expressions is copied into result string as is.
     *
     * @param s string to substitute. Null strings allowed.
     * @return substituted string.
     */
    public String substitute(final String s) {
        if (parameters == null) {
            throw new IllegalStateException("setParameters must be called before calling substitute");
        }

        int i = firstCandidate(s); //Remember the first index of $
        if (i < 0) { //skip strings without $ char, or when the $ is the last char
            return s;
        }
        final int len = s.length() - 1; //Last character is not checked - optimization
        StringBuilder res = null;
        int lastPos = 0;
        m1.reset(s);
        m2.reset(s);

        for (; i >= 0 && i < len; i = s.indexOf('$', i + 1)) {
            //Start of expression
            Matcher m;
            if (m1.find(i + 1) && m1.start() == i + 1) {
                m = m1;
            } else if (m2.find(i + 1) && m2.start() == i + 1) {
                m = m2;
            } else { //not an expression
                m = null;
            }
            if (m != null) {
                final String name = m.group(1);
                String v;

                if (m == m1) {
                    v = toString(parameters.getParameter(name));
                } else {
                    v = toString(Expression.compile(name).evaluate(parameters));
                }

                if (v != null) {
                    if (res == null) {
                        res = new StringBuilder(s.length());
                    }
                    if (i > lastPos) { //if we have unflushed character
                        res.append(s.substring(lastPos, i));
                    }
                    lastPos = m.end();
                    res.append(v);
                }

            }
        }
        if (res == null) {
            return s;
        }
        if (lastPos <= len) {
            res.append(s.substring(lastPos, s.length()));
        }
        return res.toString();
    }

    /**
     * Copies content from reader to writer and expands properties.
     *
     * @param reader reader to process.
     * @param writer writer to output substituted content to.
     * @throws IOException if I/O error occurs.
     */
    public void substitute(final Reader reader, final Writer writer) throws IOException {
        //Current implementation is too simple,
        // we need to provide a better implementation for stream based content.
        writer.write(substitute(IOUtils.toString(reader)));
    }

    /**
     * Reads content from reader and expands properties.
     * <p><b>Note:</b> For performance reasons use
     * {@link #substitute(java.io.Reader,java.io.Writer)} if possible.
     *
     * @param reader reader to process.
     * @return reader's content with properties expanded.
     * @throws IOException if I/O error occurs.
     * @see #substitute(java.io.Reader,java.io.Writer)
     */
    public String substitute(final Reader reader) throws IOException {
        //Current implementation is too simple,
        // we need to provide a better implementation for stream based content.
        return substitute(IOUtils.toString(reader));
    }


    /**
     * @return parameter callback used for substitution.
     */
    public ParametersCallback getParameters() {
        return parameters;
    }

    /**
     * Sets parameters callback used for substitution.
     *
     * @param parameters not null parameters callback.
     */
    public void setParameters(ParametersCallback parameters) {
        this.parameters = parameters;
    }

    /**
     * Returns string literal representing null value.
     * <p>Used when converting objects {@link #toString(Object)}.
     *
     * @return string representing null value.
     */
    public String getNullString() {
        return nullString;
    }

    /**
     * Sets string literal representing null.
     * <p>Used when converting objects {@link #toString(Object)}.
     *
     * @param nullString string literal representing null
     */
    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    /**
     * Converts specified object to string.
     * <p>{@link #getNullString()} represents null values.
     * <p>Subclasses may provide custom conversion strategy here.
     *
     * @param o object to convert to String.
     * @return string representation of object.
     */
    protected String toString(final Object o) {
        return o == null ? nullString : o.toString();
    }

    /**
     * Tests if the given string contains properties/expressions.
     *
     * @param string string to check.
     * @return true if a given string contains properties/expressions.
     */
    public static boolean hasProperties(String string) {
        return firstCandidate(string) >= 0;
    }

    static int firstCandidate(String string) {
        if (string == null) {
            return -1;
        }
        int n = string.length();
        if (n < 2) {
            return -1;
        }
        return string.indexOf('$');
    }

}
