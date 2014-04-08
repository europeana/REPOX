package pt.utl.ist.repox.accessPoint.database;

import pt.utl.ist.repox.dataProvider.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseAccess {
	public abstract String getVarType(Class classOfValue);
	public abstract Connection openDbConnection();
	public abstract boolean checkTableExists(String table, Connection con);
	public abstract void createTableIndexes(Connection con, String idType, String table, String valueType, boolean indexValue);
	public abstract void deleteTable(Connection con, String table) throws SQLException;
	public abstract String renameTableString(String oldTableName, String newTableName);
	public abstract void renameIndexString(Connection con, String newTableName, String oldTableName, boolean indexValue);
	
	public String getHeaderAndRecordQuery(DataSource dataSource, String fromDateString,
            String toDateString, Integer offset, Integer numberResults, boolean retrieveFullRecord);
	
	public String getFieldQuery(DataSource dataSource, String fromDateString, String toDateString,
            Integer offset, Integer numberResults, String field) ;
}
