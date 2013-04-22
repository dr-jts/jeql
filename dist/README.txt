  JEQL Extensible/Embeddable/ETL Query Language
  =============================================

Installation Requirements
-------------------------

- Java 1.5 or above


Installation Instructions
-------------------------

Unzip the distribution archive into a directory.

If desired, add bin/jeql.bat to the shell search path.

It may be necessary to edit the jeql.bat file to 
explicitly refer to the appropriate Java version installation directory.

The maximum memory allocation may be changed by editing 
the '-Xmx...' value in the JAVA_OPTS variable in this file.


Getting Started
---------------

JEQL scripts are text files.  They are run using the following command:

  jeql <script-name>

To see a list of all provided functions and commands:

  jeql -man

To see command-line help:

  jeql -help

See the sample directory for examples of JEQL scripts.


Resources
---------

See http://tsusiatsoftware.net/jeql for JEQL documentation and examples.


License
-------

JEQL is released as free software, but is not currently open source.

It is also available under a commercial license,
which allows embedding in commercial software.

For inquiries about licensing the software, 
please contact: mtnclimb@gmail.com

