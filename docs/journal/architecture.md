# Architecture

One of the reasons for this project is experimenting with interesting architectural solutions. In particular, I'll try
here to follow the guidelines of the
[Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html).


## Overview

As far as the source files organization is concerned, we'll have three layers immediately visible at the first level:
`adapters`, `application` and `domain`.

In addition to the traditional layers, it's necessary to add a joint class as well, wiring the dependencies among
different adapters and the application. For example, output adapters will create new instances of the use cases on their
own, injecting the specific implementations for the output ports: however, they will also need to inject implementations
for ports they don't own, like the implementation for the storage. Since the storage adapter is different than any
output adapter (like the Web adapter), we need a way to let the output adapter know which implementation to use for
the storage, when creating the use case.

To solve this problem, we provide a global `Application` class, returning the correct instances of the required ports.
Of course, this is a quick solution for the first iterations, that in the future may become something more complex like
a DI container. Each output adapter will be injected with an instance of the application, and will use it to get
instances it doesn't own. This will also allow to easily swap implementations in the future, for example replacing the
storage used.

It's important to notice, also, that the global application will know details of all the adapters (for example) it will
know which storage adapter to use: thus, it must not be placed inside the application layer, but outside of it, like it
was a "meta adapter". This means that each time a new adapter will be created, it will have to use that same application
class to gather the required external dependencies.


### Adapters

The `adapters` layer contains all technological details and choices, that will be used by the application, without it
knowing about any specific choice, by means of proper interfaces and the Dependency Inversion Principle wherever
necessary.

#### Tests
This is the first way we'll use to interact with the application. These are also the only exception to the directory
structure we are defining, since it's just easier to follow the Maven standard, and place all tests under the `test`
directory, next to the `main` one.

#### Web
The idea of jGroph was born as a Web application, so there should be a Web interface to it. However, with this
architecture we'd like to shift the focus of the project from Web-centric, to application-centric, so that what's really
important is the application (and domain) logic, while the fact that it can be consumed as a Web application should
became a detail. However, this cannot cleanly be done with the default Maven structure, since the `webapp` directory
sits at the same level as the `main` directory, clearly stating that this will be a Web application, so we might
consider to implement adapters as Maven sub-modules in the future, so that the core project stays clean of any Web
reference (or any other adapter reference as well).

#### Console
Partly for utility reasons, partly for experimental reasons, it'll be nice to have a console interface to the
application. This will allow to perform low level tasks (like maintenance, or fix stuff) on the application, directly
from the server, and to actually experiment one of the core advantages of the Clean Architecture, that is being able to
build several versions of the same application, differing only by the way to access it, with minimal effort.

#### Remote console
This would be only experimental and instructional, again to showcase the power of the Clean Architecture as far as
decoupling the application from its various user interfaces is concerned. The remote console would be a client-server
frontend to jGroph where a handmade application server will be built, serving the application use cases to the clients
over a custom (and very simple) protocol. It's of no practical use since the Web exists already, but it will be fun to
build.


### Application

Following the tenets of the
[Screaming Architecture](https://8thlight.com/blog/uncle-bob/2011/09/30/Screaming-Architecture.html), we want the real
purpose of our application to be immediately visible and understandable to the reader of the source code. The fact that
a certain presentational pattern (like MVC), or framework (like Spring) is used, should be of no concern, at least at
first, to whom is reading the code. The most important thing to understand at first is, what does this application do,
and this is described by its use cases.

That's why the core `application` directory will only contain use cases (also called "interactors" in the Clean
Architecture parlance), and related interfaces used as ports. The adapters will use these use cases to access the
application functionality, but this is just a detail. The use cases will of course also know and use the domain
entities.


## Domain

The domain layer will contain domain entities, describing the core concepts of the domain, unrelated to any application,
or even software at all, concern. In the case of jGroph, these will likely be very few and simple. They won't know
anything at all regarding the external world, not that there is a Web (or console) application, and not even that there
is a software application at    all.