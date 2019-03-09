package top.buaaoo.project13;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String REGEX_FR = "\\(FR,\\+?\\d{1,8},(UP|DOWN),\\+?\\d{1,18}\\)";
    private static final String REGEX_ER = "\\(ER,\\+?\\d{1,8},\\+?\\d{1,18}\\)";
    private static Pattern pFR = Pattern.compile(REGEX_FR);
    private static Pattern pER = Pattern.compile(REGEX_ER);

    public static void main(String[] args) {
        try {
            RequestQueue reqQueue = new RequestQueue();
            Scanner scan = new Scanner(System.in);
            for (int i = 0; i < 100; i++) {
                String str = scan.nextLine();
                str = str.replaceAll(" +", ""); // 去掉字符串里的空格
                if (str.equals("RUN")) {
                    break;
                }
                Matcher mFR = pFR.matcher(str);
                Matcher mER = pER.matcher(str);
                if (mFR.matches()) { // 利用正则表达式进行初步的格式匹配
                    String[] strSplit = str.split("[,\\)]");
                    int floor = Integer.parseInt(strSplit[1]);
                    long time = Long.parseLong(strSplit[3]);
                    String direction = strSplit[2];
                    if (Request.checkFormat(floor, time, direction) == true) {
                        Request FR = new Request(floor, time, direction);
                        reqQueue.enQueue(FR); // 将FR加入队列
                    } else {
                        System.out.println("INVALID[" + str + "]");
                    }
                } else if (mER.matches()) { // 利用正则表达式进行初步的格式匹配
                    String[] strSplit = str.split("[,\\)]");
                    int floor = Integer.parseInt(strSplit[1]);
                    long time = Long.parseLong(strSplit[2]);
                    String direction = "ER";  //表示该请求为电梯内请求
                    if (Request.checkFormat(floor, time, direction) == true) {
                        Request ER = new Request(floor, time, direction);
                        reqQueue.enQueue(ER); // 将ER加入队列
                    } else {
                        System.out.println("INVALID[" + str + "]");
                    }
                } else {
                    System.out.println("INVALID[" + str + "]");
                }
            }
            scan.close();
            Elevator elevator = new Elevator();
            ALS_Scheduler scheduler = new ALS_Scheduler(reqQueue, elevator);
            while (!reqQueue.isEmpty()) {
                Request reqTemp = reqQueue.deQueue();
                Request mainReq;
                if (!reqTemp.getIsExecuted() && !reqTemp.getIsSame()) {
                    mainReq = reqTemp;
                    scheduler.updateStatus(elevator.getFloor(), mainReq.getFloor());
                    elevator.setTime(Math.max(elevator.getTime(), mainReq.getTime()));
                    if (elevator.getStatus().equals("STILL")) {
                        scheduler.runStill(mainReq);
                        continue;
                    } 
                    do {
                        scheduler.runUpOrDown(mainReq);
                        reqTemp = mainReq;
                        mainReq = scheduler.changeMainRequest(mainReq);
                    } while (mainReq != reqTemp);
                } 
            }
        }
        catch (Throwable t) {
            return;
        }
    }
}
