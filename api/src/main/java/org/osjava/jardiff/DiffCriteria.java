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
