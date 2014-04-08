package pt.utl.ist.repox.dataProvider;

import eu.europeana.core.util.web.EmailSender;
import freemarker.template.TemplateException;
import org.dom4j.Element;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pt.utl.ist.repox.RepoxConfigurationEuropeana;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilEuropeana;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 27-06-2011
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class DataSourceContainerEuropeana extends DataSourceContainer{
    protected String nameCode;
    protected String name;
//    protected String exportPath;

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


    private void sendEmail(Task.Status exitStatus, File logFile) throws MessagingException, TemplateException, IOException {
        String smtpServer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer();
        String smtpPort = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getSmtpPort();
        if(smtpServer == null || smtpServer.isEmpty()) {
            return;
        }

        String fromEmail = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getDefaultEmail();
        String recipientsEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getAdministratorEmail();
        String adminMailPass =  ((RepoxContextUtilEuropeana)ConfigSingleton.getRepoxContextUtil()).getRepoxManager().getConfiguration().getMailPassword();
        String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();

        EmailSender emailSender = new EmailSender();
        String pathIngestFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("ingest.html.ftl").getFile(), "ISO-8859-1");
        emailSender.setTemplate(pathIngestFile.substring(0, pathIngestFile.lastIndexOf("/")) + "/ingest");


        HashMap map = new HashMap<String, String>();
        map.put("exitStatus", exitStatus.toString());
        map.put("id", getDataSource().getId());

        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setUsername(fromEmail);
        mail.setPassword(adminMailPass);
        mail.setPort(Integer.valueOf(smtpPort));
        mail.setHost(smtpServer);

        Properties mailConnectionProperties = (Properties)System.getProperties().clone();
        mailConnectionProperties.put("mail.smtp.auth", "true");
        mailConnectionProperties.put("mail.smtp.starttls.enable","true");
        mailConnectionProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailConnectionProperties.put("mail.smtp.socketFactory.fallback", "false");
        mail.setJavaMailProperties(mailConnectionProperties);

        emailSender.setMailSender(mail);
        emailSender.sendEmail(recipientsEmail, fromEmail, subject, map, logFile.getAbsolutePath());

    }

    @Override
    public Element createElement() {
        Element dataSourceElement = getDataSource().createElement();

        dataSourceElement.addAttribute("name", getName());
        dataSourceElement.addAttribute("nameCode", getNameCode());
//        dataSourceElement.addAttribute("exportPath", getExportPath());

        return dataSourceElement;
    }

    /**
     * DataSourceContainerEuropeana
     * @param dataSource
     * @param nameCode
     * @param name
     * @param exportPath
     */
    public DataSourceContainerEuropeana(DataSource dataSource, String nameCode, String name, String exportPath) {
        this.dataSource = dataSource;
        this.nameCode = (nameCode != null ? nameCode : "");
        this.name = (name != null ? name : "");
//        this.exportPath = (exportPath != null ? exportPath : "");
    }

    public DataSourceContainerEuropeana() {
    }


}
