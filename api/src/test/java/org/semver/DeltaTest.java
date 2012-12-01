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
package org.semver;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.osjava.jardiff.FieldInfo;

public class DeltaTest {

    private static final Set<Delta.Difference> EMPTY_DIFFERENCES = Collections.<Delta.Difference>emptySet();
    
    @Test
    public void inferVersion() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);
        
        Assert.assertEquals(version.next(Version.Element.MAJOR), Delta.inferNextVersion(version, Delta.CompatibilityType.NON_BACKWARD_COMPATIBLE));
        Assert.assertEquals(version.next(Version.Element.MINOR), Delta.inferNextVersion(version, Delta.CompatibilityType.BACKWARD_COMPATIBLE_USER));
        Assert.assertEquals(version.next(Version.Element.PATCH), Delta.inferNextVersion(version, Delta.CompatibilityType.BACKWARD_COMPATIBLE_IMPLEMENTER));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldInferWithNullVersionFail() {
        Delta.inferNextVersion(null, Delta.CompatibilityType.BACKWARD_COMPATIBLE_IMPLEMENTER);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldInferWithNullCompatibilityTypeFail() {
        Delta.inferNextVersion(new Version(1, 0, 0), null);
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
    public void shouldEmptyDeltaBeImplementerBackwareCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(EMPTY_DIFFERENCES).infer(version);
        
        Assert.assertEquals(new Version(major, minor, patch+1), inferedVersion);
    }

    @Test
    public void shouldDeltaWithAddsBeUserBackwareCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(Collections.singleton(new Delta.Add("class", new FieldInfo(0, "", "", "", null)))).infer(version);
        
        Assert.assertEquals(new Version(major, minor+1, 0), inferedVersion);
    }

    @Test
    public void shouldDeltaWithChangesBeNonBackwareCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(Collections.singleton(new Delta.Change("class", new FieldInfo(0, "", "", "", null), new FieldInfo(0, "", "", "", null)))).infer(version);
        
        Assert.assertEquals(new Version(major+1, 0, 0), inferedVersion);
    }

    @Test
    public void shouldDeltaWithRemovesBeNonBackwareCompatible() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        final Version inferedVersion = new Delta(Collections.singleton(new Delta.Remove("class", new FieldInfo(0, "", "", "", null)))).infer(version);
        
        Assert.assertEquals(new Version(major+1, 0, 0), inferedVersion);
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
        Assert.assertTrue(new Delta(EMPTY_DIFFERENCES).validate(new Version(0, 0, 0), new Version(0, 0, 1)));
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
        Assert.assertTrue(new Delta(EMPTY_DIFFERENCES).validate(new Version(1, 1, 0), new Version(1, 1, 1)));
    }

    @Test
    public void shouldValidateWithIncorrectVersionFail() {
        Assert.assertFalse(new Delta(Collections.singleton(new Delta.Remove("class", new FieldInfo(0, "", "", "", null)))).validate(new Version(1, 1, 0), new Version(1, 1, 1)));
    }

}