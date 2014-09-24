package pt.utl.ist.repox.accessPoint.manager;

import pt.utl.ist.repox.configuration.RepoxConfiguration;
import pt.utl.ist.repox.database.DatabaseAccessDerby;
import pt.utl.ist.repox.database.DatabaseAccessMysql;
import pt.utl.ist.repox.database.DatabaseAccessPostgresql;

/**
 */
public class AccessPointManagerFactory {
    /**
     * @param configuration
     * @return AccessPointsManager
     */
    public static AccessPointsManager getInstance(RepoxConfiguration configuration) {
        if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(org.apache.derby.jdbc.EmbeddedDriver.class.getName())) {
            return new AccessPointsManagerSql(new DatabaseAccessDerby(configuration));
        } else if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(org.postgresql.Driver.class.getName())) {
            return new AccessPointsManagerSql(new DatabaseAccessPostgresql(configuration));
        } else if (configuration.getDatabaseDriverClassName() != null && configuration.getDatabaseDriverClassName().equals(com.mysql.jdbc.Driver.class.getName())) { return new AccessPointsManagerSql(new DatabaseAccessMysql(configuration)); }

        throw new UnsupportedOperationException("Database driver: " + configuration.getDatabaseDriverClassName() + " unsupported.");
    }
}
