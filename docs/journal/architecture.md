# Architecture

One of the reasons for this project is experimenting with interesting architectural solutions. In particular, I'll try
here to follow the guidelines of the [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html).


## Overview

The most basic setup for the Clean Architecture includes three distinct layers: the `domain`, the `use cases` and the
`adapters`. Since the main objective of Clean Architecture is allowing multiple different ways of communicating with the
same application, we are using a multi-module Maven setup: this is needed because a single Maven configuration (POM)
will generate only one artifact, while we want to generate several of them, according to the situation, for example one
or more WARs for Web applications (API or HTML applications), and one or more JARs (command-line applications, etc.).

It turns out that Maven modules play quite well with the decoupling suggested by Clean Architecture. In our case we are
starting with the following modules:
- `jgroph-core`: contains both use cases (`net.slc.jgroph.application`) and domain (`net.slc.jgroph.domain`), for now
(in the future they could be separated in two different modules). This module doesn't depend on any other module.
- `jgroph-infrastructure`: contains custom infrastructural elements, which is currently only the container. This module
 doesn't depend on any other module.
- `jgroph-inmemorystorage`: this is one of the input ports, fetching data from databases, search engines, etc. This
depends only on the core module
- `jgroph-application`: this is a very simple module containing all the necessary wiring. Since the various layers use
interfaces to communicate to each other, we need a component that decides which instances need to be injected in place
of every interface. This depends on the infrastructure, since it uses the container, and on input ports, but not on
output ports. This is because output ports may need to reference input ports (think of a Web controller needing to
inject the storage instance into the use case service), but the opposite never happens.
- `jgroph-api`: this is one of the output ports, which are the ones with which the end user actually interacts. In this
case it's a Web API, depending on the core, infrastructure and application.

While applying the DIP ensures that the three main layers (adapters, application and domain) stay as decoupled as
possible, to prevent different adapters from knowing about each other (which is important to easily swap them around),
we use the DI container. This, however, needs to define a centralized place where all implementations are bound to the
required interfaces.

For example, the Web adapter will need to build a `Repository`, which is an application interface,
without knowing that it will really be an in-memory repository implementation: this means that we have to place
somewhere the binding between `Repository` and the chosen implementation.

This centralized service will then need to be loaded immediately at the application startup, and the container to be
provided to all adapters from there. The problem here is that there can be multiple places where the startup happens,
like Web servlets, or the main class of a console application: the same bootstrap procedure will then need to be
replicated in all these places.

Having modularized the application allows us to easily reach this goal. The application module is the centralized place
where all the wiring happens. When a new output port is added, like a command line interface, it will just need to
require the application module, and it'll inherit the same wiring that the other output ports are using.


### Infrastructure

In the traditional layered architecture, the infrastructure is where all modules dealing with specific implementations
of services and components reside. For example, here we find modules to connect to a MySQL database, or to use the
Tomcat application server.

Usually, this layer is almost fully implemented as third party libraries, so that the existence of a `infrastructure`
directory is not always necessarily justified. However, for this project I wanted to avoid using third parties wherever
possible, meaning that some infrastructural module will be built as part of the project.

#### DI container
With a DI container we can avoid all the boilerplate related to pass factories around to maintain different services
oblivious of which actual implementations they will be created with. In particular, being able to automatically create
all dependencies given only their interface can allow adapters to be independent from one another: for example, the
Web adapter will only know that a `Repository` interface is needed, because it's defined in the application, but it
won't have to know of the existence of an in-memory storage, which will be the one actually providing the repository
implementation.

In the spirit of the project, only a very simple DI container has been created, and stored as an infrastructural tool,
as if it was just another third party library.


### Adapters

The `adapters` layer contains all technological details and choices, that will be used by the application, without it
knowing about any specific choice, by using proper interfaces and the Dependency Inversion Principle wherever necessary.

#### Tests
This is the first way we'll use to interact with the application. These are also the only exception to the directory
structure we are defining, since it's just easier to follow the Maven standard, and place all tests under the `test`
directory, next to the `main` one.

#### Web
The idea of jGroph was born as a Web application, so there should be a Web interface to it. However, with this
architecture we'd like to shift the focus of the project from Web-centric, to application-centric, so that what's really
important is the application (and domain) logic, while the fact that it can be consumed as a Web application should
became a detail. In traditional, single-module, Maven application, we would have to choose the Web as the only interface
of our application, since a single Maven configuration can produce only one artifact. However, with a Maven multi-module
setup, we can separate all Web details from the application logic, in its own sub-module, so that the core project stays
clean of any Web reference (or any other adapter reference in general).

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