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
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * A reusable class which uses the ASM to build up ClassInfo about a 
 * java class file.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class ClassInfoVisitor extends EmptyVisitor
{
    /**
     * The class file version.
     */
    private int version;

    /**
     * The access flags for the class.
     */

    private int access;

    /**
     * The internal name of the class.
     */
    private String name;

    /**
     * The signature of the class
     */
    private String signature;
    
    /**
     * The internal name of the superclass.
     */
    private String supername;

    /**
     * An array of internal names of interfaces implemented by this class.
     */
    private String[] interfaces;

    /**
     * A map of method signature to a MethodInfo describing the method.
     */
    private Map<String, MethodInfo> methodMap;

    /**
     * A map of field signature to a FieldInfo describing the field.
     */
    private Map<String, FieldInfo> fieldMap;
    
    /**
     * Reset this ClassInfoVisitor so that it can be used to visit another
     * class.
     */
    public void reset() {
        methodMap = new HashMap<String, MethodInfo>();
        fieldMap = new HashMap<String, FieldInfo>();
    }
    
    /**
     * The the classInfo this ClassInfoVisitor has built up about a class
     */
    public ClassInfo getClassInfo() {
        return new ClassInfo(version, access, name, signature, supername,
                             interfaces, methodMap, fieldMap);
    }
    
    /**
     * Receive notification of information about a class from ASM.
     *
     * @param version the class file version number.
     * @param access the access flags for the class.
     * @param name the internal name of the class.
     * @param signature the signature of the class.
     * @param supername the internal name of the super class.
     * @param interfaces the internal names of interfaces implemented.
     */
    public void visit(int version, int access, String name, String signature,
                      String supername, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.supername = supername;
        this.interfaces = interfaces;
    }
    
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        methodMap.put(name + desc, new MethodInfo(access, name, desc,
                                                  signature, exceptions));
        return null;
    }
    
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        fieldMap.put(name,
                     new FieldInfo(access, name, desc, signature, value));
        return this;
    }
}
