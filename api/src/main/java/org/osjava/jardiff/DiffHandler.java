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

/**
 * An interface for classes which wish to receive information about
 * differences in class files between two different jar file version to
 * implement.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public interface DiffHandler
{
    /**
     * Start a diff between two versions, where string a is the old version
     * and string b is the new version.
     * 
     * @param a the name of the old version
     * @param b the name of the new version
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startDiff(String a, String b)
        throws DiffException;

    /**
     * Start the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startOldContents() throws DiffException;

    /**
     * Start the list of new contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startNewContents() throws DiffException;

    /**
     * Add a contained class.
     *
     * @param info information about a class
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void contains(ClassInfo info) throws DiffException;

    /**
     * End the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endOldContents() throws DiffException;

    /**
     * End the list of new contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endNewContents() throws DiffException;

    /**
     * Start the list of removed classes.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startRemoved() throws DiffException;
    
    /**
     * Notification that a class was removed.
     *
     * @param classinfo information about the class that has been removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classRemoved(ClassInfo classinfo) throws DiffException;
    
    /**
     * End of list of removed classes.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endRemoved() throws DiffException;
    
    /**
     * Start of list of added classes.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startAdded() throws DiffException;
    
    /**
     * Notification that a class was added.
     *
     * @param classinfo information about the class that has been removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classAdded(ClassInfo classinfo) throws DiffException;
    
    /**
     * End of list of removed classes.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endAdded() throws DiffException;
    
    /**
     * Start list of changed classes.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startChanged() throws DiffException;
    
    /**
     * Start information about class changes for the classname passed.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startClassChanged(String string) throws DiffException;
    
    /**
     * The field was removed for the current class that has changed.
     *
     * @param fieldinfo Information about the field removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldRemoved(FieldInfo fieldinfo) throws DiffException;
    
    /**
     * The method was removed for the current class that has changed.
     *
     * @param methodinfo Information about the method removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodRemoved(MethodInfo methodinfo) throws DiffException;
    
    /**
     * The field was added for the current class that has changed.
     *
     * @param fieldinfo Information about the field added.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldAdded(FieldInfo fieldinfo) throws DiffException;
    
    /**
     * The method was added for the current class that has changed.
     *
     * @param methodinfo Information about the method added.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodAdded(MethodInfo methodinfo) throws DiffException;
    
    /**
     * The current class has changed.
     * This is called when a class's interfaces or superclass or access
     * flags have changed.
     *
     * @param oldClassinfo Information about the old class.
     * @param newClassinfo Information about the new class.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classChanged(ClassInfo oldClassinfo, ClassInfo newClassinfo)
        throws DiffException;
    
    /**
     * The current class has been deprecated.
     *
     * @param oldClassinfo Information about the old class.
     * @param newClassinfo Information about the new class.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classDeprecated(ClassInfo oldClassinfo, ClassInfo newClassinfo)
	    throws DiffException;

    /**
     * A field on the current class has changed.
     *
     * @param oldFieldinfo Information about the old field.
     * @param newFieldinfo Information about the new field.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldChanged(FieldInfo oldFieldinfo, FieldInfo newFieldinfo)
        throws DiffException;
    
    /**
     * A field on the current class has been deprecated.
     *
     * @param oldFieldinfo Information about the old field.
     * @param newFieldinfo Information about the new field.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldDeprecated(FieldInfo oldFieldinfo, FieldInfo newFieldinfo)
	    throws DiffException;

    /**
     * A method on the current class has changed.
     *
     * @param oldMethodInfo Information about the old method.
     * @param newMethodInfo Information about the new method.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodChanged
        (MethodInfo oldMethodInfo, MethodInfo newMethodInfo) throws DiffException;

    /**
     * The method has been deprecated.
     *
     * @param oldMethodInfo Information about the old method.
     * @param newMethodInfo Information about the new method.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodDeprecated(MethodInfo oldMethodInfo,
	    MethodInfo newMethodInfo) throws DiffException;

    /**
     * End of changes for the current class.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endClassChanged() throws DiffException;
    
    /**
     * End of class changes.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endChanged() throws DiffException;
    
    /**
     * End of the diff.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endDiff() throws DiffException;
}
