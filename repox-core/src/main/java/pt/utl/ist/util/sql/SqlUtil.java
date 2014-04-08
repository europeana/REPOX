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
    
    public static String escapeCharacters(String val){
        return val.replaceAll("'","\\'");
    }
    
    public static int getCount(String table, String filter, Connection con){
        int ret=0;
        try {
            PreparedStatement stm = con.prepareStatement("select count(*) from "+table + (filter==null ? "" : " "+filter));
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                ret=rs.getInt(1);
            }
            rs.close();
            stm.close();
            return ret;
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }                        
    }   
    
    
    public static Connection connect(String driver, String url, String user, String password) throws SQLException{
    	try {
			Class.forName(driver);
	        return DriverManager.getConnection(url,user,password);    	
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
    }
    
    public static int getCount(String query,  Connection con){
        return getInteger(query, con);                        
    }    
	

    public static int getCount(PreparedStatement stm){
        return getInteger(stm);                    
    }      
    
    
    public static Integer getInteger(String query,  Connection con){
        PreparedStatement stm = null;          
        try {
            stm = con.prepareStatement(query);      
            return getInteger(stm);
        } catch(SQLException e) { 
            throw new RuntimeException(e);
	    }finally {
	    	try {
	    		stm.close();
	        } catch(Exception e) { 
	            //ignore
	        }
        }                        
    }    
	

    public static Integer getInteger(PreparedStatement stm){
    	Number ret=0;
        try {
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                ret=(Number)rs.getObject(1);
            }
            rs.close();  
            stm.close();
            return ret==null ? null : ret.intValue();
        } catch(SQLException e) { 
            throw new RuntimeException(e);
	    }                    
    }     
    
	public static List listValues(PreparedStatement stm) {
        List<Object> ret=new ArrayList<Object>();
        try {
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                ret.add(rs.getObject(1));
            }
            rs.close();  
            stm.close();
            return ret;
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }
	}   
    
	public static List listValues(String query, Connection con) {
    	PreparedStatement ps=null;
        try {
        	ps=con.prepareStatement(query);
    		return listValues(ps);
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }finally {
        	try {
        		ps.close();
            } catch(Exception e) { 
                //ignore
            }
        }
	}

	public static Object getSingleValue(PreparedStatement stm) {
        Object ret=null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()){
                ret=rs.getObject(1);
            }
            rs.close();  
            stm.close();
            return ret;
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }
	}
	
	
	
	public static Object getSingleValue(String query, Connection con) {
    	PreparedStatement ps=null;
        try {
        	ps=con.prepareStatement(query);
    		return getSingleValue(ps);
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }finally {
        	try {
        		ps.close();
            } catch(Exception e) { 
                //ignore
            }
        }
	}
	

	public static String getSingleString(PreparedStatement stm) {
        String ret=null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()){
                ret=rs.getString(1);
            }
            rs.close();  
            stm.close();
            return ret;
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }
	}
	
	
	
	public static String getSingleString(String query, Connection con) {
    	PreparedStatement ps=null;
        try {
        	ps=con.prepareStatement(query);
    		return getSingleString(ps);
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }finally {
        	try {
        		ps.close();
            } catch(Exception e) { 
                //ignore
            }
        }
	}
	


	public static Long getSingleLong(PreparedStatement stm) {
		Long ret=null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()){
                ret=rs.getLong(1);
            }
            rs.close();  
            stm.close();
            return ret;
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }
	}
	
	
	
	public static Long getSingleLong(String query, Connection con) {
    	PreparedStatement ps=null;
        try {
        	ps=con.prepareStatement(query);
    		return getSingleLong(ps);
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }finally {
        	try {
        		ps.close();
            } catch(Exception e) { 
                //ignore
            }
        }
	}

	

	public static Object[] getSingleRow(PreparedStatement stm) {
        Object[] ret=null;
        try {
            ResultSet rs = stm.executeQuery();
            if (rs.next()){
            	ret=new Object[rs.getMetaData().getColumnCount()];
            	for (int i=1; i<=rs.getMetaData().getColumnCount() ; i++) {
                    ret[i-1]=rs.getObject(i);
            	}
            }
            rs.close();  
            stm.close();
            return ret;
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }
	}
	
	public static Object[] getSingleRow(String query, Connection con) {
    	PreparedStatement ps=null;
        try {
        	ps=con.prepareStatement(query);
    		return getSingleRow(ps);
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }finally {
        	try {
        		ps.close();
            } catch(Exception e) { 
                //ignore
            }
        }
	}	
	

	
	
	public static List<SqlResultRow> listRows(PreparedStatement stm) {
        List<SqlResultRow> ret=new ArrayList<SqlResultRow>();
        try {
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
            	ret.add(new SqlResultRow(rs));
            }
            rs.close();  
            stm.close();
            return ret;
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }
	}   
    
	public static List<SqlResultRow> listRows(String query, Connection con) {
    	PreparedStatement ps=null;
        try {
        	ps=con.prepareStatement(query);
    		return listRows(ps);
        } catch(SQLException e) { 
            throw new RuntimeException(e);
        }finally {
        	try {
        		ps.close();
            } catch(Exception e) { 
                //ignore
            }
        }
	}

	
	public static int runUpdate(PreparedStatement ps) {
        return runUpdate(ps, true);
	}
	

	public static int runUpdate(PreparedStatement ps, boolean closeStatement) {
        try {
			int ret=ps.executeUpdate();
			return ret;
	    } catch(SQLException e) { 
	        throw new RuntimeException(e);
	    }finally {
	    	try {
	    		if(closeStatement)
	    			ps.close();
	        } catch(Exception e) { 
	            //ignore
	        }
	    }
	}
	
	public static int runUpdate(String query, Connection con) {
        try {
			PreparedStatement ps=con.prepareStatement(query);
			return runUpdate(ps);
	    } catch(SQLException e) { 
	        throw new RuntimeException(e);
	    }
	}
	
	
	public static void setParameter(PreparedStatement ps, int idx, Object val) throws SQLException{
		if(val instanceof String) {
			ps.setString(1, (String)val);
		}else if(val instanceof Integer) {
			ps.setInt(1, ((Integer)val).intValue());
		}else if(val instanceof Date) {
			ps.setDate(1, new java.sql.Date(((Date)val).getTime()));
		}else if(val instanceof byte[]) {
			ps.setBytes(1, (byte[]) val);
		}else {
			throw new RuntimeException("Index type not implemented");
		}
	}
}
