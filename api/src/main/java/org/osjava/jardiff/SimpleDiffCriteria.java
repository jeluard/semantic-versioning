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
import java.util.Arrays;
import java.util.HashSet;

/**
 * A specific type of DiffCriteria which is only true for classes, methods
 * and fields which are not synthetic, and are public or protected.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class SimpleDiffCriteria implements DiffCriteria
{
    /**
     * Check if a class is valid.
     * If the class is not synthetic and is public or protected, return true.
     *
     * @param info Info describing the class.
     * @return True if the class meets the criteria, false otherwise.
     */
    public boolean validClass(ClassInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    /**
     * Check if a method is valid.
     * If the method is not synthetic and is public or protected, return true.
     *
     * @param info Info describing the method.
     * @return True if the method meets the criteria, false otherwise.
     */
    public boolean validMethod(MethodInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    /**
     * Check if a field is valid.
     * If the method is not synthetic and is public or protected, return true.
     *
     * @param info Info describing the field.
     * @return True if the field meets the criteria, false otherwise.
     */
    public boolean validField(FieldInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    /**
     * Check if there is a change between two versions of a class.
     * Returns true if the access flags differ, or if the superclass differs
     * or if the implemented interfaces differ.
     *
     * @param oldInfo Info about the old version of the class.
     * @param newInfo Info about the new version of the class.
     * @return True if the classes differ, false otherwise.
     */
    public boolean differs(ClassInfo oldInfo, ClassInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        // Yes classes can have a null supername, e.g. java.lang.Object !
        if(oldInfo.getSupername() == null) {
            if(newInfo.getSupername() != null) {
                return true;
            }
        } else if (!oldInfo.getSupername().equals(newInfo.getSupername())) {
            return true;
        }
        java.util.Set oldInterfaces
            = new HashSet(Arrays.asList(oldInfo.getInterfaces()));
        java.util.Set newInterfaces
            = new HashSet(Arrays.asList(newInfo.getInterfaces()));
        if (!oldInterfaces.equals(newInterfaces))
            return true;
        return false;
    }
    
    /**
     * Check if there is a change between two versions of a method.
     * Returns true if the access flags differ, or if the thrown
     * exceptions differ.
     *
     * @param oldInfo Info about the old version of the method.
     * @param newInfo Info about the new version of the method.
     * @return True if the methods differ, false otherwise.
     */
    public boolean differs(MethodInfo oldInfo, MethodInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (oldInfo.getExceptions() == null
            || newInfo.getExceptions() == null) {
            if (oldInfo.getExceptions() != newInfo.getExceptions())
                return true;
        } else {
            java.util.Set oldExceptions
                = new HashSet(Arrays.asList(oldInfo.getExceptions()));
            java.util.Set newExceptions
                = new HashSet(Arrays.asList(newInfo.getExceptions()));
            if (!oldExceptions.equals(newExceptions))
                return true;
        }
        return false;
    }
    
    /**
     * Check if there is a change between two versions of a field.
     * Returns true if the access flags differ, or if the inital value
     * of the field differs.
     *
     * @param oldInfo Info about the old version of the field.
     * @param newInfo Info about the new version of the field.
     * @return True if the fields differ, false otherwise.
     */
    public boolean differs(FieldInfo oldInfo, FieldInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (oldInfo.getValue() == null || newInfo.getValue() == null) {
            if (oldInfo.getValue() != newInfo.getValue())
                return true;
        } else if (!oldInfo.getValue().equals(newInfo.getValue()))
            return true;
        return false;
    }
}
