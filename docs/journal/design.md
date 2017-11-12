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


## On null handling

Java allows `null` to be used in place of any non-primitive value: this means that any method argument, and any method
return value might be `null`, even when that's not expected. If it's expected that a field or parameter might have no
value, it's best to use `Optional`s (even if the use cases of `null` and `Optional` don't fully overlap). If a value
must always be there, instead, we have to ensure that this is really the case.

There are generally two ways to ensure that a value is not `null`: either check it at the caller, or check it at the
callee. For example:
```java
// caller
Type value = getValue();
object.method(value);
```
```java
// callee
public void method(Type value)
{
    // do something with value
}
```

In this example, we could check that `value` returned by `getValue()` is not `null`, before passing it to `method()`, or
we could check that `value` inside `method()` is not `null`. From practical purposes there is no difference, because
the error is spot at the same time. However, of course it's pointless and wasteful to duplicate the same check in every
place; furthermore, from a code readability perspective it's also better to avoid cluttering the code with `null` checks
everywhere.

From this I gather the following heuristic:
- since the response we'll have when finding a `null` would be throwing a `NullPointerException`, it's pointless to do
the check if the value is going to be used within the same method, or methods of the same class immediately called by
it, because Java itself is going to throw a `NullPointerException` anyway. Thus, only bother with checking if the
value that might be `null` will be passed to other objects, or stored to be used at a later time, because in that case
it will be possible to actually fail faster.
- if an object is going to be used a lot (entities and value objects for example), place the `null` check only once
inside the method, rather than on all the callers that will use it, unless you have full control of how these objects
are constructed (for example, not from user or database data), and you can prove that values will never be `null`.
- if an object is going to be used only once, or a few times anyway (general service classes like controllers, views,
database wrappers, etc.), place the `null` check on the caller: this has the benefit of moving all checks up the stack,
far from the lower layers that are mostly concerned with application and domain logic.
- avoid adding `null` checks if you can logically prove that those values cannot possibly be `null`, because you are
in control of the whole call stack. This applies to private methods, or to values not coming from external parties
(user, database, etc.). Entities and value objects are used so much that it's generally hard to be sure that they will
never be constructed from external values, so the rule of checking `null`s inside them still applies.

Adding too many `null` checks could have a negative impact on the performance as well, and this is another reason to
prefer using logic inference to prove that values cannot be `null`, or leveraging static analysis, rather than adding
checks.


## Test design

### Unit testing asynchronous code

Asynchronous code relies upon some infrastructure providing the actual asynchronous mechanism (like an event loop). From
inside unit tests, we of course don't want to wait for asynchronous events to happen, and thus we have to remove the
actual asynchronous behavior, while still keeping its interface in the SUT.

Usually the asynchronous mechanism is encapsulated inside some object that is used by the client (like a `loop` object
on which you can register event handlers and stuff). Thus, we can just mock this object, telling it to call our handlers
as soon as its execution is started.

Inside jGroph's asynchronous server implementation, we're using Java NIO's `AsynchronousServerSocketChannel` and
`AsynchronousSocketChannel` as providers of the asynchronous mechanism. For example, `AsynchronousSocketChannel.read()`
takes a callback (`CompletionHandler`) that is called after a message has been read from a connected client.

The naive testing solution here would be to just inject the read callback to our `Client` class, that would then be
passed to `AsynchronousSocketChannel.read()`: this way we could write a unit test verifying that the callback is indeed
been passed to the channel (we'd have to inject the channel as well), and another unit test verifying that the callback
is doing what's expected when it's called.

This, however, leads very quickly to a mess of multiple dependencies injected into our constructors, as well as multiple
factories as well, every time a dependency must be built out of information that is not available outside of a class.

There's a way, however, to avoid defining callbacks in their own classes, and keep everything inside the same class,
while still allowing full-depth unit testing. For this to be possible, we just have to mock the asynchronous provider,
so that when it's started, the callbacks are called immediately, instead of after waiting for the real asynchronous
event to happen.

For example, let's say that we have a `channel.read(callback)` code: in the real situation, `callback.call()` will be
called only after the client's message have actually been received, thus the moment we call `channel.read()` is not the
same moment `callback.call()` is called. However, if we have a mock of `channel`, we can instruct it so that as soon
as `channel.read()` is called, `callback.call()` is called as well.

From a logic perspective, we are not actually losing the asynchronous behavior, because the way the communication is
performed, as well as the actors relationships, never changed. The only difference is that the time passed from when the
asynchronous provider started, to when the callback was called, has been shrunk from non zero, to zero, like if we had
a physical infrastructure so fast that the communication between client and server was instantaneous: the communication
would still be asynchronous, because this is how it has been setup.

To actually do this inside JUnit tests, we have to do something like this:
```java
doAnswer(invocation -> {
    final ByteBuffer buf = invocation.getArgument(0);
    final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);

    buf.put(message.getBytes(UTF_8));
    handler.completed(0, client);
    return null;
}).doNothing().when(channel).read(
        eq(buffer),
        eq(0L),
        eq(null),
        eq(client),
        any(CompletionHandler.class)
);

final Consumer<String> onSuccess = mock(Consumer.class);
client.read(onSuccess, mock(Consumer.class));

verify(onSuccess).accept(message);
```

Here, our `client` registers a success callback, and expects it to be called with the right `message`. `client.read`
calls in turn `channel.read`, passing to it a `CompletionHandler` that is built from inside the client (as we said
previously, it doesn't need to be extracted to its own class), and which in turn calls the success callback. What we
want to test here, is that the `CompletionHandler` pass the right message to the callback. To do so, we need that the
handler is called as soon as `client.read` is called, because we cannot wait for the real asynchronous event (because
it will never be fired).

For this to happen, we instruct our `channel` mock so that when `read` is called, the buffer passed as first argument
is filled with the real message (which is defined in the unit test, outside of the closure), and the handler passed
as fifth argument is immediately called, passing the actual client to it, as if the asynchronous event was fired
instantaneously.

As a side note, notice that we also call `doNothing()` on the mock. This means the following: the first time that
`channel.read` is called, execute the closure passed to `doAnswer()`; all subsequent times, do nothing. This is
necessary because `channel.read`, after having taken the current message from the client, calls `client.read` again,
recursively, to wait for the next message coming from the client. If we didn't put `doNothing()`, the closure passed to
`doAnswer` would have been called again and again in an infinite recursive loop, producing a stack overflow eventually.


## Handling exceptions thrown from a separate thread

The integration tests of the asynchronous server work by spawning a new server from inside a separate thread, and then
connecting to it with a client from the main thread. However, if an exception is thrown from inside the separate thread,
and is unhandled, the thread just dies giving no notice, which makes debugging test failures quite hard.

To overcome this problem, we create a `private volatile Throwable exception` property of the test class, which is meant
to be available on all threads, and put the whole code of the separate thread inside a `try/catch` block. When an
exception is caught, we assign its reference to the `exception` property, so that it's available also from outside the
thread. Then, after waiting for the client connecting to the server, we check if `exception` is not null, in which case
we throw the exception from the main thread, making the test fail as early as possible.