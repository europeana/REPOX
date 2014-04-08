/*
 * NUtils.java
 *
 * Created on 29 de Novembro de 2001, 20:40
 */

package pt.utl.ist.util;

import java.io.*;
import java.net.URL;
import java.util.*;
/**
 *
 * @author  Nuno Freire
 */
public class NUtil {  
	
	@SuppressWarnings("unchecked")
	public static <S extends Serializable> S cloneObject(S o) {
		try {
			ByteArrayOutputStream byteArr=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(byteArr);
			oos.writeObject(o);
			oos.close();
			
			ByteArrayInputStream fis = new ByteArrayInputStream(byteArr.toByteArray());
		    ObjectInputStream s = new ObjectInputStream(fis);
		    S ret=(S) s.readObject();  
			fis.close();
			
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
    public static Properties loadPropertiesFromClassPath(String filename) {
        URL url = null;
        try{ 
            url = Thread.currentThread().getContextClassLoader().getResource(filename);
            if (url == null){
                    url = (new File(filename)).toURI().toURL();
            }
            InputStream strIn = url.openStream();
            Properties properties=new Properties();
            properties.load(strIn);
            strIn.close();
            return properties;
        }catch(Exception e){
            throw new RuntimeException(url.getFile(),e);
        }
    }
    
    public static File getResource(String resourceName){
    	String path=NUtil.getPathOfResource(resourceName);
    	if (path==null)
    		return null;
        return new File(path);
    }
    
    public static String getPathOfResource(String resourceName){
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url==null)
            return null;
        String ret=null;
        try{
            ret=java.net.URLDecoder.decode(url.getFile(),"iso-8859-1");
        }catch(java.io.UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
        if (ret==null)
            return null;
        if (ret.charAt(0)=='/' && ret.charAt(2)==':' && ret.charAt(3)=='/')
            return ret.substring(1);
        return ret;
    }

    public static String readResource(String resourceName) throws IOException{
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url==null)
            return null;
//        String ret=null;
        try{
        	InputStream in=url.openStream();
        	
        	
            StringBuffer ret=new StringBuffer();
//          char[] buf=new char[1024];
          char c=' ';
          Reader reader=new InputStreamReader(in);
          
          int r=0;
          while((r=reader.read()) != -1){
              ret.append((char)r);
          }
          reader.close();
          in.close();
          return ret.toString();
        }catch(java.io.UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
    }
    
    
    public static File findBeaconLocation(String beacon){
        return new File(NUtil.getPathOfResource(beacon)).getParentFile();
    }
//     public static void main(String args[]) {
//         try{
//            //System.err.println(NUtil.getPathOfResource("bnd_repository.xml") );         
//         }catch(Exception e){
//            e.printStackTrace();
//        }
//    }   
    

    public static String arrayToString(Object[] array, String prefix, String suffix, String separator){
        return listToString(arrayToList(array), prefix,suffix,separator);
    }
    
    
    public static String listToString(List list, String prefix, String suffix, String separator){
        if (list==null)
            return null;
        return iteratorToString(list.iterator(), prefix,suffix,separator);
    }

    public static String iteratorToString(Iterator i, String prefix, String suffix, String separator){
        StringBuffer sb=new StringBuffer();
        boolean first=true;
        for (; i.hasNext();) {
            if (first) { 
                first=false;
            }else
                sb.append(separator);
            sb.append(prefix);  
            sb.append(i.next().toString());
            sb.append(suffix);
        }
        return sb.toString();
    }
    
    
    public static String[] objectArrayToStringArray(Object[] objs){
        if (objs==null)
            return null;
        String[] ret=new String[objs.length];
        for (int idx=0 ; idx<objs.length ; idx++) {
            ret[idx]=objs[idx].toString();
        }
        return ret;
    }
    
    public static List arrayToList(Object[] objs){
        if (objs==null)
            return null;
        List ret=new ArrayList(objs.length);
        for (Object obj : objs) {
            ret.add(obj);
        }
        return ret;
    }

    public static List arrayToIntegerList(Object[] objs){
        if (objs==null)
            return null;
        List ret=new ArrayList(objs.length);
        for (Object obj : objs) {
            if (!((String) obj).equals(""))
                ret.add(Integer.valueOf((String) obj));
        }
        return ret;
    }
    
    
    public static Map createMap(Object[] keysValues){
    	Map ret=new HashMap();
    	
    	for (int i = 0; i < keysValues.length; i=i+2) {
			ret.put(keysValues[i],keysValues[i+1]);
		}
    	
    	return ret;
    }
    

    public static String[] listToStringArray(List objs){
        return collectionToStringArray(objs);
    }
    

    public static String[] collectionToStringArray(Collection objs){
        if (objs==null)
            return null;
        String[] ret=new String[objs.size()];
        int idx=0;
        for (Object o: objs) {
            ret[idx]=o.toString();
            idx++;
        }
        return ret;
    }
    
    public static boolean compareCollections(Collection cola, Collection colb) {
    	if(cola.size() != colb.size())
    		return false;
    	Iterator ita=cola.iterator();
    	Iterator itb=colb.iterator(); 
    	while(ita.hasNext()) {
    		Object itema=ita.next();
    		Object itemb=itb.next();
    		
    		if(!itema.equals(itemb))
    			return false;
    	}
    	return true;
    }
    
    public static String stackTraceToString(Throwable t) {
    	PrintStreamString pss=new PrintStreamString();
    	t.printStackTrace(pss.out);
    	return pss.toString();
    }
    
    
    public static String getSystemCharset() {
    	String defaultEncoding = new InputStreamReader(
    	        new ByteArrayInputStream(new byte[0])).getEncoding();
    	return defaultEncoding;
    }
    
    
    public static void redirectStdOut(File targetFile) throws FileNotFoundException{    
    	OutputStreamReplicator out=new OutputStreamReplicator(System.out, new FileOutputStream(targetFile));
    	System.setOut(new PrintStream(out));
//    	System.setOut(new PrintStream(targetFile));
    }
    public static void redirectStdErr(File targetFile) throws FileNotFoundException{
    	OutputStreamReplicator err=new OutputStreamReplicator(System.err, new FileOutputStream(targetFile));
    	System.setErr(new PrintStream(err));
//    	System.setErr(new PrintStream(targetFile));
    }
    
//   genericos nao funcionam em arrayss  
//    public static <A> A[] collectionToArray(Collection<A> col) {
//    	Object[] ret=new Object[col.size()];
//    	int idx=0;
//    	for (A obj: col) {
//    		ret[idx]=obj;
//    		idx++;
//    	}
//    	return (A[])ret;
//    }
    
}
