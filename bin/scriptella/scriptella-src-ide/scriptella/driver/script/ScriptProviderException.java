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
package scriptella.driver.script;

import scriptella.spi.ProviderException;

/**
 * Thrown to indicate an error in scripting engine.
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class ScriptProviderException extends ProviderException {
    public ScriptProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptProviderException(String message, Throwable cause, String errorStatement) {
        super(message, cause);
        setErrorStatement(errorStatement);
    }


    public String getProviderName() {
        return "javax.script";
    }
}
