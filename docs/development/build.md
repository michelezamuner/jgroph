# Build

## Code coverage

### Cobertura

There are several tools available to do code coverage in Java. For instance, [cobertura](http://www.mojohaus.org/cobertura-maven-plugin/) is very easy to setup in our `pom.xml`:
```xml
<project>
    ...
    <reporting>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>cobertura-maven-plugin</artifactId>
                <version>2.7</version>
            </plugin>
        </plugins>
    </reporting>
</project>
```

after which we can just run `mvn cobertura:cobertura` and check the nice HTML report at `target/site/cobertura/index.html`.


### JaCoCo

Despite being easy to install, Cobertura [doesn't seem to be the best when it comes to integration tests support](http://stackoverflow.com/questions/2188192#20682858). A better alternative might be [JaCoCo](http://www.eclemma.org/jacoco/trunk/doc/maven.html) instead. This is trickier to setup, since the basic configuration is:
```xml
<project>
    ...
    <build>
        ...
        <plugins>
            ...
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.7.9</version>
                <executions>
                    <execution>
                        <id>default-prepare-agent</id>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>default-check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>default-report</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

after which we can run `mvn test` and `mvn jacoco:report`. The reports will be at `target/site/jacoco/index.html`.

We can set a minimum coverage, so that the build will fail if that coverage is not met:
```xml
<execution>
    <id>default-check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

which will be applied when running integration tests with `mvn verify`.

Maven already provides several information and reports about the application on the `target/site` local website. Instead of having to separately open `target/site/jacoco`, it's possible to include Jacoco's reports on the default site, just adding the plugin to the `reporting` section, in addition to the one under `build`:
```xml
<project>
    ...
    <reporting>
        <plugins>
            ...
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.7.9</version>
                <reportSets>
                    <reportSet>
                        <reports>
                            <report>report</report>
                        </reports>
                    </reportSet>
                </reportSets>
            </plugin>
        </plugins>
    </reporting>
</project>
```

The `reportSets` section is needed to prevent JaCoCo from creating an additional aggregate report inside the site, but to put everything inside a single report instead.


## Mutation tests

Mutation tests can perform additional inspections compared to regular unit tests: they work changing certain parts of the target code, so that its logic is changed. Changing the logic of the target code should result in the tests failing: however, sometimes this is not the case, and this means that the tests are not exercising the code in a meaningful way, hence the tests should be improved. The parts of the test code that are changed are called mutants, and each time a test fails because of this changes, a mutant is killed. Mutants that are still alive after all tests are run indicates problems with the tests.

A solid tool for mutation testing in Java is [Pitest](http://pitest.org), with its [Maven plugin](http://pitest.org/quickstart/maven/):
```xml
<project>
    ...
    <build>
        ...
        <plugins>
            ...
            <groupId>org.pitest</groupId>
            <artifactId>pitest-maven</artifactId>
            <version>1.2.2</version>
            <configuration>
                <targetTests>
                    <param>net.slc.jgroph.*Test</param>
                </targetTests>
            </configuration>
        </plugins>
    </build>
</project>
```

By default Pitest runs all tests that are present in the codebase: this, however, will fail because in our current setup the Jetty integrated server is required to successfully run the integration tests, and Pitest is not automatically spawning Jetty. To avoid running integration tests, we add the `targetTests` configuration parameter, where we specify that only classes ending with `Test` need to be considered for mutation testing.

To run Pitest, we need to run all tests first, and then manually call the `mutationCoverage` goal:
```
$ mvn clean test org.pitest:pitest-maven:mutationCoverage
```

the build will fail if some mutants were not killed.

Pitest generates an HTML report. As usual, we want that report to be available in the project site:
```xml
<project>
    ...
    <reporting>
        ...
        <plugins>
            ...
            <plugin>
                <groupId>org.pitest</groupId>
                <artifactId>pitest-maven</artifactId>
                <version>1.2.2</version>
                <reportSets>
                    <reportSet>
                        <reports>
                            <report>report</report>
                        </reports>
                    </reportSet>
                </reportSets>
            </plugin>
        </plugins>
    </reporting>
</project>
```

To generate this report inside the project site, we still need to manually invoke the `mutationCoverage` goal:
```
$ mvn clean test org.pitest:pitest-maven:mutationCoverage site
```

Be aware that, if you configure Pitest like this, then you won't be able to call `site` without having also run the mutation tests, because during `site` Pitest will look for the directory containing its own reports (which by default is `target/pit-reports`), and if it's not found the build will fail. To overcome this issue, and be able to run `site` even without the coverage report, I changed the Pitest report directory to simply `target` (which is guaranteed to always exist):
```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.pitest</groupId>
                <artifactId>pitest-maven</artifactId>
                <version>1.2.2</version>
                <configuration>
                    <reportsDirectory>${project.build.directory}</reportsDirectory>
                </configuration>
                <targetTests>
                    <param>net.slc.jgroph.*Test</param>
                </targetTests>
            </plugin>
        </plugins>
    </build>

    <reporting>
        <plugins>
            <plugin>
                <groupId>org.pitest</groupId>
                <artifactId>pitest-maven</artifactId>
                <version>1.2.2</version>
                <configuration>
                    <reportsDirectory>${project.build.directory}</reportsDirectory>
                </configuration>
                <reportSets>
                    <reportSet>
                        <reports>
                            <report>report</report>
                        </reports>
                    </reportSet>
                </reportSets>
            </plugin>
        </plugins>
    </reporting>
</project>
```

Notice how we need to first indicate where the reports must be written inside `build`, and then where they must be read from inside `reporting`.


### References

- http://automationrhapsody.com/mutation-testing-java-pitest/


## Findbugs

The Java ecosystem provides several QA tools that can be used to check code quality, find bugs, and ensure that the codebase adheres to a certain style. A common such tool is [findbugs](http://findbugs.sourceforge.net), which also comes with a [Maven plugin](https://gleclaire.github.io/findbugs-maven-plugin/).

Findbugs comes with an integrated graphical application that can be used to display the check report: however, since we're running everything from inside the VM (where there isn't any graphical server available), it's preferable to get all reports under the application's local website built by Maven:
```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>findbugs-maven-plugin</artifactId>
            <version>3.0.4</version>
            <configuration>
                <effort>Max</effort>
                <threshold>Low</threshold>
            </configuration>
        </plugin>
    </plugins>
</reporting>
```

It would be possible also to let findbugs run during each build, and making the build fail if some bugs are found. To achieve this, we add [the following configuration](https://www.petrikainulainen.net/programming/maven/findbugs-maven-plugin-tutorial/):
```xml
<project>
    ...
    <build>
        ...
        <plugins>
            ...
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>findbugs-maven-plugin</artifactId>
                <version>3.0.4</version>
                <configuration>
                    <effort>Max</effort>
                    <threshold>Low</threshold>
                    <xmlOutput>true</xmlOutput>
                    <findbugsXmlOutputDirectory>${project.build.directory}/findbugs</findbugsXmlOutputDirectory>
                </configuration>
                <executions>
                    <execution>
                        <id>analyze-compile</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

Findbugs can be extended [with plugins](https://gualtierotesta.wordpress.com/2015/06/07/findbugs-plugins/), such as [FB-Contrib](https://github.com/mebigfatguy/fb-contrib) and [Find Security Bugs](http://find-sec-bugs.github.io).


### References

- http://www.ffbit.com/blog/2014/05/21/skipping-jacoco-execution-due-to-missing-execution-data-file/
- http://www.baeldung.com/intro-to-findbugs


## Checking `null` values

A common problem with Java code is handling NPEs (`NullPointerException`s). Java allows `null` to be used in place of any non-primitive value: this means that any method argument, and any method return value might be `null`, even when that's not expected. Traditionally, one would add `null` checks everywhere, but this would very quickly clutter the codebase. If it's expected that a field or parameter might have no value, it's best to use `Optional`s (even if the use cases of `null` and `Optional` don't fully overlap). If a value must always be there, instead, we have to ensure that this is really the case.

FindBugs can help with checking if values that shouldn't be `null` are actually not `null`, but it's a bit tricky to setup, and doesn't handle indirect calls very well. An easier and better tool is definitely the [checker framework](https://checkerframework.org). Setup installation for Maven can be found [here](https://checkerframework.org/manual/#maven), but in short these are the changes to make in the main POM:
```xml
<properties>
    ...
    <annotatedJdk>${org.checkerframework:jdk8:jar}</annotatedJdk>
</properties>

<dependencies>
    <dependency>
        <groupId>org.checkerframework</groupId>
        <artifactId>checker</artifactId>
        <version>2.3.1</version>
    </dependency>
    <dependency>
        <groupId>org.checkerframework</groupId>
        <artifactId>checker-qual</artifactId>
        <version>2.3.1</version>
    </dependency>
</dependencies>

<build>
    ...
    <plugins>
        ...
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <goals>
                        <goal>properties</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.6.1</version>
            <configuration>
                <compilerArguments>
                    <Xmaxerrs>10000</Xmaxerrs>
                    <Xmaxwarns>10000</Xmaxwarns>
                </compilerArguments>
                <annotationProcessors>
                    <annotationProcessor>org.checkerframework.checker.nullness.NullnessChecker</annotationProcessor>
                </annotationProcessors>
                <compilerArgs>
                    <arg>-AprintErrorStack</arg>
                    <arg>-Xbootclasspath/p:${annotatedJdk}</arg>
                    <arg>-Xlint:all,-options,-path</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

This would configure Maven to run only the `nullness` checker at every build. In addition to this, the checker framework also supports several other checkers, but here we're interested in this one. Once this is in place, every property and parameter is by default considered to be non-null (as if the `@NonNull` annotations was used): every time a property or parameter could possibly be `null`, an error is raised during the build, also considering the initialization period, during object construction.

Sometimes this can be annoying, for example with unit tests properties that are initialized by the mocking framework: to disable initialization checks for a whole class we just have to use the `@SuppressWarning("initialization")` annotation on the class. In some cases it might not be possible to fix a "nullness" error, for example because it's caused by a third party API that allows `null` to be used, without of course being using the proper annotation required by the checker framework. In this case, we can use the `@SuppressWarning("nullness")` to prevent a certain method or variable from being checked.


## PMD

Another useful QA tool is [PMD](https://pmd.github.io), which can catch additional problems within the code, like unused variables, coding styles not adhered to, and the like. A [Maven plugin](https://maven.apache.org/plugins/maven-pmd-plugin/) is available, that can be configured the same way as findbugs, so that reports are automatically added to the local site:
```xml
<reporting>
    <plugins>
        ...
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-pmd-plugin</artifactId>
            <version>3.8</version>
        </plugin>
    </plugins>
</reporting>
```

You can configure PMD to [automatically fail the build](https://maven.apache.org/plugins/maven-pmd-plugin/examples/violationChecking.html) during the `verify` phase, if violations are found:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-pmd-plugin</artifactId>
            <version>3.8</version>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                        <goal>cpd-check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```


### References

- https://stackoverflow.com/questions/4297014/
- https://gualtierotesta.wordpress.com/2013/10/14/pmd-and-maven/
- https://maven.apache.org/plugins/maven-pmd-plugin/examples/violationChecking.html


## CI

Most of these QA tools are worth being automatically run in a Continuous Integration environment. Usually this makes sense for projects worked on by several people, to make sure that the code coming from everyone abides by the same standards of quality. However, I've seen that also a single-man project can benefit from automatic checks: this is mainly because it's very easy to forget running all these tools on a consistent manner once one gets carried away by design and coding issues.

To help with this, I've set up a `ci` [Maven profile](https://maven.apache.org/guides/introduction/introduction-to-profiles.html) to get to the following configuration:
- JaCoCo will break builds if the coverage is under the selected threshold only in the CI profile: this is to allow me to run `verify` during development without it constantly failing due to missed coverage targets, and to postpone improving the coverage at a later time.
- Similarly, Findbugs will break builds only during CI, so that I can wait fixing bugs when I finished thinking about design and implementation (but still before pushing changes to the remote repository, if CI is configured to run at that time).
- PMD will perform checks and possibly make the build fail only during CI, for the same reasons as above.
- Apparently Pitest cannot be bound to an existing target (like `compile` or `verify`), but needs to be called explicitly with the `org.pitest:pitest-maven:mutationCoverage` target: this means that we can avoid worrying about it making builds fail, because we'll call that target only inside the CI environment.
- Of course we won't call the `site` target from the CI environment, to avoid wasting time generating reports that no one will read (we'll generate them locally, though).

Thus, the command we'll be calling from inside the CI environment will be:
```bash
$ mvn -P ci clean verify org.pitest:pitest-maven:mutationCoverage
```

This could fail in the following circumstances:
- During the compilation step inside `verify` if Findbugs finds some bugs.
- During the unit tests step inside `verify` if some unit tests fail.
- During the integration tests step inside `verify` if some integration tests fail.
- During PMD checks inside `verify` is some violations are found by PMD.
- During code coverage check inside `verify` is the coverage is below the selected threshold.
- During the Pitest target, if some mutants fail to be killed.


### References

- https://maven.apache.org/guides/introduction/introduction-to-profiles.html


## Running QA tools in a multi-module setup

QA tools like FindBugs or PMD are run issuing the `site` command of Maven. This is a bit tricky to do in a multi-module setup, though, because each module depends on other modules, that might not be available from an online repository.

First of all, we want to run `site` separately for each specific sub-module, because if we run it on the parent project we don't get any information, since the parent project is empy. For example:
```
$ mvn -pl jgroph-api site
```

this would run `site` for the `jgroph-api` module. The problem with this, is that `jgroph-api` depends on a number of other modules. If they have not been installed into `jgroph-api`, the command will fail, because the dependencies won't be found. Thus, we need to first install the whole project, and then run `site` from inside a module:
```
$ mvn install
$ mvn -pl jgroph-api site
```

Of course, this also means that running something like `mvn -pl jgroph-api clean site` doesn't make any sense, since it would wipe out the installed dependencies first, and then fail because they are not found.
