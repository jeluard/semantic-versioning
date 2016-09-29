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
    private final int version;

    /**
     * The class signature.
     */
    private final String signature;

    /**
     * The formal type parameters
     */
    private final String formalTypeParams;

    /**
     * The internal classname of the superclass.
     */
    private final String supername;

    private final String superClassSignature;

    /**
     * A map of names of internal classnames of interfaces implemented
     * by the class.
     * Keyed by interface name, value is the type signature for the interface.
     */
    private final Map<String, String> interfaces;

    /**
     * A map of method signature to MethodInfo, for the methods provided
     * by this class.
     */
    private final Map<String, MethodInfo> methodMap;

    /**
     * A map of field signature to FieldInfo, for the fields provided by
     * this class.
     */
    private final Map<String, FieldInfo> fieldMap;

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
    public ClassInfo(int version, int access, String name, String signature, String formalTypeParams,
                     String supername, String superSignature, Map<String, String> interfaces,
                     Map<String, MethodInfo> methodMap, Map<String, FieldInfo> fieldMap) {
        super(access, name);
        this.version = version;
        this.signature = signature;
        this.formalTypeParams = formalTypeParams;
        this.supername = supername;
        this.superClassSignature = superSignature;
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

    @Override
    public final String getDesc() {
        return null;
    }

    @Override
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
        return interfaces.keySet().toArray(new String[0]);
    }

    /**
     * Get the map of method signatures to methods.
     *
     * @return a map with method signatures as keys, and MethodInfos as values.
     */
    public final Map<String, MethodInfo> getMethodMap() {
        return methodMap;
    }

    /**
     * Get the map of field signatures to fields.
     *
     * @return a map with field signatures as keys, and FieldInfos as values.
     */
    public final Map<String, FieldInfo> getFieldMap() {
        return fieldMap;
    }

    /**
     * Get the full signature of the super class, including any type parameters.
     */
    public String getSuperClassSignature() {
        return superClassSignature;
    }

    /**
     * Get the formal type parameters declared for this class, if they exist.
     */
    public String getFormalTypeParams() {
        return formalTypeParams;
    }

    /**
     * Get the map of interfaces and their corresponding type signatures.
     */
    public final Map<String, String> getInterfaceSignatures() {
        return interfaces;
    }
}
