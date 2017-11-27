[![Build Status](https://travis-ci.org/michelezamuner/jgroph.svg?branch=master)](https://travis-ci.org/michelezamuner/jgroph)

# jGroph

*Groph* is a Web application I built to manage bookmarks, the way I like it to be done. More than this, however, Groph
is the go-to playground to experiment with different technologies, programming techniques and design solutions. This
iteration, it's Java and Clean Architecture, thus the *j*.

## Application functionality

Since the most interesting thing about this project is the opportunity to experiment with back-end technologies and
design/architecture, the functionality of the application is kept to a minimum, as well as the shininess of the
front-end.

With jGroph I can store *Resources*, which are bookmarks referencing Web pages by saving their Link and Title. I can
also add any number of *Tags* to each Resource, to help organize them.

The interesting spin here is that Tags also work as categories, meaning that each Tag has a Parent Tag (up to a global
"root" tag), and thus a Resource not only belongs to its immediate Tags, but also to each of their parents.

This allows to create a tidy category tree which can be navigated easily, where each Resource can be located under
multiple categories.

A Search functionality is also provided, to find Resources based on keywords that might be contained in the link or
title.


## Development process

The core idea behind the development process of Groph is borrowed from MVP and Agile. Being a personal project on which
I can work sparingly, I had to face two basic issues:
- lack of development time
- risk of losing focus/motivation after long periods of inactivity

I realized I could fight these hurdles by partitioning the development in small iterations, each of which would be
focused only on one simple feature or improvement I could complete in a small amount of hours.

Focusing on small tasks, I can keep my motivation up, since at all times I feel I'm just about to deliver some kind of
result, while avoiding being stuck on giant, over-complicated stuff that take forever to be done, and keep throwing out
bugs.

This, in addition to the project being fully test-driven developed, makes me confident that I can leave it there for
some time, and always find a working project with a well-defined set of developed features any time I come back, thus
avoiding the situation where I can't remember what the hell I was doing the last time I was working on it.


## Design

A lot of details about design and implementation choices can be found under `docs/journal`, which is continuously
updated as long as the actual development progresses, and thus ends up being a bit chaotic, unlike a proper
documentation that could be prepared for important releases. Given the extremely flexible and experimental nature of the
project, it makes little sense to write a nice and tidy documentation assuming that some long-term decisions have been
taken, while no such thing has clearly ever been done.

A part from being written in Java, however, the main design choice here is the application of the [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html).
The core tenet of this architectural style is the ability to easily allow to reuse the application and domain layers for
very different kinds of adapters, such as a Web and console application. In the case of this project, more than one kind
of primary adapters are intended to be created:
- a REST Web API
- a Web application, possibly leveraging the REST API through AJAX
- a console application
- a so-called "remote console" which is basically an excuse to try to build an asynchronous TCP server, providing Groph
use cases from yet another different adapter
- ...maybe more in the future?

Every part of the project is being built following the tenets of Test-Driven Design. In particular:
- integration tests are written, stating what the desired functionality will be from a end-user perspective. This
usually involves spinning up real servers, and making real local calls. This is the "outer" cycle of Test-Driven
Development, and these tests never pass, except for when the feature is completed, at which time development on it is
stopped
- unit tests are written for the classes that are deemed necessary, the most simple implementation to make tests pass is
created, then refactoring and additional tests are written. This is the "inner" red-green-refactor cycle of Test-Driven
Development
- other changes to production code are not added unless there's a failing test requiring them, as usual

Particular care has been put into code quality, also to check which tools were offered by Java's landscape in this
regard. Continuous integration is taken care by the Travis CI service, linked to the GitHub repository, so that the
pipeline is executed at every push and PR merge, including the following tasks:
- unit tests are run
- integration tests are run
- code coverage is collected from unit tests, and a minimum coverage is enforced
- mutation tests are run
- the Findbugs tool is run
- the PMD tool is run

errors detected at any of these steps will make the build fail, and thus prevent the code from being merged.