/*
 * org.osjava.jardiff.DiffHandler
 *
 * $Id: IOThread.java 1952 2005-08-28 18:03:41Z cybertiger $
 * $URL: https://svn.osjava.org/svn/osjava/trunk/osjava-nio/src/java/org/osjava/nio/IOThread.java $
 * $Rev: 1952 $
 * $Date: 2005-08-28 18:03:41 +0000 (Sun, 28 Aug 2005) $
 * $Author: cybertiger $
 *
 * Copyright (c) 2005, Antony Riley
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name JarDiff nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
