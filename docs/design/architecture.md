# Architecture

The Bookmarks Services Context implements the Bookmarks Services Subdomain of the Bookmarks Domain. Bookmarks Services provides generic features for managing bookmarks, independently from any actual client the end users or systems will directly use. For example, the Bookmarks Web Subdomain can provide a Web user interface allowing users to perform operations on bookmarks from their browser, but for doing so it will delegate all actual operations to the Bookmarks Services Subdomain.


## Use cases and ports

@todo: add missing use cases


### List All Bookmarks

This is a development-only, temporary use case, since a realistic use case listing all bookmarks will need some kind of pagination. In the first iterations, however, we'll be fine creating a use case just listing all bookmarks, to be used as groundwork for the following, more usable version.

List All Bookmarks will be a read-only, synchronous operation: this means that it will be exposed both as SDK (thus a single-process installation will just load the proper classes and add the correct configuration), and behind a synchronous service, that for learning purposes can easily be both a HTTP REST service, and an AMQP service providing synchronous client-server functionality, like ZeroMQ. On the other side, List All Bookmarks will use the query model, abstracting the denormalized storage. This is the ports layout then:

Primary ports:
- SDK
- HTTP REST
- Synchronous AMQP

Secondary ports:
- Query Model


## Code layout

The Bookmarks Services Context will need at least one module per port: single-process clients will need to just include the core code as a JAR, with no extra adapters, for example to handle HTTP or AMQP. Clients communicating through the HTTP REST port will need a Web server to talk to, an thus the Bookmarks Services Context will need to produce a WAR to be loaded into a servlet container. Again, communication through AMQP will need yet a different artifact.

To keep application concerns (use cases) well distinct from domain modeling, we'll also split the two into different modules. Additional modules may be added if needed, for example to host infrastructural components that don't belong to adapters, use cases nor the domain.


## Systems deployment

Bookmarks Services won't have any responsibility concerning scalability and performances: these will be handled separately on each client's side, since different clients will have different requirements on this matter. This means that Bookmarks Services will be deployed in a single server, with no replication and no caching. Communication with clients will happen through ports like HTTP or AMQP services, thus allowing clients to reside on different servers, as well as the same as Bookmarks Services.