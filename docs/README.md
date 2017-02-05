## Project setup

We are going to build a classic Java Web application with Maven as build tool, and Jetty as application server (servlet container). The first step, thus, is bootstrapping the project folder, using the `maven-archetype-webapp` maven archetype:

```
$ mvn archetype:generate -DgroupId=net.slc.jgroph -DartifactId=jgroph -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```

### References
- [Create a Web application with Maven](http://www.mkyong.com/maven/how-to-create-a-web-application-project-with-maven/)


## Infrastructure
A common category of problems that we face when developing applications includes differences of behavior between local and remote (e.g. production) environments. To protect ourselves from this kind of issues, we want to setup a development environment that resembles as closely as possible the remote ones. The easiest method to achieve this, given the nature of this project, is using Vagrant virtual machines.

Thus, we want to create a virtual environment with certain characteristics, whose configuration will be committed along with the project's code, meaning a `Vagrantfile` and a provisioning script.

The production environment will be an OpenShift "Do It Yourself" gear, since OpenShift doesn't provide a gear with Jetty, yet. Here lies our first compromise: OpenShift runs on RedHat systems, and thus I'd first thought to use a CentOS or Fedora Vagrant box; however, after investing some time into trying them, I gave up, due to a lot of little issues that I found using those boxes, that I didn't experience with the Ubuntu ones, with which I'm more familiar. Example of issues I found were:
- being unable to make NFS synced folders work
- synced folders not automatically updating when changing files in the host environment, due to some problem with the rsync automatic update
- weird problems during the machine boot due to Vagrant timing out when trying to connect to the machine, for which some additional configuration needed to be added to the Vagrantfile, that didn't work consistently anyway.
For these reasons, I opted for a `ubuntu/trusty64` box, that didn't give any of the previously mentioned problems.

Speaking of NFS synced folders, at first I thought it would be cool to use them, and they worked just fine with the Ubuntu box. However, in the end I discarded this option, and get back to the default `vboxsf`, because if I ever had to continue the development on a Windows host, I imagine NFS folders to be quite hard to setup there, and I want to cut down operations work as much as possible. Also, the speed benefit is not that important, at least in the beginning.

I'd like to keep the memory of the virtual machine at a minimum, to be able to immediately spot performance issues. However, this can quite likely be a stupid choice because it will slow down infrastructure operations like provisioning, JVM boot, code compilation, etc., that happen almost never on the production machine, but quite often on the development one. However, since the laptop I use to work on personal projects has quite low specs, I still prefer to keep the VM memory low. For now I settled to `512` MB.

Now we get to the VM provisioning. On more serious setups, I would separate everything related to the infrastructure from the project repository. However, in this case it's more important for me to keep everything in one place, since personal projects are very messy and volatile in nature. Thus, I'm including a `provision.sh` Bash script in the repository. The provisioning procedure should setup the Vagrant box so that it resemble as closely as possible the RedHat server, at least for the features we are interested in:
- OpensShift comes with JDK 8 pre-installed at `/etc/alternatives/java_sdk_8_0`, so I need to download JDK 8 from Oracle and install it in that location
- OpenShift comes with a bunch of environmental variables, in particular those containing the paths of the user directories, in addition to certain values such as the IP address and port that we are allowed to use for our Web server. These need to be setup with the same names in the Ubuntu machine: to do this I just add a `/etc/profile.d/custom.sh` script, containing variables definitions.
Additionally, I added two custom variables containing the starting and maximum memory to be used when launching the JVM. These are calculated as half and three quarters of the current machine memory, and will be used in the server start script. In the production environment, of course, these won't be defined, but in that case we will use default values. This is nice to have to keep the JVM at a controlled size.

The next step is configuring the scripts that OpenShift will use on the production server (and that will also be used locally) during the startup/shutdown procedures. Taking inspiration from the [Jetty on OpenShift repository](https://github.com/openshift-quickstart/jetty-openshift-quickstart) I first prepared a `start` and `stop` script. However, only later I discovered that for some reason the `stop` script wasn't properly destroying the running server process in the production server, that would be still be present during the following startup, preventing the new server instance to be created due to conflicting IP address and port. Thus, I moved the code to kill the existing server process in the startup script as well.

Another difference with the suggested setup is related to the use of Maven. While it's true that OpenShift comes with Maven pre-installed, that application is using the JDK 7 installed on OpenShift, and I couldn't figure out how to make it use JDK 8. The result was that, using the provided Maven, I wasn't able to compile Java 8 code. The solution was to download a local copy of Maven, which would use the `$JAVA_HOME` that I defined in the startup script, pointing to the provided JDK 8.

The Jetty version I downloaded (compared to the Jetty Openshift repo one), is shipped with a weird initial configuration, which basically doesn't work, meaning that you can't even serve static files, let alone servlets. If you go into the demo folder and launch Jetty from there, it works fine, but to make it work from the main Jetty folder you have to rebuild the configuration. In particular, what I had to do was deleting `start.ini`, and first launch Jetty with the options `--create-startd` and `--add-to-start=jsp,http,webapp,deploy` to build a proper configuration. At that point, launching Jetty again as a server, it was working fine.

### References
- [Using `rhc` on Ubuntu](https://developers.openshift.com/getting-started/debian-ubuntu.html)
- [Common `rhc` commands](https://developers.openshift.com/managing-your-applications/common-rhc-commands.html)
- [Jetty on OpenShift](https://github.com/openshift-quickstart/jetty-openshift-quickstart)


## Web Application

Jetty can serve multiple applications on different Web paths. However, in our case we only want to serve one application, so everything will be served from the root path. Doing this in Jetty is very easy, because it's just a matter of providing a `ROOT.war` or a `ROOT` folder inside the `webapps` folder. If you just want to serve static contents, just go with the `ROOT` folder; otherwise, you have to create a WAR file.

To provide this in the production environment, we follow the example of the Jetty OpenShift repo, which adds a `openshift` profile to `pom.xml` defining how to build `ROOT.war` in the `deployments` project folder, which is then symlinked inside the Jetty installation folder, as `webapps`.

To work locally, it's just easier to use the `org.eclipse.jetty/jetty-maven-plugin`, so I installed it. With that in place, you can start a Jetty server locally with just `mvn jetty:run`: this will also monitor your project's file and rebuild the application each time a file change is detected, allowing you not having to repeatedly restart the server. This feature, however, is only limited to the Web files, meaning the files under `webapp`. Having the web app being automatically redeployed at every file change is a bit trickier. First, we have to enable the file scan, setting the `scanIntervalSeconds` configuration property of the Jetty Maven plugin at a certain number of seconds (for example, `1`), and restart the Jetty server to load the new configuration. From now on, each time we rebuild the java files, for example with `mvn:compile`, the Jetty plugin will automatically detect the code changes and restart the server.

To use Jetty as a servlet container, we must provide servlets inside the WAR archive. There are two ways of configuring servlets. The first, and traditional one, working with all versions of the Servlet API, is properly configuring the `WEB-INF/web.xml` file, for instance with:

```xml
<web-app>
  <servlet>
    <servlet-name>jgroph</servlet-name>
    <servlet-class>net.slc.jgroph.SimpleServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>jgroph</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
</web-app>
```

In this case the servlet class has nothing special:

```java
package net.slc.jgroph;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleServlet extends HttpServlet
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        out.println("SimpleServlet Executed");
        out.flush();
        out.close();
    }
}
```

If you're using the Servlet API 3.0 or greater, you can make use of the annotations API, which makes for a quite simpler configuration. In this case, `web.xml` can be empty, provided that you specify that you're using the version 3 of the Servlet API:

```xml
<web-app
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns="http://java.sun.com/xml/ns/javaee"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
        http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
    id="jgroph"
    version="3.0">
</web-app>
```

And all the servlet configuration is added through annotations in the servlet class definition itself:

```xml
package net.slc.jgroph;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

@WebServlet(name="jgroph", urlPatterns={"/"})
public class SimpleServlet extends HttpServlet
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        out.println("SimpleServlet Executed");
        out.flush();
        out.close();
    }
}
```

### Resources
[Java Servlet Specification 3.0](http://download.oracle.com/otndocs/jcp/servlet-3.0-fr-oth-JSpec/)
[Java Servlet Specification 2.5](https://jcp.org/aboutJava/communityprocess/mrel/jsr154/index2.html)
[Servlet 3.0 annotations](https://blogs.oracle.com/swchan/entry/servlet_3_0_annotations)
[Basic `web.xml` configuration](http://tutorials.jenkov.com/java-servlets/web-xml.html)
[Configuring the Jetty Maven Plugin](http://www.eclipse.org/jetty/documentation/current/jetty-maven-plugin.html)


## Database

To avoid wasting OpenShift gears, we'll use a SQlite3 single-file database. At first we'll just use a simple JDBC driver, so no JPA or other fancy stuff. The chosen one is `org.xerial/sqlite-jdbc`.

Before starting with the database connection, we want to put in place some very primitive migration management mechanism. The first thing I'd do is creating a migration SQL script containing the tables' definitions:

```sql
CREATE TABLE resource (id INTEGER, address TEXT, title TEXT, PRIMARY KEY (id));
CREATE TABLE category (id INTEGER, name TEXT, parent INTEGER, PRIMARY KEY(id), FOREIGN KEY(parent) REFERENCES category(id));
CREATE TABLE resources_categories(resource INTEGER, category INTEGER, PRIMARY KEY (resource, category), FOREIGN KEY (resource) REFERENCES resource(id), FOREIGN KEY (category) REFERENCES category(id));
```

This of course contains SQlite3-valid SQL. The database can be initialized with a script like `echo migration.sql > sqlite3 jgroph.db`.

### Resources
[SQLite3-JDBC not supporting concurrent updates](http://www.javaworld.com/article/2073036/sqlite-and-web-applications.html)
[SQLite Java tutorial](http://www.sqlitetutorial.net/sqlite-java/)
[`xerial/sqlite-jdbc`](https://github.com/xerial/sqlite-jdbc)


## Reference of Java Technologies

### Language features
Java SE 7 came with the following new features:
- support for dynamic languages to be running on the JVM
- compressed 64-bit pointers to improve performance on 64-bit JVMs.
- *diamonds*: avoid repeating the type in the right side of declarations:

```java
Map<String Map<String, Map<Integer, List<MyBean>>>> map = new Hashtable<>();
```

- *try-with-resources*: classes implementing `java.lang.AutoCloseable` have their resources automatically closed (the close method is automatically called) in an implicit `finally` block, and any exception thrown at that moment are added to an existing exception, or thrown after the resources have been closed.

```java
try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(...)) {
    // set up statement
    try (ResultSet resultSet = statement.executeQuery()) {
        // do something with result set
    }
} catch (SQLException e) {
    // do something with exception
}
```

- *multi-catch*: multiple kinds of exceptions can be caught in a single `catch` block, provided that one is not of a subclass of the other.

```java
try {
    // do something
} catch (MyException | YourException e) {
    // handle these exceptions the same way
}
```

- *binary literals*: such as `0b11110001000`.
- *underscores in numeric literals*: such as `1_928` or `0b111_1000_1000`.
- `String`s can be used as `switch` arguments.

Java SE 8 comes with the following new features:
- *lambda expressions*:
```java
Thread thread = new Thread(() -> {
    // do something
});
```
- *method references*:
```java
Thread thread = new Thread(this::doSomething);
```
- *JSR 310*: a new date and time API

### Java APIs
Several Java technologies will be explored during the development of this application. Here's a reference of technologies that may be found:

Included in Java SE:
- JDBC: *Java DataBase Connector*
- JNDI: *Java Naming and Directory Interface*
- JAAS: *Java Authentication and Authorization Service*
- JAXP: *Java API for XML Processing*
- JMX: *Java Management Extensions*
- SAAJ: *SOAP with Attachments API for Java*
- StAX: *Streaming API for XML*
- JAF: *Java Beans Activation Framework*
- JAXB: *Java Architecture for XML Binding*

Included in Java EE, Web Services Technologies:
- Web Services for J2EE
- JAX-RPC: *Java API for XML-based RPC*
- JAXR: *Java API for XML Registries*
- JAX-WS: *Java API for XML-based Web Services*
- Web Service Metadata for the Java Platform
- JAX-RS: *Java API for RESTful Web Services*
- JAXM: *Java APIs for XML Messaging*

Included in Java EE, Web Application Technologies:
- Servlet
- JSP: *Java Server Pages*
- JSTL: *Java Server Pages Standard Tag Library*
- JSF: *Java Server Faces*
- JUEL: *Java Unified Expression Language*
- Java API for WebSockets
- Java API for JSON Processing

Included in Java EE, Enterprise Application Technologies:
- EJB: *Enterprise Java Beans*
- Connector Architecture
- JMS: *Java Message Service*
- JTA: *Java Transaction API*
- Java Mail
- JAP: *Java Persistence API*
- Common Annotations API
- CDI: *Contexts and Dependency Injection for Java*
- Dependency Injection for Java
- Bean Validation
- Managed Beans
- Interceptors
- Batch Applications for the Java Platform 1.0 and Concurrency Utilities for Java EE

Included in Java EE, Management and Security Technologies
- JACC: *Java Authorization Service Provider Contract for Containters*
- Enterprise Edition Management API
- Enterprise Edition Deployment API
- JASPIC: *Java Authentication Service Provider Interface for Containers*

### Java Web applications
Java Web applications are run by a *Web container*, also known as *Servlet container*. More complex Java Enterprise applications are instead run by *Application containers*. While Web containers only implement a limited subset of the Java EE technologies, such as Servlet, JSP and JSTL, application containers implement the full Java EE specification.

Standard Java EE Web applications are deployed either as WAR files, or as directories. A WAR file is much like regular JAR files, meaning compressed archives with a specific directory structure recognized by the JVM. Both WAR files and raw Web directories have the same structure:
- a `META-INF` directory
- a `WEB-INF` directory
- other static files accessible from the Web
Thus, the difference between a WAR file and a raw Web directory are just the `META-INF` and `WEB-INF` directories (in addition to the fact that the WAR is a compressed archive of course). All resources contained inside `META-INF` and `WEB-INF` are not directly accessible from the Web.

The `WEB-INF` directory is responsible for containing files needed by the Web container to determine how to deploy and run the application. It contains:
- a `classes` directory, containing in turn all compiled Java classes, and representing the root of the code package. Additionally, it contains a `META-INF` directory providing various application resources,
- a `lib` directory, containing libraries as JAR files, used by the core package classes- a `tags` and a `tld` directory, containing JSP tag files and tag library descriptors, respectively
- a `i18n` directory, containing internationalization and localization files.

The `META-INF` directory contains the application manifest files, and additional resources for specific application servers or Web containers.

Only files contained inside `WEB-INF` are included in the application classpath: this means that they can be loaded with the `ClassLoader`.

In addition to the application classes and resources necessary to run the application, we also need to provide information to the Web container about how the application should be deployed. This is achieved by the *deployment descriptor* which is the set of metadata describing how the application should be deployed in terms of servlet, listeners, filters, and all the other Java EE technologies. Traditionally, the deployment descriptor was all contained inside `/WEB-INF/web.xml`: however, since Servlet 3.0 it's possible to add descriptor elements also through additional means:
- annotations can be added directly inside servlet classes
- servlet fragments can be used, which are JAR files containing the `/META-INF/web-fragment.xml` descriptor, in addition to annotations as well, and can also contain servlet, filters, etc. Web fragments can be activated in the desired order, specified inside `web-fragment.xml`, or inside the application's `web.xml`.

The JVM specification requires three different class loaders to be used. The root class loader is used to load Java core classes, the `java.*` ones; then comes the extension class loader, which loads classes from extension JARs located in the JRE installation directory; last comes the application class loader, responsible to load the application classes. These loaders are related to each other in a hierarchy where the extension class loader is parent of the application class loader, and the root one is parent of the extension one.

Regular Java applications use the *parent-first class loader delegation model*: when a class loader is requested to load a class, it first asks its parent, and only if it can't load the class, it will try to do it itself. This means that when a class is requested to be loaded by the application, the application class loader is asked first: this one first checks with the extension loader to see if it can load the class, and in turn the latter asks the root loader; the first finding the class, loads it. This way, it's impossible to overwrite the root classes, because they will always be the first ones to be loaded.

This model, however, poses some problems when it comes to Web applications. According to the Java EE specification, Web application are run by a Web container, or application server: this component is a Java application in its own rights, and as such it will use its own classes and libraries. However, a Web container is designed to handle many different Web applications: each one of them can be regarded as another Java application with its own classes and libraries. A very common problem here is that the same library, with different versions, is used by more than one of these coexisting Web application, or even also by the application server itself.

For this reason, Java EE applications need to use the *parent-last class loader delegation model*. Web containers are requested to assign each Web application it's own class loader: the regular container class loader is parent of each application's class loader. Unlike the previous model, though, when an application class loader is required to load a class, it asks its parent (the container class loader) only if it cannot load it: this way if an application is shipped with a specific version of a library, its classes are always guaranteed to be loaded first, and thus with the correct version. The only exception to this rule regards core Java classes, that are still loaded by the root class loader first, to avoid security issues with someone trying to override core Java classes. Additionally, since each application class loader is child of the server one, there is no risk that an application loads a class from another one.



### References
- "Professional Java for Web Applications", by Williams, 2014.
