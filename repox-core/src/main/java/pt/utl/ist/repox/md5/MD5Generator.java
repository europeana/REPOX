package pt.utl.ist.repox.md5;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.security.MessageDigest;

public class MD5Generator {
	String message;

	public String MD5(String message) {
		this.message = message;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			StringBuffer sb = new StringBuffer();
			byte[] md5 = md.digest(message.getBytes());

            for (byte aMd5 : md5) {
                String tmpStr = "0" + Integer.toHexString((0xff & aMd5));
                sb.append(tmpStr.substring(tmpStr.length() - 2));
            }
			
			return (sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}
	
	public static void main(String[] args) {
		if(args == null || (args.length != 1 && args.length != 4)) {
			System.out.println("MD5Generator: Invalid arguments");
			return;
		}
		MD5Generator generator = new MD5Generator();
		
		if(args.length == 1) {
			System.out.print(generator.MD5(args[0]));			
		}
		else if(args.length == 4) {
			File inputFile = new File(args[0]);
			File outputFile = new File(args[3]);
			String user = args[1];
			String digestPassword = generator.MD5(args[2]);
			
			SAXReader reader = new SAXReader();
			String resultString = null;
	        try {
				Document document = reader.read(inputFile);
				resultString = document.asXML().replaceAll("\\$repox_userLogin", user);
				resultString = resultString.replaceAll("\\$mypassword", digestPassword);
			}
			catch (DocumentException e) {
				System.err.println("Could not read file: " + inputFile.getAbsolutePath());
				return;
			}
			
			try {
				Document document = DocumentHelper.parseText(resultString);
				XMLWriter writer = new XMLWriter(new FileOutputStream(outputFile), OutputFormat.createPrettyPrint());
				writer.write(document);
				writer.close();
			}
			catch (DocumentException e) {
				System.err.println("Could not parse resulting document of file: " + inputFile.getAbsolutePath());
				return;
			}
			catch (UnsupportedEncodingException e) {
				System.err.println("Unsupported Encoding: " + e.getMessage());
				return;
			}
			catch (FileNotFoundException e) {
				System.err.println("Could not create file: " + outputFile.getAbsolutePath());
				return;
			}
			catch (IOException e) {
				System.err.println("Could not write to file: " + outputFile.getAbsolutePath());
				e.printStackTrace();
			}
		}
		
	}

}