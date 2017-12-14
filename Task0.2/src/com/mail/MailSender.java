package com.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender
{
    static Properties SetProperties(String WhatMail){
        Properties properties = new Properties();
        if(WhatMail=="Google"){
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "465");
        }
        else if(WhatMail == "Rambler"){
            properties.put("mail.smtp.host", "mail.rambler.ru");

            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "25");
        }
        return properties;
    }
    static MimeMessage getMimeMessage(Session ourSession, String mailAddress, String subject, String msg){
        MimeMessage Messg = new MimeMessage(ourSession);
        try {
            Messg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress));
            Messg.setSubject(subject);
            Messg.setText(msg);
        } catch (javax.mail.internet.AddressException e) {
            System.out.println("Invalid Address!");
        }
        catch (MessagingException e){}
        return  Messg;

    }
    public static void SendMessage(String fromWhatMail,String passwordMail,String toWhatMail,String topic, String messageMail){
        try {
            Properties props;
            if(fromWhatMail.contains("gmail"))
                props = SetProperties("Google");
            else props = SetProperties("Rambler");

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {protected PasswordAuthentication getPasswordAuthentication() { return new PasswordAuthentication(fromWhatMail,passwordMail); }});
            Transport.send(getMimeMessage(session,toWhatMail,topic, messageMail));
            System.out.println("Message was sent!!!!");
        } catch (MessagingException e) {
            System.out.println(e.toString());//у меня лично рамблер не работает, хз. но пусть будет по приколу
            //System.out.println("Username or Password aren't correct. try again!");
        }

    }

}
