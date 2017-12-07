package com.task;

import  java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



class ParallelChecking extends Thread {
    @Override
    public void run() {

        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))) {
            char c = '0';
            while(true) {
                c = (char) input.read();
                if(c==' ')
                {
                    if(ProcessInfoFirstMode.IsWorking()) {
                        ProcessInfoFirstMode.Stop();
                        Thread.sleep(10000);


                    }
                    else if(ProcessInfoSecondMode.IsWorking()) {
                        ProcessInfoSecondMode.Stop();
                        Thread.sleep(10000);
                    }
                }

            }
        } catch (UnsupportedEncodingException e){}
        catch (IOException e){}
        catch (InterruptedException e){}
    }
}
public class Main_Task{
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    public static void OutDateAndTime(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
    }
    public static int StartingChoseOfMode() {
        System.out.println("Choose the mode:\n1 - Mode showing table with all processes and information about them\n2 - Information about RAM and CPU with Progress bar and CPU diagram\n<After the process will start you can cancel it by entering SINGLE CHAR Spacebar>");
        boolean check = false;
        try  {
            char c = 0;
            int choose = 0;
            while (!check) {
                c = (char) System.in.read();
                if (c == '1') break;
                else if (c == '2') break;

            }
            return Character.getNumericValue(c);
        } catch (UnsupportedEncodingException e){}
        catch (IOException e){}
        return 1;
    }

    public static void main(String... arg) throws IOException, InterruptedException {

        boolean exit=false;
        ParallelChecking chk = new ParallelChecking();
        chk.start();
        System.out.println("<First launch may be slow.. Wait some time after choosing of the mode. Be patient>");
        while (!exit)
        {
            int choose = Main_Task.StartingChoseOfMode();
            if (choose == 1) {
                ProcessInfoFirstMode.Start();
            } else if (choose == 2) {
                ProcessInfoSecondMode.Start();
            }
        }
    }
}





