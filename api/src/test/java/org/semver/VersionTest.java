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

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

    @Test
    public void shouldNegativVersionBeRejected() {
        try {
            new Version(-1, 0, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            new Version(0, -1, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            new Version(0, 0, -1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldValidVersionBeParsed() {
        Version.parse("1.2");
        Version.parse("1.2.3");
        Version.parse("10.20.30");
        Version.parse("1.2.3beta");
        Version.parse("1.2.3.DEV");
        Version.parse("1.2.3.DEV-SNAPSHOT");
        Version.parse("1.2-SNAPSHOT");
        Version.parse("1.2.3-SNAPSHOT");
        Version.parse("1.2.3-RC-SNAPSHOT");
        Version.parse("1.2-RC-SNAPSHOT");
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldInvalidVersion1NotBeParsed() {
        Version.parse("invalid");
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldInvalidVersion2NotBeParsed() {
        Version.parse("1.2.3.4");
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldInvalidVersion3NotBeParsed() {
        Version.parse("a.2.3");
    }

    @Test
    public void shouldMajorBeCorrectlyIncremented() {
        Assert.assertEquals(Version.parse("10.0.0"), Version.parse("9.0.0").next(Version.Element.MAJOR));
    }

    @Test
    public void shouldMinorBeCorrectlyIncremented() {
        Assert.assertEquals(Version.parse("0.10.0"), Version.parse("0.9.0").next(Version.Element.MINOR));
    }

    @Test
    public void shouldPatchBeCorrectlyIncremented() {
        Assert.assertEquals(Version.parse("0.0.10"), Version.parse("0.0.9").next(Version.Element.PATCH));
    }

    @Test
    public void shouldDevelopmentBeInDevelopment() {
        Assert.assertTrue(Version.parse("0.1.1").isInDevelopment());
        Assert.assertFalse(Version.parse("1.1.1").isInDevelopment());
    }

    @Test
    public void shouldStableVersionBeStable() {
        Assert.assertTrue(Version.parse("1.1.1").isStable());
        Assert.assertFalse(Version.parse("0.1.1").isStable());
    }

    @Test
    public void isNewer() {
        Assert.assertTrue(Version.parse("1.0.0").compareTo(Version.parse("0.0.0")) < 0);
        Assert.assertTrue(Version.parse("0.0.0").compareTo(Version.parse("1.0.0")) > 0);
        Assert.assertTrue(Version.parse("1.1.0").compareTo(Version.parse("1.0.0")) < 0);
        Assert.assertTrue(Version.parse("1.0.0").compareTo(Version.parse("1.1.0")) > 0);
        Assert.assertTrue(Version.parse("1.0.1").compareTo(Version.parse("1.0.0")) < 0);
        Assert.assertTrue(Version.parse("1.0.0").compareTo(Version.parse("1.0.1")) > 0);
        Assert.assertTrue(Version.parse("1.0.0Beta").compareTo(Version.parse("1.0.0Alpha")) < 0);
        Assert.assertFalse(Version.parse("0.0.0").compareTo(Version.parse("0.0.0")) < 0);
        Assert.assertFalse(Version.parse("0.0.0").compareTo(Version.parse("0.0.1")) < 0);
    }

    @Test
    public void next() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        Assert.assertEquals(version.next(Version.Element.MAJOR), new Version(major+1, 0, 0));
        Assert.assertEquals(version.next(Version.Element.MINOR), new Version(major, minor+1, 0));
        Assert.assertEquals(version.next(Version.Element.PATCH), new Version(major, minor, patch+1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldNextWithNullComparisonTypeFail() {
        final int major = 1;
        final int minor = 2;
        final int patch = 3;
        final Version version = new Version(major, minor, patch);

        version.next(null);
    }  
    
}
