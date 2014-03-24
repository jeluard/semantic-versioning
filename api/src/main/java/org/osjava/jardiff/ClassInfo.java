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
import java.util.Map;

/**
 * Information about a class file.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public final class ClassInfo extends AbstractInfo
{
    /**
     * The classfile version number.
     */
    private int version;

    /**
     * The class signature.
     */
    private String signature;

    /**
     * The internal classname of the superclass.
     */
    private String supername;

    /**
     * An array of names of internal classnames of interfaces implmented 
     * by the class.
     */
    private String[] interfaces;

    /**
     * A map of method signature to MethodInfo, for the methods provided 
     * by this class.
     */
    private Map methodMap;

    /**
     * A map of field signature to FieldInfo, for the fields provided by 
     * this class.
     */
    private Map fieldMap;
    
    /**
     * Create a new classinfo.
     *
     * @param version the class file version number.
     * @param access the access flags for the class.
     * @param name the internal name of the class.
     * @param signature the signature of the class.
     * @param interfaces an array of internal names of interfaces implemented
     *                   by the class.
     * @param methodMap a map of methods provided by this class.
     * @param fieldMap a map of fields provided by this class.
     */
    public ClassInfo(int version, int access, String name, String signature,
                     String supername, String[] interfaces, Map methodMap,
                     Map fieldMap) {
        super(access, name);
        this.version = version;
        this.signature = signature;
        this.supername = supername;
        this.interfaces = interfaces;
        this.methodMap = methodMap;
        this.fieldMap = fieldMap;
    }
    
    /**
     * Get the class file version.
     *
     * @return The class file version as specified in the java language spec.
     */
    public final int getVersion() {
        return version;
    }
    
    /**
     * Get the class signature.
     *
     * @return the class signature
     */
    public final String getSignature() {
        return signature;
    }
    
    /**
     * Get the internal name of the superclass.
     *
     * @return the internal name of the superclass
     */
    public final String getSupername() {
        return supername;
    }
    
    /**
     * Get the internal names of the interfaces implemented by this class
     *
     * @return an array of internal names of classes implemented by the class.
     */
    public final String[] getInterfaces() {
        return interfaces;
    }
    
    /**
     * Get the map of method signatures to methods.
     *
     * @return a map with method signatures as keys, and MethodInfos as values.
     */
    public final Map getMethodMap() {
        return methodMap;
    }
    
    /**
     * Get the map of field signatures to fields.
     *
     * @return a map with field signatures as keys, and FieldInfos as values.
     */
    public final Map getFieldMap() {
        return fieldMap;
    }
}
