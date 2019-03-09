package top.buaaoo.project5;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.BufferedWriter;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String REGEX_FR = "\\(FR,\\+?\\d{1,8},(UP|DOWN)\\)";
    private static final String REGEX_ER = "\\(ER,#[123],\\+?\\d{1,8}\\)";
    private static Pattern pFR = Pattern.compile(REGEX_FR);
    private static Pattern pER = Pattern.compile(REGEX_ER);
    static long initialTime;

    public static void main(String[] args) {

        try {
            RequestQueue reqQueue = new RequestQueue();

            // File file = new File("result.txt");
            // if (!file.exists()) {
            // file.createNewFile();
            // }
            // PrintWriter OutputFile = new PrintWriter(new BufferedWriter(new
            // FileWriter("result.txt")));
            PrintWriter OutputFile = new PrintWriter("result.txt", "utf-8");
            OutputHandler.setPrintWriter(OutputFile);

            MultiThreadScheduler scheduler = new MultiThreadScheduler(reqQueue);
            Thread schedulerThread = new Thread(scheduler);
            schedulerThread.start();

            Scanner scan = new Scanner(System.in);
            for (int i = 0; i < 50; i++) {
                // if (i == 1) {
                // try {
                // Thread.sleep(10000);
                // }
                // catch (InterruptedException e) {
                // }
                // }
                // if (i == 2) {
                // try {
                // Thread.sleep(1000);
                // }
                // catch (InterruptedException e) {
                // }
                // }
                // if (i == 3) {
                // try {
                // Thread.sleep(1000);
                // }
                // catch (InterruptedException e) {
                // }
                // }

                String str = scan.nextLine();
                long currentTime = new Date().getTime();

                if (i == 0) {
                    initialTime = currentTime;
                }
                double T = (currentTime - initialTime) / 1000.0;
                T = new BigDecimal(T).setScale(1, RoundingMode.HALF_UP).doubleValue();// 是否四舍五入
                str = str.replaceAll(" +", ""); // 去掉字符串里的空格
                if (str.equals("END")) {

                    break;
                }

                String[] strArray = str.split(";", 11);
                for (int j = 0; j < Math.min(strArray.length, 10); j++) {
                    Matcher mFR = pFR.matcher(strArray[j]);
                    Matcher mER = pER.matcher(strArray[j]);
                    if (mFR.matches()) { // 利用正则表达式进行初步的格式匹配
                        Floor.makeFloorRequest(strArray[j], T, reqQueue);
                    }
                    else if (mER.matches()) { // 利用正则表达式进行初步的格式匹配
                        Elevator.makeElevatorRequest(strArray[j], T, reqQueue);
                    }
                    else {
                        OutputHandler.printInvalidRequest(strArray[j], T);
                    }
                }
                if (strArray.length > 10) {
                    for (int j = 10; j < strArray.length; j++) {
                        OutputHandler.printInvalidRequest(strArray[j], T);
                    }
                }
                // try {
                // Thread.sleep(1);
                // } catch (InterruptedException e) {
                // }

            }
            scan.close();
            // Thread.currentThread().setDaemon(true);
            // scan.nextLine();
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
            while (FloorRequestHandler.getFRHandlerThreadNum() != 0 || !Floor.isFree()) {
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                }

            }

            scheduler.stopThread();
            // try {
            // schedulerThread.join();
            // }
            // catch (InterruptedException e) {
            // }
            //

            // System.out.println("mainThread is end.");
            // Scheduler scheduler = new Scheduler(reqQueue);
            // ALS_Scheduler scheduler = new ALS_Scheduler(reqQueue);
            // scheduler.work();

            // OutputFile.close();
            // System.exit(0);
        }
        catch (Throwable e) {
            // e.printStackTrace();//////////////////////////////////////////////
            // return;
            System.exit(0);
        }
    }
}
