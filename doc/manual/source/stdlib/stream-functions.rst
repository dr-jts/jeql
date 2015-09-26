.. _stream-functions:

Stream Functions
================

Stream functions take advantage of JEQL's deterministic table streaming semantics 
to make some common transformations easy.

KEEP
----

* KEEP(value, boolean [,init-value ]) pseudo-function allows preserving a value from a previous row in a stream

PREV
----

* PREV(value [,init-value] ) pseudo-function allows referring to a value from the previous row

COUNTER
-------

INDEX
-----
