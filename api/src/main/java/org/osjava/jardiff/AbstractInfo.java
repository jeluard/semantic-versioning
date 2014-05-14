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
     * Get the descriptor
     *
     * @return The descriptor.
     */
    public abstract String getDesc();

    /**
     * Get the signature
     *
     * @return The signature.
     */
    public abstract String getSignature();

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
     * @return true if it is varargs.
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
     * Retrieve the access level for this class, method or field.
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
