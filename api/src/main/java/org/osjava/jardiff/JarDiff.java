/*
 * org.osjava.jardiff.JarDiff
 * 
 * $Id: IOThread.java 1952 2005-08-28 18:03:41Z cybertiger $ $URL:
 * https://svn.osjava.org/svn/osjava/trunk/osjava-nio/src/java/org/osjava/nio/IOThread.java $ $Rev: 1952 $ $Date:
 * 2005-08-28 18:03:41 +0000 (Sun, 28 Aug 2005) $ $Author: cybertiger $
 * 
 * Copyright (c) 2005, Antony Riley All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * + Neither the name JarDiff nor the names of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
/*
 * import javax.xml.transform.ErrorListener; import javax.xml.transform.Transformer; import
 * javax.xml.transform.TransformerException; import javax.xml.transform.TransformerFactory; import
 * javax.xml.transform.sax.SAXTransformerFactory; import javax.xml.transform.sax.TransformerHandler; import
 * javax.xml.transform.stream.StreamResult; import javax.xml.transform.stream.StreamSource;
 */

import org.objectweb.asm.ClassReader;


/**
 * A class to perform a diff between two jar files.
 * 
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class JarDiff {
    /**
     * A map containing information about classes which are dependencies. Keys are internal class names. Values are
     * instances of ClassInfo.
     */
    protected Map            depClassInfo = new HashMap();

    /**
     * A map containing information about classes in the old jar file. Keys are internal class names. Values are
     * instances of ClassInfo.
     */
    protected Map            oldClassInfo = new TreeMap();

    /**
     * A map containing information about classes in the new jar file. Keys are internal class names. Values are
     * instances of ClassInfo.
     */
    protected Map            newClassInfo = new TreeMap();

    /**
     * An array of dependencies which are jar files, or urls.
     */
    private URL[]            deps;

    /**
     * A class loader used for loading dependency classes.
     */
    private URLClassLoader   depLoader;

    /**
     * The name of the old version.
     */
    private String           oldVersion;

    /**
     * The name of the new version.
     */
    private String           newVersion;

    /**
     * Class info visitor, used to load information about classes.
     */
    private ClassInfoVisitor infoVisitor  = new ClassInfoVisitor();

    /**
     * Create a new JarDiff object.
     */
    public JarDiff() {}

    /**
     * Set the name of the old version.
     * 
     * @param oldVersion the name
     */
    public void setOldVersion( final String oldVersion ) {
        this.oldVersion = oldVersion;
    }

    /**
     * Get the name of the old version.
     * 
     * @return the name
     */
    public String getOldVersion() {
        return this.oldVersion;
    }

    /**
     * Set the name of the new version.
     * 
     * @param newVersion
     */
    public void setNewVersion( final String newVersion ) {
        this.newVersion = newVersion;
    }

    /**
     * Get the name of the new version.
     * 
     * @return the name
     */
    public String getNewVersion() {
        return this.newVersion;
    }

    /**
     * Set the dependencies.
     * 
     * @param deps an array of urls pointing to jar files or directories containing classes which are required
     *            dependencies.
     */
    public void setDependencies( final URL[] deps ) {
        this.deps = deps;
    }

    /**
     * Get the dependencies.
     * 
     * @return the dependencies as an array of URLs
     */
    public URL[] getDependencies() {
        return this.deps;
    }

    /**
     * Load classinfo given a ClassReader.
     * 
     * @param reader the ClassReader
     * @return the ClassInfo
     */
    private synchronized ClassInfo loadClassInfo( final ClassReader reader ) throws IOException {
        this.infoVisitor.reset();
        reader.accept( this.infoVisitor, false );
        return this.infoVisitor.getClassInfo();
    }

    /**
     * Load all the classes from the specified URL and store information about them in the specified map. This currently
     * only works for jar files, <b>not</b> directories which contain classes in subdirectories or in the current
     * directory.
     * 
     * @param infoMap the map to store the ClassInfo in.
     * @throws DiffException if there is an exception reading info about a class.
     */
    private void loadClasses( final Map infoMap, final URL path ) throws DiffException {
        try {
            File jarFile = null;
            if ( !"file".equals( path.getProtocol() ) || ( path.getHost() != null ) ) {
                // If it's not a local file, store it as a temporary jar file.
                // java.util.jar.JarFile requires a local file handle.
                jarFile = File.createTempFile( "jardiff", "jar" );
                // Mark it to be deleted on exit.
                jarFile.deleteOnExit();
                InputStream in = path.openStream();
                OutputStream out = new FileOutputStream( jarFile );
                byte[] buffer = new byte[4096];
                int i;
                while ( ( i = in.read( buffer, 0, buffer.length ) ) != -1 ) {
                    out.write( buffer, 0, i );
                }
                in.close();
                out.close();
            } else {
                // Else it's a local file, nothing special to do.
                jarFile = new File( path.getPath() );
            }
            loadClasses( infoMap, jarFile );
        } catch ( IOException ioe ) {
            throw new DiffException( ioe );
        }
    }

    /**
     * Load all the classes from the specified URL and store information about them in the specified map. This currently
     * only works for jar files, <b>not</b> directories which contain classes in subdirectories or in the current
     * directory.
     * 
     * @param infoMap the map to store the ClassInfo in.
     * @param file the jarfile to load classes from.
     * @throws IOException if there is an IOException reading info about a class.
     */
    private void loadClasses( final Map infoMap, final File file ) throws DiffException {
        try {
            JarFile jar = new JarFile( file );
            Enumeration e = jar.entries();
            while ( e.hasMoreElements() ) {
                JarEntry entry = (JarEntry) e.nextElement();
                String name = entry.getName();
                if ( !entry.isDirectory() && name.endsWith( ".class" ) ) {
                    ClassReader reader = new ClassReader( jar.getInputStream( entry ) );
                    ClassInfo ci = loadClassInfo( reader );
                    infoMap.put( ci.getName(), ci );
                }
            }
        } catch ( IOException ioe ) {
            throw new DiffException( ioe );
        }
    }

    /**
     * Load old classes from the specified URL.
     * 
     * @param loc The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException.
     */
    public void loadOldClasses( final URL loc ) throws DiffException {
        loadClasses( this.oldClassInfo, loc );
    }

    /**
     * Load new classes from the specified URL.
     * 
     * @param loc The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException.
     */
    public void loadNewClasses( final URL loc ) throws DiffException {
        loadClasses( this.newClassInfo, loc );
    }

    /**
     * Load old classes from the specified File.
     * 
     * @param file The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException
     */
    public void loadOldClasses( final File file ) throws DiffException {
        loadClasses( this.oldClassInfo, file );
    }

    /**
     * Load new classes from the specified File.
     * 
     * @param file The location of a jar file to load classes from.
     * @throws DiffExeption if there is an IOException
     */
    public void loadNewClasses( final File file ) throws DiffException {
        loadClasses( this.newClassInfo, file );
    }

    /**
     * Perform a diff sending the output to the specified handler, using the specified criteria to select diffs.
     * 
     * @param handler The handler to receive and handle differences.
     * @param criteria The criteria we use to select differences.
     * @throws DiffException when there is an underlying exception, e.g. writing to a file caused an IOException
     */
    public void diff( final DiffHandler handler, final DiffCriteria criteria ) throws DiffException {
        // TODO: Build the name from the MANIFEST rather than the filename
        handler.startDiff( this.oldVersion, this.newVersion );
        Iterator i;

        handler.startOldContents();
        i = this.oldClassInfo.entrySet().iterator();
        while ( i.hasNext() ) {
            Map.Entry entry = (Map.Entry) i.next();
            ClassInfo ci = (ClassInfo) entry.getValue();
            if ( criteria.validClass( ci ) ) {
                handler.contains( ci );
            }
        }
        handler.endOldContents();

        handler.startNewContents();
        i = this.newClassInfo.entrySet().iterator();
        while ( i.hasNext() ) {
            Map.Entry entry = (Map.Entry) i.next();
            ClassInfo ci = (ClassInfo) entry.getValue();
            if ( criteria.validClass( ci ) ) {
                handler.contains( ci );
            }
        }
        handler.endNewContents();

        java.util.Set onlyOld = new TreeSet( this.oldClassInfo.keySet() );
        java.util.Set onlyNew = new TreeSet( this.newClassInfo.keySet() );
        java.util.Set both = new TreeSet( this.oldClassInfo.keySet() );
        onlyOld.removeAll( this.newClassInfo.keySet() );
        onlyNew.removeAll( this.oldClassInfo.keySet() );
        both.retainAll( this.newClassInfo.keySet() );
        handler.startRemoved();
        i = onlyOld.iterator();
        while ( i.hasNext() ) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) this.oldClassInfo.get( s );
            if ( criteria.validClass( ci ) ) {
                handler.classRemoved( ci );
            }
        }
        handler.endRemoved();
        handler.startAdded();
        i = onlyNew.iterator();
        while ( i.hasNext() ) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) this.newClassInfo.get( s );
            if ( criteria.validClass( ci ) ) {
                handler.classAdded( ci );
            }
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
        while ( i.hasNext() ) {
            String s = (String) i.next();
            ClassInfo oci = (ClassInfo) this.oldClassInfo.get( s );
            ClassInfo nci = (ClassInfo) this.newClassInfo.get( s );
            if ( criteria.validClass( oci ) || criteria.validClass( nci ) ) {
                Map oldMethods = oci.getMethodMap();
                Map oldFields = oci.getFieldMap();
                Map newMethods = nci.getMethodMap();
                Map newFields = nci.getFieldMap();
                Iterator j = oldMethods.entrySet().iterator();
                while ( j.hasNext() ) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if ( criteria.validMethod( (MethodInfo) entry.getValue() ) ) {
                        removedMethods.add( entry.getKey() );
                    }
                }
                j = oldFields.entrySet().iterator();
                while ( j.hasNext() ) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if ( criteria.validField( (FieldInfo) entry.getValue() ) ) {
                        removedFields.add( entry.getKey() );
                    }
                }
                j = newMethods.entrySet().iterator();
                while ( j.hasNext() ) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if ( criteria.validMethod( (MethodInfo) entry.getValue() ) ) {
                        addedMethods.add( entry.getKey() );
                    }
                }
                j = newFields.entrySet().iterator();
                while ( j.hasNext() ) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if ( criteria.validField( (FieldInfo) entry.getValue() ) ) {
                        addedFields.add( entry.getKey() );
                    }
                }
                changedMethods.addAll( removedMethods );
                changedMethods.retainAll( addedMethods );
                removedMethods.removeAll( changedMethods );
                addedMethods.removeAll( changedMethods );
                changedFields.addAll( removedFields );
                changedFields.retainAll( addedFields );
                removedFields.removeAll( changedFields );
                addedFields.removeAll( changedFields );
                j = changedMethods.iterator();
                while ( j.hasNext() ) {
                    String desc = (String) j.next();
                    MethodInfo oldInfo = (MethodInfo) oldMethods.get( desc );
                    MethodInfo newInfo = (MethodInfo) newMethods.get( desc );
                    if ( !criteria.differs( oldInfo, newInfo ) ) {
                        j.remove();
                    }
                }
                j = changedFields.iterator();
                while ( j.hasNext() ) {
                    String desc = (String) j.next();
                    FieldInfo oldInfo = (FieldInfo) oldFields.get( desc );
                    FieldInfo newInfo = (FieldInfo) newFields.get( desc );
                    if ( !criteria.differs( oldInfo, newInfo ) ) {
                        j.remove();
                    }
                }
                boolean classchanged = criteria.differs( oci, nci );
                if ( true || classchanged || !removedMethods.isEmpty() || !removedFields.isEmpty()
                        || !addedMethods.isEmpty() || !addedFields.isEmpty() || !changedMethods.isEmpty()
                        || !changedFields.isEmpty() ) {
                    handler.startClassChanged( s );
                    handler.startRemoved();
                    j = removedFields.iterator();
                    while ( j.hasNext() ) {
                        handler.fieldRemoved( (FieldInfo) oldFields.get( j.next() ) );
                    }
                    j = removedMethods.iterator();
                    while ( j.hasNext() ) {
                        handler.methodRemoved( (MethodInfo) oldMethods.get( j.next() ) );
                    }
                    handler.endRemoved();
                    handler.startAdded();
                    j = addedFields.iterator();
                    while ( j.hasNext() ) {
                        handler.fieldAdded( (FieldInfo) newFields.get( j.next() ) );
                    }
                    j = addedMethods.iterator();
                    while ( j.hasNext() ) {
                        handler.methodAdded( (MethodInfo) newMethods.get( j.next() ) );
                    }
                    handler.endAdded();
                    handler.startChanged();
                    if ( classchanged ) {
                        handler.classChanged( oci, nci );
                    }
                    j = changedFields.iterator();
                    while ( j.hasNext() ) {
                        Object tmp = j.next();
                        handler.fieldChanged( (FieldInfo) oldFields.get( tmp ), (FieldInfo) newFields.get( tmp ) );
                    }
                    j = changedMethods.iterator();
                    while ( j.hasNext() ) {
                        Object tmp = j.next();
                        handler.methodChanged( (MethodInfo) oldMethods.get( tmp ),
                                ( (MethodInfo) newMethods.get( tmp ) ) );
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
}
