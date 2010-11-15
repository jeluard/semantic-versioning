/**
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2010 Julien Eluard
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     [http://www.apache.org/licenses/LICENSE-2.0]
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.semver.jardiff;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import org.osjava.jardiff.AbstractDiffHandler;
import org.osjava.jardiff.AbstractInfo;
import org.osjava.jardiff.ClassInfo;
import org.osjava.jardiff.DiffException;
import org.osjava.jardiff.FieldInfo;
import org.osjava.jardiff.MethodInfo;

/**
 *
 * {@link org.osjava.jardiff.DiffHandler} implementation accumulating changes.
 *
 */
public final class AccumulatingDiffHandler extends AbstractDiffHandler {

    public static class Difference implements Comparable<Difference> {
        
        private final String className;
        private final AbstractInfo info;
        
        public Difference(@Nonnull final String className, @Nonnull final AbstractInfo info) {
            this.className = className;
            this.info = info;
        }

        public String getClassName() {
            return this.className;
        }

        public AbstractInfo getInfo() {
            return info;
        }

        @Override
        public int compareTo(final Difference other) {
            return getClassName().compareTo(other.getClassName());
        }
        
    }
    
    public static class Add extends Difference {
        
        public Add(@Nonnull final String className, @Nonnull final AbstractInfo info) {
            super(className, info);
        }
        
    }
    
    public static class Change extends Difference {
        
        private final AbstractInfo modifiedInfo;
        
        public Change(@Nonnull final String className, @Nonnull final AbstractInfo info, @Nonnull final AbstractInfo modifiedInfo) {
            super(className, info);
            
            this.modifiedInfo = modifiedInfo;
        }

        public AbstractInfo getModifiedInfo() {
            return this.modifiedInfo;
        }
        
    }
    
    public class Remove extends Difference {
        
        public Remove(@Nonnull final String className, @Nonnull final AbstractInfo info) {
            super(className, info);
        }
        
    }
    
    private String currentClassName;
    private final Set<String> includes;
    private final Set<String> excludes;
    private final Set<Difference> differences = new HashSet<Difference>();

    public AccumulatingDiffHandler() {
        this(Collections.<String>emptySet(), Collections.<String>emptySet());
    }

    public AccumulatingDiffHandler(@Nonnull final Set<String> includes, @Nonnull final Set<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    @Override
    public void startDiff(final String previous, final String current) throws DiffException {
    }

    @Override
    public void endDiff() throws DiffException {
    }

    @Override
    public void startOldContents() throws DiffException {
    }

    @Override
    public void endOldContents() throws DiffException {
    }

    @Override
    public void startNewContents() throws DiffException {
    }

    @Override
    public void endNewContents() throws DiffException {
    }

    @Override
    public void contains(final ClassInfo classInfo) throws DiffException {
    }

    @Override
    public void startAdded() throws DiffException {
    }

    @Override
    public void classAdded(final ClassInfo classInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Add(getClassName(classInfo.getName()), classInfo));
    }

    @Override
    public void fieldAdded(final FieldInfo fieldInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Add(this.currentClassName, fieldInfo));
    }

    @Override
    public void methodAdded(final MethodInfo methodInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Add(this.currentClassName, methodInfo));
    }

    @Override
    public void endAdded() throws DiffException {
    }

    @Override
    public void startChanged() throws DiffException {
    }

    @Override
    public void startClassChanged(final String className) throws DiffException {
        this.currentClassName = getClassName(className);
    }

    @Override
    public void classChanged(final ClassInfo oldClassInfo, final ClassInfo newClassInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Change(this.currentClassName, oldClassInfo, newClassInfo));
    }

    @Override
    public void fieldChanged(final FieldInfo oldFieldInfo, final FieldInfo newFieldInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Change(this.currentClassName, oldFieldInfo, newFieldInfo));
    }

    @Override
    public void methodChanged(final MethodInfo oldMethodInfo, final MethodInfo newMethodInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Change(this.currentClassName, oldMethodInfo, newMethodInfo));
    }

    @Override
    public void endClassChanged() throws DiffException {
    }

    @Override
    public void endChanged() throws DiffException {
    }

    @Override
    public void startRemoved() throws DiffException {
    }

    @Override
    public void classRemoved(final ClassInfo classInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Remove(this.currentClassName, classInfo));
    }

    @Override
    public void fieldRemoved(final FieldInfo fieldInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Remove(this.currentClassName, fieldInfo));
    }

    @Override
    public void methodRemoved(final MethodInfo methodInfo) throws DiffException {
        if (!isConsidered()) {
            return;
        }

        this.differences.add(new Remove(this.currentClassName, methodInfo));
    }

    @Override
    public void endRemoved() throws DiffException {
    }

    /**
     *
     * Is considered a class whose package:
     * * is included
     * * is not excluded
     *
     * If includes are provided then package must be defined here.
     *
     * @return
     */
    private boolean isConsidered() {
        for (final String exclude : this.excludes) {
            if (this.currentClassName.startsWith(exclude)) {
                return false;
            }
        }

        if (!this.includes.isEmpty()) {
            for (final String include : this.includes) {
                if (this.currentClassName.startsWith(include)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public Set<Difference> getDifferences() {
        return Collections.unmodifiableSet(this.differences);
    }

}
