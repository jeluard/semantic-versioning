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

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.objectweb.asm.Opcodes.*;
import static org.semver.Delta.inferNextVersion;
import static org.semver.Delta.CompatibilityType.BACKWARD_COMPATIBLE_IMPLEMENTER;
import static org.semver.Delta.CompatibilityType.BACKWARD_COMPATIBLE_USER;
import static org.semver.Delta.CompatibilityType.NON_BACKWARD_COMPATIBLE;
import static org.semver.Version.Element.MAJOR;
import static org.semver.Version.Element.MINOR;
import static org.semver.Version.Element.PATCH;

import org.semver.Delta.Difference;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.osjava.jardiff.ClassInfo;
import org.osjava.jardiff.FieldInfo;
import org.osjava.jardiff.MethodInfo;

public class DeltaTest {

    private static final Set<Difference> EMPTY_DIFFERENCES = Collections.<Difference>emptySet();

    @Test
    public void inferVersion() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        assertEquals(version.next(MAJOR), inferNextVersion(version, NON_BACKWARD_COMPATIBLE));
        assertEquals(version.next(MINOR), inferNextVersion(version, BACKWARD_COMPATIBLE_USER));
        assertEquals(version.next(PATCH), inferNextVersion(version, BACKWARD_COMPATIBLE_IMPLEMENTER));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldInferWithNullVersionFail() {
        inferNextVersion(null, BACKWARD_COMPATIBLE_IMPLEMENTER);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldInferWithNullCompatibilityTypeFail() {
        inferNextVersion(new Version(1, 0, 0), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldNullVersionNotBeInferable() {
        new Delta(EMPTY_DIFFERENCES).infer(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldDevelopmentVersionNotBeInferable() {
        new Delta(EMPTY_DIFFERENCES).infer(new Version(0, 0, 0));
    }

    @Test
    public void shouldEmptyDeltaBeImplementerBackwardCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(EMPTY_DIFFERENCES).infer(version);

        assertEquals(new Version(major, minor, patch+1), inferedVersion);
    }

    @Test
    public void shouldDeltaWithAddsBeUserBackwardCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(Collections.singleton(new Delta.Add("class", new FieldInfo(0, "", "", "", null)))).infer(version);

        assertEquals(new Version(major, minor+1, 0), inferedVersion);
    }

    @Test
    public void shouldDeltaWithChangesBeNonBackwardCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(Collections.singleton(new Delta.Change("class", new FieldInfo(0, "", "", "", null), new FieldInfo(0, "", "", "", null)))).infer(version);

        assertEquals(new Version(major+1, 0, 0), inferedVersion);
    }

    @Test
    public void shouldDeltaWithRemovesBeNonBackwardCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(Collections.singleton(new Delta.Remove("class", new FieldInfo(0, "", "", "", null)))).infer(version);

        assertEquals(new Version(major+1, 0, 0), inferedVersion);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldValidateWithNullPreviousVersionFail() {
        new Delta(EMPTY_DIFFERENCES).validate(null, new Version(1, 0, 0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldValidateWithNullCurrentVersionFail() {
        new Delta(EMPTY_DIFFERENCES).validate(new Version(1, 0, 0), null);
    }

    @Test
    public void shouldValidateWithCurrentVersionInDevelopmentSucceed() {
      validate(EMPTY_DIFFERENCES, new Version(0, 0, 0), new Version(0, 0, 1), true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldValidateWithPreviousVersionNextCurrentVersionFail() {
        new Delta(EMPTY_DIFFERENCES).validate(new Version(1, 1, 0), new Version(1, 0, 0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldValidateWithPreviousVersionEqualsCurrentVersionFail() {
        new Delta(EMPTY_DIFFERENCES).validate(new Version(1, 0, 0), new Version(1, 0, 0));
    }

    @Test
    public void shouldValidateWithCorrectVersionsSucceed() {
      validate(EMPTY_DIFFERENCES, new Version(1, 1, 0), new Version(1, 1, 1), true);
    }

    @Test
    public void shouldValidateWithCorrectPreVersionsSucceed() {
      validate(EMPTY_DIFFERENCES, new Version(1, 1, 0, "-", "rc1"), new Version(1, 1, 0, "-", "rc2"), true);
    }

    @Test
    public void shouldValidateWithIncorrectVersionFail() {
      validate(Collections.singleton(new Delta.Remove("class", new FieldInfo(0, "", "", "", null))), new Version(1, 1, 0), new Version(1, 1, 1), false);
    }

    @Test
    public void upgradeMinorVersionOnClassDeprecated() {
      validate(singleton(new Delta.Deprecate("class", new ClassInfo(1, 0, "", "", "", null, null, null), new ClassInfo(1, 0, "", "", "", null, null, null))), new Version(1, 1, 0), new Version(1, 2, 0), true);
    }

    @Test
    public void upgradeMinorVersionOnFieldDeprecated() {
      validate(singleton(new Delta.Deprecate("class", new FieldInfo(0, "", "", "", null), new FieldInfo(0, "", "", "", null))), new Version(1, 1, 0), new Version(1, 2, 0), true);
    }

    @Test
    public void upgradeMinorVersionOnMethodDeprecated() {
      validate(singleton(new Delta.Deprecate("class", new MethodInfo(0, "", "", "", null), new MethodInfo(0, "", "", "", null))), new Version(1, 1, 0), new Version(1, 2, 0), true);
    }

    @Test
    public void addedInterfaceOnClassIsCompatible() {
        ClassInfo oldClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "super", new String[] { "Interface1" }, null, null);
        ClassInfo newClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "super", new String[] { "Interface1", "Interface2" }, null, null);
        validate(singleton(new Delta.Change("class", oldClassInfo, newClassInfo)), new Version(1, 1, 0), new Version(1, 2, 0), true);
    }

    @Test
    public void removeInterfaceOnClassIsIncompatible() {
        ClassInfo oldClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "super", new String[] { "Interface1", "Interface2" }, null, null);
        ClassInfo newClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "super", new String[] { "Interface1" }, null, null);
        validate(singleton(new Delta.Change("class", oldClassInfo, newClassInfo)), new Version(1, 1, 0), new Version(1, 2, 0), false);
    }

    @Test
    public void classVisibilityChangeIsIncompatible() {
        ClassInfo oldClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "super", new String[0], null, null);
        ClassInfo newClassInfo = new ClassInfo(V1_8, ACC_PRIVATE, "class", "class Foo", "super", new String[0], null, null);
        validate(singleton(new Delta.Change("class", oldClassInfo, newClassInfo)), new Version(1, 1, 0), new Version(1, 2, 0), false);
    }

    @Test
    public void classSuperChangeIsIncompatible() {
        ClassInfo oldClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "super", new String[0], null, null);
        ClassInfo newClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "newsuper", new String[0], null, null);
        validate(singleton(new Delta.Change("class", oldClassInfo, newClassInfo)), new Version(1, 1, 0), new Version(1, 2, 0), false);
    }

    @Test
    public void classByteChangeIsIncompatible() {
        ClassInfo oldClassInfo = new ClassInfo(V1_7, ACC_PUBLIC, "class", "class Foo", "super", new String[0], null, null);
        ClassInfo newClassInfo = new ClassInfo(V1_8, ACC_PUBLIC, "class", "class Foo", "super", new String[0], null, null);
        validate(singleton(new Delta.Change("class", oldClassInfo, newClassInfo)), new Version(1, 1, 0), new Version(1, 2, 0), false);
    }

    private void validate(Set<? extends Delta.Difference> differences, Version previous, Version current, boolean valid) {
      assertEquals(
          "accept differences " + differences + " when changing version from " + previous + " to " + current,
          valid,
          new Delta(differences).validate(previous, current));
    }
}