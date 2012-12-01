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
import java.util.LinkedHashSet;
import java.util.Set;

import de.tototec.cmdoption.CmdOption;
import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;

/**
 * 
 * CLI interface.
 * 
 */
public class Main {

    static class Config {
        @CmdOption(names = { "--help", "-h" }, description = "Show this help and exit.", isHelp = true)
        boolean help;

        @CmdOption(names = { "--diff", "-d" }, conflictsWith = { "--check", "--infer", "--validate" }, description = "Show the differences between two jars.")
        public boolean diff;

        @CmdOption(names = { "--check", "-c" }, conflictsWith = { "--diff", "--infer", "--validate" }, description = "Check the compatibility of two jars.")
        public boolean check;

        @CmdOption(names = { "--infer", "-i" }, requires = { "--base-version" }, conflictsWith = { "--diff", "--check",
                "--validate" }, description = "Infer the version of the new jar based on the previous jar.")
        public boolean infer;

        @CmdOption(names = { "--validate", "-v" }, requires = { "--base-version", "--new-version" }, conflictsWith = {
                "--diff", "--check", "--infer" }, description = "Validate that the versions of two jars fulfil the semver specification.")
        public boolean validate;

        @CmdOption(names = { "--base-jar" }, args = { "JAR" }, minCount = 1, description = "The base jar.")
        public String baseJar;

        @CmdOption(names = { "--new-jar" }, args = { "JAR" }, minCount = 1, description = "The new jar.")
        public String newJar;

        final Set<String> includes = new LinkedHashSet<String>();

        @CmdOption(names = { "--includes" }, args = { "INCLUDE;..." }, description = "Semicolon separated list of full qualified class names to be included.")
        public void setIncludes(String includes) {
            if (includes != null) {
                this.includes.addAll(Arrays.asList(includes.split(";")));
            }
        }

        final Set<String> excludes = new LinkedHashSet<String>();

        @CmdOption(names = { "--excludes" }, args = { "EXCLUDE;..." }, description = "Semicolon separated list of full qualified class names to be excluded.")
        public void setExcludes(String excludes) {
            if (excludes != null) {
                this.excludes.addAll(Arrays.asList(excludes.split(";")));
            }
        }

        @CmdOption(names = { "--base-version" }, args = { "VERSION" }, description = "Version of the base jar (given with --base-jar).")
        public String baseVersion;

        @CmdOption(names = { "--new-version" }, args = { "VERSION" }, description = "Version of the new jar (given with --new-jar).")
        public String newVersion;
    }

    public static void main(final String[] args) throws IOException {
        Config config = new Config();
        CmdlineParser cmdlineParser = new CmdlineParser(config);
        // Load translations of command line descriptions
        cmdlineParser.setResourceBundle(Main.class.getPackage().getName() + ".Messages", Main.class.getClassLoader());
        cmdlineParser.setProgramName("semver");
        cmdlineParser.setAboutLine("Semantic Version validator.");
        try {
            cmdlineParser.parse(args);
        } catch (CmdlineParserException e) {
            System.err.println("Error: " + e.getLocalizedMessage() + "\nRun semver --help for help.");
            System.exit(1);
        }

        if (config.help) {
            cmdlineParser.usage();
            System.exit(0);
        }

        final Comparer comparer = new Comparer(new File(config.baseJar), new File(config.baseJar), config.includes,
                config.excludes);
        final Delta delta = comparer.diff();

        if (config.diff) {
            Dumper.dump(delta);
        }

        if (config.check) {
            System.out.println(delta.computeCompatibilityType());
        }

        if (config.infer) {
            System.out.println(delta.infer(Version.parse(config.baseVersion)));
        }

        if (config.validate) {
            System.out.println(delta.validate(Version.parse(config.baseVersion), Version.parse(config.newVersion)));
        }

    }
}
