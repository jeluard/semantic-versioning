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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * Version following semantic defined by <a href="http://semver.org/">Semantic Versioning</a> document.
 * 
 */
public class Version implements Comparable<Version> {
    
    /**
     * {@link Version} element type. From most meaningful to less meaningful.
     */
    public enum Type {
        MAJOR, MINOR, PATCH, SPECIAL;

        public boolean isAtLeast(@Nonnull final Version.Type type) {
            return compareTo(type) <= 0;
        }

    }

    private final static String FORMAT = "(\\d)\\.(\\d)\\.(\\d)([A-Za-z][0-9A-Za-z-]*)?";
    private final static Pattern PATTERN = Pattern.compile(Version.FORMAT);

    private final int major;
    private final int minor;
    private final int patch;
    private final String special;

    public Version(@Nonnegative final int major, @Nonnegative final int minor, @Nonnegative final int patch) {
        this(major, minor, patch, null);
    }

    public Version(@Nonnegative final int major, @Nonnegative final int minor, @Nonnegative final int patch, @Nonnull final String special) {
        if (major < 0) {
            throw new IllegalArgumentException(Type.MAJOR+" must be positive");
        }
        if (minor < 0) {
            throw new IllegalArgumentException(Type.MINOR+" must be positive");
        }
        if (patch < 0) {
            throw new IllegalArgumentException(Type.PATCH+" must be positive");
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.special = special;
    }

    /**
     *
     * Creates a Version from a string representation. Must match Version#FORMAT.
     *
     * @param version
     * @return
     */
    public static Version parse(@Nonnull final String version) {
        final Matcher matcher = Version.PATTERN.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("<"+version+"> does not match format "+Version.FORMAT);
        }

        final int major = Version.parseElement(matcher.group(1), Type.MAJOR);
        final int minor = Version.parseElement(matcher.group(2), Type.MINOR);
        final int patch = Version.parseElement(matcher.group(3), Type.PATCH);

        if (matcher.groupCount() == 4) {
            return new Version(major, minor, patch, matcher.group(4));
        } else {
            return new Version(major, minor, patch);
        }
    }

    /**
     * @param number
     * @param type
     * @return int representation of provided number
     */
    private static @Nonnegative int parseElement(@Nonnull final String number, @Nonnull final Version.Type type) {
        try {
            return Integer.valueOf(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(type+" must be an integer", e);
        }
    }

    /**
     * @param type
     * @return next {@link Version} regarding specified {@link Version.Type}
     */
    public Version next(@Nonnull final Version.Type type) {
        switch (type) {
            case MAJOR:
                return new Version(this.major+1, 0, 0);
            case MINOR:
                return new Version(this.major, this.minor+1, 0);
            case PATCH:
                return new Version(this.major, this.minor, this.patch+1);
            default:
                throw new IllegalArgumentException("Unknown type <"+type+">");
        }
    }

    /**
     * @param other
     * @return most important differing {@link Version.Type} component between this and another {@link Version}, null if both are same.
     */
    public Version.Type delta(@Nonnull final Version other) {
        if (this.major != other.major) {
            return Version.Type.MAJOR;
        } else if (this.minor != other.minor) {
            return Version.Type.MINOR;
        } else if (this.patch != other.patch) {
            return Version.Type.PATCH;
        } else if (this.special.equals(other.special)) {
            return null;
        } else {
            return null;
        }
    }

    public boolean isInDevelopment() {
        return this.major == 0;
    }

    public boolean isStable() {
        return !isInDevelopment();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + this.major;
        hash = 43 * hash + this.minor;
        hash = 43 * hash + this.patch;
        hash = 43 * hash + (this.special != null ? this.special.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (!(object instanceof Version)) {
            return false;
        }

        final Version other = (Version) object;
        if (other.major != this.major || other.minor != this.minor || other.patch != this.patch) {
            return false;
        }
        return (this.special == null) ? other.special == null : this.special.equals(other.special);
    }

    @Override
    public int compareTo(final Version other) {
        if (equals(other)) {
            return 0;
        }

        if (other.major > this.major) {
            return 1;
        } else if (other.major == this.major) {
            if (other.minor > this.minor) {
                return 1;
            } else if (other.minor == this.minor) {
                if (other.patch > this.patch) {
                    return 1;
                } else if (other.special != null) {
                    return other.special.compareTo(this.special);
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.major).append(".").append(this.minor).append(".").append(this.patch);
        if (this.special != null) {
            builder.append(this.special);
        }
        return builder.toString();
    }

}
