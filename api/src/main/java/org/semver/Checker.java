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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

import org.osjava.jardiff.AbstractInfo;
import org.osjava.jardiff.ClassInfo;
import org.osjava.jardiff.DiffException;
import org.osjava.jardiff.JarDiff;
import org.osjava.jardiff.SimpleDiffCriteria;
import org.semver.jardiff.AccumulatingDiffHandler;
import org.semver.jardiff.AccumulatingDiffHandler.Difference;

/**
 * 
 * Allows to compare content of JARs. Provides convenient methods to validate that chosen {@link Version} are correct.
 * 
 */
public class Checker {

    /**
     * Library compatibility type. From most compatible to less compatible.
     */
    public enum CompatibilityType {
        
        BACKWARD_COMPATIBLE_IMPLEMENTER,
        
        BACKWARD_COMPATIBLE_USER,

        NON_BACKWARD_COMPATIBLE
    }

    /**
     *
     * Infers next {@link Version} depending on provided {@link CompatibilityType}.
     *
     * @param version
     * @param compatibilityType
     * @return
     */
    public static Version inferNextVersion(@Nonnull final Version version, @Nonnull final CompatibilityType compatibilityType) {
        switch (compatibilityType) {
            case BACKWARD_COMPATIBLE_IMPLEMENTER:
                return version.next(Version.Type.PATCH);
            case BACKWARD_COMPATIBLE_USER:
                return version.next(Version.Type.MINOR);
            case NON_BACKWARD_COMPATIBLE:
                return version.next(Version.Type.MAJOR);
            default:
                throw new IllegalArgumentException("Unknown type <"+compatibilityType+">");
        }
    }

    /**
     * @param compatibilityType
     * @param type
     * @return true if specified {@link CompatibilityType} 
     */
    public static boolean isTypeCompatible(final CompatibilityType compatibilityType, final Version.Type type) {
        switch (compatibilityType) {
            case BACKWARD_COMPATIBLE_IMPLEMENTER:
                return type.isAtLeast(Version.Type.PATCH);
            case BACKWARD_COMPATIBLE_USER:
                return type.isAtLeast(Version.Type.MINOR);
            case NON_BACKWARD_COMPATIBLE:
                return type.isAtLeast(Version.Type.MAJOR);
            default:
                throw new IllegalArgumentException("Unknown type <"+compatibilityType+">");
        }
    }

    /**
     * @param previousJAR
     * @param currentJAR
     * @param includes
     * @param excludes
     * @return all {@link Difference} between both JARs
     * @throws IOException
     */
    public final Set<AccumulatingDiffHandler.Difference> diff(final File previousJAR, final File currentJAR, final Set<String> includes, final Set<String> excludes) throws IOException {
        if (!previousJAR.isFile()) {
            throw new IllegalArgumentException("<"+previousJAR+"> is not a valid file");
        }
        if (!currentJAR.isFile()) {
            throw new IllegalArgumentException("<"+currentJAR+"> is not a valid file");
        }
        
        try {
            final JarDiff jarDiff = new JarDiff();
            jarDiff.loadOldClasses(previousJAR);
            jarDiff.loadNewClasses(currentJAR);
            final AccumulatingDiffHandler handler = new AccumulatingDiffHandler(includes, excludes);
            jarDiff.diff(handler, new SimpleDiffCriteria());
            return handler.getDifferences();
        } catch (DiffException e) {
            throw new RuntimeException(e);
        }
    }

    protected final String extractActionType(final Difference difference) {
        final String actionType = difference.getClass().getSimpleName();
        return actionType.endsWith("e")?actionType+"d":actionType+"ed";
    }

    protected final String extractInfoType(final AbstractInfo info) {
        final String simpleClassName = info.getClass().getSimpleName();
        return simpleClassName.substring(0, simpleClassName.indexOf("Info"));
    }

    protected final String extractDetails(final Difference difference) {
        if (difference instanceof AccumulatingDiffHandler.Change) {
            final AccumulatingDiffHandler.Change change = (AccumulatingDiffHandler.Change) difference;
            return extractDetails(difference.getInfo())+" "+extractAccessDetails(difference.getInfo(), change.getModifiedInfo());
        } else {
            return extractDetails(difference.getInfo());
        }
    }

    protected final String extractDetails(final AbstractInfo info) {
        final StringBuilder builder = new StringBuilder();
        if (!(info instanceof ClassInfo)) {
            builder.append(info.getName());
        }
        return builder.toString();
    }
    
    protected final void accumulateAccessDetails(final String access, final boolean previousAccess, final boolean currentAccess, final List<String> added, final List<String> removed) {
        if (previousAccess != currentAccess) {
            if (previousAccess) {
                removed.add(access);
            } else {
                added.add(access);
            }
        }
    }
    
    protected final String extractAccessDetails(final AbstractInfo previousInfo, final AbstractInfo currentInfo) {
        final List<String> added = new LinkedList<String>();
        final List<String> removed = new LinkedList<String>();
        accumulateAccessDetails("abstract", previousInfo.isAbstract(), currentInfo.isAbstract(), added, removed);
        accumulateAccessDetails("annotation", previousInfo.isAnnotation(), currentInfo.isAnnotation(), added, removed);
        accumulateAccessDetails("bridge", previousInfo.isBridge(), currentInfo.isBridge(), added, removed);
        accumulateAccessDetails("deprecated", previousInfo.isDeprecated(), currentInfo.isDeprecated(), added, removed);
        accumulateAccessDetails("enum", previousInfo.isEnum(), currentInfo.isEnum(), added, removed);
        accumulateAccessDetails("final", previousInfo.isFinal(), currentInfo.isFinal(), added, removed);
        accumulateAccessDetails("interface", previousInfo.isInterface(), currentInfo.isInterface(), added, removed);
        accumulateAccessDetails("native", previousInfo.isNative(), currentInfo.isNative(), added, removed);
        accumulateAccessDetails("package-private", previousInfo.isPackagePrivate(), currentInfo.isPackagePrivate(), added, removed);
        accumulateAccessDetails("private", previousInfo.isPrivate(), currentInfo.isPrivate(), added, removed);
        accumulateAccessDetails("protected", previousInfo.isProtected(), currentInfo.isProtected(), added, removed);
        accumulateAccessDetails("public", previousInfo.isPublic(), currentInfo.isPublic(), added, removed);
        accumulateAccessDetails("static", previousInfo.isStatic(), currentInfo.isStatic(), added, removed);
        accumulateAccessDetails("strict", previousInfo.isStrict(), currentInfo.isStrict(), added, removed);
        accumulateAccessDetails("super", previousInfo.isSuper(), currentInfo.isSuper(), added, removed);
        accumulateAccessDetails("synchronized", previousInfo.isSynchronized(), currentInfo.isSynchronized(), added, removed);
        accumulateAccessDetails("synthetic", previousInfo.isSynthetic(), currentInfo.isSynthetic(), added, removed);
        accumulateAccessDetails("transcient", previousInfo.isTransient(), currentInfo.isTransient(), added, removed);
        accumulateAccessDetails("varargs", previousInfo.isVarargs(), currentInfo.isVarargs(), added, removed);
        accumulateAccessDetails("volatile", previousInfo.isVolatile(), currentInfo.isVolatile(), added, removed);
        final StringBuilder details = new StringBuilder();
        if (!added.isEmpty()) {
            details.append("added: ");
            for (final String access : added) {
                details.append(access).append(" ");
            }
        }
        if (!removed.isEmpty()) {
            details.append("removed: ");
            for (final String access : removed) {
                details.append(access).append(" ");
            }    
        }
        return details.toString().trim();
    }

    /**
     * 
     * Dumps on {@link System#out} all differences between both JARs.
     * 
     * @param previousJAR
     * @param currentJAR
     * @param includes
     * @param excludes
     * @throws IOException
     */
    public final void dumpDiff(final File previousJAR, final File currentJAR, final Set<String> includes, final Set<String> excludes) throws IOException {
        final Set<AccumulatingDiffHandler.Difference> differences = diff(previousJAR, currentJAR, includes, excludes);
        final List<AccumulatingDiffHandler.Difference> sortedDifferences = new LinkedList<AccumulatingDiffHandler.Difference>(differences);
        Collections.sort(sortedDifferences);
        String currentClassName = "";
        for (final AccumulatingDiffHandler.Difference difference : sortedDifferences) {
            if (!currentClassName.equals(difference.getClassName())) {
                System.out.println("Class "+difference.getClassName());
            }
            System.out.println(" "+extractActionType(difference)+" "+extractInfoType(difference.getInfo())+" "+extractDetails(difference));
            currentClassName = difference.getClassName();
        }
    }

    protected final boolean contains(final Set<AccumulatingDiffHandler.Difference> differences, final Class<? extends Difference> type) {
        for (final Difference difference : differences) {
            if (type.isInstance(difference)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param differences
     * @return {@link CompatibilityType} based on specified {@link Difference}
     */
    protected final CompatibilityType computeCompatibilityType(final Set<AccumulatingDiffHandler.Difference> differences) {
        if (!contains(differences, AccumulatingDiffHandler.Change.class) &&
            !contains(differences, AccumulatingDiffHandler.Remove.class)) {
            return CompatibilityType.NON_BACKWARD_COMPATIBLE;
        } else if (!contains(differences, AccumulatingDiffHandler.Add.class)) {
            return CompatibilityType.BACKWARD_COMPATIBLE_USER;
        } else {
            return CompatibilityType.BACKWARD_COMPATIBLE_IMPLEMENTER;
        }
    }
    
    public final CompatibilityType check(final File previousJAR, final File currentJAR, final Set<String> includes, final Set<String> excludes) throws IOException {
        return computeCompatibilityType(diff(previousJAR, currentJAR, includes, excludes));
    }

    /**
     * @param previous
     * @param previousJAR
     * @param currentJAR
     * @param includes
     * @param excludes
     * @return an inferred {@link Version} for current JAR based on previous JAR content/version.
     * @throws IOException
     */
    public final Version infer(final Version previous, final File previousJAR, final File currentJAR, final Set<String> includes, final Set<String> excludes) throws IOException {
        final CompatibilityType compatibilityType = new Checker().check(previousJAR, currentJAR, includes, excludes);
        return Checker.inferNextVersion(previous, compatibilityType);
    }

    /**
     * @param previous
     * @param previousJAR
     * @param current
     * @param currentJAR
     * @param includes
     * @param excludes
     * @return true if {@link Version} provided for current JAR is compatible with previous JAR content/version.
     * @throws IOException
     */
    public final boolean validate(final Version previous, final File previousJAR, final Version current, final File currentJAR, final Set<String> includes, final Set<String> excludes) throws IOException {
        final CompatibilityType compatibilityType = new Checker().check(previousJAR, currentJAR, includes, excludes);
        return isTypeCompatible(compatibilityType, previous.delta(current));
    }
    
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
        Checker.failIfNotEnoughArguments(arguments, 3, "Usage: ["+DIFF_ACTION+"|"+CHECK_ACTION+"|"+INFER_ACTION+"|"+VALIDATE_ACTION+"] (previousVersion) previousJar (currentVersion) currentJar (includes) (excludes)");

        final String action = arguments[0];
        if (DIFF_ACTION.equals(action)) {
            Checker.failIfNotEnoughArguments(arguments, 3, "Usage: "+DIFF_ACTION+" previousJar currentJar (includes) (excludes)");

            new Checker().dumpDiff(new File(arguments[1]), new File(arguments[2]), extractFiltersIfAny(arguments, 3), extractFiltersIfAny(arguments, 4));
        } else if (CHECK_ACTION.equals(action)) {
            Checker.failIfNotEnoughArguments(arguments, 3, "Usage: "+CHECK_ACTION+" previousJar currentJar (includes) (excludes)");
                     
            System.out.println(new Checker().check(new File(arguments[1]), new File(arguments[2]), extractFiltersIfAny(arguments, 3), extractFiltersIfAny(arguments, 4)));
        } else if (INFER_ACTION.equals(action)) {
            Checker.failIfNotEnoughArguments(arguments, 4, "Usage: "+INFER_ACTION+" previousVersion previousJar currentJar (includes) (excludes)");

            System.out.println(new Checker().infer(Version.parse(arguments[1]), new File(arguments[2]), new File(arguments[3]), extractFiltersIfAny(arguments, 4), extractFiltersIfAny(arguments, 5)));
        } else if (VALIDATE_ACTION.equals(action)) {
            Checker.failIfNotEnoughArguments(arguments, 5, "Usage: "+VALIDATE_ACTION+" previousVersion previousJar currentVersion currentJar (includes) (excludes)");
            
            System.out.println(new Checker().validate(Version.parse(arguments[1]), new File(arguments[2]), Version.parse(arguments[3]), new File(arguments[4]), extractFiltersIfAny(arguments, 5), extractFiltersIfAny(arguments, 6)));
        } else {
            System.out.println("First argument must be one of ["+DIFF_ACTION+"|"+CHECK_ACTION+"|"+INFER_ACTION+"|"+VALIDATE_ACTION+"]");
            System.exit(-1);
        }
    }

}
