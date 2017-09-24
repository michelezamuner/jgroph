# Architecture

One of the reasons for this project is experimenting with interesting architectural solutions. In particular, I'll try
here to follow the guidelines of the [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html).


## Overview

As far as the source files organization is concerned, we'll have four layers immediately visible at the first level:
`infrastructure`, `adapters`, `application`, and `domain`, in order of decreasing level of details contained.

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