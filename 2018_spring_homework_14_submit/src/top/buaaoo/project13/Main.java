package top.buaaoo.project13;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    /*
     * OVERVIEW: Main类，负责处理输入请求，检查请求的有效性，以及控制调度器的正常工作等;
     * 表示对象：Boolean isFirst, long lastTime;
     * 抽象函数：AF(c) = {isFirst, lastTime} where c.isFirst == isFirst, c.lastTime == lastTime;
     * 不变式：(0<=c.lastTime<=4294967295L);
     */
    
    private static Boolean isFirst=true;  //isFirst代表是否有可能成为第一个有效输入
    private static long lastTime=0; //lastTime代表上一个有效请求的时间
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: ((lastTime >=0) && (lastTime<=4294967295L)) ==> (\result == true);
     *           (!((lastTime >=0) && (lastTime<=4294967295L))) ==> (\result == false);
     */
    public boolean repOK() {
        return ((lastTime >=0) && (lastTime<=4294967295L));
    }
    
    
    /**
     * @REQUIRES: (direction != null && (direction.equals("UP") || direction.equals("DOWN") || direction.equals("ER")));
     * @MODIFIES: isFirst;lastTime;
     * @EFFECTS: (!(floor<1 || floor>10) && !(floor==1 && "DOWN".equals(direction) || floor==10 && "UP".equals(direction)) && !(time<0 || time>4294967295L) && isFirst==true && (floor==1 && "UP".equals(direction) && time==0)) ==> (\result == true && isFirst==false);
     *  (!(floor<1 || floor>10) && !(floor==1 && "DOWN".equals(direction) || floor==10 && "UP".equals(direction)) && !(time<0 || time>4294967295L) && isFirst==false && time >= lastTime) ==> (\result == true && lastTime == time);
     *  ((floor<1 || floor>10) || (floor==1 && "DOWN".equals(direction) || floor==10 && "UP".equals(direction)) || (time<0 || time>4294967295L) || isFirst==true && !(floor==1 && "UP".equals(direction) && time==0) || time < lastTime) ==> (\result == false);
     */
    private static boolean checkFormat(int floor, long time, String direction) {
        if(floor<1 || floor>10) {
            return false;
        }
        if(floor==1 && "DOWN".equals(direction) || floor==10 && "UP".equals(direction)) {
            return false;
        }
        if(time<0 || time>4294967295L) {
            return false;
        }
        if(isFirst) {
            if(!(floor==1 && "UP".equals(direction) && time==0)) {
                return false; 
            }
            else {
                isFirst = false; 
            }
        }
        else {
            if(time < lastTime) {
                return false;
            }
            else {
                lastTime = time;
            }
        }
        return true;
    }
    
    /**
     * @REQUIRES: reqQueue != null;
     * @MODIFIES: reqQueue;System.out;
     * @EFFECTS: (\all Request request;request为从System.in中读取到的字符串str对应的有效的请求;reqQueue.contains(request));
     *           (\all String str;str为从System.in中读取到的不合法输入;System.out有对应的无效提示输出);
     */
    private static void inputHandle(RequestQueue reqQueue) {
        
        String REGEX_FR = "\\(FR,\\+?\\d{1,8},(UP|DOWN),\\+?\\d{1,18}\\)";
        String REGEX_ER = "\\(ER,\\+?\\d{1,8},\\+?\\d{1,18}\\)";
        Pattern pFR = Pattern.compile(REGEX_FR);
        Pattern pER = Pattern.compile(REGEX_ER);
        
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
                if (checkFormat(floor, time, direction) == true) {
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
                if (checkFormat(floor, time, direction) == true) {
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
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: System.out;
     * @EFFECTS: 未catch到任何异常 ==> System.out输出所有System.in获得请求的调度结果;
     *           catch到有异常发生 ==> 程序退出;
     */
    public static void main(String[] args) {
        try {
            RequestQueue reqQueue = new RequestQueue();
            inputHandle(reqQueue);
            Elevator elevator = new Elevator();
            Scheduler scheduler = new Scheduler(reqQueue, elevator);
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
            System.exit(0);
        }
    }
}
