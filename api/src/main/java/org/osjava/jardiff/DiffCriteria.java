/*
 * org.osjava.jardiff.DiffCriteria
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
 * An interface for choosing which API differences are interesting.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public interface DiffCriteria
{
    /**
     * Check if the class described by classinfo is interesting.
     *
     * @return true if classinfo is interesting, false otherwise.
     */
    public boolean validClass(ClassInfo classinfo);
    
    /**
     * Check if the method described by methodinfo is interesting.
     *
     * @return true if methodinfo is interesting, false otherwise.
     */
    public boolean validMethod(MethodInfo methodinfo);
    
    /**
     * Check if the method described by fieldinfo is interesting.
     *
     * @return true if fieldinfo is interesting, false otherwise.
     */
    public boolean validField(FieldInfo fieldinfo);
    
    /**
     * Check if the differences between the class described by infoA and 
     * the class described by infoB are interesting.
     *
     * @return true if the changes are interesting, false otherwise.
     */
    public boolean differs(ClassInfo infoA, ClassInfo infoB);
    
    /**
     * Check if the differences between the method described by infoA and
     * the method described by infoB are interesting.
     *
     * @return true if the changes are interesting, false otherwise.
     */
    public boolean differs(MethodInfo methodinfo, MethodInfo methodinfo_1_);
    
    /**
     * Check if the differences between the field described by infoA and the
     * field described by infoB are interesting.
     *
     * @return true if the changes are interesting, false otherwise.
     */
    public boolean differs(FieldInfo fieldinfo, FieldInfo fieldinfo_2_);
}
