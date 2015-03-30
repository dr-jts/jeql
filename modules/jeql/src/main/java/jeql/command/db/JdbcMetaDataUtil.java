/*
 * Decompiled with CFR 0_98.
 */
package jeql.command.db;

public class JdbcMetaDataUtil {
    public static String dbTypeName(int jdbcTypeCode) {
        String typeName = "UNKNOWN";
        switch (jdbcTypeCode) {
            case -7: {
                typeName = "BIT";
                break;
            }
            case -6: {
                typeName = "TINYINT";
                break;
            }
            case -5: {
                typeName = "BIGINT";
                break;
            }
            case -4: {
                typeName = "LONGVARBINARY";
                break;
            }
            case -3: {
                typeName = "VARBINARY";
                break;
            }
            case -2: {
                typeName = "BINARY";
                break;
            }
            case -1: {
                typeName = "LONGVARCHAR";
                break;
            }
            case 0: {
                typeName = "NULL";
                break;
            }
            case 1: {
                typeName = "CHAR";
                break;
            }
            case 2: {
                typeName = "NUMERIC";
                break;
            }
            case 3: {
                typeName = "DECIMAL";
                break;
            }
            case 4: {
                typeName = "INTEGER";
                break;
            }
            case 5: {
                typeName = "SMALLINT";
                break;
            }
            case 6: {
                typeName = "FLOAT";
                break;
            }
            case 7: {
                typeName = "REAL";
                break;
            }
            case 8: {
                typeName = "DOUBLE";
                break;
            }
            case 12: {
                typeName = "VARCHAR";
                break;
            }
            case 91: {
                typeName = "DATE";
                break;
            }
            case 92: {
                typeName = "TIME";
                break;
            }
            case 93: {
                typeName = "TIMESTAMP";
            }
        }
        return typeName;
    }

    public static boolean dbTypeTakesPrecision(int jdbcTypeCode) {
        if (jdbcTypeCode != 2 && jdbcTypeCode != 3) {
            return false;
        }
        return true;
    }

    public static boolean dbTypeTakesSize(int jdbcTypeCode) {
        if (jdbcTypeCode != 1 && jdbcTypeCode != 12) {
            return false;
        }
        return true;
    }
}

