package pt.utl.ist.repox.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.configuration.DefaultRepoxContextUtil;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 */
public class DefaultEmailUtil implements EmailUtil {
    //	possible hosts: "mail.clix.pt"; "mail.ist.utl.pt"; "smtp.ist.utl.pt"; "mail.inesc-id.pt"; "smtp.inesc-id.pt"; "inesc-id.inesc-id.pt";
    //	/home/dreis/temp/lixo.csv
    //  repox@ist.utl.pt

    private boolean send(String from, String[] to, String subject, String message, File[] attachments) throws Exception {
        String host = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer();
        boolean useAuthentication = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().isUseMailSSLAuthentication();
        final String user = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
        final String password = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getMailPassword();
        int port = Integer.valueOf(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpPort());

        Properties props = setProperties(host, useAuthentication, port, port);

        try {
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
            //            session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));

            InternetAddress[] addressTo = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                addressTo[i] = new InternetAddress(to[i]);
            }

            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            addMessageContent(msg, message, attachments);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, user, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

            deleteZipFilesAfterEmailSent(attachments);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void deleteZipFilesAfterEmailSent(File[] attachments) {
        if (attachments != null) {
            for (File file : attachments) {
                if (file.getName().contains(".zip")) file.delete();
            }
        }
    }

    private void addMessageContent(MimeMessage msg, String message, File[] attachments) throws MessagingException {
        if (attachments == null || attachments.length == 0) {
            // Create the message part
            msg.setText(message);
        } else {
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(message);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Fill attachments
            for (File file : attachments) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(file.getName());
                multipart.addBodyPart(messageBodyPart);
            }

            // Put parts in message
            msg.setContent(multipart);
        }
    }

    private Properties setProperties(String host, boolean useSSLAuthentication, int port, int sport) {
        Properties props = new Properties();

        props.put("mail.smtp.host", host);

        if (useSSLAuthentication) {
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.socketFactory.port", sport);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        return props;
    }

    @Override
    public void sendEmail(String fromEmail, String[] recipientsEmail, String subject, String message, File[] attachments, HashMap<String, Object> map) throws MessagingException, FileNotFoundException {
        try {
            send(fromEmail, recipientsEmail, subject, message, attachments);
        } catch (Exception e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
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

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        //        String smtpServer = "smtp.gmail.com";
        String fromEmail = "eudml@eudml.org";
        String[] recipientsEmail = new String[] { "joao.a.edmundo@gmail.com" };
        File[] attachments = new File[] { new File("src/test/resources/xslImportTest/new.xsl") };
        DefaultEmailUtil emailUtilDefault = new DefaultEmailUtil();
        emailUtilDefault.sendEmail(fromEmail, recipientsEmail, "REPOX email", "Test message", attachments, null);
        //        File newFile = new File("D:\\Projectos\\TESTS\\threads.xml");
        //        EmailUtilDefault emailUtilDefault = new EmailUtilDefault();
        //        emailUtilDefault.createZipFile(newFile);
        System.exit(0);
    }
}
