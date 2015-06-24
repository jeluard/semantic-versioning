_This library is in dormant state and won't add any new feature. You might want to consider [japicmd](https://github.com/siom79/japicmp) instead._ 

![CI status](https://secure.travis-ci.org/jeluard/semantic-versioning.png)

Version number changes implications are not always clearly identified. Can I be sure this new minor version didn't break the public API? 
As a library writer, how to continuously validate I don't break binary compatibility?

`semantic-versioning` is a Java library allowing to validate (using bytecode inspection) if library version numbers follows Semantic Versioning principles as defined by [Semantic Versioning](http://semver.org).
It can check JAR files or classes to identify changes between versions and validate if the new version number is correct according to semver.

`semantic-versioning` is available as an [API](#api), a [command line tool](#cli) and a [maven enforcer](http://maven.apache.org/enforcer/maven-enforcer-plugin/) [rule](#rule).

<a name="api"></a>
## API overview

Semantic Versioning also provides an API for programmatically validating your project's version number.

```java
final File previousJar = ...;
final File currentJar = ...;
        
final Comparer comparer = new Comparer(previousJar, currentJar, ..., ...);
final Delta delta = comparer.diff();

final String compatibilityType = ...;

//Validates that computed and provided compatibility type are compatible.
final Delta.CompatibilityType expectedCompatibilityType = Delta.CompatibilityType.valueOf(compatibilityType);
final Delta.CompatibilityType detectedCompatibilityType = delta.computeCompatibilityType();
if (detectedCompatibilityType.compareTo(expectedCompatibilityType) > 0) {
  //Not compatible.
}

//Provide version number for previous and current Jar files.
final Version previous = Version.parse(...);
final Version current = Version.parse(...);

//Validates that current version number is valid based on semantic versioning principles.
final boolean compatible = delta.validate(previous, current);
```

<a name="cli"></a>
## CLI

This simple command line tool looks at Java JAR files and determine API changes.

### Built-in help

```
% java -jar semver.jar --help
Semantic Version validator.

Usage: semver [options]

Options:
  --base-jar JAR          The base jar.
  --base-version VERSION  Version of the base jar (given with --base-jar).
  --check,-c              Check the compatibility of two jars.
  --diff,-d               Show the differences between two jars.
  --excludes EXCLUDE;...  Semicolon separated list of full qualified class names
						  or partly qualified class names with wild cards
                          to be excluded.
  --help,-h               Show this help and exit.
  --includes INCLUDE;...  Semicolon separated list of full qualified class names
						  or partly qualified class names with wild cards
                          to be included.
  --infer,-i              Infer the version of the new jar based on the previous
                          jar.
  --new-jar JAR           The new jar.
  --new-version VERSION   Version of the new jar (given with --new-jar).
  --validate,-v           Validate that the versions of two jars fulfil the
                          semver specification.
```

### Diff

Dump all changes between two JARs on standard output.

```
% java -jar semver.jar --diff --base-jar previousJar --new-jar current.jar
Class org.project.MyClass
 Added Class 
Class org.project.MyClass2
 Added Method method1
 Removed Field field1
 Changed Field field2 removed: final
```

### Excludes / Includes

In- or exclude classes for the validation by specifying a fully qualified 
class name or using wild cards. There are two wild cards: `*` and `**`.
`*` is a wild card for an arbitrary number of characters but at most one 
folder hierarchy. 
`**` is a wild card for an arbitrary number of characters and an arbitrary 
number of folder hierarchies. 

```
% java -jar semver.jar --excludes **/MyClass; org/**/MyClass; org/**/*Class;
```

### Check

Check compatibility type between two JARs.

```
% java -jar semver.jar --check --base-jar previousJar --new-jar current.jar
BACKWARD_COMPATIBLE_IMPLEMENTER
```

### Infer

Infer JAR version based on a previously versioned JAR.

```
% java -jar semver.jar --infer --base-version 1.0.0 --base-jar previous.jar --new-jar current.jar
1.0.1
```

### Validate

Validate JAR version based on a previously versioned JAR.

```
% java -jar semver.jar --validate --base-version 1.0.0 --base-jar previous.jar --new-version 1.0.1 --new-jar current.jar
true
```

<a name="rule"></a>
## Maven Enforcer Rule

The enforcer rule offers a rule for checking project's version against a previously released artifact.

### Checking a project's compatibility

In order to check your project's compatibility, you must add the enforcer rule as a dependency to
the maven-enforcer-plugin and then configure the maven-enforcer-plugin to run the rule:

```xml
<project>
  ...
  <build>
    ...
    <plugins>
      ...
      <plugin>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>1.0.1</version>
        ...
        <dependencies>
            ...
            <dependency>
                <groupId>org.semver</groupId>
                <artifactId>enforcer-rule</artifactId>
                <version>0.9.33</version>
            </dependency>
            ...
        </dependencies>
        ...
        <executions>
           ....
          <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals>
              <goal>enforce</goal>
            </goals>
            <configuration>
              <rules>
                <requireBackwardCompatibility implementation="org.semver.enforcer.RequireBackwardCompatibility">
                  <compatibilityType>BACKWARD_COMPATIBLE_IMPLEMENTER</compatibilityType>
                </requireBackwardCompatibility>
              </rules>
            </configuration>
          </execution>
          ...
        </executions>
        ...
      </plugin>
      ...
    </plugins>
    ...
  </build>
  ...
</project>
```

Once you have configured your project, maven-enforcer will be able to throw a build error if current version is not backward compatible with last released one. 

You can force strict checking (i.e. compatibility type must exactly match specified one):

```xml
<configuration>
  <rules>
    <requireBackwardCompatibility implementation="org.semver.enforcer.RequireBackwardCompatibility">
      ...
      <strictChecking>true</strictChecking>
      ...
    </requireBackwardCompatibility>
  </rules>
</configuration>
```

### Checking a project's version

In order to check your project's version, you must add the enforcer rule as a dependency to
the maven-enforcer-plugin and then configure the maven-enforcer-plugin to run the rule:

```xml
<project>
  ...
  <build>
    ...
    <plugins>
      ...
      <plugin>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>1.0.1</version>
        ...
        <dependencies>
            ...
            <dependency>
                <groupId>org.semver</groupId>
                <artifactId>enforcer-rule</artifactId>
                <version>0.9.33</version>
            </dependency>
            ...
        </dependencies>
        ...
        <executions>
           ....
          <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals>
              <goal>enforce</goal>
            </goals>
            <configuration>
              <rules>
                <requireSemanticVersioningConformance implementation="org.semver.enforcer.RequireSemanticVersioningConformance" />
              </rules>
            </configuration>
          </execution>
          ...
        </executions>
        ...
      </plugin>
      ...
    </plugins>
    ...
  </build>
  ...
</project>
```

Once you have configured your project, maven-enforcer will be able to throw a build error if current version follows semantic versioning principles. 

## Dumping details

Dump details of detected changes:

```xml
<configuration>
  <rules>
    <require...>
      ...
      <dumpDetails>true</dumpDetails>
      ...
    </require...>
  </rules>
</configuration>
```

### Checking against a well known version

You can force check with a well known version:

```xml
<configuration>
  <rules>
    <require...>
      ...
      <previousVersion>1.0.0</previousVersion>
      ...
    </require...>
  </rules>
</configuration>
```

### Filtering

Both rules allow to filter classes/packages:

```xml
<require...>
  ...
  <includes>
    <include>org/project/MyClass</include>
    <include>org/project/internal</include>
  </includes>
  <excludes>
    <exclude>org/project/MyClass</exclude>
    <exclude>org/project/internal</exclude>
  </excludes>
  ...
</require...>
```

## Maven dependency

```xml
<dependency>
    <groupId>org.semver</groupId>
    <artifactId>enforcer-rule</artifactId>
    <version>0.9.33</version>
</dependency>
```

## License

Released under [Apache 2 license](http://www.apache.org/licenses/LICENSE-2.0.html).
