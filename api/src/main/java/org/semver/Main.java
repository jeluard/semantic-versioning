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

package org.semver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * CLI interface.
 * 
 */
public class Main {

    private static void failIfNotEnoughArguments(final String[] arguments, final int minimalSize, final String message) {
        if (arguments.length < minimalSize) {
            System.out.println(message);
            System.exit(-1);
        }
    }
    
    private static final String DIFF_ACTION = "diff";
    private static final String CHECK_ACTION = "check";
    private static final String INFER_ACTION = "infer";
    private static final String VALIDATE_ACTION = "validate";
    
    private static Set<String> extractFiltersIfAny(final String[] arguments, final int position) {
        try {
            final String filters = arguments[position];
            return new HashSet<String>(Arrays.asList(filters.split(";")));
        } catch (IndexOutOfBoundsException e) {
            return Collections.emptySet();
        }
    }
    
    public static void main(final String[] arguments) throws IOException {
        Main.failIfNotEnoughArguments(arguments, 3, "Usage: ["+DIFF_ACTION+"|"+CHECK_ACTION+"|"+INFER_ACTION+"|"+VALIDATE_ACTION+"] (previousVersion) previousJar (currentVersion) currentJar (includes) (excludes)");

        final String action = arguments[0];
        if (DIFF_ACTION.equals(action)) {
            final Comparer comparer = new Comparer(new File(arguments[1]), new File(arguments[2]), extractFiltersIfAny(arguments, 3), extractFiltersIfAny(arguments, 4));
            Dumper.dump(comparer.diff());
        } else if (CHECK_ACTION.equals(action)) {   
            final Comparer comparer = new Comparer(new File(arguments[1]), new File(arguments[2]), extractFiltersIfAny(arguments, 3), extractFiltersIfAny(arguments, 4));
            final Delta delta = comparer.diff();
            System.out.println(delta.computeCompatibilityType());
        } else if (INFER_ACTION.equals(action)) {
            Main.failIfNotEnoughArguments(arguments, 4, "Usage: "+INFER_ACTION+" previousVersion previousJar currentJar (includes) (excludes)");

            final Comparer comparer = new Comparer(new File(arguments[2]), new File(arguments[3]), extractFiltersIfAny(arguments, 4), extractFiltersIfAny(arguments, 5));
            final Delta delta = comparer.diff();
            System.out.println(delta.infer(Version.parse(arguments[1])));
        } else if (VALIDATE_ACTION.equals(action)) {
            Main.failIfNotEnoughArguments(arguments, 5, "Usage: "+VALIDATE_ACTION+" previousVersion previousJar currentVersion currentJar (includes) (excludes)");
            
            final Comparer comparer = new Comparer(new File(arguments[2]), new File(arguments[4]), extractFiltersIfAny(arguments, 5), extractFiltersIfAny(arguments, 6));
            final Delta delta = comparer.diff();
            System.out.println(delta.validate(Version.parse(arguments[1]), Version.parse(arguments[3])));
        } else {
            System.out.println("First argument must be one of ["+DIFF_ACTION+"|"+CHECK_ACTION+"|"+INFER_ACTION+"|"+VALIDATE_ACTION+"]");
            System.exit(-1);
        }
    }

}
