.. _agg-functions:

Aggregate Functions
===================

Aggregate functions may be used in the result-list of a SELECT expression. 
The SELECT may be be explicitly grouped using the GROUP BY clause. 
If no GROUP BY clause is present then the group is implicitly the entire collection of rows in the input.

Many standard SQL aggregate functions are provided (e.g. SUM, MAX, MIN, etc.). 
Geometric aggregate functions are useful for computing geometric results.

Numeric Aggregate Functions
---------------------------

avg( number )

Computes the average of the values in the group.

count( * )

Computes the count of the number of rows in the group.

max( number )

Computes the maximum value of the input over the group.

min( number )

Computes the minimum value of the input over the group.

stdDev( number )

Computes the standard deviation of the values in the group.

sum( number )

Computes the sum of the values in the group.

Value-Selecting Aggregate Functions
-----------------------------------

first( input )

Returns the value of the first non-null value in the group.

last( input )

Returns the value of the last non-null value in the group.

String Aggregate Functions
--------------------------

concat( String )

Computes the string concatenation of the values in the group (expressed as strings).

Geometric Aggregate Functions
-----------------------------

geomCollect( Geometry )

Collects the geometries in the group into a MultiGeometry of the appropriate type.

geomConnect( Geometry )

Connects the points in the group into a LineString.

geomConvexHull( Geometry )

Computes the convex hull of the geometries in the group.

geomExtent( Geometry )

Computes the extent (i.e. envelope or Minimum Bounding Rectangle) of the geometries in the group.

geomUnion( Geometry )

Computes the union of the geometries in the group.

geomUnionMem( Geometry )

Computes the union of the geometries in the group. Uses a faster in-memory implementation.
