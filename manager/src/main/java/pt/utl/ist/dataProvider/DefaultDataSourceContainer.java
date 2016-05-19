package pt.utl.ist.dataProvider;

import freemarker.template.TemplateException;

import org.dom4j.Element;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxConfiguration;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.sru.SruRecordUpdateDataSource;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.EmailSender;
import pt.utl.ist.z3950.DataSourceZ3950;

import javax.mail.MessagingException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Gilberto Pedrosa
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 01, 2014
 */
@XmlRootElement(name = "defaultDatasetContainer")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A Default Dataset Container")
public class DefaultDataSourceContainer extends DataSourceContainer{
    @XmlElement
    @ApiModelProperty(position = 2)
    protected String nameCode;
    @XmlElement
    @ApiModelProperty(position = 3)
    protected String name;

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getExportPath() {
//        return exportPath;
//    }
//
//    public void setExportPath(String exportPath) {
//        this.exportPath = exportPath;
//    }


//    private void sendEmail(Task.Status exitStatus, File logFile) throws MessagingException, TemplateException, IOException {
//        String smtpServer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer();
//        String smtpPort = ((DefaultRepoxConfiguration)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getSmtpPort();
//        if(smtpServer == null || smtpServer.isEmpty()) {
//            return;
//        }
//
//        String fromEmail = ((DefaultRepoxConfiguration)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getDefaultEmail();
//        String recipientsEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getAdministratorEmail();
//        String adminMailPass =  ((DefaultRepoxContextUtil)ConfigSingleton.getRepoxContextUtil()).getRepoxManager().getConfiguration().getMailPassword();
//        String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();
//
//        EmailSender emailSender = new EmailSender();
//        String pathIngestFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("ingest.html.ftl").getFile(), "ISO-8859-1");
//        emailSender.setTemplate(pathIngestFile.substring(0, pathIngestFile.lastIndexOf("/")) + "/ingest");
//
//
//        HashMap map = new HashMap<String, String>();
//        map.put("exitStatus", exitStatus.toString());
//        map.put("id", getDataSource().getId());
//
//        JavaMailSenderImpl mail = new JavaMailSenderImpl();
//        mail.setUsername(fromEmail);
//        mail.setPassword(adminMailPass);
//        mail.setPort(Integer.valueOf(smtpPort));
//        mail.setHost(smtpServer);
//
//        Properties mailConnectionProperties = (Properties)System.getProperties().clone();
//        mailConnectionProperties.put("mail.smtp.auth", "true");
//        mailConnectionProperties.put("mail.smtp.starttls.enable","true");
//        mailConnectionProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        mailConnectionProperties.put("mail.smtp.socketFactory.fallback", "false");
//        mail.setJavaMailProperties(mailConnectionProperties);
//
//        emailSender.setMailSender(mail);
//        emailSender.sendEmail(recipientsEmail, fromEmail, subject, map, logFile.getAbsolutePath());
//
//    }

    @Override
    public Element createElement() {
        Element dataSourceElement = getDataSource().createElement();

        dataSourceElement.addAttribute("name", getName());
        dataSourceElement.addAttribute("nameCode", getNameCode());

        return dataSourceElement;
    }

    /**
     * DefaultDataSourceContainer
     * 
     * @param dataSource
     * @param nameCode
     * @param name
     * @param exportPath
     */
    public DefaultDataSourceContainer(DataSource dataSource, String nameCode, String name, String exportPath) {
        this.dataSource = dataSource;
        this.nameCode = (nameCode != null ? nameCode : "");
        this.name = (name != null ? name : "");
    }

    public DefaultDataSourceContainer() {
    } 
    
    /**
     * Copy constructor
     * @param defaultDataSourceContainer
     */
    public DefaultDataSourceContainer(DefaultDataSourceContainer defaultDataSourceContainer) {
        this.dataSource = defaultDataSourceContainer.getDataSource();
        this.nameCode = defaultDataSourceContainer.getNameCode();
        this.name = defaultDataSourceContainer.getName();
    }
}
