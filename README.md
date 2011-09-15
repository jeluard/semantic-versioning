Semantic Versioning is a Java library allowing to validate if library version numbers follows Semantic Versioning principles as defined by [http://semver.org](Semantic Versioning). ![StillMaintained](http://stillmaintained.com/jeluard/semantic-versioning.png)

Project version number changes implication are not always clearly identified. Will this patch released be safe to use in my project? 
Does this minor version increment implies my implementation of some API is no more complete? 
  
Semantic Versioning can check JAR files to identify breaking changes between versions and identify if your version number is correct according to Semantic Versioning principles.

# CLI

This simple command line tool looks at Java JAR files and determine API changes.
You might download self contained JAR file from [github](https://github.com/downloads/jeluard/semantic-versioning/semver-0.9.10.jar).


## Diff

Dump all changes between two JARs on standard output.

```
% java -jar semver.jar previousJar currentJar (includes) (excludes)
Class org.project.MyClass
 Added Class 
Class org.project.MyClass2
 Added Method method1
 Removed Field field1
 Changed Field field2 removed: final
```

## Check

Check compatibility type between two JARs.

```
% java -jar semver.jar previousJar currentJar (includes) (excludes)
BACKWARD_COMPATIBLE_IMPLEMENTER
```

## Infer

Infer JAR version based on a previously versioned JAR.

```
% java -jar semver.jar previousVersion previousJar currentJar (includes) (excludes)
1.0.0
```

## Validate

Validate JAR version based on a previously versioned JAR.

```
% java -jar semver.jar previousVersion previousJar currentVersion currentJar (includes) (excludes)
true
```

# Enforcer Rule

The enforcer rule offers a rule for checking project's version against a previously released artifact.

## Checking a project's compatibility

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
                <version>0.9.10</version>
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

## Checking a project's version

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
                <version>0.9.10</version>
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

# API overview

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

# Maven dependency

```xml
<dependency>
    <groupId>org.semver</groupId>
    <artifactId>enforcer-rule</artifactId>
    <version>0.9.10</version>
</dependency>
```