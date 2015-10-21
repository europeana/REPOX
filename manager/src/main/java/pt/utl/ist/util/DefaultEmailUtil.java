package pt.utl.ist.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxConfiguration;
import freemarker.template.TemplateException;

public class DefaultEmailUtil implements EmailUtil {

    @Override
    public void sendEmail(String fromEmail, String[] recipientsEmail,
            String subject, String message, File[] attachments, HashMap<String, Object> map) throws IOException, MessagingException, TemplateException {
        String smtpServer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer();
        String smtpPort = ((DefaultRepoxConfiguration)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getSmtpPort();
        if (smtpServer == null || smtpServer.isEmpty()) {
            return;
        }

        String adminMailPass = ((DefaultRepoxConfiguration)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getMailPassword();

        EmailSender emailSender = new EmailSender();
        String pathIngestFile = null;

        if (map.get("mailType").equals("ingest")) {
            pathIngestFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("ingest.html.ftl").getFile(), "ISO-8859-1");
            emailSender.setTemplate(pathIngestFile.substring(0, pathIngestFile.lastIndexOf("/")) + "/ingest");
        } else if (map.get("mailType").equals("userAccount")) {
            pathIngestFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("sendUserData.html.ftl").getFile(), "ISO-8859-1");
            emailSender.setTemplate(pathIngestFile.substring(0, pathIngestFile.lastIndexOf("/")) + "/sendUserData");
        } else if (map.get("mailType").equals("sendFeedback")) {
            pathIngestFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("sendFeedbackEmail.html.ftl").getFile(), "ISO-8859-1");
            emailSender.setTemplate(pathIngestFile.substring(0, pathIngestFile.lastIndexOf("/")) + "/sendFeedbackEmail");
        }

        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setPort(Integer.valueOf(smtpPort));
        mail.setHost(smtpServer);

        if (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().isUseMailSSLAuthentication()) {
            mail.setUsername(fromEmail);
            mail.setPassword(adminMailPass);

            Properties mailConnectionProperties = (Properties)System.getProperties().clone();
            mailConnectionProperties.put("mail.smtp.auth", "true");
            mailConnectionProperties.put("mail.smtp.starttls.enable", "true");
            mailConnectionProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailConnectionProperties.put("mail.smtp.socketFactory.fallback", "false");
            mail.setJavaMailProperties(mailConnectionProperties);
        }

        emailSender.setMailSender(mail);
        if (map.get("mailType").equals("ingest")) {
            emailSender.sendEmail(recipientsEmail[0], fromEmail, subject, map, attachments.length > 0 ? attachments[0].getAbsolutePath() : "");
        } else if (map.get("mailType").equals("userAccount") || map.get("mailType").equals("sendFeedback")) {
            emailSender.sendEmail(recipientsEmail[0], fromEmail, subject, map, "");
        }

        // delete zip files on attachments
        if (attachments != null) {
            for (File file : attachments) {
                if (file.getName().contains(".zip"))
                    file.delete();
            }
        }
    }

    @Override
    public File createZipFile(File logFile) {
        // These are the files to include in the ZIP file
        String[] files = new String[] { logFile.getAbsolutePath() };
        String[] filenames = new String[] { logFile.getName() };

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        File zippedFile = new File(logFile.getParentFile().getAbsolutePath() + File.separator + logFile.getName() + ".zip");
        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zippedFile));

            // Compress the files
            for (int i = 0; i < filenames.length; i++) {
                FileInputStream in = new FileInputStream(files[i]);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filenames[i]));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
        }
        return zippedFile;
    }
}
