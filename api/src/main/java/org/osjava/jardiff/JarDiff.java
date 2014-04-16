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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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



import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

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
    protected Map oldClassInfo = new TreeMap();

    /**
     * A map containing information about classes in the new jar file.
     * Keys are internal class names.
     * Values are instances of ClassInfo.
     */
    protected Map newClassInfo = new TreeMap();

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
    private synchronized ClassInfo loadClassInfo(ClassReader reader)
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

    private void diff(DiffHandler handler, DiffCriteria criteria,
        String oldVersion, String newVersion,
        Map oldClassInfo, Map newClassInfo) throws DiffException
    {
        // TODO: Build the name from the MANIFEST rather than the filename
        handler.startDiff(oldVersion, newVersion);
        Iterator i;

        handler.startOldContents();
        i = oldClassInfo.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            ClassInfo ci = (ClassInfo) entry.getValue();
            if(criteria.validClass(ci)) {
                handler.contains(ci);
            }
        }
        handler.endOldContents();

        handler.startNewContents();
        i = newClassInfo.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            ClassInfo ci = (ClassInfo) entry.getValue();
            if(criteria.validClass(ci)) {
                handler.contains(ci);
            }
        }
        handler.endNewContents();

        java.util.Set onlyOld = new TreeSet(oldClassInfo.keySet());
        java.util.Set onlyNew = new TreeSet(newClassInfo.keySet());
        java.util.Set both = new TreeSet(oldClassInfo.keySet());
        onlyOld.removeAll(newClassInfo.keySet());
        onlyNew.removeAll(oldClassInfo.keySet());
        both.retainAll(newClassInfo.keySet());
        handler.startRemoved();
        i = onlyOld.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) oldClassInfo.get(s);
            if (criteria.validClass(ci))
                handler.classRemoved(ci);
        }
        handler.endRemoved();
        handler.startAdded();
        i = onlyNew.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) newClassInfo.get(s);
            if (criteria.validClass(ci))
                handler.classAdded(ci);
        }
        handler.endAdded();
        java.util.Set removedMethods = new TreeSet();
        java.util.Set removedFields = new TreeSet();
        java.util.Set addedMethods = new TreeSet();
        java.util.Set addedFields = new TreeSet();
        java.util.Set changedMethods = new TreeSet();
        java.util.Set changedFields = new TreeSet();
        handler.startChanged();
        i = both.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo oci = (ClassInfo) oldClassInfo.get(s);
            ClassInfo nci = (ClassInfo) newClassInfo.get(s);
            if (criteria.validClass(oci) || criteria.validClass(nci)) {
                Map oldMethods = oci.getMethodMap();
                Map oldFields = oci.getFieldMap();
                Map extOldMethods = new HashMap(oldMethods);
                Map extOldFields = new HashMap(oldFields);

                String superClass = oci.getSupername();
                while (superClass != null && oldClassInfo.containsKey(superClass)) {
                    ClassInfo sci = (ClassInfo) oldClassInfo.get(superClass);
                    Iterator j = sci.getFieldMap().entrySet().iterator();
                    while (j.hasNext()) {
                        Map.Entry entry = (Map.Entry) j.next();
                        if (!((FieldInfo)entry.getValue()).isPrivate()
                            && !extOldFields.containsKey(entry.getKey())) {
                            extOldFields.put(entry.getKey(), entry.getValue());
                        }
                    }
                    j = sci.getMethodMap().entrySet().iterator();
                    while (j.hasNext()) {
                        Map.Entry entry = (Map.Entry) j.next();
                        if (!((MethodInfo)entry.getValue()).isPrivate()
                            && !extOldMethods.containsKey(entry.getKey())) {
                            extOldMethods.put(entry.getKey(), entry.getValue());
                        }
                    }
                    superClass = sci.getSupername();
                }

                Map newMethods = nci.getMethodMap();
                Map newFields = nci.getFieldMap();
                Iterator j = oldMethods.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validMethod((MethodInfo) entry.getValue()))
                        removedMethods.add(entry.getKey());
                }
                j = oldFields.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validField((FieldInfo) entry.getValue()))
                        removedFields.add(entry.getKey());
                }
                j = newMethods.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validMethod((MethodInfo) entry.getValue()))
                        addedMethods.add(entry.getKey());
                }
                j = newFields.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validField((FieldInfo) entry.getValue()))
                        addedFields.add(entry.getKey());
                }

                changedMethods.addAll(removedMethods);
                changedMethods.retainAll(addedMethods);
                removedMethods.removeAll(changedMethods);
                removedMethods.removeAll(extOldMethods.keySet());
                addedMethods.removeAll(changedMethods);
                changedFields.addAll(removedFields);
                changedFields.retainAll(addedFields);
                removedFields.removeAll(changedFields);
                removedFields.removeAll(extOldFields.keySet());
                addedFields.removeAll(changedFields);
                j = changedMethods.iterator();
                while (j.hasNext()) {
                    String desc = (String) j.next();
                    MethodInfo oldInfo = (MethodInfo) oldMethods.get(desc);
                    MethodInfo newInfo = (MethodInfo) newMethods.get(desc);
                    if (!criteria.differs(oldInfo, newInfo))
                        j.remove();
                }
                j = changedFields.iterator();
                while (j.hasNext()) {
                    String desc = (String) j.next();
                    FieldInfo oldInfo = (FieldInfo) oldFields.get(desc);
                    FieldInfo newInfo = (FieldInfo) newFields.get(desc);
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
                    j = removedFields.iterator();
                    while (j.hasNext())
                        handler
                            .fieldRemoved((FieldInfo) oldFields.get(j.next()));
                    j = removedMethods.iterator();
                    while (j.hasNext())
                        handler.methodRemoved((MethodInfo)
                                oldMethods.get(j.next()));
                    handler.endRemoved();
                    handler.startAdded();
                    j = addedFields.iterator();
                    while (j.hasNext())
                        handler
                            .fieldAdded((FieldInfo) newFields.get(j.next()));
                    j = addedMethods.iterator();
                    while (j.hasNext())
                        handler.methodAdded((MethodInfo)
                                newMethods.get(j.next()));
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
                    j = changedFields.iterator();
		    while (j.hasNext()) {
			Object tmp = j.next();
			FieldInfo oldFieldInfo = (FieldInfo) oldFields.get(tmp);
			FieldInfo newFieldInfo = (FieldInfo) newFields.get(tmp);
			// Was only deprecated?
			if (wasDeprecated(oldFieldInfo, newFieldInfo)
				&& !criteria.differs(
					cloneDeprecated(oldFieldInfo),
					newFieldInfo))
			    handler.fieldDeprecated(oldFieldInfo, newFieldInfo);
			else
			    handler.fieldChanged(oldFieldInfo, newFieldInfo);
		    }
                    j = changedMethods.iterator();
                    while (j.hasNext()) {
			Object tmp = j.next();
			MethodInfo oldMethodInfo = (MethodInfo) oldMethods
				.get(tmp);
			MethodInfo newMethodInfo = (MethodInfo) newMethods
				.get(tmp);
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
