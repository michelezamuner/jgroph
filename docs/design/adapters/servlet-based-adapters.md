# Servlet-based Adapters


## Optimization of dependency construction in servlets

Servlets are created by the application container only once (either at the startup of the container, or at the first incoming request, depending on the configuration), and at that time the `init()` method of the servlet is called. On the other hand, the `service()` method (and all related ones, like `doGet()`, etc.) are called once for each incoming request.

Dependencies that don't depend on the current request should be created only once, at the servlet creation, and persisted across multiple different requests, to avoid incurring in the performance penalty of re-creating them each time. These dependencies can be built once and for all inside the servlet's `init()` method, and provided to the application by means of the DI container. Dependencies depending on the current request and/or response, on the other hand, will need to be created anew upon each request.

Of course it would be better to limit the number of dependencies that need to be re-created each time to the minimum possible. For example, presenters depend on the actual response, and use cases depend on the current presenter, so new versions of them have to be instantiated at each request, even if the same presentation and service logic is requested, just with different input data.

However, this problem is actually important only if we are dealing with dependencies carrying heavy states, like big strings. Dependencies that are stateless services (including services carrying other services as "state"), are so lightweight that we can avoid worrying too much about having to create them repeatedly.