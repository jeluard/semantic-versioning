/*
 * org.osjava.jardiff.AbstractInfo
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

import org.objectweb.asm.Opcodes;

/**
 * An abstract class representing information about a class, method or field.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public abstract class AbstractInfo
{
    /**
     * The string used to represent a class, method or field with public 
     * access.
     */
    public final String ACCESS_PUBLIC = "public";
    
    /**
     * The string used to represent a class, method or field with protected 
     * access.
     */
    public final String ACCESS_PROTECTED = "protected";

    /**
     * The string used to represent a class, method or field with package
     * private access.
     * Package private access is the default access level used by java when
     * you do not specify one of public, protected or private.
     */
    public final String ACCESS_PACKAGE = "package";

    /**
     * The string used to represent a class, method or field with private
     * access.
     */
    public final String ACCESS_PRIVATE = "private";

    /**
     * The access flags for this class, method or field.
     */
    private final int access;
    
    /**
     * The internal name of this class, method or field.
     */
    private final String name;
    
    /**
     * Construct a new AbstractInfo with the specified access and name.
     *
     * @param access The access flags for this class, method or field.
     * @param name The internal name of this class, method or field.
     */
    public AbstractInfo(int access, String name) {
        this.access = access;
        this.name = name;
    }
    
    /**
     * Get the access flags for this class, method or field.
     *
     * @return the access flags.
     */
    public final int getAccess() {
        return access;
    }
    
    /**
     * Get the internal name of this class, method or field.
     *
     * @return the name
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Test if this class, method or field is public.
     *
     * @return true if it is public.
     */
    public final boolean isPublic() {
        return (access & Opcodes.ACC_PUBLIC) != 0;
    }
    
    /**
     * Test if this class, method or field is protected.
     *
     * @return true if it is protected.
     */
    public final boolean isProtected() {
        return (access & Opcodes.ACC_PROTECTED) != 0;
    }
    
    /**
     * Test if this class, method or field is package private.
     *
     * @return true if it is package private.
     */
    public final boolean isPackagePrivate() {
        return (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | 
                    Opcodes.ACC_PRIVATE)) == 0;
    }
    
    /**
     * Test if this class, method or field is private.
     *
     * @return true if it is private.
     */
    public final boolean isPrivate() {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }
    
    /**
     * Test if this class, method or field is abstract.
     *
     * @return true if it is abstract.
     */
    public final boolean isAbstract() {
        return (access & Opcodes.ACC_ABSTRACT) != 0;
    }
    
    /**
     * Test if this class, method or field is annotation
     *
     * @return true if it is annotation.
     */
    public final boolean isAnnotation() {
        return (access & Opcodes.ACC_ANNOTATION) != 0;
    }
    
    /**
     * Test if this class, method or field is a bridge
     *
     * @return true if it is a bridge.
     */
    public final boolean isBridge() {
        return (access & Opcodes.ACC_BRIDGE) != 0;
    }
    
    /**
     * Test if this class, method or field is deprecated.
     *
     * @return true if it is deprecated.
     */
    public final boolean isDeprecated() {
        return (access & Opcodes.ACC_DEPRECATED) != 0;
    }
    
    /**
     * Test if this class, method or field is an enum.
     *
     * @return true if it is an enum.
     */
    public final boolean isEnum() {
        return (access & Opcodes.ACC_ENUM) != 0;
    }
    
    /**
     * Test if this class, method or field is final.
     *
     * @return true if it is final.
     */
    public final boolean isFinal() {
        return (access & Opcodes.ACC_FINAL) != 0;
    }
    
    /**
     * Test if this class, method or field is an interface.
     *
     * @return true if it is an interface.
     */
    public final boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }
    
    /**
     * Test if this class, method or field is native.
     *
     * @return true if it is native.
     */
    public final boolean isNative() {
        return (access & Opcodes.ACC_NATIVE) != 0;
    }
    
    /**
     * Test if this class, method or field is static.
     *
     * @return true if it is static.
     */
    public final boolean isStatic() {
        return (access & Opcodes.ACC_STATIC) != 0;
    }
    
    /**
     * Test if this class, method or field is string.
     *
     * @return true if it is strict.
     */
    public final boolean isStrict() {
        return (access & Opcodes.ACC_STRICT) != 0;
    }
    
    /**
     * Test if this class, method or field is super.
     *
     * @return true if it is super.
     */
    public final boolean isSuper() {
        return (access & Opcodes.ACC_SUPER) != 0;
    }
    
    /**
     * Test if this class, method or field is synchronized.
     *
     * @return true if it is synchronized
     */
    public final boolean isSynchronized() {
        return (access & Opcodes.ACC_SYNCHRONIZED) != 0;
    }
    
    /**
     * Test if this class, method or field is synthetic.
     *
     * @return true if it is synchronized.
     */
    public final boolean isSynthetic() {
        return (access & Opcodes.ACC_SYNTHETIC) != 0;
    }
    
    /**
     * Test if this class or field is transient.
     * If this flag is set on a method it means something different.
     *
     * @return true if it is transient.
     */
    public final boolean isTransient() {
        return !(this instanceof MethodInfo) &&
            ((access & Opcodes.ACC_TRANSIENT) != 0);
    }
    
    /**
     * Test if this method is varargs.
     * If this flag is set on a class or field it means something different.
     * Well, it probably shouldn't be set on a class as it would make
     * no sense, it only really makes sense on fields and methods.
     *
     * @return true if it is vargargs.
     */
    public final boolean isVarargs() {
        return (this instanceof MethodInfo) && 
            ((access & Opcodes.ACC_VARARGS) != 0);
    }
    
    /**
     * Test if this class, method or field is volatile.
     * 
     * @return true if it is volatile.
     */
    public final boolean isVolatile() {
        return (access & Opcodes.ACC_VOLATILE) != 0;
    }
    
    /**
     * Retrivie the access level for this class, method or field.
     *
     * @return the access level
     */
    public final String getAccessType() {
        if (isPublic())
            return ACCESS_PUBLIC;
        if (isProtected())
            return ACCESS_PROTECTED;
        if (isPrivate())
            return ACCESS_PRIVATE;
        return ACCESS_PACKAGE;
    }
}
