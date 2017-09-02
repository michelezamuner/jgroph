# Quality Assurance


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

I also wanted to experiment with some code coverage tool. At first I tried
[cobertura](http://www.mojohaus.org/cobertura-maven-plugin/), which is very easy to setup in your `pom.xml`:
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
                        <id>default-report</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>default-check</id>
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

after which you can run `mvn test` and `mvn jacoco:report`. The reports will be at `target/site/jacoco/index.html`.

With this configuration, however, code coverage will be analyzed both with unit and integration tests. I'd prefer,
instead, only unit tests to be providing code coverage, and using integration tests only to confirm that the system can
work with external parties. To have coverage only for unit tests, just remove the `default-check` execution item, and
work only with the `default-prepare-agent` one.

You can set a minimum coverage, so that the build will fail if that coverage is not met:
```xml
<execution>
    <id>default-prepare-agent</id>
    <goals>
        <goal>prepare-agent</goal>
    </goals>
    <configuration>
        <rules>
            <rule implementation="org.jacoco.maven.RuleConfiguration">
                <element>BUNDLE</element>
                <limits>
                    <limit implementation="org.jacoco.report.check.Limit">
                        <counter>COMPLEXITY</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

Maven already provides several information and reports about the application on the `target/site` local website. Instead
of having to separately open `target/site/jacoco`, it's possible to include Jacoco's reports on the default site, just
adding the plugin to the `reporting` section, in addition to the one under `build`:
```xml
<project>
    <reporting>
        <plugins>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.7.9</version>
            </plugin>
        </plugins>
    </reporting>
</project>
```


## Mutation tests

Mutation tests can perform additional inspections compared to regular unit tests: they work changing certain parts of
the target code, so that its logic is changed. Changing the logic of the target code should result in the tests failing:
however, sometimes this is not the case, and this means that the tests are not exercising the code in a meaningful way,
hence the tests should be improved. The parts of the test code that are changed are called mutants, and each time a
test fails because of this changes, a mutant is killed. Mutants that are still alive after all tests are run indicates
problems with the tests.

A solid tool for mutation testing in Java is [Pitest](http://pitest.org), with its
[Maven plugin](http://pitest.org/quickstart/maven/):
```xml
<project>
    ...
    <build>
        ...
        <plugins>
            ...
            <groupId>org.pitest</groupId>
            <artifactId>pitest-maven</artifactId>
            <version>1.2.0</version>
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
                <version>1.2.0</version>
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


## Code quality
The Java ecosystem provides several QA tools that can be used to check code quality, find bugs, and ensure that the
codebase adheres to a certain style.

A common tool is [findbugs](http://findbugs.sourceforge.net), which also comes with a
[Maven plugin](https://gleclaire.github.io/findbugs-maven-plugin/). Findbugs comes with an integrated graphical
application that can be used to display the check report: however, since we're running everything from inside the VM
(where there isn't any graphical server available), it's preferable to get all reports under the application's local
website built by Maven:
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
achieve this, you would add
[the following configuration](https://www.petrikainulainen.net/programming/maven/findbugs-maven-plugin-tutorial/):
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

Findbugs can also be tweaked using annotations directly in the Java code. Check
[this question](https://stackoverflow.com/questions/31157511) and
[the manual](http://findbugs.sourceforge.net/manual/annotations.html).

Findbugs can be extended [with plugins](https://gualtierotesta.wordpress.com/2015/06/07/findbugs-plugins/), such as
[FB-Contrib](https://github.com/mebigfatguy/fb-contrib) and [Find Security Bugs](http://find-sec-bugs.github.io).

Another useful QA tool is [PMD](https://pmd.github.io), which can catch additional problems within the code, like
unused variables, coding styles not adhered to, and the like. A
[Maven plugin](https://maven.apache.org/plugins/maven-pmd-plugin/) is available, that can be configured the same way
as findbugs, so that reports are automatically added to the local site:
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
- https://www.codacy.com
- http://automationrhapsody.com/mutation-testing-java-pitest/