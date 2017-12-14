package com.mail;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


class Main{
    static  String enterMailFrom(BufferedReader bfReader) throws IOException{
        Boolean checkCycle = false;
        String mailFrom = "";
        System.out.println("Enter eMail that you want to use for sending");
        while (!checkCycle){
            mailFrom = bfReader.readLine();
            if(mailFrom.contains("@gmail")||mailFrom.contains("@rambler")) checkCycle=true;
            else System.out.println("Your mail must be Gmail or Rambler for correct work! Try again");
        }
        return mailFrom;
    }
    static String enterMailTo(BufferedReader bfReader) throws IOException{
        String mailTo="";
        boolean checkCycle=false;
        System.out.println("Enter eMail address where you want to send your message");
        while (!checkCycle){
            mailTo = bfReader.readLine();
            if(mailTo.contains("@")) checkCycle=true;
            else System.out.println("Your mail must be MAIL ADDRESS, not a random array of symbols! Try again(TIP - Adress must contain @ symbol!)");
        }
        return mailTo;
    }
    static String enterDefaultData(BufferedReader bfReader) throws IOException{
        return  bfReader.readLine();
    }
    static void ownInitializingExecute(BufferedReader bfReader)throws IOException{
        String mailFrom="";
        String passMail="";
        String mailTo="";
        String subjectMsg="";
        String messageOwn="";
        mailFrom = enterMailFrom(bfReader);
        System.out.println("Enter password of this eMail");
        //
        passMail=enterDefaultData(bfReader);
        //
       mailTo=enterMailTo(bfReader);
       //
        System.out.println("Enter subject of your message");
        subjectMsg = enterDefaultData(bfReader);
        //
        System.out.println("Enter your message");
        messageOwn = enterDefaultData(bfReader);
        //
        System.out.println("Trying to send message..");
        MailSender.SendMessage(mailFrom,passMail,mailTo,subjectMsg,messageOwn);
    }
    static  void defaultInitializingExecute(){
        MailSender.SendMessage("dead1deadline@gmail.com", "spamming","dead1deadline@gmail.com", "spam", "Example2");

    }
    static int chooseExecutionVariant(BufferedReader br)throws IOException{
        String choose = br.readLine();
        if(choose.toCharArray()[0]=='1')return  1;
        else return 2;
    }
    public static void main(String[] args) {
        while (true) {
            System.out.println("1 - Enter own data for messaging\n2 - Default data");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
               if(chooseExecutionVariant(br)==1)
                    ownInitializingExecute(br);
                    else defaultInitializingExecute();
            } catch (IOException e){}
        }
    }

    }

