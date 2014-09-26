package pt.utl.ist.accessPoint.manager;

import pt.utl.ist.configuration.RepoxConfiguration;
import pt.utl.ist.database.DerbyDatabaseAccess;
import pt.utl.ist.database.MysqlDatabaseAccess;
import pt.utl.ist.database.PostgresqlDatabaseAccess;

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
