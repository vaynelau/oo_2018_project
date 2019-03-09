package top.buaaoo.project10;

import java.awt.Point;
import java.io.File;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;

public class InputHandler implements Runnable, Constant {
    /**
     * Overview: 输入处理类，主要负责从控制台读入相关字符串并进行相应处理，根据输入对系统进行相应的初始化
     * 
     */
    
    private Vector<Request> requestQueue;
    private int count = 0;

    /**
     * @REQUIRES: requestQueue != null;
     * @MODIFIES: this.requestQueue;
     * @EFFECTS: this.requestQueue == requestQueue;
     */
    public InputHandler(Vector<Request> requestQueue) {
        this.requestQueue = requestQueue;
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == (requestQueue != null);
     */
    public boolean repOK() {
        return requestQueue != null;
    }
    
    
    /**
     * @REQUIRES: inputStr != null;Main.mapInfo!=null;Light.lightMap != null;MapInfo.flowMap!=null;Main.taxiQueue!=null;requestQueue!=null;GUIInfo.graph!=null;GUIGV.guiInfo.map!=null;
     * @MODIFIES:System.out;Main.mapInfo;Light.lightMap;MapInfo.flowMap;Main.taxiQueue;requestQueue;GUIInfo.graph;GUIGV.guiInfo.map;
     * @EFFECTS: LoadFile文件正确 ==> 地图，红绿灯，流量，出租车，请求初始化 && \result==true;
     *           LoadFile文件不存在 ==> System.out控制台输出无效提示信息 && \result==false;
     *           LoadFile文件错误或有其他异常发生 ==> 程序结束;
     */
    private boolean loadFile(String inputStr) {
        String[] strArray = inputStr.split("[\\[\\]]");
        File loadFile = new File(strArray[1]);
        if (!loadFile.exists()) {
            System.out.println("INVALID[" + inputStr + "]");
            return false;
        }
        try {
            Scanner scan = new Scanner(loadFile);
            setMap(scan);
            setLight(scan);
            setFlow(scan);
            setTaxi(scan);
            setRequest(scan);
        }
        catch (Throwable e) {
            System.exit(0);
        }

        return true;
    }

    /**
     * @REQUIRES: scan != null;Main.mapInfo!=null;
     * @MODIFIES: Main.mapInfo;
     * @EFFECTS: LoadFile文件正确 ==>装入指定地图或默认地图文件;
     *           LoadFile文件错误或有其他异常发生 ==>程序结束; 
     */
    private void setMap(Scanner scan) {
        String str = null;
        while (scan.hasNextLine()) {
            str = scan.nextLine();
            if ("#map".equals(str)) {
                break;
            }

        }
        if ("#map".equals(str)) {
            str = scan.nextLine();
            if (!"#end_map".equals(str)) {
                Main.mapInfo.readMap(str);
                str = scan.nextLine();
            } else {
                Main.mapInfo.readMap("map.txt");
            }
            //Main.taxiGUI.loadMap(Main.mapInfo.map, MAP_SIZE);
        }else {
            System.exit(0);
        }

    }
    
    /**
     * @REQUIRES: scan != null;Light.lightMap != null;
     * @MODIFIES: Light.lightMap;
     * @EFFECTS: LoadFile文件正确 ==>装入指定红绿灯文件;
     *           LoadFile文件错误或有其他异常发生 ==>程序结束; 
     */
    private void setLight(Scanner scan) {
        String str = null;
        while (scan.hasNextLine()) {
            str = scan.nextLine();
            if ("#light".equals(str)) {
                break;
            }
        }
        if ("#light".equals(str)) {
            str = scan.nextLine();
            if (!"#end_light".equals(str)) {
                Light trafficLight = new Light();
                trafficLight.readLight(str);
                new Thread(trafficLight).start();
                str = scan.nextLine();
            }
        }else {
            System.exit(0);
        }

    }

    /**
     * @REQUIRES: scan != null;MapInfo.flowMap!=null;
     * @MODIFIES: MapInfo.flowMap;
     * @EFFECTS: LoadFile文件正确 ==>将文件中指定道路流量初始值更新，其余道路流量为0;
     *           LoadFile文件错误或有其他异常发生 ==>程序结束;
     */
    private void setFlow(Scanner scan) {
        String str = null;
        String[] strArray;
        while (scan.hasNextLine()) {
            str = scan.nextLine();
            if ("#flow".equals(str)) {
                break;
            }
        }
        if ("#flow".equals(str)) {
            str = scan.nextLine();
            int x1, y1, x2, y2, count;
            while (!"#end_flow".equals(str)) {
                str = str.replace(" ", "");
                strArray = str.split("[\\(\\),]");
                x1 = Integer.parseInt(strArray[1]);
                y1 = Integer.parseInt(strArray[2]);
                x2 = Integer.parseInt(strArray[4]);
                y2 = Integer.parseInt(strArray[5]);
                count = Integer.parseInt(strArray[6]);
                MapInfo.setFlow(x1, y1, x2, y2, count);
                str = scan.nextLine();
            }
        }else {
            System.exit(0);
        }

    }
    
    
    /**
     * @REQUIRES: scan != null;Main.taxiQueue!=null;Main.taxiGUI!=null;
     * @MODIFIES: Main.taxiQueue;
     * @EFFECTS: LoadFile文件正确 ==>将对应出租车设置为文件中指定的状态、信用和位置信息，未说明的出租车位置随机状态空闲，初始化100辆出租车;
     *           LoadFile文件错误或有其他异常发生 ==>程序结束;
     */
    private void setTaxi(Scanner scan) {
        String str = null;
        String[] strArray;
        Taxi taxi;
        Point point;
        int num, status, x, y;
        long credit;
        while (scan.hasNextLine()) {
            str = scan.nextLine();
            if ("#taxi".equals(str)) {
                break;
            }
        }
        if ("#taxi".equals(str)) {
            str = scan.nextLine();
            for (int i = 0; i < TAXI_NUM; i++) {
                x = (int) Math.floor(MAP_SIZE * Math.random());
                y = (int) Math.floor(MAP_SIZE * Math.random());
                point = new Point(x, y);
                taxi = new Taxi(i, point, WAIT, Main.mapInfo, Main.taxiGUI);
                Main.taxiQueue.add(taxi);
            }

            while (!"#end_taxi".equals(str)) {
                // 服务状态取值为0，接单状态取值为1，等待服务取值为2，停止状态取值为3。
                strArray = str.split("[ \\(\\),]");
                num = Integer.parseInt(strArray[0]);
                status = Integer.parseInt(strArray[1]);
                credit = Long.parseLong(strArray[2]);
                x = Integer.parseInt(strArray[4]);
                y = Integer.parseInt(strArray[5]);
                taxi = Main.taxiQueue.get(num);
                taxi.setPoint(new Point(x, y));
                taxi.setCredit(credit);
                switch (status) {
                case 0:
                    taxi.setStatus(SERVE);
                    break;
                case 1:
                    taxi.setStatus(RECEIVE_ORDER);
                    break;
                case 2:
                    taxi.setStatus(WAIT);
                    break;
                case 3:
                    taxi.setStatus(STOP);
                    break;
                default:
                    break;
                }
                str = scan.nextLine();
            }

            for (Taxi t : Main.taxiQueue) {
                Main.taxiGUI.setTaxiStatus(t.getTaxiNum(), t.getPoint(), t.getStatus());
                new Thread(t).start();
            }
        }else {
            System.exit(0);
        }
    }

    /**
     * @REQUIRES: scan != null;requestQueue!=null;
     * @MODIFIES: requestQueue;System.out;
     * @EFFECTS: LoadFile文件正确 ==>将文件中指定的请求进行处理，如果中间内容为空，则不响应请求;
     *           LoadFile文件错误或有其他异常发生 ==>程序结束;
     */
    private void setRequest(Scanner scan) {
        String str = null;
        while (scan.hasNextLine()) {
            str = scan.nextLine();
            if ("#request".equals(str)) {
                break;
            }

        }
        if ("#request".equals(str)) {
            str = scan.nextLine();
            long currentTime = (long) Math.ceil(gv.getTime() / 100.0) * 100;
            while (!"#end_request".equals(str)) {
                Matcher mREQUEST = pREQUEST.matcher(str);
                if (!mREQUEST.matches()) {
                    System.out.println("INVALID[" + str + "]");

                } else {
                    sendRequest(str, currentTime);
                }
                str = scan.nextLine();
            }
        }else {
            System.exit(0);
        }
    }

    /**
     * @REQUIRES: str != null;0<=currentTime<=Long.MAX_VALUE;
     * @MODIFIES: requestQueue;count;System.out;
     * @EFFECTS: (!srcPoint.equals(dstPoint) && !isSameRequest(request)) ==>
     *           requestQueue.size()==\old(requestQueue.size())+1 && count==\old(count)+1;
     *           (srcPoint.equals(dstPoint) || isSameRequest(request)) ==>
     *           System.out输出提示信息并返回
     */
    private void sendRequest(String str, long currentTime) {
        String[] strArray = str.split("[\\(\\),]");
        Point srcPoint = new Point(Integer.parseInt(strArray[2]), Integer.parseInt(strArray[3]));
        Point dstPoint = new Point(Integer.parseInt(strArray[6]), Integer.parseInt(strArray[7]));
        Request request = new Request(count, currentTime, srcPoint, dstPoint);

        if (srcPoint.equals(dstPoint) || isSameRequest(request)) {
            System.out.println("INVALID[" + str + "]");
            return;
        }

        RequestScheduler requestScheduler = new RequestScheduler(request);
        new Thread(requestScheduler).start();
        requestQueue.add(request);
        count++;
    }

    /**
     * @REQUIRES: req != null;requestQueue!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (\exist int i;0<=i<requestQueue.size();req.equals(requestQueue.get(i)));
     */
    private boolean isSameRequest(Request req) {
        for (Request reqTemp : requestQueue) {
            if (req.equals(reqTemp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @REQUIRES: str != null;Main.mapInfo!=null;GUIInfo.graph!=null;GUIGV.guiInfo.map!=null;MapInfo.flowMap!=null;
     * @MODIFIES: GUIInfo.graph;GUIGV.guiInfo.map;MapInfo.flowMap;System.out;
     * @EFFECTS: 对地图上原本已存在的道路进行合法操作 ==>根据输入字符串信息对进道路行关闭和打开操作;
     *           对地图上原本不存在的道路进行操作或操作不合法 ==>System.out输出提示信息;
     */
    private void setRoad(String str) {
        String[] strArray = str.split("[\\(\\),]");
        int x1, y1, x2, y2, status;
        x1 = Integer.parseInt(strArray[1]);
        y1 = Integer.parseInt(strArray[2]);
        x2 = Integer.parseInt(strArray[3]);
        y2 = Integer.parseInt(strArray[4]);
        status = Integer.parseInt(strArray[5]);
        if (status == 0 && Main.mapInfo.isConnected(x1, y1, x2, y2)) {
            Main.taxiGUI.setRoadStatus(new Point(x1, y1), new Point(x2, y2), status);
            return;
        }
        if (status == 1 && Main.mapInfo.isConnectedFirst(x1, y1, x2, y2)) {
            Main.taxiGUI.setRoadStatus(new Point(x1, y1), new Point(x2, y2), status);
            return;
        }
        System.out.println("set road failed[" + str + "]");
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: System.out;
     * @EFFECTS: 未捕捉到异常 ==> 对控制台输入的信息根据不同的类型调用对应方法分类进行处理，对无效输入System.out进行输出提示;
     *           捕捉到异常 ==> 程序退出;
     */
    public void run() {

        boolean load = false;

        try {
            @SuppressWarnings("resource")
            Scanner scan = new Scanner(System.in);
            Matcher matcher;
            while (true) {

                String inputStr = scan.nextLine();
                long currentTime = (long) Math.ceil(gv.getTime() / 100.0) * 100;

                matcher = pLOAD.matcher(inputStr);
                if (!load && matcher.matches()) {
                    load = loadFile(inputStr);
                    continue;
                }

                matcher = pSET_ROAD.matcher(inputStr);
                if (matcher.matches()) {
                    for (String str : inputStr.split(";")) {
                        setRoad(str);
                    }
                    continue;
                }

                matcher = pSEARCH_TAXI_NUM.matcher(inputStr);
                if (matcher.matches()) {
                    String[] strArray = inputStr.split("[\\(\\)]");
                    Test.searchTaxiNum(Integer.parseInt(strArray[1]));
                    continue;
                }

                matcher = pSEARCH_TAXI_STATUS.matcher(inputStr);
                if (matcher.matches()) {
                    String[] strArray = inputStr.split("[\\(\\)]");
                    Test.searchTaxiStatus(Integer.parseInt(strArray[1]));
                    continue;
                }

                matcher = pREQUEST.matcher(inputStr);
                if (matcher.matches()) {
                    sendRequest(inputStr, currentTime);
                } else {
                    System.out.println("INVALID[" + inputStr + "]");
                }
            }
        }
        catch (Throwable e) {
            System.exit(0);
        }
    }

}
