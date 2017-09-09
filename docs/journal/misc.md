# Miscellaneous notes


## Testing that a response has a certain body

In unit test we might need to verify that a response mock is updated with a certain string. For instance, we want to
make sure that the string `"Hello, World!"` is written to the response mock by the class under test:

```java
final ByteArrayOutputStream output = new ByteArrayOutputStream();
final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
final HttpServletRespnse response = mock(HttpServletResponse.class);
when(response.getWriter()).thenReturn(writer);

// additional code...
servlet.service(request, response);
assertEquals("Hello, World!", new String(output.toByteArray(), "UTF-8"));
```


## Ensuring that a mocked method is called with a certain object

When using `verify` we can check that the a method is called with a certain actual argument:

```java
int arg = 1;
verify(object).method(arg);
```

However, this doesn't work when the argument is a more complex object, like a value object, because, even if we build
it the same way it's built by the code under test, the two instances will still be recognized as different, and even
overriding `equals()` won't help. The solution here is using Mockito's `argThat()`:

```java
verify(presenter).show(argThat((ResourceData res) -> res.equals(resource)));
```

Here, `presenter.show()` takes a `ResourceData`, `res`, when it's called, and we want to check that that object has the
same fields of another one we're providing, `resource`. `argThat()` takes an instance of a class extending
`ArgumentMatcher` which, being a functional interface, can easily be implemented by a closure.