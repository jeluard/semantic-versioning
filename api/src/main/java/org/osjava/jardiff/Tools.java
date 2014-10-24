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
        final StringBuffer ret = new StringBuffer(internalName.length());
        for (int i = 0; i < internalName.length(); i++) {
            final char ch = internalName.charAt(i);
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

    private static boolean has(final int value, final int mask) {
        return (value & mask) != 0;
    }
    private static boolean not(final int value, final int mask) {
        return (value & mask) == 0;
    }

    private static boolean isLessAccessPermitted(int oldAccess, int newAccess) {
        if (has(newAccess, Opcodes.ACC_PUBLIC)) {
            return false;
        } else if (has(newAccess, Opcodes.ACC_PROTECTED)) {
            return has(oldAccess, Opcodes.ACC_PUBLIC);
        } else if (has(newAccess, Opcodes.ACC_PRIVATE)) {
            return not(oldAccess, Opcodes.ACC_PRIVATE);
        } else {
            return has(oldAccess, Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED);
        }
    }

    /**
     * @deprecated Use {@link #isClassAccessChange(int, int)}.
     */
    public static boolean isAccessChange(int oldAccess, int newAccess) {
        return isClassAccessChange(oldAccess, newAccess);
    }

    /**
     * Returns whether a class's newAccess is incompatible with oldAccess
     * following <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html">Java Language Specification, Java SE 7 Edition</a>:
     * <ul>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.1">13.4.1 abstract Classes</a><ul>
     *     <li>If a class that was not declared abstract is changed to be declared abstract,
     *         then pre-existing binaries that attempt to create new instances of that class
     *         will throw either an InstantiationError at link time,
     *         or (if a reflective method is used) an InstantiationException at run time.
     *         Such changes <b>break backward compatibility</b>!</li>
     *     <li>Changing a class that is declared abstract to no longer be declared abstract
     *         <b>does not break compatibility</b> with pre-existing binaries.</li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.2">13.4.2 final Classes</a><ul>
     *     <li>If a class that was not declared final is changed to be declared final,
     *         then a VerifyError is thrown if a binary of a pre-existing subclass of this class is loaded,
     *         because final classes can have no subclasses.
     *         Such changes <b>break functional backward compatibility</b>!</li>
     *     <li>Changing a class that is declared final to no longer be declared final
     *         <b>does not break compatibility</b> with pre-existing binaries.</li>
     *     </ul></li>
     * </ul>
     *
     * @param oldAccess
     * @param newAccess
     * @return
     */
    public static boolean isClassAccessChange(final int oldAccess, final int newAccess) {
        if ( not(oldAccess, Opcodes.ACC_ABSTRACT) && has(newAccess, Opcodes.ACC_ABSTRACT) ) {
            return true; // 13.4.1 #1
        } else  if ( not(oldAccess, Opcodes.ACC_FINAL) && has(newAccess, Opcodes.ACC_FINAL) ) {
            return true; // 13.4.2 #1
        } else {
            final int compatibleChanges = Opcodes.ACC_ABSTRACT |     // 13.4.1 #2
                                          Opcodes.ACC_FINAL ;        // 13.4.2 #2
            // FIXME Opcodes.ACC_VOLATILE ?
            final int oldAccess2 = oldAccess & ~compatibleChanges;
            final int newAccess2 = newAccess & ~compatibleChanges;
            return oldAccess2 != newAccess2;
        }
    }

    /**
     * Returns whether a field's newAccess is incompatible with oldAccess
     * following <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html">Java Language Specification, Java SE 7 Edition</a>:
     * <ul>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.9">13.4.7 Access to Members and Constructors</a><ul>
     *     <li>Changing the declared access of a member or constructor to permit less access
     *        <b>may break compatibility</b> with pre-existing binaries, causing a linkage error to be thrown when these binaries are resolved.
     *     </li>
     *     <li>The binary format is defined so that changing a member or constructor to be more accessible does not cause a
     *         linkage error when a subclass (already) defines a method to have less access.
     *     </li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.9">13.4.9 final Fields and Constants</a><ul>
     *     <li>If a field that was not declared final is changed to be declared final,
     *         then it <b>can break compatibility</b> with pre-existing binaries that attempt to assign new values to the field.</li>
     *     <li>Deleting the keyword final or changing the value to which a <i>non-final</i> field is initialized
     *         <b>does not break compatibility</b> with existing binaries.</li>
     *     <li>If a field is a constant variable (§4.12.4),
     *         then deleting the keyword final or changing its value
     *         will <i>not break compatibility</i> with pre-existing binaries by causing them not to run,
     *         but they will not see any new value for the usage of the field unless they are recompiled.
     *         This is true even if the usage itself is not a compile-time constant expression (§15.28).
     *         Such changes <b>break functional backward compatibility</b>!</li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.10">13.4.10 static Fields</a><ul>
     *     <li>If a field that is not declared private was not declared static
     *         and is changed to be declared static, or vice versa,
     *         then a linkage error, specifically an IncompatibleClassChangeError,
     *         will result if the field is used by a pre-existing binary which expected a field of the other kind.
     *         Such changes <b>break backward compatibility</b>!</li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.11">13.4.11. transient Fields </a><ul>
     *     <li>Adding or deleting a transient modifier of a field
     *         <b>does not break compatibility</b> with pre-existing binaries.</li>
     *   </ul></li>
     *   <li><a href="http://www.wsu.edu/UNIX_Systems/java/langspec-1.0/13.doc.html#45194">13.4.11 volatile Fields (JLS 1.0)</a><ul>
     *     <li>If a field that is not declared private was not declared volatile
     *         and is changed to be declared volatile, or vice versa, then a linkage time error,
     *         specifically an IncompatibleClassChangeError, may result if the field is used
     *         by a preexisting binary that expected a field of the opposite volatility.
     *         Such changes <b>break backward compatibility</b>!</li>
     *   </ul></li>
     * </ul>
     *
     * @param oldAccess
     * @param newAccess
     * @return
     */
    public static boolean isFieldAccessChange(final int oldAccess, final int newAccess) {
        if (isLessAccessPermitted(oldAccess, newAccess)) {
            return true; // 13.4.7
        }
        if ( not(oldAccess, Opcodes.ACC_FINAL) && has(newAccess, Opcodes.ACC_FINAL) ) {
            return true; // 13.4.9 #1
        } else {
            final int compatibleChanges = Opcodes.ACC_FINAL |         // 13.4.9 #2
                                          Opcodes.ACC_TRANSIENT;      // 13.4.11 #1
            final int accessPermissions = Opcodes.ACC_PUBLIC |
                                          Opcodes.ACC_PROTECTED |
                                          Opcodes.ACC_PRIVATE;
            final int oldAccess2 = oldAccess & ~compatibleChanges & ~accessPermissions;
            final int newAccess2 = newAccess & ~compatibleChanges & ~accessPermissions;
            return oldAccess2 != newAccess2;
        }
    }

    /**
     * Returns whether a method's newAccess is incompatible with oldAccess
     * following <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html">Java Language Specification, Java SE 7 Edition</a>:
     * <ul>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.9">13.4.7 Access to Members and Constructors</a><ul>
     *     <li>Changing the declared access of a member or constructor to permit less access
     *        <b>may break compatibility</b> with pre-existing binaries, causing a linkage error to be thrown when these binaries are resolved.
     *     </li>
     *     <li>The binary format is defined so that changing a member or constructor to be more accessible does not cause a
     *         linkage error when a subclass (already) defines a method to have less access.
     *     </li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.16">13.4.16 abstract Methods</a><ul>
     *     <li>Changing a method that is declared abstract to no longer be declared abstract
     *         <b>does not break compatibility</b> with pre-existing binaries.</li>
     *     <li>Changing a method that is not declared abstract to be declared abstract
     *         <b>will break compatibility</b> with pre-existing binaries that previously invoked the method, causing an AbstractMethodError.</li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.17">13.4.17 final</a><ul>
     *     <li>Changing a method that is declared final to no longer be declared final
     *         <b>does not break compatibility</b> with pre-existing binaries.</li>
     *     <li>Changing an instance method that is not declared final to be declared final
     *         <b>may break compatibility</b> with existing binaries that depend on the ability to override the method.</li>
     *     <li>Changing a class (static) method that is not declared final to be declared final
     *         <b>does not break compatibility</b> with existing binaries, because the method could not have been overridden.</li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.18">13.4.18 native Methods</a><ul>
     *     <li>Adding or deleting a native modifier of a method
     *         <b>does not break compatibility</b> with pre-existing binaries.</li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.19">13.4.19 static Methods</a><ul>
     *     <li>If a method that is not declared private is also declared static (that is, a class method)
     *         and is changed to not be declared static (that is, to an instance method), or vice versa,
     *         then <i>compatibility with pre-existing binaries may be broken</i>, resulting in a linkage time error,
     *         namely an IncompatibleClassChangeError, if these methods are used by the pre-existing binaries.
     *         Such changes <b>break functional backward compatibility</b>!</li>
     *     </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.20">13.4.20 synchronized Methods</a><ul>
     *     <li>Adding or deleting a synchronized modifier of a method
     *         <b>does not break compatibility</b> with pre-existing binaries.</li>
     *   </ul></li>
     *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.4.21">13.4.21 Method and Constructor Throws</a><ul>
     *     <li>Changes to the throws clause of methods or constructors
     *         <b>do not break compatibility</b> with pre-existing binaries; these clauses are checked only at compile time.</li>
     *   </ul></li>
     * </ul>
     *
     * @param oldAccess
     * @param newAccess
     * @return
     */
    public static boolean isMethodAccessChange(final int oldAccess, final int newAccess) {
        if (isLessAccessPermitted(oldAccess, newAccess)) {
            return true; // 13.4.7
        }
        if ( not(oldAccess, Opcodes.ACC_ABSTRACT) && has(newAccess, Opcodes.ACC_ABSTRACT) ) {
            return true; // 13.4.16 #2
        } else  if ( not(oldAccess, Opcodes.ACC_FINAL) && not(oldAccess, Opcodes.ACC_STATIC) &&
                     has(newAccess, Opcodes.ACC_FINAL) ) {
			return true; // 13.4.17 #2 excluding and #3
		} else {
		    final int compatibleChanges = Opcodes.ACC_ABSTRACT |     // 13.4.16 #1
		                                  Opcodes.ACC_FINAL |        // 13.4.17 #1
		                                  Opcodes.ACC_NATIVE |       // 13.4.18 #1
		                                  Opcodes.ACC_SYNCHRONIZED;  // 13.4.20 #1
            final int accessPermissions = Opcodes.ACC_PUBLIC |
                                          Opcodes.ACC_PROTECTED |
                                          Opcodes.ACC_PRIVATE;
			final int oldAccess2 = oldAccess & ~compatibleChanges & ~accessPermissions;
			final int newAccess2 = newAccess & ~compatibleChanges & ~accessPermissions;
			return oldAccess2 != newAccess2;
		}
	}
}
