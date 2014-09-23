package pt.utl.ist.repox.accessPoint.database;

import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.configuration.RepoxConfiguration;

/**
 */
public class AccessPointManagerFactory {
    /**
     * @param configuration
     * @return AccessPointsManager
     */
    public static AccessPointsManager getInstance(RepoxConfiguration configuration) {
        if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(org.apache.derby.jdbc.EmbeddedDriver.class.getName())) {
            return new AccessPointsManagerSqlDefault(new DatabaseAccessDerby(configuration));
        } else if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(org.postgresql.Driver.class.getName())) {
            return new AccessPointsManagerSqlDefault(new DatabaseAccessPostgresql(configuration));
        } else if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(com.mysql.jdbc.Driver.class.getName())) { return new AccessPointsManagerSqlDefault(new DatabaseAccessMysql(configuration)); }

        throw new UnsupportedOperationException("Database driver: " + configuration.getDatabaseDriverClassName() + " unsupported.");
    }
}
