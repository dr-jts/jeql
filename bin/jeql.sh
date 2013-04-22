#!/bin/sh

if test "x$LIB_DIR" = "x"; then
        LIB_DIR=`dirname $0`/../lib/
fi

# to change L&F add eg -Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel
JAVA_LOOKANDFEEL="-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel"
#JAVA_LOOKANDFEEL=""

JAVA_OPTS="-Xms256M -Xmx1024M"

if test "x$LIB_DIR" = "x"; then
        LIB_DIR=`dirname $0`/../lib/
fi

#---------------------------------#
# dynamically build the classpath #
#---------------------------------#

CP=
for i in `ls ${LIB_DIR}/*.jar`
do
  CP=${CP}:${i}
done

#---------------------------#
# run the program           #
#---------------------------#
java -cp ".:${CP}" $JAVA_OPTS $JAVA_LOOKANDFEEL jeql.JeqlCmd $1 $2 $3 $4 $5 $6 $7 $8 $9
