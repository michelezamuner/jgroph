# Adapters using servlets


## Servlets and MVC

Servlets are always the entry point for each request/response cycle, and they get basic `HttpServletRequest`s as input. These requests might have been already prepared according to the current application's routing schema, for example if some routing have been setup inside `web.xml`, or in a context listener. For example, we could setup different servlets for different roots, like `bookmarks/`, `tags/`, etc. Alternatively, we could use a single servlet, which would then work as the router, parsing requests' data and creating and running controllers accordingly.

The advantage of using multiple servlets is that we leverage some built-in functionality of the servlet container, which would let us implement less functionality, and have better performances. On the other hand, routing configuration would be split across at least two places, because the routing capabilities that can be provided by the servlet container are limited, and some logic must always be provided by the application when it comes to decide how to build controllers.

Using only one servlet as a router constructing controllers has the main advantage of representing the single source of truth about routing handling. In particular, this way it's much easier to add new routes/controllers than having to change the `web.xml` file every time. As a side note, using a single servlet keeps the code structure more simple, because it avoids the need to also define a context listener in order to unit test the servlet (cmp. [notes on servlets](https://github.com/michelezamuner/notes/java/web-applications/servlets.md)). Additionally, if we heavily rely on `web.xml`, we might end up depending on functionality that is limited to a specific servlet container that we are using at a certain moment, making it impossible for us to port the application to a different container at a second time.


## Deployment of Web adapters belonging to different contexts

Web adapters can be found in different bounded contexts of the application. For example, the API and Web contexts will of course need Web adapters, being Web clients themselves, but also the Bookmarks Services context will need a Web adapter, to provide an HTTP API for read-only synchronous communication. Now, according to the deployment strategy we decide to employ, it might happen that these different contexts will be hosted on the same server: in this case, it could make sense to reuse the same servlet container for all three, instead of spawning a new container for each.

When hosting multiple applications on the same container, each application must be assigned a prefix, which will show up as the first part of the request path. For example, API calls would look like `jgroph.domain/api/bookmarks/`, while HTTP service calls would look like `jgroph.domain/services/bookmarks/`. Of course some of these services might need to be hidden from public usage; also, if `jgroph.domain/` is the path where the Web context is exposed, there might be risks of naming conflicts, if the Web application suddenly decides to add a page named `services` or something. A better alternative could be to move the string identifying the type of service to the domain name instead, like `api.jgroph.domain/bookmarks/` and `services.jgroph.domain/bookmarks`, while `jgroph.domain/api/` and `jgroph.domain/services/` might just refer to static pages of the Web application.


## Optimization of dependency construction in servlets

Servlets are created by the application container only once (either at the startup of the container, or at the first incoming request, depending on the configuration), and at that time the `init()` method of the servlet is called. On the other hand, the `service()` method (and all related ones, like `doGet()`, etc.) are called once for each incoming request.

Dependencies that don't depend on the current request should be created only once, at the servlet creation, and persisted across multiple different requests, to avoid incurring in the performance penalty of re-creating them each time. These dependencies can be built once and for all inside the servlet's `init()` method, and provided to the application by means of the DI container. Dependencies depending on the current request and/or response, on the other hand, will need to be created anew upon each request.

Of course it would be better to limit the number of dependencies that need to be re-created each time to the minimum possible. For example, presenters depend on the actual response, and use cases depend on the current presenter, so new versions of them have to be instantiated at each request, even if the same presentation and service logic is requested, just with different input data.

However, this problem is actually important only if we are dealing with dependencies carrying heavy states, like big strings. Dependencies that are stateless services (including services carrying other services as "state"), are so lightweight that we can avoid worrying too much about having to create them repeatedly.