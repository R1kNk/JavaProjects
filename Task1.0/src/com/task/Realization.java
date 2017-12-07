package com.task;
import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.*;

class ProcessInfoFirstMode // provides info about first mode (with table in which  Name of process, PID, UID, UsedMemory, CPUUsage% )
{
    static int cores = Runtime.getRuntime().availableProcessors();

    private static boolean isWorking;//bool to start or stop process of info ouput
    public static boolean IsWorking() {
        synchronized (ProcessInfoFirstMode.class) {
            return isWorking;
        }
    }

    static final class ProcessInfo{
        public int[] PID_UID_MEM;
        public double CpuLoadPercents;
        public String ProcessName;
        public boolean NameIsGiven;
        ProcessInfo()
        {
            PID_UID_MEM = new int[3];
            CpuLoadPercents = 0;
            ProcessName="";
            NameIsGiven=false;
        }
    } // some similarity of struct to save info about processes in the List
    public static void Start()throws IOException, InterruptedException{
        isWorking=true;
        while (isWorking) {
            OutInfo();
        }
    }// start the while cycle
    public static void Stop(){isWorking=false;}//Stops output
    static void OutInfo() throws  IOException, InterruptedException {
        try {

            ArrayList<ProcessInfo> ListInfo = GetInfo();
            Formatter fmt = new Formatter();

            fmt.format("%30s %5s %2s %13s %8s","ProcessName","PID","UID","Memory","CPUUsage");
            System.out.println(fmt);
            Formatter fmt1 = new Formatter();
            double MemBuf=0;
            double check=0;
            for (int i = 0; i < ListInfo.size() ; i++) {
                MemBuf = ListInfo.get(i).PID_UID_MEM[2];
                check+=ListInfo.get(i).CpuLoadPercents;
                System.out.format("%30s %5s %2s %11s %2s %7s%1s",ListInfo.get(i).ProcessName,ListInfo.get(i).PID_UID_MEM[0],ListInfo.get(i).PID_UID_MEM[1],(Math.rint((MemBuf/1024)*100.00)/100.00), "MB",(Math.rint((ListInfo.get(i).CpuLoadPercents)*100.00)/100.00), "%");
                System.out.println();
            }
            System.out.println("===============================================================");
            Main_Task.OutDateAndTime();
            System.out.println("===============================================================");

        }
        catch (IOException T){}
    } //outs all info about processes
    static  ArrayList<ProcessInfo> GetInfo() throws  IOException, InterruptedException{
        ArrayList<ProcessInfo> ListInfo = new ArrayList<ProcessInfo>();
        //
        Process cmdInfo = Runtime.getRuntime().exec(new String[]{"cmd.exe","/C","tasklist"}); // executing cmd and taking information about processes using InputStreamReader
        cmdInfo.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(cmdInfo.getInputStream()));
        int buf_i=0;
        while ((line = stdout.readLine()) != null) {
            buf_i++;
            if(buf_i>=4)
                ListInfo.add(GetName_PID_UID_MEM(line.toCharArray()));
        }
        stdout.close();
        //
        ListInfo = GetCPULoadPercentsList(ListInfo);

        return ListInfo;
    }// Gets info about all processes
    static ProcessInfo GetName_PID_UID_MEM(char[] current_string){
        ProcessInfo buf_info = new ProcessInfo();
        //
        String number_now="";

        int iter_arr=0;
        for (int i = 0; i < current_string.length; i++) {
            if(Character.isAlphabetic((current_string[i]))&&!buf_info.NameIsGiven)
            {
                while (i+1!=current_string.length)
                {
                    if(current_string[i]==' '&&current_string[i+1]==' ') break;
                    buf_info.ProcessName+=current_string[i]; i++;
                    buf_info.NameIsGiven=true;
                }
                //continue;
            }
            if(Character.isDigit(current_string[i]))
            {
                if(iter_arr!=2) {
                    number_now += current_string[i];
                    if (i + 1 == current_string.length || current_string[i + 1] == ' ') {
                        buf_info.PID_UID_MEM[iter_arr] = Integer.valueOf(number_now);
                        number_now = "";
                        iter_arr++;
                    }
                } else
                {
                    while(Character.isDigit(current_string[i]))
                    {
                        number_now+=current_string[i];
                        if(!Character.isDigit(current_string[i+1])&&Character.isDigit(current_string[i+2])) i+=2;
                        else i++;
                    }
                    buf_info.PID_UID_MEM[2] = Integer.valueOf(number_now);
                    number_now="";
                    iter_arr=0;
                }
            }
        }
        return  buf_info;
    }// just pulling out info from string
    static ArrayList<ProcessInfo> GetCPULoadPercentsList(ArrayList<ProcessInfo> list)throws IOException, InterruptedException{

        Pair<Integer,Double> PairBuf;

        //
        Map CPUS1 = new HashMap<Integer,Double>();
        Map CPUS2 = new HashMap<Integer,Double>();
        Process PowerShellCPU = Runtime.getRuntime().exec("powershell.exe Get-Process  | select CPU, ID");
        PowerShellCPU.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(PowerShellCPU.getInputStream()));
        int buf_i=0;
        while ((line = stdout.readLine()) != null) {
            buf_i++;
            if (buf_i >= 4) {
                PairBuf = TakeCPUsAndIDFromString(line);
                CPUS1.put(PairBuf.ID,PairBuf.CPUs);
            }
        }
        int RefreshInterval = 1000;
        Thread.sleep(RefreshInterval);
        stdout.close();
        //
        PowerShellCPU = Runtime.getRuntime().exec("powershell.exe Get-Process  | select CPU, ID");
        PowerShellCPU.getOutputStream().close();
        stdout = new BufferedReader(new InputStreamReader(PowerShellCPU.getInputStream()));
        buf_i=0;
        while ((line = stdout.readLine()) != null) {
            buf_i++;
            if(buf_i>=4) {
                PairBuf = TakeCPUsAndIDFromString(line);
                CPUS2.put(PairBuf.ID,PairBuf.CPUs);
            }
        }
        stdout.close();
        for (int i=0; i<list.size();i++) {
            double oldCpu=0;
            double newCPU=0;

            Set<Map.Entry<Integer, Double>> buf = CPUS1.entrySet();
            for (Map.Entry<Integer, Double> set: buf) {
                if(set.getKey()==list.get(i).PID_UID_MEM[0]) {oldCpu=set.getValue(); break;}
            }
            //
            buf = CPUS2.entrySet();
            for (Map.Entry<Integer, Double> set: buf) {
                if(set.getKey()==list.get(i).PID_UID_MEM[0]) {newCPU=set.getValue(); break;}
            }
            double CPUPerc = ((newCPU-oldCpu) / (RefreshInterval/1000))*100;
            if(CPUPerc>=0.0)
                list.get(i).CpuLoadPercents = CPUPerc/cores;
            else list.get(i).CpuLoadPercents = 0.0;

        }
        return list;
    } // gets info about CPU load by each process and transform it to percents
    static Pair<Integer, Double> TakeCPUsAndIDFromString(String aim){
        Pair<Integer,Double> PairResult=new Pair<Integer,Double>(-1,-1.0);
        if(aim.isEmpty()) return PairResult;
        char[] str = aim.toCharArray();

        String buf="";
        for (int i = 11; i < str.length; i++) {
            if(Character.isDigit(str[i])) buf+=str[i];
        }
        PairResult.ID=Integer.valueOf(buf);
        buf="";

        for (int i = 0; i < 11; i++) {
            if(str[i]!=' ') buf+=str[i];
        }
        if(buf!="") PairResult.CPUs= Double.valueOf(buf);
        else PairResult.CPUs=0.0;
        return  PairResult;
    } // get and sort info from the string to pair<int,double>
}
class ProcessInfoSecondMode{
    static OperatingSystemMXBean MxBeanOp = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();//for RAM info
    static Character[] progressBar = new Character[102];//array for RAM Progress bar
    static Character[][] CPUDiagram = new Character[54][64]; // matrix for CPU diagram
    //
    static int time=0; // time using in diagram like time-counter
    private static boolean isWorking;//bool to start or stop process of info ouput
    public static boolean IsWorking() {
        synchronized (ProcessInfoFirstMode.class) {
            return isWorking;
        }
    }
    public static void Start()throws  IOException, InterruptedException{
        isWorking=true;
        time=1;
        CPUDiagram = ClearCPUDiagram(time);
        OutInfo();
    }// start the while cycle
    public static void Stop(){isWorking=false;}//Stops output
    static void OutInfo() {
        while (isWorking) {
            progressBar[0] = '[';
            progressBar[101] = ']';
            for (int i = 1; i < 101; i++)
                progressBar[i] = '-';
            for (int i = 1; i < GetMemoryUsagePercent() + 1; i++) {
                progressBar[i] = '|';
            }
            System.out.println("RAM:\nTotal RAM: " + (int)(GetTotalMemory()/1024)+" MB" + "\nFree RAM: " + (int)(GetFreeMemory()/1024)+" MB" + " Used RAM: " + (int)(GetUsedMemory()/1024)+" MB");
            System.out.println("Progress Bar:\n");
            for (int i = 0; i < progressBar.length; i++)
                System.out.print(progressBar[i]);
            System.out.print(" (" + GetMemoryUsagePercent() + "% usage)\n");
            System.out.println("CPU diagram:\n");
            if (time % 60 == 1) {
                CPUDiagram = ClearCPUDiagram(time);
            }
            CPUDiagram = SetNewData(CPUDiagram, GetGeneralCPUUsage(), time);
            OutCPUDiagram(CPUDiagram);
            time++;

            System.out.println("===============================================================");
            Main_Task.OutDateAndTime();
            System.out.println("===============================================================");
        }

    } // outputs info
    static  double GetFreeMemory(){

        return MxBeanOp.getFreePhysicalMemorySize();
    }
    static  double GetTotalMemory(){

        return  MxBeanOp.getTotalPhysicalMemorySize();
    }
    static  double GetUsedMemory(){

        return  GetTotalMemory()-GetFreeMemory();
    }
    static  int GetGeneralCPUUsage() {
        try {
            Process PowerShellCPU = Runtime.getRuntime().exec("powershell.exe Get-WmiObject win32_processor | Measure-Object -property LoadPercentage -Average | Select Average");
            PowerShellCPU.getOutputStream().close();
            String line;
            BufferedReader stdout = new BufferedReader(new InputStreamReader(PowerShellCPU.getInputStream()));
            String buf_num="";
            while ((line = stdout.readLine()) != null) {
                char[] array = line.toCharArray();
                for (int i = 0; i <array.length ; i++) {
                    if(Character.isDigit(array[i])) buf_num+=array[i];
                }
            }
            return  Integer.valueOf(buf_num);

        } catch (IOException e){}

        return  0;
    }
    static  int GetMemoryUsagePercent(){

        return  (int)((GetUsedMemory()/GetTotalMemory())*100);
    }
    static Character[][] ClearCPUDiagram(int time){
        Character[][] Diagram = new Character[54][64];
        for (int i = 0; i <Diagram.length ; i++) {
            for (int j = 0; j <Diagram[0].length ; j++) {
                Diagram[i][j]=' ';
            }
        }

        //
        for (int i = 0; i < Diagram[0].length; i++)
            Diagram[50][i]='-';
        //
        for (int i = 0; i < Diagram.length-3; i++)
            Diagram[i][3]='|';
        int buf_percents = 100;
        for (int i = 0; i < Diagram.length-3; i++) {
            if(String.valueOf(buf_percents).length()==3){
                Diagram[i][0] = String.valueOf(buf_percents).toCharArray()[0];
                Diagram[i][1] = String.valueOf(buf_percents).toCharArray()[1];
                Diagram[i][2] = String.valueOf(buf_percents).toCharArray()[2];
            } else if(String.valueOf(buf_percents).length()==2){
                Diagram[i][0] = ' ';
                Diagram[i][1] = String.valueOf(buf_percents).toCharArray()[0];
                Diagram[i][2] = String.valueOf(buf_percents).toCharArray()[1];
            } else if(String.valueOf(buf_percents).length()==1){
                Diagram[i][0] = ' ';
                Diagram[i][1] = ' ';
                Diagram[i][2] = String.valueOf(buf_percents).toCharArray()[0];
            }
            buf_percents-=2;

        }
        for (int i = 4; i < 64; i++) {
            if(String.valueOf(time).length()==3){
                Diagram[51][i] = String.valueOf(time).toCharArray()[0];
                Diagram[52][i] = String.valueOf(time).toCharArray()[1];
                Diagram[53][i] = String.valueOf(time).toCharArray()[2];
            } else if(String.valueOf(time).length()==2){
                Diagram[51][i] = String.valueOf(time).toCharArray()[0];
                Diagram[52][i] = String.valueOf(time).toCharArray()[1];
                Diagram[53][i] = ' ';
            } else if(String.valueOf(time).length()==1){
                Diagram[51][i] = String.valueOf(time).toCharArray()[0];
                Diagram[52][i] = ' ';
                Diagram[53][i] = ' ';
            }
            time++;
        }


        return  Diagram;
    } //clears old diagram and add new time
    static Character[][] SetNewData(Character[][] diagram, int Percents, int time){
        Character[][] Diag = diagram;
        int column = 0;
        if(Percents%2!=0) Percents--;
        if(time>=60) column=time%60+3;
        else column = time+3;
        System.out.println(Percents);
        int row = 54 - (Percents/2)-5;
        for (int i = 49; i > row ; i--) {
            Diag[i][column]='*';
        }
        return  Diag;
    } //adds new data to diagram
    static  void OutCPUDiagram(Character[][] diagram){
        for (int i = 0; i <diagram.length ; i++) {
            for (int j = 0; j <diagram[0].length ; j++) {
                System.out.print(diagram[i][j]);
            }
            System.out.println();
        }
    }

}//provides info about CPU and RAM including progress bar and CPU diagram

class Pair<L,R> {

    public  L ID;
    public  R CPUs;

    public Pair(L left, R right) {
        this.ID = left;
        this.CPUs = right;
    }


    @Override
    public int hashCode() { return ID.hashCode() ^ CPUs.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.ID.equals(pairo.ID) &&
                this.CPUs.equals(pairo.CPUs);
    }

}// generalized class of pair
