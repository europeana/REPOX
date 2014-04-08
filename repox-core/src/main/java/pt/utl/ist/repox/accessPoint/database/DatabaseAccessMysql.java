package pt.utl.ist.repox.accessPoint.database;

import org.apache.log4j.Logger;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.util.sql.SqlUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class DatabaseAccessMysql implements DatabaseAccess {
	private static final Logger log = Logger.getLogger(DatabaseAccessMysql.class);

	protected RepoxConfiguration configuration;
	protected String dbUrl;
    protected Properties dbProps;

	public DatabaseAccessMysql(RepoxConfiguration configuration) {
		super();

		try {
			this.configuration = configuration;

			dbUrl = configuration.getDatabaseUrl();
			/*if(configuration.isDatabaseCreate()) {
				dbUrl += ";create=true";
			}*/

            dbProps = new Properties();
            dbProps.setProperty("user", configuration.getDatabaseUser());
            dbProps.setProperty("password", configuration.getDatabasePassword());

			log.info("Database URL connection: " + dbUrl);

			Class.forName(configuration.getDatabaseDriverClassName()).newInstance();
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getVarType(Class classOfValue) {
		String valueType = "varchar(255)";

		if(classOfValue.equals(Date.class)) {
			valueType = "date";
		} else if(classOfValue.equals(Integer.class)) {
			valueType = "int(11)";
		} else if(classOfValue.equals(Long.class)) {
			valueType = "bigint(20)";
		} else if(classOfValue.equals(byte[].class)) {
			valueType = "mediumblob";
		}

		return valueType;
	}

    public boolean checkTableExists(String table, Connection con) {
        try {
            SqlUtil.getSingleValue("select * from " + table + " limit 0", con);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

	public Connection openDbConnection() {
		try {
			return DriverManager.getConnection(dbUrl, dbProps);
		}
		catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public void createTableIndexes(Connection con, String idType, String table, String valueType, boolean indexValue) {
		String createTableQuery = "CREATE TABLE " + table + " (id int NOT NULL AUTO_INCREMENT PRIMARY KEY, " + "nc "
		+ idType + " NOT NULL, " + "value " + valueType + ", deleted SMALLINT)";
		log.info(createTableQuery);
		SqlUtil.runUpdate(createTableQuery, con);

		String iSystemIndexQuery = "CREATE INDEX " + table + "_i_nc ON " + table + "(nc)";
		SqlUtil.runUpdate(iSystemIndexQuery, con);

		if(indexValue) {
			String valueIndexQuery = "CREATE INDEX " + table + "_i_val ON " + table + "(value)";
			SqlUtil.runUpdate(valueIndexQuery, con);
		}
	}

	public void deleteTable(Connection con, String table) throws SQLException {
		PreparedStatement statement = con.prepareStatement("drop table " + table);
		SqlUtil.runUpdate(statement);
	}

    public String renameTableString(String oldTableName, String newTableName) {
        return "ALTER TABLE " + oldTableName + " RENAME TO " + newTableName;
    }

    public void renameIndexString(Connection con, String newTableName, String oldTableName, boolean indexValue) {
        String dropIndexQuery = "DROP INDEX " + oldTableName + "_i_nc ON " + newTableName;
        SqlUtil.runUpdate(dropIndexQuery, con);

        String createIndexQuery = "CREATE INDEX " + newTableName + "_i_nc ON " + newTableName + "(nc)";
        SqlUtil.runUpdate(createIndexQuery, con);

        if(indexValue) {
            dropIndexQuery = "DROP INDEX " + oldTableName + "_i_val ON " + newTableName;
            SqlUtil.runUpdate(dropIndexQuery, con);

            createIndexQuery = "CREATE INDEX " + newTableName + "_i_val ON " + newTableName + "(value)";
            SqlUtil.runUpdate(createIndexQuery, con);
        }
    }
	
	
	@Override
	public String getHeaderAndRecordQuery(DataSource dataSource,
			String fromDateString, String toDateString, Integer offset,
			Integer numberResults, boolean retrieveFullRecord) {

		String recordTable = (AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD).toLowerCase();
		String timestampTable = (AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD).toLowerCase();

		if (offset == null || offset < 0)
			offset = 0;
		boolean noResultLimit = (numberResults == null || numberResults <= 0);

		String query = "select " + recordTable + ".nc, " + timestampTable
				+ ".deleted" + ", " + timestampTable + ".value" + ", "
				+ recordTable + ".id";
		if (retrieveFullRecord) {
			query += ", " + recordTable + ".value";
		}

		query += " from " + recordTable + ", " + timestampTable + " where "
				+ recordTable + ".nc = " + timestampTable + ".nc";

		if (fromDateString != null || toDateString != null) {
			if (fromDateString != null) {
				query += " and " + timestampTable + ".value >= '"
						+ fromDateString + "'";
			}
			if (toDateString != null) {
				query += " and " + timestampTable + ".value <= '"
						+ toDateString + "'";
			}
		}

        query += " and " + recordTable + ".id > " + offset + " order by "
                + recordTable + ".id";
        if (!noResultLimit)
            query += " limit " + numberResults;

		return query;
	}


	
	@Override
	public String getFieldQuery(DataSource dataSource, String fromDateString, String toDateString,
            Integer offset, Integer numberResults, String field) {

		String recordTable = (dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD).toLowerCase();
		String timestampTable = (dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD)
				.toLowerCase();
		
		String query = "select " + recordTable.toLowerCase() + "." + field + " from " + recordTable.toLowerCase();
        if(fromDateString != null || toDateString != null) {
            query += ", " + timestampTable.toLowerCase() + " where " + recordTable.toLowerCase() + ".nc = "
                    + timestampTable.toLowerCase() + ".nc";
            if(fromDateString != null) {
                query += " and " + timestampTable.toLowerCase() + ".value >= '" + fromDateString + "'";
            }
            if(toDateString != null) {
                query += " and " + timestampTable.toLowerCase() + ".value <= '" + toDateString + "'";
            }
        }
		
		return query;
	}
}
