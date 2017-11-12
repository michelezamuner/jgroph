# Miscellaneous


## Generating hash codes

To generate hash codes for objects, I've followed the advices at [this link](https://stackoverflow.com/questions/113511#answer-113600).


## Integration tests of asynchronous server

Inside integration tests for the asynchronous server, the ports used by the server being spawned from within each
different test must all be different from each other (i.e. `8000`, `8001`, `8002`, etc.). This is because tests are
executed in parallel, and thus the servers are all living at the same time, meaning that they cannot use the same
port.

Also, these tests contain `Thread.sleep(1000)` statements here and there: this is because we have to wait for our test
client to actually connect to the server, and perform any requested operation (like writing) before verifying the
results. Being these integration tests, we are connecting to a real server, which is spawned in a parallel thread, and
as such operations are not instantaneous.
