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
/**
 * $Header: $
 * $Revision: $
 * $Date: $
 *
 * Copyright 2003-2005
 * All rights reserved.
 */
package scriptella.spi;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Global ETL Context available to drivers.
 * @author Fyodor Kupolov
 * @version 1.0
 */
public interface DriverContext extends ParametersCallback {
    public URL getScriptFileURL();


    /**
     * Resolves a fileUrl URI relative to base URL.
     * <p><b>Examples:</b></p>
     * <code><pre>
     * baseUrl = "file:///tmp/doc.xml"
     * uri = "http://site.com/file.html"
     * </pre></code>
     * Resolves to: <code>http://site.com/file.html</code>
     * <code><pre>
     * baseUrl = "file:///tmp/doc.xml"
     * uri = "file.html"
     * </pre></code>
     * Resolves to: <code>file:///tmp/file.html</code>
     *
     * @param uri URI to resolve..
     * @return resolved file URL.
     * @see scriptella.execution.EtlContext#getScriptFileURL()
     * @throws MalformedURLException if uri is malformed or cannot be resolved.
     */
    public URL resolve(final String uri) throws MalformedURLException;
}
