/*
 * Created on 28/Nov/2005
 *
 */
package pt.utl.ist.util.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SqlResultRow {
	Object[] row;
	
	public SqlResultRow(ResultSet rs) throws SQLException{
    	row=new Object[rs.getMetaData().getColumnCount()];
    	for (int i=1; i<=rs.getMetaData().getColumnCount() ; i++) {
    		row[i-1]=rs.getObject(i);
    	}
	}

	public String getString(int index) {
		return (String) row[index];
	}

	public int getInt(int index) {
		if (row[index] instanceof Long)
			return ((Long)row[index]).intValue();
		if (row[index] instanceof BigDecimal)
			return ((BigDecimal)row[index]).intValue();
		if (row[index] instanceof Double)
			return ((Double)row[index]).intValue();		
		if (row[index] instanceof BigInteger)
				return ((BigInteger)row[index]).intValue();
		return (Integer) row[index];
	}

	public Integer getInteger(int index) {
		if (row[index]==null)
			return null;
		return getInt(index);
	}

	public Date getDate(int index) {
		return (Date) row[index];
	}	
	
}
