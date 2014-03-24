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
 * An abstract implementation of DiffHandler which provides utility methods.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public abstract class AbstractDiffHandler implements DiffHandler
{
    /**
     * Get the java classname given the internal class name internalName.
     *
     * @return the classname for internalName
     */
    protected final String getClassName(String internalName) {
        StringBuffer ret = new StringBuffer(internalName.length());
        for (int i = 0; i < internalName.length(); i++) {
            char ch = internalName.charAt(i);
            switch (ch) {
            case '$':
            case '/':
                ret.append('.');
                break;
            default:
                ret.append(ch);
            }
        }
        return ret.toString();
    }
}
