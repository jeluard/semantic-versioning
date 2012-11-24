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

		@CmdOption(names = { "--diff", "-d" }, conflictsWith = { "--check", "--infer", "--validate" })
		public boolean diff;

		@CmdOption(names = { "--check", "-c" }, conflictsWith = { "--diff", "--infer", "--validate" })
		public boolean check;

		@CmdOption(names = { "--infer", "-i" }, requires = { "--base-version" }, conflictsWith = { "--diff", "--check",
				"--validate" })
		public boolean infer;

		@CmdOption(names = { "--validate", "-v" }, requires = { "--base-version", "--new-version" }, conflictsWith = {
				"--diff", "--check", "--infer" })
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

		@CmdOption(names = { "--base-version" }, args = { "VERSION" })
		public String baseVersion;

		@CmdOption(names = { "--new-version" }, args = { "VERSION" })
		public String newVersion;
	}

	public static void main(final String[] args) throws IOException {
		Config config = new Config();
		CmdlineParser cmdlineParser = new CmdlineParser(config);
		cmdlineParser.setProgramName("semver");
		try {
			cmdlineParser.parse(args);
		} catch (CmdlineParserException e) {
			System.err.println("Error: " + e.getMessage() + "\nRun semver --help for help.");
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
