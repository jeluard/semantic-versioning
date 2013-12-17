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
 * A class to hold information about a method.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public final class MethodInfo extends AbstractInfo
{
    /**
     * The method descriptor.
     */
    private String desc;

    /**
     * The signature of the method.
     */
    private String signature;

    /**
     * An array of the exceptions thrown by this method.
     */
    private String[] exceptions;
    
    /**
     * Create a new MethodInfo with the specified parameters.
     *
     * @param access The access flags for the method.
     * @param name The name of the method.
     * @param signature The signature of the method.
     * @param exceptions The exceptions thrown by the method.
     */
    public MethodInfo(int access, String name, String desc, String signature,
                      String[] exceptions) {
        super(access, name);
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }
    
    /**
     * Get the descriptor for the method.
     *
     * @return the descriptor
     */
    public final String getDesc() {
        return desc;
    }
    
    /**
     * Get the signature for the method.
     *
     * @return the signature
     */
    public final String getSignature() {
        return signature;
    }
    
    /**
     * Get the array of exceptions which can be thrown by the method.
     *
     * @return the exceptions as a String[] of internal names.
     */
    public final String[] getExceptions() {
        return exceptions;
    }
}
