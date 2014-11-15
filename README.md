# Hive2Hive - Process Framework
[![Build Status](https://travis-ci.org/Hive2Hive/ProcessFramework.svg?branch=master)](https://travis-ci.org/Hive2Hive/ProcessFramework)

This is a lightweight and extensible framework for asynchronous and synchronous process composition. Initially started and used as a sub-module of [Hive2Hive](https://github.com/Hive2Hive/Hive2Hive), an open-source Java library for secure, distributed, P2P-based file synchronization and sharing, this framework now is a standalone module that might be helpful for your projects, too. It's licensed under the [MIT License](http://opensource.org/licenses/MIT) and any contribution is welcome.

## Features
- simple, straightforward API
- **supports**:
 - *rollback*
 - *result computation*
 - *pause/resume*
 - *asynchronous execution/rollback*
- **easily extendable** due to the use of [GoF Design Patterns](http://en.wikipedia.org/wiki/Design_Patterns):
  - all components have the same API
  - processes can be built by using a [Composite](http://en.wikipedia.org/wiki/Composite_pattern):
    - use default containers or define your own
    - define your own process steps
    - process trees can be built by **nesting components** (containers, steps)
  - processes can be extended by **adding behaviour/state** by using a [Decorator](http://en.wikipedia.org/wiki/Decorator_pattern):
    - use default decorators or define your own
  
## API Demonstration

The following code shows the most fundamental things that can be done with the framework.

**Creating a `ProcessStep`**

Simply inherit from the abstract class `ProcessStep<T>`, where `T` is the type of the result, and implement `doExecute()` (and `doRollback()`). (Tip: `T` can also be `Void`.)
```java
public class MyProcessStep extends ProcessStep<Integer> {

	@Override
	protected Integer doExecute() throws InvalidProcessStateException, ProcessExecutionException {
		
		// do some stuff...
		int result = 1 + 2 + 3;
		
		// tag for rollback
		setRequiresRollback(true);
		
		return result;
	}

	@Override
	protected Integer doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		
		// rollback stuff...
		int result = 0;
		
		// untag for rollback
		setRequiresRollback(false);
		
		return result;
	}
}
```
**Execution and Rollback**

Then, the above created step can be executed and its result retrieved as follows. In case of an execution error, it could directly be rolled back.
```java
ProcessStep<Integer> step1 = new MyProcessStep();

int result;
try {
   result = step1.execute();
} catch (ProcessExecutionException ex) {
   result = step1.rollback();
}
```
**Asynchronous Components**

Every `IProcessComponent<T>` can be wrapped with the `AsyncComponent` decorator. With this, the component's execution and rollback run on a separate thread and immediately return control to the caller.
```java
ProcessStep<Integer> step2 = new MyProcessStep();
IProcessComponent<Future<Integer>> asyncStep2 = new AsyncComponent<Integer>(step2);

Future<Integer> futureResult = asyncStep2.execute();
```
**Process Composition**

Classes inheriting from `ProcessComposition<T>` allow to nest `IProcessComponent`s and to build process trees. The below example shows the usage of the default `SyncProcess`.
```java
ProcessComposite<Void> composite1 = new SyncProcess();
ProcessComposite<Void> composite2 = new SyncProcess();

composite1.add(step1);       // add a single ProcessStep
composite1.add(asyncStep2);  // add an async component
composite1.add(composite2);  // add another ProcessComposite
```
Now, we ***could*** make the whole composite run asynchronously.
```java
IProcessComponent<Future<Void>> asyncComposite = new AsyncComponent<Void>(composite1);

try {
  // execute composite sync
  composite.execute();
	
  // or async
  asyncComposite.execute();

} catch (ProcessExecutionException ex) {
	
  // rollback composite sync
  composite.execute();

  // or async
  asyncComposite.rollback();
}
```
**Pause and Resume**

`IProcessComponent<T>`s that are wrapped with the `AsyncComponent` decorator can be paused/resumed because they run asynchronously.
```java
asyncComposite.execute();
asyncComposite.pause();

// do other stuff meanwhile...

asyncComposite.resume();
```

## Architecture UML
### Class Diagram
![Architecture UML](http://hive2hive.com/wp-content/uploads/2014/11/Hive2Hive-Process-Framework.png)
- `IProcessComponent<T>`: Basic interface for all process components. Defines all common functionality. `T` is the type of the result computed by this `IProcessComponent`.
- `ProcessComponent<T>`: Abstract base class for all process components. Keeps track of a process components' most essential properties and functionalities.
- `ProcessStep<T>`: Abstract base class for all normal `IProcessComponent`s (leaf). These normal components represent a specific operation and do not contain other `IProcessComponent`s.
- `ProcessComposite<T>`: Abstract base class for all composite `IProcessComponent`s. These composites contain other `IProcessComponent`s.
- `ProcessDecorator<T>`: Abstract base class for all decorators that decorate `IProcessComponent`s. These decorators provide additional behavior or state to existing implementations of `IProcessComponent`s.

## Installation
There are three easy ways to get and include the Hive2Hive Process Framework into your project.

If you just want to use the framework, either refer to option 1 or 2.  
If you want to [contribute to the project](#contribution), please refer to option 3.
- **Option 1: Add Maven dependency** *(recommended)*  
  You can add the latest stable release as an [Apache Maven](http://maven.apache.org/) dependency and fetch it from our repository. Add the following to your `pom.xml` and make sure to select the most recent version.  
```xml
  <repository>
    <id>hive2hive.org</id>
    <url>http://repo.hive2hive.org</url>
  </repository>
  ...
  <dependency>
    <groupId>org.hive2hive</groupId>
    <artifactId>org.hive2hive.processframework</artifactId>
    <version>1.X.X</version>
  </dependency>
```
- **Option 2: Add JAR-file directly**  
  In case you don't want to use Maven, you can just download the [latest stable release](https://github.com/Hive2Hive/ProcessFramework/releases). The required `.jar`-files is packed and delivered to you as a `.zip`.
- **Option 3: Clone from GitHub**  
  If you want to contribute to the Hive2Hive ProcessFramework project, this is what you should do. Cloning from the `dev` branch allows yout to get the *bleeding edge* of development. This option is not recommended if you just want to use the library.

## Documentation

For more exact details and documentation about this process library, please visit http://hive2hive.com/process-framework/.

For more details and documentation about the Hive2Hive project, please visit http://www.hive2hive.com/.

The source code itself is thoroughly documented using JavaDoc.

## Contribution

The library is intended to be improved and extended so that we all profit from its capabilities.
For more information, please refer to our main project [Contribute to Hive2Hive](https://github.com/Hive2Hive/Hive2Hive#contribution).

## Contact

If you have any questions, feel uncomfortable or uncertain about an issue or your changes, feel free to reach us via email at [info@hive2hive.com](mailto:info@hive2hive.com). Please consider posting your question on StackOverflow (using the [`hive2hive`](http://stackoverflow.com/questions/tagged/hive2hive) tag) in case it is a technical question that might interest other developers, too.

We provide you with all information you need and will happily help you via email, Skype, remote pairing or whatever you are comfortable with.
