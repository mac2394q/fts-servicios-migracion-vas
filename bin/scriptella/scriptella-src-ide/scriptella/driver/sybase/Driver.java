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
package scriptella.driver.sybase;

import scriptella.jdbc.GenericDriver;

/**
 * Scriptella Adapter for Sybase ASE/ASA databases.
 * <p>For configuration details and examples see <a href="package-summary.html">overview page</a>.
 *
 * @author Kirill Volgin
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class Driver extends GenericDriver {
    public static final String SYBASE_DRIVER_NAME = "com.sybase.jdbc.SybDriver";
    public static final String SYBASE_JDBC2_DRIVER_NAME = "com.sybase.jdbc2.jdbc.SybDriver";
    public static final String SYBASE_JDBC3_DRIVER_NAME = "com.sybase.jdbc3.jdbc.SybDriver";
    public static final String SYBASE_TDS_DRIVER_NAME = "net.sourceforge.jtds.jdbc.Driver";


    public Driver() {
        loadDrivers(SYBASE_JDBC3_DRIVER_NAME, SYBASE_JDBC2_DRIVER_NAME, SYBASE_DRIVER_NAME, SYBASE_TDS_DRIVER_NAME);
    }

}