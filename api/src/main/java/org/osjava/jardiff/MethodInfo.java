/*
 * org.osjava.jardiff.MethodInfo
 *
 * $Id: IOThread.java 1952 2005-08-28 18:03:41Z cybertiger $
 * $URL: https://svn.osjava.org/svn/osjava/trunk/osjava-nio/src/java/org/osjava/nio/IOThread.java $
 * $Rev: 1952 $
 * $Date: 2005-08-28 18:03:41 +0000 (Sun, 28 Aug 2005) $
 * $Author: cybertiger $
 *
 * Copyright (c) 2005, Antony Riley
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name JarDiff nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
