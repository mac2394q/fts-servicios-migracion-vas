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
package scriptella.driver.janino;

import scriptella.spi.ProviderException;

/**
 * Thrown to indicate Janino failure.
 */
public class JaninoProviderException extends ProviderException {
    public JaninoProviderException() {
    }

    public JaninoProviderException(String message) {
        super(message);
    }

    public JaninoProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaninoProviderException(Throwable cause) {
        super(cause);
    }

    public String getProviderName() {
        return Driver.DIALECT_IDENTIFIER.getName();
    }

    @Override
    public ProviderException setErrorStatement(String errStmt) {
        return super.setErrorStatement(errStmt);
    }

}