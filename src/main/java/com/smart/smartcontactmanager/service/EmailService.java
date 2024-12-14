package com.smart.smartcontactmanager.service;

import java.util.Properties; // Correct import

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {


    
    public boolean sendEmail(String subject, String message, String to) {

        boolean f = false;

        String from = "goswamitushar00000@gmail.com"; // Replace with your email address
        String host = "smtp.gmail.com";

        // Get the system properties
        Properties properties = System.getProperties();

        // Setting important information to properties object
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Step 1: Get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("goswamitushar00000@gmail.com", "umce rawt jwfw yhkh");
            }
        });

        session.setDebug(true);

        try {
            // Step 2: Compose the Message [text, multimedia]
            MimeMessage m = new MimeMessage(session);

            // Setting the from field
            m.setFrom(new InternetAddress(from));

            // Adding recipient to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Setting the subject of the message
            m.setSubject(subject);

            //m.setText(message);
            m.setContent(message,"text/html");


            // Creating the MimeMultipart object
   /*          MimeMultipart mimeMultipart = new MimeMultipart();

            // Creating and adding text body part
            MimeBodyPart textMime = new MimeBodyPart();
            textMime.setText(message);
            mimeMultipart.addBodyPart(textMime);

            // Creating and adding file attachment body part
            MimeBodyPart fileMime = new MimeBodyPart();
            String path = "C:\\Users\\tusha\\Downloads\\rohit.jpeg"; // Adjust file path as needed
            File file = new File(path);
            fileMime.attachFile(file);
            mimeMultipart.addBodyPart(fileMime); */

            // Setting the content of the message
           // m.setContent(mimeMultipart);

            // Step 3: Send the email
            Transport.send(m);

            System.out.println("Email sent successfully...");
            f = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;
    }
    
}
