/*
 * Decompiled with CFR 0_98.
 * 
 * Could not load the following classes:
 *  jeql.command.db.DbCommandBase
 *  jeql.command.db.driver.JdbcUtil
 *  jeql.engine.ExecutionException
 *  jeql.engine.Scope
 *  jeql.engine.Table
 *  jeql.engine.row.RowList
 *  jeql.engine.row.RowSchema
 *  jeql.util.SystemUtil
 *  jeql.util.TypeUtil
 */
package jeql.command.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import jeql.api.error.ExecutionException;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.command.db.driver.JdbcUtil;
import jeql.engine.Scope;
import jeql.util.SystemUtil;
import jeql.util.TypeUtil;

public class DbDDL
extends DbCommandBase {
    protected String tableName = "new_table";
    private String sql = null;
    protected Table tbl = null;
    private DbColumn[] dbSchema = null;
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setDefault(Table tbl) {
        this.tbl = tbl;
    }

    public String getCreate() {
        if (this.dbSchema != null) {
            return this.dbCreate(this.tableName, this.dbSchema);
        }
        return this.jeqlCreate();
    }

    private String dbCreate(String tableName, DbColumn[] dbSchema) {
        StringBuffer buf = new StringBuffer();
        buf.append("CREATE TABLE " + tableName + " ( " + SystemUtil.LINE_TERM);
        for (int i = 0; i < dbSchema.length; ++i) {
            buf.append(String.valueOf(dbSchema[i].name) + " " + dbSchema[i].getTypeSpec());
            if (i < dbSchema.length - 1) {
                buf.append(",");
            }
            buf.append(SystemUtil.LINE_TERM);
        }
        buf.append(")");
        return buf.toString();
    }

    private String jeqlCreate() {
        StringBuffer buf = new StringBuffer();
        buf.append("CREATE TABLE " + this.tableName + " ( " + SystemUtil.LINE_TERM);
        if (this.tbl != null) {
            RowSchema schema = this.tbl.getRows().getSchema();
            for (int i = 0; i < schema.size(); ++i) {
                buf.append(String.valueOf(schema.getName(i)) + " " + TypeUtil.nameForType((Class)schema.getType(i)));
                if (i < schema.size() - 1) {
                    buf.append(",");
                }
                buf.append(SystemUtil.LINE_TERM);
            }
        }
        buf.append(")");
        return buf.toString();
    }

    public void execute(Scope scope) throws Exception {
        if (this.tbl == null) {
            if (this.sql == null) {
                this.sql = "SELECT * FROM " + this.tableName;
            }
            this.dbSchema = this.readSchema(this.sql);
        }
    }

    private DbColumn[] extractSchema(ResultSet rs) throws SQLException {
        ResultSetMetaData rsm = rs.getMetaData();
        int ncol = rsm.getColumnCount();
        DbColumn[] cols = new DbColumn[ncol];
        for (int i = 0; i < ncol; ++i) {
            int icol = i + 1;
            cols[i] = new DbColumn(rsm.getColumnName(icol), rsm.getColumnTypeName(icol), rsm.getColumnType(icol), rsm.getColumnDisplaySize(icol), rsm.getPrecision(icol), rsm.getScale(icol), rsm.isNullable(icol), rsm.getColumnLabel(icol));
        }
        return cols;
    }

    private DbColumn[] readSchema(String sql) {
        this.conn = JdbcUtil.createConnection((String)this.jdbcDriver, (String)this.url, (String)this.user, (String)this.password);
        try {
            this.conn.setAutoCommit(false);
            this.stmt = this.conn.createStatement();
            this.stmt.setFetchSize(1);
            this.stmt.setFetchDirection(1000);
            this.rs = this.stmt.executeQuery(sql);
            return this.extractSchema(this.rs);
        }
        catch (SQLException ex) {
            this.close();
            throw new ExecutionException(ex.getMessage());
        }
    }

    private void close() {
        try {
            if (this.rs != null) {
                this.rs.close();
            }
        }
        catch (SQLException var1_1) {
            // empty catch block
        }
        this.rs = null;
        try {
            if (this.stmt != null) {
                this.stmt.close();
            }
        }
        catch (SQLException var1_2) {
            // empty catch block
        }
        this.stmt = null;
        try {
            if (this.conn != null) {
                this.conn.close();
            }
            this.conn = null;
        }
        catch (SQLException var1_3) {
            // empty catch block
        }
    }
}

