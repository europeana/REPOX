package eu.europeana.core.util.web;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Handle all email sending
 * 
 * @author Gerald de Jong <geralddejong@gmail.com>
 * @author Borys Omelayenko <borys.omelayenko@kb.nl>
 */

public class EmailSender {
    private static final String TEMPLATE_NAME_AFFIX_TEXT = ".txt.ftl";
    private static final String TEMPLATE_NAME_AFFIX_HTML = ".html.ftl";
    private Logger              log                      = Logger.getLogger(getClass());
    private JavaMailSender      mailSender;
    private String              template;

    /**
     * @param mailSender
     */
    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * @param templateName
     */
    public void setTemplate(String templateName) {
        this.template = templateName;
    }

    /**
     * Send and email
     * 
     * @param toEmail
     * @param fromEmail
     * @param subject
     * @param model
     * @param fileAttachment
     * @throws IOException
     * @throws TemplateException
     */
    public void sendEmail(final String toEmail, final String fromEmail, final String subject, final Map<String, Object> model, final String fileAttachment) throws IOException, TemplateException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException, IOException {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                mimeMessage.setFrom(new InternetAddress(fromEmail));
                mimeMessage.setSubject(subject);

                Multipart mp = new MimeMultipart("alternative");

                // Create a "text" Multipart message
                BodyPart textPart = new MimeBodyPart();
                Template textTemplate = getResourceTemplate(template + TEMPLATE_NAME_AFFIX_TEXT);
                final StringWriter textWriter = new StringWriter();
                try {
                    textTemplate.process(model, textWriter);
                } catch (TemplateException e) {
                    throw new MailPreparationException("Can't generate text subscription mail", e);
                }
                textPart.setDataHandler(new DataHandler(new DataSource() {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new ByteArrayInputStream(textWriter.toString().getBytes("utf-8"));
                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        throw new IOException("Read-only data");
                    }

                    @Override
                    public String getContentType() {
                        return "text/plain";
                    }

                    @Override
                    public String getName() {
                        return "main";
                    }
                }));
                mp.addBodyPart(textPart);

                // Create a "HTML" Multipart message
                Multipart htmlContent = new MimeMultipart("related");
                BodyPart htmlPage = new MimeBodyPart();
                Template htmlTemplate = getResourceTemplate(template + TEMPLATE_NAME_AFFIX_HTML);
                final StringWriter htmlWriter = new StringWriter();
                try {
                    htmlTemplate.process(model, htmlWriter);
                } catch (TemplateException e) {
                    throw new MailPreparationException("Can't generate HTML subscription mail", e);
                }
                htmlPage.setDataHandler(new DataHandler(new DataSource() {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new ByteArrayInputStream(htmlWriter.toString().getBytes("utf-8"));
                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        throw new IOException("Read-only data");
                    }

                    @Override
                    public String getContentType() {
                        return "text/html";
                    }

                    @Override
                    public String getName() {
                        return "main";
                    }
                }));
                htmlContent.addBodyPart(htmlPage);
                BodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(htmlContent);
                mp.addBodyPart(htmlPart);

                //Attach the file to the message
                MimeBodyPart attachmentPart = new MimeBodyPart();
                if (!fileAttachment.isEmpty()) {
                    FileDataSource fileDataSource = new FileDataSource(fileAttachment);
                    attachmentPart.setDataHandler(new DataHandler(fileDataSource));
                    attachmentPart.setFileName(fileDataSource.getName());
                    mp.addBodyPart(attachmentPart);
                }
                mimeMessage.setContent(mp);
            }
        };

        try {
            mailSender.send(preparator);
        } catch (Exception e) {
            log.error("to: " + toEmail);
            log.error("subject: " + subject);
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                log.error(MessageFormat.format("{0} = {1}", entry.getKey(), entry.getValue()));
            }
            throw new IOException("Unable to send email", e);
        }
        /*
         * Multipart mp = new MimeMultipart("alternative");
         * 
         * // plain text email Template templateText = getResourceTemplate(template + TEMPLATE_NAME_AFFIX_TEXT); BodyPart textPart = new MimeBodyPart(); textPart.setDataHandler(new DataHandler(new DataSource() { public InputStream getInputStream() throws IOException { return new
         * StringBufferInputStream(textWriter.toString()); } public OutputStream getOutputStream() throws IOException { throw new IOException("Read-only data"); } public String getContentType() { return "text/plain"; } public String getName() { return "main"; } }));
         * 
         * mp.addBodyPart(textPart);
         * 
         * // html email try { Template templateHtml = getResourceTemplate(template + TEMPLATE_NAME_AFFIX_TEXT); Multipart htmlContent = new MimeMultipart("related"); BodyPart htmlPage = new MimeBodyPart(); htmlPage.setDataHandler(new DataHandler(new DataSource() { public InputStream
         * getInputStream() throws IOException { return new StringBufferInputStream(htmlWriter.toString()); } public OutputStream getOutputStream() throws IOException { throw new IOException("Read-only data"); } public String getContentType() { return "text/html"; } public String getName() { return
         * "main"; } })); htmlContent.addBodyPart(htmlPage); BodyPart htmlPart = new MimeBodyPart(); htmlPart.setContent(htmlContent); mp.addBodyPart(htmlPart); } catch (Exception e) { // TODO: log if no html template found }
         * 
         * mimeMessage.setContent(mp);
         * 
         * 
         * SimpleMailMessage message = new SimpleMailMessage(); message.setSubject(subject); message.setFrom(fromEmail); message.setTo(toEmail); String emailText = createEmailText(model); message.setText(emailText); mailSender.send(message);
         */
    }

    /**
     * @param fileName
     * @return Template given the fileName
     * @throws IOException
     */
    protected Template getResourceTemplate(String fileName) throws IOException {
        return getTemplate(fileName, new InputStreamReader(new DataInputStream(new FileInputStream(fileName))));
    }

    private static Template getTemplate(String name, Reader reader) throws IOException {
        Configuration configuration = new Configuration();
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        return new Template(name, reader, configuration);
    }
}