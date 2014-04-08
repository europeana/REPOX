package pt.utl.ist.repox.util;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


public interface EmailUtil {
    public void sendEmail(String fromEmail, String[] recipientsEmail, String subject, String messageText, File[] logFile, HashMap<String, Object> map) throws FileNotFoundException, MessagingException, UnsupportedEncodingException, TemplateException, IOException;
    public File createZipFile(File logFile);
}
