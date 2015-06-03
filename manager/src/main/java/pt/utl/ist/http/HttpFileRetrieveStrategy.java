/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;
import pt.utl.ist.util.FileUtil;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 */
@XmlRootElement(name = "HttpFileRetrieveStrategy")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An HttpFileRetrieveStrategy")
public class HttpFileRetrieveStrategy implements FileRetrieveStrategy {
    private static final Logger log = Logger.getLogger(HttpFileRetrieveStrategy.class);
    public static final String HTTPFILERETRIEVESTRATEGY = "HttpFileRetrieveStrategy";
    public static final String OLDCLASS = "pt.utl.ist.repox.http.DataSourceHttp";

    @XmlElement
    @ApiModelProperty(required = true)
    private String              url;

    /**
     * Reaquired from JAXB
     */
    public HttpFileRetrieveStrategy() {
        super();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean retrieveFiles(String dataSourceId) {
        createOutputDirPath(url, dataSourceId);

        String tempRepoxFolder = getOutputHttpPath(url, dataSourceId);

        try {
            URL sourceURL = new URL(url);
            InputStream inputStream = sourceURL.openStream();

            String outputFileName = url.substring(url.lastIndexOf("/") + 1);

            File outuputFile = new File(tempRepoxFolder + "/" + outputFileName);
            FileOutputStream outPutStream = new FileOutputStream(outuputFile);
            int c;
            while ((c = inputStream.read()) != -1) {
                outPutStream.write(c);
            }
            inputStream.close();
            outPutStream.close();

            if (outuputFile.exists()) {
                URLConnection urlConnection = sourceURL.openConnection();
                outuputFile.setLastModified(urlConnection.getLastModified());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param url
     * @param set
     * @return boolean
     */
    public static boolean createOutputDirPath(String url, String set) {
        File output = new File(getOutputHttpPath(url, set));
        if (output.exists()) {
            try {
                FileUtils.deleteDirectory(output);
                log.info("Deleted Data Source HTTP dir with success from Data Source.");

            } catch (IOException e) {
                log.error("Unable to delete Data Source HTTP dir from Data Source.");
            }
        }
        return output.mkdir();
    }

    /**
     * @param url
     * @param set
     * @return String
     */
    public static String getOutputHttpPath(String url, String set) {
        String httpRequestPath = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getHttpRequestPath();
        String outputDirString = httpRequestPath + File.separator + FileUtil.sanitizeToValidFilename(url) + "-" + FileUtil.sanitizeToValidFilename(set);
        return outputDirString;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param url
     */
    public HttpFileRetrieveStrategy(String url) {
        this.url = url;
    }
}
