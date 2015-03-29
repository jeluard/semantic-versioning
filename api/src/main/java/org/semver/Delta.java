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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.osjava.jardiff.AbstractInfo;
import org.osjava.jardiff.ClassInfo;

/**
 *
 * Encapsulates differences between two sets of classes.
 * <br />
 * Provides convenient methods to validate that chosen {@link Version} are correct.
 *
 */
@Immutable
public final class Delta {

    /**
     * Library compatibility type. From most compatible to less compatible.
     */
    public enum CompatibilityType {

        BACKWARD_COMPATIBLE_IMPLEMENTER,

        BACKWARD_COMPATIBLE_USER,

        NON_BACKWARD_COMPATIBLE;

        /**
         * Return the lesser of two compatibility types.
         */
        public static CompatibilityType min(CompatibilityType l, CompatibilityType r) {
            if (l == NON_BACKWARD_COMPATIBLE || r == NON_BACKWARD_COMPATIBLE) {
                return NON_BACKWARD_COMPATIBLE;
            }
            if (l == BACKWARD_COMPATIBLE_USER || r == BACKWARD_COMPATIBLE_USER) {
                return BACKWARD_COMPATIBLE_USER;
            }
            return BACKWARD_COMPATIBLE_IMPLEMENTER;
        }
    }

    @Immutable
    public static class Difference implements Comparable<Difference> {

        private final String className;
        private final AbstractInfo info;

        public Difference(@Nonnull final String className, @Nonnull final AbstractInfo info) {
            if (className == null) {
                throw new IllegalArgumentException("null className");
            }
            if (info == null) {
                throw new IllegalArgumentException("null info");
            }

            this.className = className;
            this.info = info;
        }

        @Nonnull
        public String getClassName() {
            return this.className;
        }

        @Nonnull
        public AbstractInfo getInfo() {
            return info;
        }

        @Override
        public int compareTo(final Difference other) {
            return getClassName().compareTo(other.getClassName());
        }

    }

    @Immutable
    public static class Add extends Difference {

        public Add(@Nonnull final String className, @Nonnull final AbstractInfo info) {
            super(className, info);
        }

    }

    @Immutable
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

    @Immutable
    public static class Deprecate extends Difference {

	private final AbstractInfo modifiedInfo;

	public Deprecate(@Nonnull final String className,
		@Nonnull final AbstractInfo info,
		@Nonnull final AbstractInfo modifiedInfo) {
	    super(className, info);

	    this.modifiedInfo = modifiedInfo;
	}

	public AbstractInfo getModifiedInfo() {
	    return this.modifiedInfo;
	}
    }

    @Immutable
    public static class Remove extends Difference {

        public Remove(@Nonnull final String className, @Nonnull final AbstractInfo info) {
            super(className, info);
        }

    }

    private final Set<Difference> differences;

    public Delta(@Nonnull final Set<? extends Difference> differences) {
        this.differences = Collections.unmodifiableSet(differences);
    }

    @Nonnull
    public final Set<Difference> getDifferences() {
        return this.differences;
    }

    /**
     * @param differences
     * @return {@link CompatibilityType} based on specified {@link Difference}
     */
    @Nonnull
    public final CompatibilityType computeCompatibilityType() {

        if (contains(this.differences, Remove.class)) {
            return CompatibilityType.NON_BACKWARD_COMPATIBLE;
        } else if (contains(this.differences, Change.class)) {
            // Innocent, until proven guilty.
            CompatibilityType compatibilityType = CompatibilityType.BACKWARD_COMPATIBLE_IMPLEMENTER;

            // Look at all Changes, see if any are backwards incompatible.
            for (final Difference difference : this.differences) {
                if (!(difference instanceof Change))  {
                    continue;
                }

                Change change = (Change)difference;

                // If this is a class change, here's how we handle it:
                //  * new superclass = backwards incompatible
                //  * added interface = backwards compatible user (but not implementer)
                //  * removed interface = backwards incompatible
                //  * visibility reduced = backwards incompatible
                //  * bytecode version increased = backwards incompatible
                if (change.getInfo() instanceof ClassInfo) {
                    ClassInfo oldClassInfo = (ClassInfo)change.getInfo();
                    ClassInfo newClassInfo = (ClassInfo)change.getModifiedInfo();

                    if (!oldClassInfo.getSupername().equals(newClassInfo.getSupername())) {
                        compatibilityType = CompatibilityType.NON_BACKWARD_COMPATIBLE;
                    }

                    List<String> oldInterfaces = Arrays.asList(oldClassInfo.getInterfaces());
                    List<String> newInterfaces = Arrays.asList(newClassInfo.getInterfaces());

                    List<String> interfaceIntersection = new ArrayList<String>(newInterfaces);

                    if (interfaceIntersection.size() < oldInterfaces.size()) {
                        // Old set of interfaces is not a subset of the new set of interfaces.
                        // This is backwards incompatible.
                        compatibilityType = CompatibilityType.NON_BACKWARD_COMPATIBLE;
                    } else if (newInterfaces.size() > oldInterfaces.size()) {
                        // New class has more interfaces than original, this is not implementer compatible.
                        compatibilityType = CompatibilityType.min(compatibilityType, CompatibilityType.BACKWARD_COMPATIBLE_USER);
                    }

                    // Visibility reduced in new version, that's not compatible.
                    if (oldClassInfo.isPublic() && !newClassInfo.isPublic()) {
                        compatibilityType = CompatibilityType.NON_BACKWARD_COMPATIBLE;
                    }

                    // Bytecode version is higher in new version, that's not considered compatible.
                    if (newClassInfo.getVersion() > oldClassInfo.getVersion()) {
                        compatibilityType = CompatibilityType.NON_BACKWARD_COMPATIBLE;
                    }
                } else {
                    // We don't special case field / method changes (for now)
                    // This means we may not be handling other special cases correctly, and reporting
                    // non backwards compatible when we shouldn't be.
                    compatibilityType = CompatibilityType.NON_BACKWARD_COMPATIBLE;
                }
            }

            return compatibilityType;
        } else if (contains(this.differences, Add.class) ||
                contains(this.differences, Deprecate.class)) {
            return CompatibilityType.BACKWARD_COMPATIBLE_USER;
        } else {
            return CompatibilityType.BACKWARD_COMPATIBLE_IMPLEMENTER;
        }
    }

    protected final boolean contains(final Set<Difference> differences, final Class<? extends Difference> type) {
        for (final Difference difference : differences) {
            if (type.isInstance(difference)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * Infers next {@link Version} depending on provided {@link CompatibilityType}.
     *
     * @param version
     * @param compatibilityType
     * @return
     */
    @Nonnull
    public static Version inferNextVersion(@Nonnull final Version version, @Nonnull final CompatibilityType compatibilityType) {
        if (version == null) {
            throw new IllegalArgumentException("null version");
        }
        if (compatibilityType == null) {
            throw new IllegalArgumentException("null compatibilityType");
        }

        switch (compatibilityType) {
            case BACKWARD_COMPATIBLE_IMPLEMENTER:
                return version.next(Version.Element.PATCH);
            case BACKWARD_COMPATIBLE_USER:
                return version.next(Version.Element.MINOR);
            case NON_BACKWARD_COMPATIBLE:
                return version.next(Version.Element.MAJOR);
            default:
                throw new IllegalArgumentException("Unknown type <"+compatibilityType+">");
        }
    }

    /**
     * @param previous
     * @return an inferred {@link Version} for current JAR based on previous JAR content/version.
     * @throws IOException
     */
    @Nonnull
    public final Version infer(@Nonnull final Version previous) {
        if (previous == null) {
            throw new IllegalArgumentException("null previous");
        }
        if (previous.isInDevelopment()) {
            throw new IllegalArgumentException("Cannot infer for in development version <"+previous+">");
        }

        final CompatibilityType compatibilityType = computeCompatibilityType();
        return inferNextVersion(previous, compatibilityType);
    }

    /**
     * @param previous
     * @param current
     * @return true if {@link Version} provided for current JAR is compatible with previous JAR content/version.
     * @throws IOException
     */
    public final boolean validate(@Nonnull final Version previous, @Nonnull final Version current) {
        if (previous == null) {
            throw new IllegalArgumentException("null previous");
        }
        if (current == null) {
            throw new IllegalArgumentException("null current");
        }
        if (current.compareTo(previous) <= 0) {
            throw new IllegalArgumentException("Current version <"+previous+"> must be more recent than previous version <"+current+">.");
        }
        //When in development public API is not considered stable
        if (current.isInDevelopment()) {
            return true;
        }

        //Current version must be superior or equals to inferred version
        final Version inferredVersion = infer(previous);
        // if the current version is a pre-release then the corresponding release need to be superior or equal
        return current.toReleaseVersion().compareTo(inferredVersion) >= 0;
    }

}
