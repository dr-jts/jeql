/*
 * Decompiled with CFR 0_98.
 */
package jeql.command.db;

import jeql.command.db.JdbcMetaDataUtil;

class DbColumn {
    String name;
    String typeName;
    int typeCode;
    int size;
    int precision;
    int scale;
    int isNullable;
    String label = null;

    public DbColumn(String name, String typeName, int typeCode, int size, int precision, int scale, int isNullable, String label) {
        this.name = name;
        this.typeName = typeName.toUpperCase();
        this.typeCode = typeCode;
        this.size = size;
        this.precision = precision;
        this.scale = scale;
        this.isNullable = isNullable;
        this.label = label;
    }

    public String getTypeSpec() {
        StringBuffer strBuf = new StringBuffer();
        String nullModifier = null;
        if (this.isNullable == 0) {
            nullModifier = " NOT NULL";
        }
        String sizeSpec = null;
        if (JdbcMetaDataUtil.dbTypeTakesPrecision(this.typeCode)) {
            sizeSpec = "(" + this.precision + "," + this.scale + ")";
        }
        if (JdbcMetaDataUtil.dbTypeTakesSize(this.typeCode)) {
            sizeSpec = "(" + this.size + ")";
        }
        strBuf.append(this.typeName);
        if (sizeSpec != null) {
            strBuf.append(sizeSpec);
        }
        if (nullModifier != null) {
            strBuf.append(nullModifier);
        }
        return strBuf.toString();
    }
}

