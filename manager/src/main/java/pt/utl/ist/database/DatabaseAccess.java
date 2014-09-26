package pt.utl.ist.database;

import pt.utl.ist.dataProvider.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseAccess {
    String getVarType(Class classOfValue);

    Connection openDbConnection();

    boolean checkTableExists(String table, Connection con);

    void createTableIndexes(Connection con, String idType, String table, String valueType, boolean indexValue);

    void deleteTable(Connection con, String table) throws SQLException;

    String renameTableString(String oldTableName, String newTableName);

    void renameIndexString(Connection con, String newTableName, String oldTableName, boolean indexValue);

    String getHeaderAndRecordQuery(DataSource dataSource, String fromDateString, String toDateString, Integer offset, Integer numberResults, boolean retrieveFullRecord);

    String getFieldQuery(DataSource dataSource, String fromDateString, String toDateString, Integer offset, Integer numberResults, String field);
}
