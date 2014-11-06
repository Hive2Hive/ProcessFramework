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
- **easily extendable** using [GoF Design Patterns](http://en.wikipedia.org/wiki/Design_Patterns):
  - all components have the same API
  - build up your own process tree by nesting components
  - Processes can be built by using a [Composite](http://en.wikipedia.org/wiki/Composite_pattern):
    - use default containers or define your own
    - define your own process steps
  - Processes can be extended by adding behaviour/state by using a [Decorator](http://en.wikipedia.org/wiki/Decorator_pattern):
    - use default decorators or define your own
  
## API Demonstration

Coming soon!

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
