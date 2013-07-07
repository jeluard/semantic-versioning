/*
 * org.osjava.jardiff.FieldInfo
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
 * Information about a field of a class.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public final class FieldInfo extends AbstractInfo
{
    /**
     * The field descriptor for this field.
     */
    private String desc;
    
    /**
     * The signature for this field.
     */
    private String signature;
    
    /**
     * The initial value of this field.
     */
    private Object value;
    
    /**
     * Create a new FieldInfo
     *
     * @param access The access flags.
     * @param name The name of the field.
     * @param desc The field discriptor.
     * @param signature The signature of this field.
     * @param value The initial value of the field.
     */
    public FieldInfo(int access, String name, String desc, String signature,
                     Object value) {
        super(access, name);
        this.desc = desc;
        this.signature = signature;
        this.value = value;
    }
    
    /**
     * Get the descriptor for this FieldInfo.
     *
     * @return The field descriptor.
     */
    public final String getDesc() {
        return desc;
    }
    
    /**
     * Get the signature for this fieldinfo.
     *
     * @return The signature.
     */
    public final String getSignature() {
        return signature;
    }
    
    /**
     * Get the initial value for this fieldinfo
     *
     * @return The initial value.
     */
    public final Object getValue() {
        return value;
    }
}
