package pt.utl.ist.util;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 */
public interface EmailUtil {
    /**
     * @param fromEmail
     * @param recipientsEmail
     * @param subject
     * @param messageText
     * @param logFile
     * @param map
     * @throws FileNotFoundException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     * @throws TemplateException
     * @throws IOException
     */
    public void sendEmail(String fromEmail, String[] recipientsEmail, String subject, String messageText, File[] logFile, HashMap<String, Object> map) throws FileNotFoundException, MessagingException, UnsupportedEncodingException, TemplateException, IOException;

    /**
     * @param logFile
     * @return File
     */
    public File createZipFile(File logFile);
}
