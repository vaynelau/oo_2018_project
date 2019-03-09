package top.buaaoo.project6;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHandler {

    private static final String REGEX = "IF \\[.+\\] (renamed|modified|path-changed|size-changed) THEN (record-summary|record-detail|recover)";
    private static Pattern pREGEX = Pattern.compile(REGEX);
    private static ArrayList<Thread> monitoringThreadQueue = new ArrayList<Thread>();
    public static void readMonitoringJobs() {

        Scanner scan = new Scanner(System.in);
        String[] inputStrArray = new String[500];
        File[] MonitoringObjectArray = new File[30];
        int count1 = 0, count2 = 0;
        for (int i = 0; i < 500; i++) {
            String str = scan.nextLine();
            if (str.equals("END")) {
                break;
            }
            Matcher mREGEX = pREGEX.matcher(str);

            if (!mREGEX.matches()) {
                System.out.println("INVALID[" + str + "]");
                continue;
            }

            
            String[] strArray = str.split("[\\[\\]]");

            File monitoringObject = new File(strArray[1]);

            if (!monitoringObject.exists()) { // 是否使用文件安全类
                System.out.println("The monitoring object does not exist.");
                continue;
            }
            if (!monitoringObject.isAbsolute()) {
                System.out.println("The input path is not an absolute path.");
                continue;
            }
            
            int sameFlag = 0;
            for (int j = 0; j < count1; j++) {
                String[] s = inputStrArray[j].split("[\\[\\]]");
                File f = new File(s[1]);
                if (strArray[2].equals(s[2]) && f.equals(monitoringObject)) {
                    sameFlag = 1;
                    break;
                }
            }
            if (sameFlag == 1) {
                System.out.println("SAME[" + str + "]");////////////////////////////////
                continue;
            }

            
            String[] strArray2 = strArray[2].split(" ");

            if (strArray2[3].equals("recover")
                    && (strArray2[1].equals("modified") || strArray2[1].equals("size-changed"))) {
                System.out.println(strArray2[1] + " does not match with recover.");
                continue;

            }
            
            sameFlag = 0;
            for (int j = 0; j < count2; j++) {
                if (monitoringObject.equals(MonitoringObjectArray[j])) {
                    sameFlag = 1;
                    break;
                }
            }
            if (sameFlag == 0) {
                if (count2 >= 10) {
                    System.out.println("The monitoring object is equal to 10.");
                    continue;
                }
                MonitoringObjectArray[count2++] = monitoringObject;
            }


            inputStrArray[count1++] = str;

            MonitoringJob job = new MonitoringJob(monitoringObject, strArray2[3], str);
            switch (strArray2[1]) { // 将监控作业加入监控队列
            case "renamed":
                RenamedTrigger renamed = new RenamedTrigger(job);
                Thread t1 = new Thread(renamed);
                monitoringThreadQueue.add(t1);
                break;
            case "modified":
                ModifiedTrigger modified = new ModifiedTrigger(job);
                Thread t2 = new Thread(modified);
                monitoringThreadQueue.add(t2);
                break;
            case "path-changed":
                PathChangedTrigger pathChanged = new PathChangedTrigger(job);
                Thread t3 = new Thread(pathChanged);
                monitoringThreadQueue.add(t3);
                break;
            case "size-changed":
                SizeChangedTrigger sizeChanged = new SizeChangedTrigger(job);
                Thread t4 = new Thread(sizeChanged);
                monitoringThreadQueue.add(t4);
                break;

            default:
                break;
            }

        }
        scan.close();
        
        for (Thread t : monitoringThreadQueue) {
            t.start();
        }

    }

}
