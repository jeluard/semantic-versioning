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

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

/*
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
*/

/**
 * A class to perform a diff between two jar files.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class JarDiff
{
    /**
     * A map containing information about classes which are dependencies.
     * Keys are internal class names.
     * Values are instances of ClassInfo.
     */
    protected Map depClassInfo = new HashMap();

    /**
     * A map containing information about classes in the old jar file.
     * Keys are internal class names.
     * Values are instances of ClassInfo.
     */
    protected Map<String, ClassInfo> oldClassInfo = new TreeMap<String, ClassInfo>();

    /**
     * A map containing information about classes in the new jar file.
     * Keys are internal class names.
     * Values are instances of ClassInfo.
     */
    protected Map<String, ClassInfo> newClassInfo = new TreeMap<String, ClassInfo>();

    /**
     * An array of dependencies which are jar files, or urls.
     */
    private URL[] deps;

    /**
     * A class loader used for loading dependency classes.
     */
    private URLClassLoader depLoader;

    /**
     * The name of the old version.
     */
    private String oldVersion;

    /**
     * The name of the new version.
     */
    private String newVersion;

    /**
     * Class info visitor, used to load information about classes.
     */
    private ClassInfoVisitor infoVisitor = new ClassInfoVisitor();

    /**
     * Create a new JarDiff object.
     */
    public JarDiff() {
    }

    /**
     * Set the name of the old version.
     *
     * @param oldVersion the name
     */
    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    /**
     * Get the name of the old version.
     *
     * @return the name
     */
    public String getOldVersion() {
        return oldVersion;
    }

    /**
     * Set the name of the new version.
     *
     * @param newVersion the version
     */
    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    /**
     * Get the name of the new version.
     *
     * @return the name
     */
    public String getNewVersion() {
        return newVersion;
    }

    /**
     * Set the dependencies.
     *
     * @param deps an array of urls pointing to jar files or directories
     *             containing classes which are required dependencies.
     */
    public void setDependencies(URL[] deps) {
        this.deps = deps;
    }

    /**
     * Get the dependencies.
     *
     * @return the dependencies as an array of URLs
     */
    public URL[] getDependencies() {
        return deps;
    }

    /**
     * Load classinfo given a ClassReader.
     *
     * @param reader the ClassReader
     * @return the ClassInfo
     */
    public synchronized ClassInfo loadClassInfo(ClassReader reader)
        throws IOException
    {
        infoVisitor.reset();
        reader.accept(infoVisitor, 0);
        return infoVisitor.getClassInfo();
    }

    /**
     * Load all the classes from the specified URL and store information
     * about them in the specified map.
     * This currently only works for jar files, <b>not</b> directories
     * which contain classes in subdirectories or in the current directory.
     *
     * @param infoMap the map to store the ClassInfo in.
     * @throws DiffException if there is an exception reading info about a
     *                       class.
     */
    private void loadClasses(Map infoMap, URL path) throws DiffException {
        try {
            File jarFile = null;
            if(!"file".equals(path.getProtocol()) || path.getHost() != null) {
                // If it's not a local file, store it as a temporary jar file.
                // java.util.jar.JarFile requires a local file handle.
                jarFile = File.createTempFile("jardiff","jar");
                // Mark it to be deleted on exit.
                jarFile.deleteOnExit();
                InputStream in = path.openStream();
                OutputStream out = new FileOutputStream(jarFile);
                byte[] buffer = new byte[4096];
                int i;
                while( (i = in.read(buffer,0,buffer.length)) != -1) {
                    out.write(buffer, 0, i);
                }
                in.close();
                out.close();
            } else {
                // Else it's a local file, nothing special to do.
                jarFile = new File(path.getPath());
            }
            loadClasses(infoMap, jarFile);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * Load all the classes from the specified URL and store information
     * about them in the specified map.
     * This currently only works for jar files, <b>not</b> directories
     * which contain classes in subdirectories or in the current directory.
     *
     * @param infoMap the map to store the ClassInfo in.
     * @param file the jarfile to load classes from.
     * @throws IOException if there is an IOException reading info about a
     *                     class.
     */
    private void loadClasses(Map infoMap, File file) throws DiffException {
        try {
            JarFile jar = new JarFile(file);
            Enumeration e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = (JarEntry) e.nextElement();
                String name = entry.getName();
                if (!entry.isDirectory() && name.endsWith(".class")) {
                    ClassReader reader
                        = new ClassReader(jar.getInputStream(entry));
                    ClassInfo ci = loadClassInfo(reader);
                    infoMap.put(ci.getName(), ci);
                }
            }
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * Load old classes from the specified URL.
     *
     * @param loc The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException.
     */
    public void loadOldClasses(URL loc) throws DiffException {
        loadClasses(oldClassInfo, loc);
    }

    /**
     * Load new classes from the specified URL.
     *
     * @param loc The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException.
     */
    public void loadNewClasses(URL loc) throws DiffException {
        loadClasses(newClassInfo, loc);
    }

    /**
     * Load old classes from the specified File.
     *
     * @param file The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException
     */
    public void loadOldClasses(File file) throws DiffException {
        loadClasses(oldClassInfo, file);
    }

    /**
     * Load new classes from the specified File.
     *
     * @param file The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException
     */
    public void loadNewClasses(File file) throws DiffException {
        loadClasses(newClassInfo, file);
    }

    /**
     * Perform a diff sending the output to the specified handler, using
     * the specified criteria to select diffs.
     *
     * @param handler The handler to receive and handle differences.
     * @param criteria The criteria we use to select differences.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void diff(DiffHandler handler, DiffCriteria criteria)
        throws DiffException
    {
        diff(handler, criteria, oldVersion, newVersion, oldClassInfo, newClassInfo);
    }

    public void diff(DiffHandler handler, DiffCriteria criteria,
        String oldVersion, String newVersion,
        Map<String, ClassInfo> oldClassInfo, Map<String, ClassInfo> newClassInfo) throws DiffException
    {
        // TODO: Build the name from the MANIFEST rather than the filename
        handler.startDiff(oldVersion, newVersion);

        handler.startOldContents();
        for (ClassInfo ci : oldClassInfo.values()) {
            if (criteria.validClass(ci)) {
                handler.contains(ci);
            }
        }
        handler.endOldContents();

        handler.startNewContents();
        for (ClassInfo ci : newClassInfo.values()) {
            if (criteria.validClass(ci)) {
                handler.contains(ci);
            }
        }
        handler.endNewContents();

        Set<String> onlyOld = new TreeSet<String>(oldClassInfo.keySet());
        Set<String> onlyNew = new TreeSet<String>(newClassInfo.keySet());
        Set<String> both = new TreeSet<String>(oldClassInfo.keySet());
        onlyOld.removeAll(newClassInfo.keySet());
        onlyNew.removeAll(oldClassInfo.keySet());
        both.retainAll(newClassInfo.keySet());

        handler.startRemoved();
        for (String s : onlyOld) {
            ClassInfo ci = oldClassInfo.get(s);
            if (criteria.validClass(ci)) {
                handler.classRemoved(ci);
            }
        }
        handler.endRemoved();

        handler.startAdded();
        for (String s : onlyNew) {
            ClassInfo ci = newClassInfo.get(s);
            if (criteria.validClass(ci)) {
                handler.classAdded(ci);
            }
        }
        handler.endAdded();

        Set<String> removedMethods = new TreeSet<String>();
        Set<String> removedFields = new TreeSet<String>();
        Set<String> addedMethods = new TreeSet<String>();
        Set<String> addedFields = new TreeSet<String>();
        Set<String> changedMethods = new TreeSet<String>();
        Set<String> changedFields = new TreeSet<String>();

        handler.startChanged();
        for (String s : both) {
            ClassInfo oci = oldClassInfo.get(s);
            ClassInfo nci = newClassInfo.get(s);
            if (criteria.validClass(oci) || criteria.validClass(nci)) {
                Map<String, MethodInfo> oldMethods = oci.getMethodMap();
                Map<String, FieldInfo> oldFields = oci.getFieldMap();
                Map<String, MethodInfo> newMethods = nci.getMethodMap();
                Map<String, FieldInfo> newFields = nci.getFieldMap();

                Map<String, MethodInfo> extNewMethods = new HashMap<String, MethodInfo>(newMethods);
                Map<String, FieldInfo> extNewFields = new HashMap<String, FieldInfo>(newFields);

                String superClass = nci.getSupername();
                while (superClass != null && newClassInfo.containsKey(superClass)) {
                    ClassInfo sci = newClassInfo.get(superClass);
                    for (Map.Entry<String, FieldInfo> entry : sci.getFieldMap().entrySet()) {
                        if (!(entry.getValue()).isPrivate()
                                && !extNewFields.containsKey(entry.getKey())) {
                            extNewFields.put(entry.getKey(), entry.getValue());
                        }
                    }
                    for (Map.Entry<String, MethodInfo> entry : sci.getMethodMap().entrySet()) {
                        if (!(entry.getValue()).isPrivate()
                                && !extNewMethods.containsKey(entry.getKey())) {
                            extNewMethods.put(entry.getKey(), entry.getValue());
                        }
                    }
                    superClass = sci.getSupername();
                }

                for (Map.Entry<String, MethodInfo> entry : oldMethods.entrySet()) {
                    if (criteria.validMethod(entry.getValue()))
                        removedMethods.add(entry.getKey());
                }
                for (Map.Entry<String, FieldInfo> entry : oldFields.entrySet()) {
                    if (criteria.validField(entry.getValue()))
                        removedFields.add(entry.getKey());
                }

                for (Map.Entry<String, MethodInfo> entry : newMethods.entrySet()) {
                    if (criteria.validMethod(entry.getValue()))
                        addedMethods.add(entry.getKey());
                }
                for (Map.Entry<String, FieldInfo> entry : newFields.entrySet()) {
                    if (criteria.validField(entry.getValue()))
                        addedFields.add(entry.getKey());
                }

                changedMethods.addAll(removedMethods);
                changedMethods.retainAll(addedMethods);
                removedMethods.removeAll(changedMethods);
                removedMethods.removeAll(extNewMethods.keySet());
                addedMethods.removeAll(changedMethods);
                changedFields.addAll(removedFields);
                changedFields.retainAll(addedFields);
                removedFields.removeAll(changedFields);
                removedFields.removeAll(extNewFields.keySet());
                addedFields.removeAll(changedFields);

                Iterator<String> j = changedMethods.iterator();
                while (j.hasNext()) {
                    String desc = j.next();
                    MethodInfo oldInfo = oldMethods.get(desc);
                    MethodInfo newInfo = newMethods.get(desc);
                    if (!criteria.differs(oldInfo, newInfo))
                        j.remove();
                }
                j = changedFields.iterator();
                while (j.hasNext()) {
                    String desc = j.next();
                    FieldInfo oldInfo = oldFields.get(desc);
                    FieldInfo newInfo = newFields.get(desc);
                    if (!criteria.differs(oldInfo, newInfo))
                        j.remove();
                }

                boolean classchanged = criteria.differs(oci, nci);
                if (classchanged || !removedMethods.isEmpty()
                        || !removedFields.isEmpty() || !addedMethods.isEmpty()
                        || !addedFields.isEmpty() || !changedMethods.isEmpty()
                        || !changedFields.isEmpty()) {
                    handler.startClassChanged(s);

                    handler.startRemoved();
                    for (String field : removedFields) {
                        handler.fieldRemoved(oldFields.get(field));
                    }
                    for (String method : removedMethods) {
                        handler.methodRemoved(oldMethods.get(method));
                    }
                    handler.endRemoved();

                    handler.startAdded();
                    for (String field : addedFields) {
                        handler.fieldAdded(newFields.get(field));
                    }
                    for (String method : addedMethods) {
                        handler.methodAdded(newMethods.get(method));
                    }
                    handler.endAdded();

                    handler.startChanged();
                    if (classchanged) {
			            // Was only deprecated?
			            if (wasDeprecated(oci, nci)
				            && !criteria.differs(cloneDeprecated(oci), nci))
			                handler.classDeprecated(oci, nci);
			            else
			                handler.classChanged(oci, nci);
                    }

                    for (String field : changedFields) {
                        FieldInfo oldFieldInfo = oldFields.get(field);
                        FieldInfo newFieldInfo = newFields.get(field);
                        // Was only deprecated?
                        if (wasDeprecated(oldFieldInfo, newFieldInfo)
                            && !criteria.differs(
                                cloneDeprecated(oldFieldInfo),
                                newFieldInfo))
                            handler.fieldDeprecated(oldFieldInfo, newFieldInfo);
                        else
                            handler.fieldChanged(oldFieldInfo, newFieldInfo);
                    }
                    for (String method : changedMethods) {
                        MethodInfo oldMethodInfo = oldMethods.get(method);
                        MethodInfo newMethodInfo = newMethods.get(method);
                        // Was only deprecated?
                        if (wasDeprecated(oldMethodInfo, newMethodInfo)
                            && !criteria.differs(
                                cloneDeprecated(oldMethodInfo),
                                newMethodInfo))
                            handler.methodDeprecated(oldMethodInfo,
                                newMethodInfo);
                        else
                            handler.methodChanged(oldMethodInfo, newMethodInfo);
                    }
                    handler.endChanged();
                    handler.endClassChanged();

                    removedMethods.clear();
                    removedFields.clear();
                    addedMethods.clear();
                    addedFields.clear();
                    changedMethods.clear();
                    changedFields.clear();
                }
            }
        }
        handler.endChanged();

        handler.endDiff();
    }

    /**
     * Determines if an {@link AbstractInfo} was deprecated. (Shortcut to avoid
     * creating cloned deprecated infos).
     */
    private static boolean wasDeprecated(AbstractInfo oldInfo,
	    AbstractInfo newInfo) {
	return !oldInfo.isDeprecated() && newInfo.isDeprecated();
    }

    /**
     * Clones the class info, but changes access, setting deprecated flag.
     * 
     * @param classInfo
     *            the original class info
     * @return the cloned and deprecated info.
     */
    private static ClassInfo cloneDeprecated(ClassInfo classInfo) {
	return new ClassInfo(classInfo.getVersion(), classInfo.getAccess()
		| Opcodes.ACC_DEPRECATED, classInfo.getName(),
		classInfo.getSignature(), classInfo.getSupername(),
		classInfo.getInterfaces(), classInfo.getMethodMap(),
		classInfo.getFieldMap());
    }

    /**
     * Clones the method, but changes access, setting deprecated flag.
     * 
     * @param methodInfo
     *            the original method info
     * @return the cloned and deprecated method info.
     */
    private static MethodInfo cloneDeprecated(MethodInfo methodInfo) {
	return new MethodInfo(methodInfo.getAccess() | Opcodes.ACC_DEPRECATED,
		methodInfo.getName(), methodInfo.getDesc(),
		methodInfo.getSignature(), methodInfo.getExceptions());
    }

    /**
     * Clones the field info, but changes access, setting deprecated flag.
     * 
     * @param fieldInfo
     *            the original field info
     * @return the cloned and deprecated field info.
     */
    private static FieldInfo cloneDeprecated(FieldInfo fieldInfo) {
	return new FieldInfo(fieldInfo.getAccess() | Opcodes.ACC_DEPRECATED,
		fieldInfo.getName(), fieldInfo.getDesc(),
		fieldInfo.getSignature(), fieldInfo.getValue());
    }
}
