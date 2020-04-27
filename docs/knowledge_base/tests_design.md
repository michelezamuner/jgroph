# Tests design

## Unit testing with dependencies

### Ensuring that the real dependencies are passed along

A typical data processing flow will involve multiple different components, laid out in a pipeline where at each stage data is given as input to a component, and come out, possibly modified, from that component, to be fed again to the next one in line. An example of this is the processing of Web requests, that will comprise the following steps:
- a request, like `GET /bookmarks/1` is received by a router component (for example a servlet)
- the router analyzes the request and creates a controller accordingly, like `BookmarksController`
- the required request data is sent to the proper controller's action, like `controller.show(1)`
- inside the action, a use case, like `GetSingleBookmark`, is built with all its dependencies, like `BookmarksPresenter` and `BookmarksQueryProcessor`; the presenter, in particular, takes the response object as dependency
- the required request data is passed to the use case, like `useCase.perform(1)`

Now we'd like to describe how these steps should properly be unit tested:
- the router's unit tests will need to ensure that the correct controller is created for a given request, and that the correct action is called with the correct parameters
- the controller's unit tests will need to ensure that the correct use case is created for the given action, with the correct dependencies, and that the correct use case method is called with the correct parameters
- the use case's unit tests will need to ensure that the presenter and the query processor are called with the correct data
- the presenter's unit tests will need to ensure that the correct presentation logic is performed with the given data

We can distinguish two cases here: when the component is creating its dependencies (this happens in the router and the controller), and when the dependencies are injected. The first kind of components has the additional responsibility of ensuring that the correct data flow is happening, meaning that the correct dependencies are created from the given input data: for example, the controller that is created by the servlet must depend on the given request; also, the presenter that is created by the controller must be injected with the original response object. Components with injected dependencies, then, can just trust that the dependencies they'll be given in the real runtime are the correct one.


### Unit testing components creating dependencies

Components that create new objects inside them cannot be properly unit tested, because we cannot mock the dependencies. What we could do in this situation is relying on factories, for example:
```java
final Presenter presenter = factory.createPresenter(response);
final UseCase useCase = factory.createUseCase(presenter);
final Controller controller = new Controller(useCase);
controller.show(id);
```

then we can just mock the factory, and inject the mock in the router to control which actual objects will be created. This approach, though, has a big problem: we cannot ensure that the correct dependencies are created anymore. For example, in the test we instruct the factory mock so that when `factory.createPresenter(response)` is called, the right presenter is returned: but who's checking that this correct association is also happening in the real execution? The factory may be discarding the original response, and create the presenter with a new, different, response object. To be sure that the correct behavior is implemented, we'd need to write a unit test for the factory to ensure this, but this would cause the same original problem again, since factories create new objects by definition.

Of course this kind of connection tests are handled by integration tests, but integration tests cannot cover all possible scenarios. Furthermore, there actually is an alternative technique allowing to write unit tests with mocks, and verifying that the data flow is correct at the same time, and that is using a dependency injection container.

The DI container is proved (through its own unit tests) to always work the same way: building objects of the given class, with the given dependencies, or returning objects from a pool of pre-defined objects. The second part of the story, when using DI containers, are service providers, which are the places where the binding between classes, dependencies, and produced objects are built. The providers can also be unit tested, to check that the container is used to create the right objects, and to apply the right bindings.

This way we move the problem of ensuring that the right bindings between dependencies are created, from the actual components to a centralized point, which are service providers, and all parts of the production chain (providers, container, and components) can be properly unit tested.


## Unit testing asynchronous code

Asynchronous code relies upon some infrastructure providing the actual asynchronous mechanism (like an event loop). From inside unit tests, we of course don't want to wait for asynchronous events to happen, and thus we have to remove the actual asynchronous behavior, while still keeping its interface in the SUT.

Usually the asynchronous mechanism is encapsulated inside some object that is used by the client (like a `loop` object on which you can register event handlers and stuff). Thus, we can just mock this object, telling it to call our handlers as soon as its execution is started.

Inside jGroph's asynchronous server implementation, we're using Java NIO's `AsynchronousServerSocketChannel` and `AsynchronousSocketChannel` as providers of the asynchronous mechanism. For example, `AsynchronousSocketChannel.read()` takes a callback (`CompletionHandler`) that is called after a message has been read from a connected client.

The naive testing solution here would be to just inject the read callback to our `Client` class, that would then be passed to `AsynchronousSocketChannel.read()`: this way we could write a unit test verifying that the callback is indeed been passed to the channel (we'd have to inject the channel as well), and another unit test verifying that the callback is doing what's expected when it's called.

This, however, leads very quickly to a mess of multiple dependencies injected into our constructors, as well as multiple factories as well, every time a dependency must be built out of information that is not available outside of a class.

There's a way, however, to avoid defining callbacks in their own classes, and keep everything inside the same class, while still allowing full-depth unit testing. For this to be possible, we just have to mock the asynchronous provider, so that when it's started, the callbacks are called immediately, instead of after waiting for the real asynchronous event to happen.

For example, let's say that we have a `channel.read(callback)` code: in the real situation, `callback.call()` will be called only after the client's message have actually been received, thus the moment we call `channel.read()` is not the same moment `callback.call()` is called. However, if we have a mock of `channel`, we can instruct it so that as soon as `channel.read()` is called, `callback.call()` is called as well.

From a logic perspective, we are not actually losing the asynchronous behavior, because the way the communication is performed, as well as the actors relationships, never changed. The only difference is that the time passed from when the asynchronous provider started, to when the callback was called, has been shrunk from non zero, to zero, like if we had a physical infrastructure so fast that the communication between client and server was instantaneous: the communication would still be asynchronous, because this is how it has been setup.

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

Here, our `client` registers a success callback, and expects it to be called with the right `message`. `client.read` calls in turn `channel.read`, passing to it a `CompletionHandler` that is built from inside the client (as we said previously, it doesn't need to be extracted to its own class), and which in turn calls the success callback. What we want to test here, is that the `CompletionHandler` pass the right message to the callback. To do so, we need that the handler is called as soon as `client.read` is called, because we cannot wait for the real asynchronous event (because it will never be fired).

For this to happen, we instruct our `channel` mock so that when `read` is called, the buffer passed as first argument is filled with the real message (which is defined in the unit test, outside of the closure), and the handler passed as fifth argument is immediately called, passing the actual client to it, as if the asynchronous event was fired instantaneously.

As a side note, notice that we also call `doNothing()` on the mock. This means the following: the first time that `channel.read` is called, execute the closure passed to `doAnswer()`; all subsequent times, do nothing. This is necessary because `channel.read`, after having taken the current message from the client, calls `client.read` again, recursively, to wait for the next message coming from the client. If we didn't put `doNothing()`, the closure passed to `doAnswer` would have been called again and again in an infinite recursive loop, producing a stack overflow eventually.


## On using Mockito's `@injectMocks`

Mockito provides the `@injectMocks` annotation to be applied to the variable of a test case class containing the system under test, so that the proper dependencies are injected into it automatically during each test's setup, either using constructor injection, or setter injection, or property injection:
```java
public class ServiceTest
{
    @Mock private Dependency dependency;
    // Service requires a Dependency instance to be injected
    @InjectMocks private Service service;

    @Test
    public void testService()
    {
        // here service has already been constructed with a mock dependency
    }
}
```

This feature, however, has two problems. The first one is that if a required dependency cannot be found, Mockito will just silently inject `null` instead of raising an error: this will result in `NullPointerException`s in unexpected places if we happen to incorrectly configure mocks. The second problem is related to the fact that the injection is done before the setup method (the one marked with the `@Before` annotation, that is executed before each test). This leads to problems in case mocks are used in the constructor:
```java
public class ServiceTest
{
    @Mock private Dependency dependency;
    @InjectMocks private Service service;

    @Before
    public void setUp()
    {
        when(dependency.getValue()).thenReturn(someValue);
    }
}

public class Service
{
    public Service(final Dependency dependency)
    {
        // do something with dependency.getValue();
    }
}
```

Here `dependency.getValue()` is mocked only *after* the SUT has been constructed, meaning that inside the SUT constructor `dependency.getValue()` still returns `null`, and this can still lead to NPEs.

Using `@InjectMocks` is, after all, just a minor enhancement, since it can usually be replaced with one line in the test setup method, reason enough to avoid using it, and construct the SUT explicitly instead:
```java
public class ServiceTest
{
    @Mock private Dependency dependency;
    private Service service;

    @Before
    public void setUp()
    {
        when(dependency.getValue()).thenReturn(someValue);

        service = new Service(dependency);
    }
}
```


#### References
- https://tedvinke.wordpress.com/2014/02/13/mockito-why-you-should-not-use-injectmocks-annotation-to-autowire-fields/