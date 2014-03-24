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
package org.semver;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

import org.osjava.jardiff.DiffException;
import org.osjava.jardiff.JarDiff;
import org.osjava.jardiff.SimpleDiffCriteria;
import org.semver.jardiff.DifferenceAccumulatingHandler;

/**
 * 
 * Allows to compare content of JARs.
 * 
 */
@NotThreadSafe
public class Comparer {

    private final File previousJAR;
    private final File currentJAR;
    private final Set<String> includes;
    private final Set<String> excludes;
    
    public Comparer(final File previousJAR, final File currentJAR, final Set<String> includes, final Set<String> excludes) {
        if (!previousJAR.isFile()) {
            throw new IllegalArgumentException("<"+previousJAR+"> is not a valid file");
        }
        if (!currentJAR.isFile()) {
            throw new IllegalArgumentException("<"+currentJAR+"> is not a valid file");
        }
        
        this.previousJAR = previousJAR;
        this.currentJAR = currentJAR;
        this.includes = includes;
        this.excludes = excludes;
    }

    /**
     * @return all {@link Difference} between both JARs
     * @throws IOException
     */
    public final Delta diff() throws IOException {        
        try {
            final JarDiff jarDiff = new JarDiff();
            jarDiff.loadOldClasses(this.previousJAR);
            jarDiff.loadNewClasses(this.currentJAR);
            final DifferenceAccumulatingHandler handler = new DifferenceAccumulatingHandler(this.includes, this.excludes);
            jarDiff.diff(handler, new SimpleDiffCriteria());
            return handler.getDelta();
        } catch (DiffException e) {
            throw new RuntimeException(e);
        }
    }

}