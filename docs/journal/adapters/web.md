# Web Adapter

The default interface to jGroph is a Web application, which will be served over the Jetty Application Server.

Jetty can serve multiple applications on different Web paths. However, in our case we only want to serve one
application, so everything will be served from the root path. Doing this in Jetty is very easy, because it's just a
matter of providing a `ROOT.war` or a `ROOT` folder inside the `webapps` folder. If you just want to serve static
contents, just go with the `ROOT` folder; otherwise, you have to create a WAR file.

To provide this in the production environment, we follow the example of the Jetty OpenShift repo, which adds a
`openshift` profile to `pom.xml` defining how to build `ROOT.war` in the `deployments` project folder, which is then
symlinked inside the Jetty installation folder, as `webapps`.

To work locally, it's just easier to use the Maven Jetty Plugin, so I installed it. With that in place, you can start 
Jetty server locally with just `mvn jetty:run`: this will also monitor your project's file and rebuild the application
each time a file change is detected, allowing you not to have to repeatedly restart the server. This feature, however,
is only limited to the Web files, meaning the files under `webapp`. Having the web app being automatically redeployed at
every file change is a bit trickier. First, we have to enable the file scan, setting the `scanIntervalSeconds`
configuration property of the Jetty Maven plugin at a certain number of seconds (for example, `1`), and restart the
Jetty server to load the new configuration. From now on, each time we rebuild the java files, for example with
`mvn:compile`, the Jetty plugin will automatically detect the code changes and restart the server.


## Configuring servlets

To use Jetty as a servlet container, we must provide servlets inside the WAR archive. There are two ways of configuring
servlets. The first, and traditional one, working with all versions of the Servlet API, is properly configuring the
`WEB-INF/web.xml` file, for instance with:
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


### Using annotations

If you're using the Servlet API 3.0 or greater, you can make use of the annotations API, that makes for a quite simpler
configuration. In this case, `web.xml` can be empty, provided that you specify that you're using the version 3 of the
Servlet API:
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

and all the servlet configuration is added through annotations in the servlet class definition itself:
```java
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

However, configuring servlets through annotations has some drawbacks, especially related to the fact that servlet
configurations should be defined inside the `web.xml` deployment descriptor (because it doesn't make sense to write
configurations in the source), and to do this we need to also define the servlet there. However, it's also possible to
use both: for instance using annotations to define the URL mapping only.


### Servlets and dependency injection

Normally servlets are automatically constructed by the application server, making it impossible to inject dependencies
in the constructor, and, by extension, to properly unit test them with mock objects. To overcome this problem, it's
possible to register a listener to the servlet context events. For example, we can hook to the context initialization
event, and add custom servlets to the context, instead of let the default servlet configuration do it.

The listener class must implement `ServletContextListener`, and can be configured to listen to the servlet events using
the `WebListener` annotation:

```java
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Application implements ServletContextListener
{
    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        Object dependency = getDependency();
        
        final ServletContext context = event.getServletContext();
        context.addServlet("servlet-name", new MyServlet(dependency)).addMapping("/*");
    }
    
    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {
        
    }
}
```

`ServletContextListener` requires to implement the two methods `contextInitialized()` and `contextDestroyed()`. The
servlet context of the application can be retrieved from the event that is passed as argument to these methods. From
the `contextInitialized()` method, then, we can explicitly construct the servlets we need, thus injecting all
dependencies required, and register them to the context.

This way of registering servlets has lower precedence than the default configuration: for example, if we used the
`WebServlet` annotation on the servlet class, that class would have been automatically created by the application
server, and *not* by our listener. Thus, we should check that the servlet we want to build inside the listener is not
already configured to be created by the application server. If the servlets are located inside the same package as the
listener, we could for example make them package-level visible (so using no visibility qualifier, instead of `public`,
in the class definition).