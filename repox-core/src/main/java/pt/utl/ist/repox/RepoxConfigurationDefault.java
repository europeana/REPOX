/*
 * Created on 23/Mar/2006
 *
 */
package pt.utl.ist.repox;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Represents all the global available to the application. Just for convenience.
 * Not to be used for having globals that are important for the model.
 * 
 * @author Nuno Freire
 */
public class RepoxConfigurationDefault extends RepoxConfiguration {

    /**
     * Creates a new instance of this class.
     * 
     * @param configurationProperties
     * @throws IOException
     */
    public RepoxConfigurationDefault(Properties configurationProperties) throws IOException {
        super(configurationProperties);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            //URL url = new URL("http://rnod.bnportugal.pt/OAI/oai.aspx?verb=ListRecords&metadataPrefix=ese");
            URL url = new URL("http://digmap2.digmap.eu/index_digital/contente/teste.xml");
            HttpURLConnection con;
            con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(600000);
            con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
            con.setRequestProperty("Accept-Encoding", "compress, gzip, identify");
            InputStream in;
            in = con.getInputStream();

            if (in != null) {
                Writer writer = new StringWriter();

                char[] buffer = new char[8048];
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                        System.out.println("writer.toString() = " + writer.toString());
                    }
                } finally {
                    in.close();
                }
                System.out.println(" = " + writer.toString());
            } else {
                System.out.println("sssss");
            }

            byte[] inputBytes = IOUtils.toByteArray(in);
            System.out.println("inputBytes = " + inputBytes);
        } catch (MalformedURLException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
