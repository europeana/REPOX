/*
 * Created on 28/Nov/2005
 *
 */
package pt.utl.ist.repox.util.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 */
public class SqlResultRow {
    Object[] row;

    /**
     * Creates a new instance of this class.
     * @param rs
     * @throws SQLException
     */
    public SqlResultRow(ResultSet rs) throws SQLException {
        row = new Object[rs.getMetaData().getColumnCount()];
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            row[i - 1] = rs.getObject(i);
        }
    }

    /**
     * @param index
     * @return Get the String of the array at the specified index
     */
    public String getString(int index) {
        return (String)row[index];
    }

    /**
     * @param index
     * @return get the int value at the specified index
     */
    public int getInt(int index) {
        if (row[index] instanceof Long) return ((Long)row[index]).intValue();
        if (row[index] instanceof BigDecimal) return ((BigDecimal)row[index]).intValue();
        if (row[index] instanceof Double) return ((Double)row[index]).intValue();
        if (row[index] instanceof BigInteger) return ((BigInteger)row[index]).intValue();
        return (Integer)row[index];
    }

    /**
     * @param index
     * @return get the Integer value at the specified index
     */
    public Integer getInteger(int index) {
        if (row[index] == null) return null;
        return getInt(index);
    }

    /**
     * @param index
     * @return get the Date at the specified position
     */
    public Date getDate(int index) {
        return (Date)row[index];
    }

}
