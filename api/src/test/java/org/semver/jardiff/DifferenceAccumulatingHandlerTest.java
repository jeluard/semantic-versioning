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
package org.semver.jardiff;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;


public class DifferenceAccumulatingHandlerTest {

    @Test
    public void shouldClassBeNotConsideredWithTwoPlaceholdersBeforeAndBehind() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "**/java/**" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithOnePlaceholderBeforeAndBehind() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "*/java/*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeNotConsideredWithTwoPlaceholderAfter() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/**" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithTwoPlaceholderBefore() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "**/java" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithOnePlaceholderAfter() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithOnePlaceholderBefore() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "*/java" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeNotConsideredWithTwoPlaceholderInside() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "de/**/java/**" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeNotConsideredWithOnePlaceholderInside() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "de/*/java/**" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithOnePlaceholderInside() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "de/*/classImpl" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithTwoPlaceholderInsideAndSpecificEnd() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/**/Impl" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassNotBeConsideredWithOnePlaceholderInsideAndSpecificEnd() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/*/*Impl" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithOnePlaceholderInsideAndSpecificEnd() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "test/*/*Impl" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithTwoPlaceholderInsidePlusHashAndUnspecificEnd3() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "test/*/*Impl/*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/Impl2/code" ) );
    }

    @Test
    public void shouldClassNotBeConsideredWithTwoPlaceholderInsidePlusHashAndUnspecificEnd2() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/*/*Impl/*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl/code" ) );
    }

    @Test
    public void shouldClassBeConsideredWithTwoPlaceholderInsidePlusHashAndUnspecificEnd() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "test/*/*Impl/*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl/code/Implem" ) );
    }

    @Test
    public void shouldClassNotBeConsideredWithTwoPlaceholderInsidePlusHashAndUnspecificEnd() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/*/*Impl/*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl/code" ) );
    }

    @Test
    public void shouldClassNotBeConsideredWithOnePlaceholderInsideAndUnspecificEnd() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/*/*Impl*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/classImpl2" ) );
    }

    @Test
    public void shouldClassNotBeConsideredWithTwoPlaceholderInsideAndUnspecificEnd() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "java/**/Impl*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/Impl" ) );
    }

    @Test
    public void shouldClassNotBeConsideredWithTwoPlaceholderInsideAndUnspecificEndWithNoUseOfPlaceholders() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "regex/**/Impl*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/Impl" ) );
    }

    @Test
    public void shouldClassBeConsideredWithTwoPlaceholderInsideAndUnspecificEndWithNoUseOfPlaceholders() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "regex/**/Impl*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should be considered: ", true, new DifferenceAccumulatingHandler( inclusionSet,
                exclusionSet ).isClassConsidered( "de/test/java/regex/test" ) );
    }

    @Test
    public void shouldClassNotBeConsideredWithTwoPlaceholderInsideAndUnspecificEndWith() {

        List<String> inclusions = new ArrayList<String>();
        Set<String> inclusionSet = new HashSet<String>( inclusions );
        List<String> exclusions = new ArrayList<String>();
        exclusions.add( "test/**/Impl*" );
        Set<String> exclusionSet = new HashSet<String>( exclusions );

        Assert.assertEquals( "Class should not be considered: ", false, new DifferenceAccumulatingHandler(
                inclusionSet, exclusionSet ).isClassConsidered( "de/test/java/regex/Impl" ) );
    }
}
