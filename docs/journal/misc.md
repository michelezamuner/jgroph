# Miscellaneous notes


## Testing that a response has a certain body

In unit test we might need to verify that a response mock is updated with a certain string. For instance, we want to
make sure that the string `"Hello, World!"` is written to the response mock by the class under test:

```java
final ByteArrayOutputStream output = new ByteArrayOutputStream();
final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
final HttpServletResponse response = mock(HttpServletResponse.class);
when(response.getWriter()).thenReturn(writer);

// additional code...
servlet.service(request, response);
assertEquals("Hello, World!", new String(output.toByteArray(), "UTF-8"));
```