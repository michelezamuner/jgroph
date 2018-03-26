[![Build Status](https://travis-ci.org/michelezamuner/jgroph.svg?branch=master)](https://travis-ci.org/michelezamuner/jgroph)

# jGroph Bookmarks Services

*Groph* is a Web application to manage bookmarks, and the go-to playground to experiment with different technologies, programming techniques and design solutions. This time it's written in Java, thus the *j*.

The Bookmarks Services is the component providing services to manage bookmarks independently from any kind of client using them.


## Design

Being an experimental and learning project, design will likely change in time, but the main idea will be to base it on Domain-Driven Design and CQRS concepts. The main Bounded Contexts that can be found are:
- Bookmarks Services: services to manage bookmarks independently from clients
- Web: Web application providing features to manage bookmarks to human users
- API: HTTP REST client to expose bookmarks services to machine-like clients

More contexts may be created for various reasons. Additional detailed information can be found in the proper [design documents](design/).

Every part of the project is being built following the tenets of Test-Driven Design. In particular:
- integration tests are written, stating what the desired functionality will be from a end-user perspective. This usually involves spinning up real servers, and making real local calls. This is the "outer" cycle of Test-Driven Development, and these tests never pass, except for when the feature is completed, at which time development on it is stopped
- unit tests are written for the classes that are deemed necessary, the most simple implementation to make tests pass is created, then refactoring and additional tests are written. This is the "inner" red-green-refactor cycle of Test-Driven Development
- other changes to production code are not added unless there's a failing test requiring them, as usual

Particular care has been put into code quality, also to check which tools were offered by Java's landscape in this regard. Continuous integration is taken care by the Travis CI service, linked to the GitHub repository, so that the pipeline is executed at every push and PR merge, including running tests with code coverage, and code quality tools; errors detected at any step will make the build fail, and prevent the code from being merged.


## Development process

The core idea behind the development process of Groph is borrowed from MVP and Agile. Being a personal project on which I can work only sparingly, I had to face two basic issues:
- lack of development time
- risk of losing focus/motivation after long periods of inactivity

I realized I could fight these hurdles by partitioning the development into small iterations, each of which would be focused only on one simple feature or improvement I could complete in a small amount of hours.

Focusing on small tasks, I can keep my motivation up, since at all times I feel I'm just about to deliver some kind of result, while avoiding being stuck on giant, over-complicated stuff that take forever to be done, and keep spitting bugs out.

This, in addition to the project being fully test-driven developed, makes me confident that I can leave it there for some time, and always find a working project with a well-defined set of developed features any time I come back, thus avoiding the situation where I can't remember what the hell I was doing the last time I was working on it.
