.. _introduction:

Introduction
============

The JEQL Query Language is a language which supports processing tabular data structures.

The 'E' in JEQL stands for all of the following:

* Extended - JEQL provides extensions to SQL to make it more powerful for processing data
* Embeddable - The JEQL engine can be embedded in other applications to provide a query language for table-oriented data models
* ETL - Extract/Transform/Load is a major use case for JEQL
* Efficient - JEQL's implementation makes development and execution faster!

Features
--------

The JEQL language and engine has the following significant features:

Tables
^^^^^^

* JEQL supports tables as first-class data structures. 
  This allows using the paradigm of Table-Oriented Programming
* JEQL can read and write tabular data from a variety of file formats (including Text, CSV, DBF and ESRI Shapefiles) and databases (including any JDBC-compatible database). It is easy to add I/O commands for new data sources.
* Because tables are a native datatype, 
  there is no "impedance mismatch" issue with accessing tabular datasources. 
  This make working with external data source such as relational databases very easy.

SQL
^^^

* Tables are processed using query statements expressed using the 
  familiar and well-understood SQL syntax and semantics.
* The version of SQL used in JEQL has many extensions designed 
  to make it easier to express common processing tasks. Some extensions are taken from other SQL dialects. 
  Others are unique to JEQL (such as table-valued constants, table-valued functions, and an Internal WITH clause allowing intermediate expressions in select column lists).

* SQL queries are expressions in the JEQL language. This makes it easy to chain multiple queries to accomplish a processing task.

Streaming
^^^^^^^^^

* Table processing uses stream semantics. 
* Streaming uses lazy evalution. This allows datasets much larger than main memory to be processed.
* Table streams are processed sequentially and deterministically.  
* Stream functions (KEEP, PREV, COUNTER, INDEX) greatly simplify processing of ordered datasets

Spatial
^^^^^^^

* JTS Geometry is a standard built-in type
* JEQL provdes a rich set of spatial functions, predicates, constructors, and aggregate functions. 
  This makes JEQL an excellent tool for processing spatial datasets.
* JEQL can read and write spatial data from a variety of formats 

  * Files: GML, KML and ESRI Shapefiles
  * Databases: PostGIS, Oracle, ESRI SDE, etc

Extensibility
^^^^^^^^^^^^^

* JEQL is easily extensible with new functions and commands.

  * Extensions are provided as Java classes. Extensions are loaded dynamically.

Tools
^^^^^
  
* The JEQL engine is supplied with a standalone script interpreter. 
* The engine is also designed to be easily embeddable in other applications to provide scripting and data manipulation capability for custom tabular data models

Java Platform
^^^^^^^^^^^^^

* The JEQL interpreter is written in 100% pure Java(TM). This provides a high degree of platform-independence. 
* JEQL runs on Java Version 1.5 and above.
* JEQL supports commmon Java scalar datatypes (ints, Strings, doubles) and useful complex ones (Date).
* It is easy to expose existing Java libraries
 
  * simple scalar processing can be exposed as JEQL functions
  * processes on composite data structures can be modelled with tabular input and output 
    and exposed as JEQL commands.

