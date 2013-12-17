/**
 * Copyright 2012 Julien Eluard
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
 * A set of Tools which do not belong anywhere else in the API at this time.
 * This is nasty, but for now, useful.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public final class Tools
{
    /**
     * Private constructor so this class can't be instantiated.
     */
    private Tools() {
        /* empty */
    }
    
    /**
     * Get the java class name given an internal class name.
     * This method currently replaces all instances of $ and / with . this
     * may not be according to the java language spec, and will almost
     * certainly fail for some inner classes.
     * 
     * @param internalName The internal name of the class.
     * @return The java class name.
     */
    public static final String getClassName(String internalName) {
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
