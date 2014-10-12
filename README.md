# Cate
**Cate** represents Continuation based Asynchronous Task Executor.
It provides some abstraction for parallel task scheduling to let you write asynchronous programming codes in
a synchronous-like style.

## Features
1. Cate use **Task** to abstract a minimal scheduling unit, which is scheduled with continuation.
2. Tasks can be combined in several control flows: serialization, spawning & collecting, loops, nested tasks, etc.
3. Middle state of a control flow(called **Context**) can be saved and resumed at any time(like call/cc),
   which also makes it easier to invoke callback based traditional asynchronous interfaces. 
4. Most primitive tasks are stateless, which means combined tasks can be reused in most of the situations.
5. Cate is very easily extended. The whole library is based on 3 basic interface: ITask, IContinuation & IScheduler.
   The former 2 interfaces are implemented to build customized control flows. The latter one can be implemented to
   encapsulate arbitrary thread pool.

## Examples
Here is an code example with detailed comments, which describes the main features:

[src/test/java/net/goldolphin/cate/TaskTest.java](src/test/java/net/goldolphin/cate/TaskTest.java)

Another example which demonstrate how to wrap a typical producer/consumer client into a **Task**-style client with timeout mechanism: 

[src/test/java/net/goldolphin/cate/WrapRemoteClientTest.java](src/test/java/net/goldolphin/cate/WrapRemoteClientTest.java)

## Links
* Source codes: [https://github.com/goldolphin/cate]((https://github.com/goldolphin/cate))