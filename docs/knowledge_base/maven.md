# Maven

## Integration tests

With Maven we get support out of the box for unit tests: just require `junit` and run `mvn test`. However, to do also integration testing, we need to install the Maven FailSafe plugin:
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

after which integration tests can be run with `mvn verify`. Unit tests, instead, are handled by the Maven SureFire plugin, which is always available. The difference between Maven SureFire and Maven FailSafe is that the latter makes the build fail only after the `post-integration` phase is complete: this allows you to tear-down test resources, like a Web server, before failing.

By default, Maven SureFire, when called with `mvn test`, will run all classes named like `Test*.java`, `*Test.java`, or `*TestCase.java`, while Maven FailSafe, when called with `mvn verify`, will run all classes named like `IT*.java`, `*IT.java`, or `*ITCase.java`. Maven FailSafe will, by default, also run all unit tests, in addition to the integration tests. Integration tests can use JUnit exactly as unit tests usually do. Additionally, integration tests run by Maven FailSafe use the final package created by the build (in the case of a Web application, a WAR file), while unit tests run by Maven SureFire use the unpacked classes directly.

Integration tests usually need to work against a real application server instance. This means that we need to spawn a Jetty instance every time we run integration tests. This can be done with the Maven Jetty plugin:
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

This is usually used to spawn an application server with `mvn jetty:run`, to use the application from an actual browser, for example. In fact the configuration includes a `scanIntervalSeconds` item, so that Jetty will "watch" changes in the filesystem, and automatically rebuild the project so that those changes are directly visible by the client.

In addition to this, we are also configuring Maven Jetty to be started before the integration tests, and stopped afterwards.


### Spawning a custom server before integration tests

We've seen how to have a new Jetty server automatically spawned before integration tests are execute, by using the Jetty Maven plugin. However, we might need to do the same with a server written by us, for which no Maven plugin of course exists. To do this we need two things:
- automatically create an executable JAR of our server module during the `package` phase
- start the server by calling its JAR before integration tests
- stop the server after integration tests

To automatically create a JAR out of a module, we use the Maven Assembly Plugin, like this:
```xml
<build>
    ...
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>3.1.0</version>
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>net.slc.jgroph.adapters.remoteconsole.router.Bootstrap</mainClass>
                    </manifest>
                </archive>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                    <configuration>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                        <finalName>${project.build.finalName}</finalName>
                        <appendAssemblyId>false</appendAssemblyId>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
    ...
</build>
```

Since we want to build an executable JAR, we need our module to have a class exposing a `public static void Main()` method, and add that class to the JAR manifest. This is taken care of by the line:

```xml
<manifest>
    <mainClass>net.slc.jgroph.adapters.remoteconsole.router.Bootstrap</mainClass>
</manifest>
```

Then, we need to configure the plugin to be called during the `package` phase (that is always executed before tests). Additionally, we want the JAR to be standalone, meaning that it should contain all the dependencies that it needs: to achieve this, we add the `DescriptorRef` `jar-with-dependencies`. Finally, we can specify a custom name for the JAR.

To start and stop the JAR whenever we need to, it's handy to prepare a simple Bash script where the server JAR is called with a line like:

```bash
nohup java -jar "${jar_path}" >"${server_log}" >&1 &
echo $! >"${pid_file}"
```

It's important that all output is redirected to files so that the task can actually be put in background and avoid hijacking the console while Maven is running. Next, when we want to stop the server, we just kill the process whose ID we stored inside `pid_file`.

This script needs to be called by Maven at specific moments. To do this, we use the Exec Maven Plugin:
```xml
<build>
    ...
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.6.0</version>
            <executions>
                <execution>
                    <id>start-server</id>
                    <phase>pre-integration-test</phase>
                    <goals>
                        <goal>exec</goal>
                    </goals>
                    <configuration>
                        <executable>bash</executable>
                        <arguments>
                            <argument>server.sh</argument>
                            <argument>start</argument>
                            <argument>${project.build.directory}/${project.build.finalName}.jar</argument>
                        </arguments>
                    </configuration>
                </execution>
                <execution>
                    <id>stop-server</id>
                    <phase>post-integration-test</phase>
                    <goals>
                        <goal>exec</goal>
                    </goals>
                    <configuration>
                        <executable>bash</executable>
                        <arguments>
                            <argument>server.sh</argument>
                            <argument>stop</argument>
                        </arguments>
                    </configuration>
                </execution>
            </executions>
            <configuration>
                <executable>bash</executable>
                <arguments>
                    <argument>-jar</argument>
                    <argument>${project.build.directory}/${project.build.finalName}.jar</argument>
                </arguments>
            </configuration>
        </plugin>
    <plugins>
    ...
</build>
```

Here the server is started during the `pre-integration-test` phase, calling `bash` with the arguments pointing at the shell script we prepared before (the shell script accepts command line arguments to understand if the server needs to be started and stopped, and in the former case requires also the path of the JAR). Then the server is stopped during the `post-integration-test` phase, calling the server script analogously.


### References

- http://stackoverflow.com/questions/1399240/
- http://blog.sonatype.com/2009/06/integration-tests-with-maven-part-1-failsafe-plugin/
- https://www.testwithspring.com/lesson/running-integration-tests-with-maven/
- http://stackoverflow.com/questions/1228709/
- http://www.javaworld.com/article/2074569/