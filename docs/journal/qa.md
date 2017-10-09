# Quality Assurance


## Faker

It's common in unit tests having to use realistic data with the systems under test, for example the input that a user
might insert, or the data that might be coming from the database. Hardcoding this data usually leads to the need of
writing triangulation tests: for example, if I tested that the class is working with `#id = 1`, I would also need to
add another test with `#id = 2`, to ensure that there wasn't a bug that wasn't triggered only when the ID was `1`
precisely. But again, there could be a bug when IDs are numbers with multiple digits, etc. This is also called
[property testing](http://www.scalatest.org/user_guide/property_based_testing).

An easy (albeit not very strict) solution to do property testing is using a faker, which is a service providing random
data of the desired type. This way, rather than hardcoding in the code a bunch of alternative data we want our test to
use, we let the test use a new random value each time it's run. While the upside of it is that the code is actually
tested with a lot of different values, as long as tests are run many times, the downside is that it's not immediately
visible that some values are causing bugs to emerge: it may happen that a test that has always passed, suddenly doesn't
pass in one occasion, and then resume passing again, in which case we have to check which value made the test fail, and
fix the bug that emerged in that occasion (maybe adding a new test with that specific kind of input).

A nice faker for Java is [java-faker](https://github.com/DiUS/java-faker), which can be installed as a regular Maven
test dependency, with no additional configuration required.


## Integration tests

With Maven we get support out of the box for unit tests: just require `junit` and run `mvn test`. However, to do also
integration testing, we need to install the Maven FailSafe plugin:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>2.20</version>
  <executions>
      <execution>
          <goals>
              <goal>integration-test</goal>
              <goal>verify</goal>
          </goals>
      </execution>
  </executions>
</plugin>
```

after which integration tests can be run with `mvn verify`. Unit tests, instead, are handled by the Maven SureFire
plugin, which is always available. The difference between Maven SureFire and Maven FailSafe is that the latter makes the
build fail only after the `post-integration` phase is complete: this allows you to tear-down test resources, like a Web
server, before failing.

By default, Maven SureFire, when called with `mvn test`, will run all classes named like `Test*.java`, `*Test.java`,
or `*TestCase.java`, while Maven FailSafe, when called with `mvn verify`, will run all classes named like `IT*.java`,
`*IT.java`, or `*ITCase.java`. Maven FailSafe will, by default, also run all unit tests, in addition to the integration
tests. Integration tests can use JUnit exactly as unit tests usually do. Additionally, integration tests run by Maven
FailSafe use the final package created by the build (in the case of a Web application, a WAR file), while unit tests run
by Maven SureFire use the unpacked classes directly.

Integration tests usually need to work against a real application server instance. This means that we need to spawn a
Jetty instance every time we run integration tests. This can be done with the Maven Jetty plugin:
```xml
<project>
    ...
    <build>
        ...
        <plugins>
            ...
            <plugin>
                <groupId>org.eclipse.jetty</groupId>
                <artifactId>jetty-maven-plugin</artifactId>
                <version>9.4.0.v20161208</version>
                <configuration>
                    <scanIntervalSeconds>10</scanIntervalSeconds>
                    <stopPort>8005</stopPort>
                    <stopKey>STOP</stopKey>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>javax.servlet</groupId>
                        <artifactId>javax.servlet-api</artifactId>
                        <version>3.1.0</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <id>default-run</id>
                        <goals>
                            <goal>run</goal>
                        </goals>
                        <configuration>
                            <scanIntervalSeconds>0</scanIntervalSeconds>
                            <daemon>true</daemon>
                        </configuration>
                    </execution>
                    <execution>
                        <id>start-jetty</id>
                        <phase>pre-integration-test</phase>
                        <goals>
                            <goal>start</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>stop-jetty</id>
                        <phase>post-integration-test</phase>
                        <goals>
                            <goal>stop</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    ...
</project>
```

This is usually used to spawn an application server with `mvn jetty:run`, to use the application from an actual browser,
for example. In fact the configuration includes a `scanIntervalSeconds` item, so that Jetty will "watch" changes in the
filesystem, and automatically rebuild the project so that those changes are directly visible by the client.

In addition to this, we are also configuring Maven Jetty to be started before the integration tests, and stopped
afterwards.


## Code coverage

I also wanted to experiment with some code coverage tool. At first I tried [cobertura](http://www.mojohaus.org/cobertura-maven-plugin/),
which is very easy to setup in your `pom.xml`:
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

after which you can just run `mvn cobertura:cobertura` and check the nice HTML report at
`target/site/cobertura/index.html`. However, I was then trying to figure out how to handle integration tests as well,
and [this post](http://stackoverflow.com/questions/2188192#20682858) convinced me to try
[JaCoCo](http://www.eclemma.org/jacoco/trunk/doc/maven.html) instead. This is trickier to setup, since the basic
configuration is:
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

after which you can run `mvn test` and `mvn jacoco:report`. The reports will be at `target/site/jacoco/index.html`.

You can set a minimum coverage, so that the build will fail if that coverage is not met:
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

Maven already provides several information and reports about the application on the `target/site` local website. Instead
of having to separately open `target/site/jacoco`, it's possible to include Jacoco's reports on the default site, just
adding the plugin to the `reporting` section, in addition to the one under `build`:
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

The `reportSets` section is needed to avoid JaCoCo to create an additional aggregate report inside the site, but to put
everything inside a single report instead.


## Mutation tests

Mutation tests can perform additional inspections compared to regular unit tests: they work changing certain parts of
the target code, so that its logic is changed. Changing the logic of the target code should result in the tests failing:
however, sometimes this is not the case, and this means that the tests are not exercising the code in a meaningful way,
hence the tests should be improved. The parts of the test code that are changed are called mutants, and each time a
test fails because of this changes, a mutant is killed. Mutants that are still alive after all tests are run indicates
problems with the tests.

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

By default Pitest runs all tests that are present in the codebase: this, however, will fail because in our current setup
the Jetty integrated server is required to successfully run the integration tests, and Pitest is not automatically
spawning Jetty. To avoid running integration tests, we add the `targetTests` configuration parameter, where we specify
that only classes ending with `Test` need to be considered for mutation testing.

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

Be aware that, if you configure Pitest like this, then you won't be able to call `site` without having also run the
mutation tests, because during `site` Pitest will look for the directory containing its own reports (which by default
is `target/pit-reports`), and if it's not found the build will fail. To overcome this issue, and be able to run `site`
even without the coverage report, I changed the Pitest report directory to simply `target` (which is guaranteed to
always exist):
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

Notice how we need to first indicate where the reports must be written inside `build`, and then where they must be read
from inside `reporting`.


## Code quality

The Java ecosystem provides several QA tools that can be used to check code quality, find bugs, and ensure that the
codebase adheres to a certain style.

A common tool is [findbugs](http://findbugs.sourceforge.net), which also comes with a [Maven plugin](https://gleclaire.github.io/findbugs-maven-plugin/).
Findbugs comes with an integrated graphical application that can be used to display the check report: however, since
we're running everything from inside the VM (where there isn't any graphical server available), it's preferable to get
all reports under the application's local website built by Maven:
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

It would be possible also to let findbugs run during each build, and making the build fail if some bugs are found. To
achieve this, you would add [the following configuration](https://www.petrikainulainen.net/programming/maven/findbugs-maven-plugin-tutorial/):
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

Findbugs can be extended [with plugins](https://gualtierotesta.wordpress.com/2015/06/07/findbugs-plugins/), such as
[FB-Contrib](https://github.com/mebigfatguy/fb-contrib) and [Find Security Bugs](http://find-sec-bugs.github.io).

Another useful QA tool is [PMD](https://pmd.github.io), which can catch additional problems within the code, like
unused variables, coding styles not adhered to, and the like. A [Maven plugin](https://maven.apache.org/plugins/maven-pmd-plugin/)
is available, that can be configured the same way as findbugs, so that reports are automatically added to the local
site:
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

You can configure PMD to [automatically fail the build](https://maven.apache.org/plugins/maven-pmd-plugin/examples/violationChecking.html)
during the `verify` phase, if violations are found:
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


## CI

Most of these QA tools are worth being automatically run in a Continuous Integration environment. Usually this makes
sense for projects worked on by several people, to make sure that the code coming from everyone abides by the same
standards of quality. However, I've seen that also a single-man project can benefit from automatic checks: this is
mainly because it's very easy to forget running all these tools on a consistent manner once one gets carried away by
design and coding issues.

To help with this, I've set up a `ci` [Maven profile](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)
to get to the following configuration:
- JaCoCo will break builds if the coverage is under the selected threshold only in the CI profile: this is to allow me
to run `verify` during development without it constantly failing due to missed coverage targets, and to postpone
improving the coverage at a later time.
- Similarly, Findbugs will break builds only during CI, so that I can wait fixing bugs when I finished thinking about
design and implementation (but still before pushing changes to the remote repository, if CI is configured to run at
that time).
- PMD will perform checks and possibly make the build fail only during CI, for the same reasons as above.
- Apparently Pitest cannot be bound to an existing target (like `compile` or `verify`), but needs to be called
explicitly with the `org.pitest:pitest-maven:mutationCoverage` target: this needs that we can avoid worrying about it
making builds fail, because we'll call that target only inside the CI environment.
- Of course we won't call the `site` target from the CI environment, to avoid wasting time generating reports that
no one will read (we'll generate them locally, though).

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


## Running QA tools in a multi-module setup

QA tools like FindBugs or PMD are run issuing the `site` command of Maven. This is a bit tricky to do in a multi-module
setup, though, because each module depends on other modules, that might not be available from an online repository.

First of all, we want to run `site` separately for each specific sub-module, because if we run it on the parent project
we don't get any information, since the parent project is empy. For example:
```
$ mvn -pl jgroph-api site
```

this would run `site` for the `jgroph-api` module. The problem with this, is that `jgroph-api` depends on a number of
other modules. If they have not been installed into `jgroph-api`, the command will fail, because the dependencies won't
be found. Thus, we need to first install the whole project, and then run `site` from inside a module:
```
$ mvn install
$ mvn -pl jgroph-api site
```

Of course, this also means that running something like `mvn -pl jgroph-api clean site` doesn't make any sense, since it
would wipe out the installed dependencies first, and then fail because they are not found.


## References

- https://developers.openshift.com/getting-started/debian-ubuntu.html
- https://developers.openshift.com/managing-your-applications/common-rhc-commands.html
- http://www.ffbit.com/blog/2014/05/21/skipping-jacoco-execution-due-to-missing-execution-data-file/
- http://stackoverflow.com/questions/1399240/
- http://blog.sonatype.com/2009/06/integration-tests-with-maven-part-1-failsafe-plugin/
- https://www.testwithspring.com/lesson/running-integration-tests-with-maven/
- http://stackoverflow.com/questions/1228709/
- http://www.javaworld.com/article/2074569/
- http://www.baeldung.com/intro-to-findbugs
- https://stackoverflow.com/questions/4297014/
- https://gualtierotesta.wordpress.com/2013/10/14/pmd-and-maven/
- https://maven.apache.org/plugins/maven-pmd-plugin/examples/violationChecking.html
- https://www.codacy.com
- http://automationrhapsody.com/mutation-testing-java-pitest/
- http://www.scalatest.org/user_guide/property_based_testing
- https://maven.apache.org/guides/introduction/introduction-to-profiles.html