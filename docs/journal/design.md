# Design


## Servlets and controllers

It's possible to unit test servlets if they are explicitly constructed inside a context listener, instead of being
automatically created by the servlet container. Now, it's interesting to see how they can be effectively tested, in
particular within a Clean Architecture.

To better identify the responsibilities, we added a controller class containing the usual CRUD actions, and left the
responsibility of routing the current request to the correct controller and action to the servlet.


### What logic should controllers contain

Since controllers belong to the adapters layer, they are just a thin layer that converts data from the format used by
the adapter, to the format needed by the application, and then call the correct use case with this data. This means that
all operations are delegated to the use case, included updating the response object (which is actually done by the
adapter's presenter, which is then called by the use case though):

```java
final Presenter presenter = new Presenter(response);
final UseCase useCase = new UseCase(presenter);
final useCase.perform(this.getValueFromRequest(request));
```

It's important to understand which functionality should be tested as part of the controller, and which as part of the
other components. The logic of the controller, as seen from the outside, is the following:
- a request, like `GET /1`, is received
- a proper response, like `{"id": 1, "title": "Title 1"}`, is returned

Diving a bit deeper, we have:
- the request `GET /1` is converted by the controller (by the servlet and the controller, really) to a set of parameters
needed by the use case: `#id = 1`
- the use case is executed with the needed request parameters, `#id = 1`
- during the execution of the use case, the presenter is called to update the response with the proper output data

The first feature should surely be tested by the controller unit test, because it's its responsibility to convert the
data from the format of the adapter (the request), to the format of the application (the arguments). Also the second
feature should be tested here, because it's the controller responsibility to ensure that the use case is called with the
proper arguments.

However, it's not the responsibility of the controller to ensure that the use case properly calls the presenter:
this would already be tested by the unit test of the use case. For the same reason, the controller should not concern
itself with whether the presenter is properly updating the response or not, because this would be tested by the unit
test of the presenter itself.


### Testing that the real request and response are used

Something we didn't test yet is the fact that the actual input and output are injected to the components. In other
words, the unit tests of the components ensure that if the proper input is given, then the proper output (or side
effects) is produced; however, they cannot verify that the proper input is indeed provided: this should be taken care of
by the servlet test (since the controller actions just get the data they need, and not the original request and
response).

For example, the unit test of the presenter can ensure that the response (injected into the presenter during its
construction) will properly be updated. However, what if I construct the presenter with a response object different
than the actual one coming from the application server? The servlet must ensure that the actual response is used to
create the presenter.

In the same way, the use case can ensure that the presenter (injected during the construction) is called to present the
output data: however it cannot tell whether that was the right presenter to use. The servlet must ensure that the right
presenter (which is the one created from the actual response) will be used to construct the use case.

These are, in summary, the things that need to be tested by the servlet unit test:
- that the request is converted to the arguments required by the controller action (and thus that the use case is using
the actual request)
- that the presenter is built from the actual response
- that the use case is built from the real presenter

Notice that we are not testing anything at all about what will be written in the response: for example, we are not
verifying that if the request is `/1`, then the response will contain `{"id": 1, "title": "Title 1"}`. We could do that,
but it would be redundant, since it follows logically after we have made sure that the unit tests of the servlet, of the
controller, of the use case, and of the presenter are passing: if the servlet and controller are correct, then the use
case will be properly called with the proper presenter, which has the proper response; if the use case is correct, then
it will call the proper presenter; finally, if the presenter is correct, it will properly update the response.

Furthermore, it would be impossible to check that the response has the correct body after the servlet call, because the
use case is mocked, hence it doesn't call the presenter, and even if it did, the presenter itself is mocked, so it
doesn't update the response. The only way would be to mock the response so that it had the proper body, and then verify
that the response had the proper body, which is of course pointless.


### How a DI container helps unit testing

Now, since we want to make a unit test of the servlet, we need to mock all dependencies: this means that, since the
servlet needs to construct several components, we have to rely on a factory that is injected into the servlet, and used
to create the dependencies without knowing their actual classes in advance (so that they can be easily replaced with
mocks).

``` java
final Presenter presenter = factory.createPresenter(response);
final UseCase useCase = factory.createUsecase(presenter);
final Controller controller = new Controller(useCase);
controller.show(id);
```

This additional intermediate object will pose some issues when it comes to ensure that the right dependencies are
passed to the right components. For example, from the servlet unit test I can ensure that the real response is passed
to the `createPresenter()` factory method: however, how can I be sure that that method is actually using the real
response to build the presenter it's returning, instead of discarding it and constructing a presenter with some other
response object?

This of course should be taken care of by a factory unit test. The problem, here, is that we would need to test it
without using mocks, because the whole point of the factory is creating new instances, and this could be a problem:
how can we assert that the request argument is passed to the presenter constructor for example? Of course we could spy
the constructor, but this is bad unit test practice, since the constructor is not part of the interface we want to test.

To overcome this problem, we use a DI container. A DI container is an object that builds the objects of the class we
request, which are either created from scratch (like calling `new`), or returned from a pool of objects that have been
bound at the beginning. Dependencies are automatically recognized and built, or injected from the ones passed
from the outside.

Now, the point of all this is that the DI container has its own unit tests, that ensure that the proper objects are
constructed, with the proper dependencies. In particular, since we are testing a generic component, we use it to build
test classes that are not part of the project, and thus they can have getters to check that the dependencies that have
been passed are the real ones (we wouldn't have wanted to add unused getters to project's classes just for testing
purposes).

Using the DI container in place of the factory, then, allows us to reach full unit test coverage, including also the
construction of the dependencies.