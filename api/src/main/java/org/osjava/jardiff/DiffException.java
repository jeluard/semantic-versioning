/**
 * Copyright 2012-2014 Julien Eluard and contributors
 * This project includes software developed by Julien Eluard: https://github.com/jeluard/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osjava.jardiff;

/**
 * A wrapper exception classes for various exceptions that can happen
 * whilst performing a diff.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class DiffException extends Exception
{
    /**
     * Create a new DiffException wrapping another exception.
     *
     * @param toWrap the wrapped exception
     */
    public DiffException(Exception toWrap) {
        super((Throwable) toWrap);
    }
}
