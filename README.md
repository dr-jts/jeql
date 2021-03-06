JEQL
====
JEQL is a scripting language for spatial processing and ETL, which runs on the JVM.  

JEQL is based on the standard, well-known SQL model.  
This provides powerful declarative specification of data querying and transformation.

SQL support
-----------

JEQL improves on SQL syntax & semantics by adding:

* a streaming model of computation
* factoring complex SQL queries into a chain of simpler queries
* defining named expressions for SELECT lists
* providing SPLIT BY as the the inverse of GROUP BY
 
Functions and Commands
----------------------

JEQL provides an extensive set of commands and functions.

Built-in commands include:

* Readers and Writers for many textual and spatial formats
* Readers and Writers for JDBC databases
* A Reader supporting Postgres/PostGIS native spatial datatypes
 
Built-in function libraries include:

* Geometry manipulation
* String manipulation
* RegEx matching and extracting

and many others...

Extensibility
-------------

JEQL is easily extensible, using the dynamic loading and reflection capabilities of the JVM.

* Functions are Java static methods
* Commands are Java classes implementing a simple interface

Tools
-----
JEQL provides the following tools:

* A command-line script runner
* A Workbench GUI
  * Edit, run and debug scripts
  * View script data tables 
  * View spatial data
  



