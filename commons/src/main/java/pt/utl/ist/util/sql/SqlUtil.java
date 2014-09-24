/*
 * SqlUtil.java
 *
 * Created on 15 de Outubro de 2003, 17:26
 */

package pt.utl.ist.util.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author  Nuno Freire
 */
public class SqlUtil {

    /**
     * @param val
     * @return String with escaped '
     */
    public static String escapeCharacters(String val) {
        return val.replaceAll("'", "\\'");
    }

    /**
     * @param table
     * @param filter
     * @param con
     * @return number of rows using filter
     */
    public static int getCount(String table, String filter, Connection con) {
        int ret = 0;
        try {
            PreparedStatement stm = con.prepareStatement("select count(*) from " + table + (filter == null ? "" : " " + filter));
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ret = rs.getInt(1);
            }
            rs.close();
            stm.close();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param driver
     * @param url
     * @param user
     * @param password
     * @return connection to the database
     * @throws SQLException
     */
    public static Connection connect(String driver, String url, String user, String password) throws SQLException {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param con
     * @return int
     */
    public static int getCount(String query, Connection con) {
        return getInteger(query, con);
    }

    /**
     * @param stm
     * @return int
     */
    public static int getCount(PreparedStatement stm) {
        return getInteger(stm);
    }

    /**
     * @param query
     * @param con
     * @return Integer
     */
    public static Integer getInteger(String query, Connection con) {
        PreparedStatement stm = null;
        try {
            stm = con.prepareStatement(query);
            return getInteger(stm);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stm.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param stm
     * @return Integer
     */
    public static Integer getInteger(PreparedStatement stm) {
        Number ret = 0;
        try {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ret = (Number)rs.getObject(1);
            }
            rs.close();
            stm.close();
            return ret == null ? null : ret.intValue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param stm
     * @return list of values
     */
    public static List listValues(PreparedStatement stm) {
        List<Object> ret = new ArrayList<Object>();
        try {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ret.add(rs.getObject(1));
            }
            rs.close();
            stm.close();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param con
     * @return list with values
     */
    public static List listValues(String query, Connection con) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            return listValues(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param stm
     * @return single value
     */
    public static Object getSingleValue(PreparedStatement stm) {
        Object ret = null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                ret = rs.getObject(1);
            }
            rs.close();
            stm.close();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param con
     * @return single value
     */
    public static Object getSingleValue(String query, Connection con) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            return getSingleValue(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param stm
     * @return single String
     */
    public static String getSingleString(PreparedStatement stm) {
        String ret = null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                ret = rs.getString(1);
            }
            rs.close();
            stm.close();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param con
     * @return single String
     */
    public static String getSingleString(String query, Connection con) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            return getSingleString(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param stm
     * @return single Long
     */
    public static Long getSingleLong(PreparedStatement stm) {
        Long ret = null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                ret = rs.getLong(1);
            }
            rs.close();
            stm.close();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param con
     * @return single Long
     */
    public static Long getSingleLong(String query, Connection con) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            return getSingleLong(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param stm
     * @return single row as an array of the columns
     */
    public static Object[] getSingleRow(PreparedStatement stm) {
        Object[] ret = null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                ret = new Object[rs.getMetaData().getColumnCount()];
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    ret[i - 1] = rs.getObject(i);
                }
            }
            rs.close();
            stm.close();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param con
     * @return single row as an array of the columns
     */
    public static Object[] getSingleRow(String query, Connection con) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            return getSingleRow(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param stm
     * @return list for SqlResultRows
     */
    public static List<SqlResultRow> listRows(PreparedStatement stm) {
        List<SqlResultRow> ret = new ArrayList<SqlResultRow>();
        try {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ret.add(new SqlResultRow(rs));
            }
            rs.close();
            stm.close();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param con
     * @return list for SqlResultRows
     */
    public static List<SqlResultRow> listRows(String query, Connection con) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            return listRows(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param ps
     * @return int of the returned code
     */
    public static int runUpdate(PreparedStatement ps) {
        return runUpdate(ps, true);
    }

    /**
     * @param ps
     * @param closeStatement
     * @return int of the returned code
     */
    public static int runUpdate(PreparedStatement ps, boolean closeStatement) {
        try {
            int ret = ps.executeUpdate();
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (closeStatement) ps.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * @param query
     * @param con
     * @return int of the returned code
     */
    public static int runUpdate(String query, Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement(query);
            return runUpdate(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param ps
     * @param idx
     * @param val
     * @throws SQLException
     */
    public static void setParameter(PreparedStatement ps, int idx, Object val) throws SQLException {
        if (val instanceof String) {
            ps.setString(1, (String)val);
        } else if (val instanceof Integer) {
            ps.setInt(1, ((Integer)val).intValue());
        } else if (val instanceof Date) {
            ps.setDate(1, new java.sql.Date(((Date)val).getTime()));
        } else if (val instanceof byte[]) {
            ps.setBytes(1, (byte[])val);
        } else {
            throw new RuntimeException("Index type not implemented");
        }
    }
}
