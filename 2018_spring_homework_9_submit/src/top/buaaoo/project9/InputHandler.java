package top.buaaoo.project9;

import java.awt.Point;
import java.io.File;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;

public class InputHandler implements Runnable, Constant {

    Vector<Request> requestQueue;
    int count = 0;
    
    /**
     * @REQUIRES: requestQueue != null;
     * @MODIFIES: this.requestQueue;
     * @EFFECTS: this.requestQueue == requestQueue;
     */
    public InputHandler(Vector<Request> requestQueue) {
        this.requestQueue = requestQueue;
    }
    
    /**
     * @REQUIRES: req != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (\exist int i;0=<i<requestQueue.size();req.equals(requestQueue.get(i)));
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
     * @REQUIRES: scan != null;
     * @MODIFIES: None;
     * @EFFECTS: 若LOAD文件中指明了地图文件，则更新当前地图，否则保持当前地图不变;
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
                Main.taxiGUI.updateMap(Main.mapInfo.map, MAP_SIZE);
                str = scan.nextLine();
            }

        }

    }
    
    /**
     * @REQUIRES: scan != null;
     * @MODIFIES: None;
     * @EFFECTS: 若LOAD文件中指定某些道路流量的基础值，则将对应道路流量初始值更新，其余道路流量为0，否则保持当前道路流量值不变;
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
            boolean flag =false;
            while (!"#end_flow".equals(str)) {
                // (2,3) (3,4) 2
                if(!flag) {
                    flag =true;
                    GUIGV.clearMemFlow();
                }
                str = str.replace(" ", "");
                strArray = str.split("[\\(\\),]");
                x1 = Integer.parseInt(strArray[1]);
                y1 = Integer.parseInt(strArray[2]);
                x2 = Integer.parseInt(strArray[4]);
                y2 = Integer.parseInt(strArray[5]);
                count = Integer.parseInt(strArray[6]);
                GUIGV.setFlow(x1, y1, x2, y2, count);
                str = scan.nextLine();
            }

        }

    }
    
    /**
     * @REQUIRES: scan != null;
     * @MODIFIES: None;
     * @EFFECTS: 若LOAD文件中指定某些出租车的状态、信用和位置信息，则将对应出租车信息更新，未说明的出租车位置仍按原来的状态随机运行;
     */
    private void setTaxi(Scanner scan) {
        String str = null;
        String[] strArray;
        while (scan.hasNextLine()) {
            str = scan.nextLine();
            if ("#taxi".equals(str)) {
                break;
            }

        }
        if ("#taxi".equals(str)) {
            str = scan.nextLine();
            int num, status, x, y;
            long credit;
            while (!"#end_taxi".equals(str)) {
                // 服务状态取值为0，接单状态取值为1，等待服务取值为2，停止状态取值为3。
                strArray = str.split("[ \\(\\),]");
                num = Integer.parseInt(strArray[0]);
                status = Integer.parseInt(strArray[1]);
                credit = Long.parseLong(strArray[2]);
                x = Integer.parseInt(strArray[4]);
                y = Integer.parseInt(strArray[5]);
                Taxi taxi = Main.taxiQueue.get(num);
                taxi.updatePoint(new Point(x, y));
                taxi.updateCredit(credit);
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
                taxi.taxiGUI.setTaxiStatus(num, new Point(x, y), taxi.getStatus());
                str = scan.nextLine();
            }

        }
    }
    
    /**
     * @REQUIRES: scan != null;
     * @MODIFIES: None;
     * @EFFECTS: 若LOAD文件中指定某些同时发出的请求，则按控制台输入请求的处理方式进行处理;如果中间内容为空，则不响应请求;
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

        }
    }
    
    /**
     * @REQUIRES: inputStr != null;
     * @MODIFIES:System.out;
     * @EFFECTS: normal_behavior:
     *           按LOAD文件内容进行相关的初始化设置
     *           文件不存在时输出无效信息
     *           exception_behavior(Throwable e):
     *           process finished with exit code 0;
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
     * @REQUIRES: str != null;0=<currentTime<=Long.MAX_VALUE;
     * @MODIFIES: requestQueue;count;System.out;
     * @EFFECTS: (!srcPoint.equals(dstPoint) && !isSameRequest(request)) ==> requestQueue.size()==\old(requestQueue.size())+1,count==\old(count)+1;
     *           (srcPoint.equals(dstPoint) || isSameRequest(request)) ==> System.out输出提示信息并返回
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
     * @REQUIRES: str != null;
     * @MODIFIES: None;
     * @EFFECTS: 根据输入字符串信息对地图上已存在的道路进行关闭和打开操作
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
        System.out.println("set road failed["+str+"]");
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: System.out;
     * @EFFECTS: normal_behavior:
     *           对控制台输入的信息根据不同的类型调用对应方法分类进行处理，对无效输入System.out进行输出提示
     *           exception_behavior(Throwable e):
     *           process finished with exit code 0;
     */
    public void run() {

        boolean load = false;

        try {
            Scanner scan = new Scanner(System.in);
            Matcher matcher;
            for(int i=0;i<MAX_INPUT_LINES;i++) {
                
                String inputStr = scan.nextLine();
                long currentTime = (long) Math.ceil(gv.getTime() / 100.0) * 100;

                if (!load && count == 0) {
                    matcher = pLOAD.matcher(inputStr);
                    if (matcher.matches()) {
                        load = loadFile(inputStr);
                        continue;
                    }
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
            scan.close();
        }
        catch (Throwable e) {
            System.exit(0);
        }
    }

}
