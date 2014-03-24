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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang.StringUtils;

/**
 *
 * Version following semantic defined by <a href="http://semver.org/">Semantic Versioning</a> document.
 * 
 */
@Immutable
public final class Version implements Comparable<Version> {

    /**
     * {@link Version} element. From most meaningful to less meaningful.
     */
    public enum Element {
        MAJOR, MINOR, PATCH, SPECIAL;
    }

    private static final String FORMAT = "(\\d+)\\.(\\d+)(?:\\.)?(\\d*)(\\.|-|\\+)?([0-9A-Za-z-.]*)?";
    private static final Pattern PATTERN = Pattern.compile(Version.FORMAT);

    private static final String SNAPSHOT_VERSION_SUFFIX = "-SNAPSHOT";

    private final int major;
    private final int minor;
    private final int patch;
    private final String separator;
    private final String special;

    public Version(@Nonnegative final int major, @Nonnegative final int minor, @Nonnegative final int patch) {
        this(major, minor, patch, null, null);
    }

    public Version(@Nonnegative final int major, @Nonnegative final int minor, @Nonnegative final int patch, @Nullable final String separator, @Nullable final String special) {
        if (major < 0) {
            throw new IllegalArgumentException(Element.MAJOR+" must be positive");
        }
        if (minor < 0) {
            throw new IllegalArgumentException(Element.MINOR+" must be positive");
        }
        if (patch < 0) {
            throw new IllegalArgumentException(Element.PATCH+" must be positive");
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.separator = separator;
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

        final int major = Integer.valueOf(matcher.group(1));
        final int minor = Integer.valueOf(matcher.group(2));
        final int patch;
        final String patchMatch = matcher.group(3);
        if (StringUtils.isNotEmpty(patchMatch)) {
           patch = Integer.valueOf(patchMatch);
        } else {
            patch = 0;
        }
        final String separator = matcher.group(4);
        final String special = matcher.group(5);
        return new Version(major, minor, patch, separator, "".equals(special) ? null : special);
    }
    
    /**
     * @param type
     * @return next {@link Version} regarding specified {@link Version.Element}
     */
    public Version next(@Nonnull final Version.Element element) {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }

        switch (element) {
            case MAJOR:
                return new Version(this.major+1, 0, 0);
            case MINOR:
                return new Version(this.major, this.minor+1, 0);
            case PATCH:
                return new Version(this.major, this.minor, this.patch+1);
            default:
                throw new IllegalArgumentException("Unknown element <"+element+">");
        }
    }

    public boolean isInDevelopment() {
        return this.major == 0;
    }

    public boolean isStable() {
        return !isInDevelopment();
    }

    public boolean isSnapshot() {
        return this.special != null && this.special.endsWith(Version.SNAPSHOT_VERSION_SUFFIX);
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

        if (this.major < other.major) {
            return -1;
        } else if (this.major == other.major) {
            if (this.minor < other.minor) {
                return -1;
            } else if (this.minor == other.minor) {
                if (this.patch < other.patch) {
                    return -1;
                } else if (this.patch == other.patch) {
                    if (this.special != null && other.special != null) {
                        return this.special.compareTo(other.special);
                    } else if (other.special != null) {
                        return -1;
                    } else if (this.special != null) {
                       return 1;
                    } // else handled by previous equals check
                }
            }
        }
        return 1; //if this (major, minor or patch) is > than other
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.major).append(".").append(this.minor).append(".").append(this.patch);
        if (this.separator != null) {
            builder.append(this.separator);
        }
        if (this.special != null) {
            builder.append(this.special);
        }
        return builder.toString();
    }

}
