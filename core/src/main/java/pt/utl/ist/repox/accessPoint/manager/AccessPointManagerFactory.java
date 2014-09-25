package pt.utl.ist.repox.accessPoint.manager;

import pt.utl.ist.repox.configuration.RepoxConfiguration;
import pt.utl.ist.repox.database.DerbyDatabaseAccess;
import pt.utl.ist.repox.database.MysqlDatabaseAccess;
import pt.utl.ist.repox.database.PostgresqlDatabaseAccess;

/**
 */
public class AccessPointManagerFactory {
    /**
     * @param configuration
     * @return AccessPointsManager
     */
    public static AccessPointsManager getInstance(RepoxConfiguration configuration) {
        if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(org.apache.derby.jdbc.EmbeddedDriver.class.getName())) {
            return new SqlAccessPointsManager(new DerbyDatabaseAccess(configuration));
        } else if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(org.postgresql.Driver.class.getName())) {
            return new SqlAccessPointsManager(new PostgresqlDatabaseAccess(configuration));
        } else if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(com.mysql.jdbc.Driver.class.getName())) {
            return new SqlAccessPointsManager(new MysqlDatabaseAccess(configuration));
        }

        throw new UnsupportedOperationException("Database driver: " + configuration.getDatabaseDriverClassName() + " unsupported.");
    }
}
