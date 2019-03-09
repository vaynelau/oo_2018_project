package top.buaaoo.project9;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

public class Taxi implements Runnable, Constant {

    volatile long credit;
    long waitTime, taxiTime, initialSystemTime;

    int taxiNum;
    volatile Point currentPoint, startingPoint, destinationPoint;
    volatile int currentStatus;
    MapInfo mapInfo;
    TaxiGUI taxiGUI;
    Request request;
    /**
     * @REQUIRES: 0=<num<100;point!=null && 0=<point.x<80 && 0=<point.y<80;0=<status<=3;mapInfo!=null;taxiGUI!=null;
     * @MODIFIES: \this.taxiNum;\this.currentPoint;\this.currentStatus;waitTime;\this.credit;\this.mapInfo;\this.taxiGUI;
     * @EFFECTS: \this.taxiNum == num;
                 \this.currentPoint == point;
                 \this.currentStatus == status;
                 waitTime == 0;
                 \this.credit == 0L;
                 \this.mapInfo == mapInfo;
                 \this.taxiGUI == taxiGUI;
     */
    public Taxi(int num, Point point, int status, MapInfo mapInfo, TaxiGUI taxiGUI) {
        this.taxiNum = num;
        this.currentPoint = point;
        this.currentStatus = status;
        waitTime = 0;
        this.credit = 0L;
        this.mapInfo = mapInfo;
        this.taxiGUI = taxiGUI;

    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == nextPoint;
     */
    private Point getNextPoint() {
        Point point = getPoint();
        Point nextPoint = null;
        int x = point.x;
        int y = point.y;
        int minFlow = Integer.MAX_VALUE;

        boolean[] connected = mapInfo.isConnected(point);
        int[] flow = GUIGV.getFlow(point);

        for (int i = 0; i < connected.length; i++) {
            if (connected[i] && flow[i] < minFlow) {
                minFlow = flow[i];
            }
        }
        while (true) {
            int random = (int) Math.floor(Math.random() * 4);// 在0,1,2,3中产生1个随机数，代表上下左右4个方向
            switch (random) {
            case UP:
                if (connected[UP] && flow[UP] == minFlow) {
                    nextPoint = new Point(x - 1, y);
                }
                break;
            case DOWN:
                if (connected[DOWN] && flow[DOWN] == minFlow) {
                    nextPoint = new Point(x + 1, y);
                }
                break;
            case LEFT:
                if (connected[LEFT] && flow[LEFT] == minFlow) {
                    nextPoint = new Point(x, y - 1);
                }
                break;
            case RIGHT:
                if (connected[RIGHT] && flow[RIGHT] == minFlow) {
                    nextPoint = new Point(x, y + 1);
                }
                break;
            default:
                break;
            }
            if(nextPoint != null) {
                break;
            }
        }
        return nextPoint;
    }
    
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == currentPoint;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized Point getPoint() {
        return currentPoint;
    }
    
    /**
     * @REQUIRES: point != null && 0=<point.x<80 && 0=<point.y<80;
     * @MODIFIES: currentPoint;
     * @EFFECTS: \result == point;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void updatePoint(Point point) {
        this.currentPoint = point;
    }
    
    /**
     * @REQUIRES: 0=<credit<=Long.MAX_VALUE;
     * @MODIFIES: \this.credit;
     * @EFFECTS: \this.credit == point;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void updateCredit(long credit) {
        this.credit = credit;
        
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == credit;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized long getCredit() {
        return credit;
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.currentStatus;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized int getStatus() {
        return this.currentStatus;
    }
    
    /**
     * @REQUIRES: 0=<status<=3;
     * @MODIFIES: waitTime;request;startingPoint;destinationPoint;request.outputFile;\this.currentStatus;
     * @EFFECTS: \this.currentStatus == status;
     *           status == WAIT && currentStatus != WAIT ==> waitTime == 0;
     *           status == ORDER_RECEIVED || status == SERVE ==> request == new Request(currentTime)&&startingPoint == request.srcPoint && destinationPoint == request.dstPoint
     *              &&request.outputFile == new PrintWriter(new BufferedWriter(new FileWriter("request" + request.num + ".txt")));
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void setStatus(int status) {
        if (status == WAIT && currentStatus != WAIT) {
            waitTime = 0;
        } else if (status == ORDER_RECEIVED || status == SERVE) {
            long currentTime = (long) Math.ceil(gv.getTime() / 100.0) * 100;
            request = new Request(currentTime);
            startingPoint = request.srcPoint;
            destinationPoint = request.dstPoint;
            try {
                request.outputFile = new PrintWriter(
                        new BufferedWriter(new FileWriter("request" + request.num + ".txt")));
            }
            catch (Exception e) {}
            request.printRequestInfo();
            request.printTaxiDispatched(this);
        }
        this.currentStatus = status;
    }
    
    /**
     * @REQUIRES: 0=<status<=3;
     * @MODIFIES: waitTime;\this.currentStatus;
     * @EFFECTS: \this.currentStatus == status;
     *           status == WAIT && currentStatus != WAIT ==> waitTime == 0;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void updateStatus(int status) {
        if (status == WAIT && currentStatus != WAIT) {
            waitTime = 0;
        }
        this.currentStatus = status;
    }
    
    /**
     * @REQUIRES: 0=<status<=3;request != null;
     * @MODIFIES: \this.request;startingPoint;destinationPoint;\this.currentStatus;
     * @EFFECTS: \this.currentStatus == status;
     *           status == ORDER_RECEIVED ==> (this.request == request && startingPoint == request.srcPoint && destinationPoint == request.dstPoint);
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void updateStatus(int status, Request request) {
        if (status == ORDER_RECEIVED) {
            this.request = request;
            startingPoint = request.srcPoint;
            destinationPoint = request.dstPoint;
            
        }
        this.currentStatus = status;
    }
    
    /**
     * @REQUIRES: srcPoint!= null && 0=<srcPoint.x<80 && 0=<srcPoint.y<80;dstPoint!=null && 0=<dstPoint.x<80 && 0=<dstPoint.y<80;
     * @MODIFIES: taxiTime;
     * @EFFECTS: taxiTime == ((gv.getTime()-initialSystemTime)/100)*100;
     */
    public void running(Point srcPoint, Point dstPoint) {
        if (srcPoint.equals(dstPoint)) {
            return;
        }
        Vector<Point> shortestPath = GUIInfo.getShortestPath(srcPoint, dstPoint);
        for (Point p : shortestPath) {
            if(!mapInfo.isConnected(getPoint().x, getPoint().y, p.x, p.y)){
                break;
            }
            try {
                long sleepTime = initialSystemTime + taxiTime + 500 - gv.getTime();
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
            catch (Exception e) {}
            updatePoint(p);
            request.printTaxiRunningInfo(this);
            taxiTime += 500;
            taxiGUI.setTaxiStatus(taxiNum, p, getStatus());
        }
        if(!getPoint().equals(dstPoint)) {
            running(getPoint(), dstPoint);
        }
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: credit;
     * @EFFECTS: 每抢一次单信用值加1;
     */
    public void searchRequest() {
        int dx, dy;
        for (Request request : Main.requestQueue) {
            dx = request.srcPoint.x - getPoint().x;
            dy = request.srcPoint.y - getPoint().y;
            if (Math.abs(dx) < 3 && Math.abs(dy) < 3 && !request.isDistributed() && !request.isScramble(taxiNum)) {
                request.setScramble(taxiNum);
                updateCredit(credit + 1);
            }
        }
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: taxiTime;initialSystemTime;
     * @EFFECTS: normal_behavior:
     *           initialSystemTime等于方法开始时的系统时间;
     *           taxiTime == ((gv.getTime()-initialSystemTime)/100)*100;
     *           exception_behavior(Throwable e):
     *           process finished with exit code 0;
     */
    public void run() {

        try {
            taxiTime = 0;
            initialSystemTime = gv.getTime();

            while (true) {
                switch (getStatus()) {

                case STOP:
                    try {
                        long sleepTime = initialSystemTime + taxiTime + 1000 - gv.getTime();
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    }
                    catch (Exception e) {}
                    taxiTime += 1000;

                    if (getStatus() == STOP) {
                        updateStatus(WAIT);
                    } else if (getStatus() == ORDER_RECEIVED || getStatus() == SERVE) {
                        request.printTaxiRunningInfo(this);
                    }
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    break;

                case SERVE:
                    running(getPoint(), destinationPoint);
                    updateCredit(getCredit() + 3);
                    if (getStatus() == SERVE) {
                        updateStatus(STOP);
                    }
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    break;

                case WAIT:
                    Point prePoint = getPoint();
                    searchRequest();
                    
                    try {
                        long sleepTime = initialSystemTime + taxiTime + 500 - gv.getTime();
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    }
                    catch (Exception e) {}
                    taxiTime += 500;
                    waitTime += 500;
                    Point nextPoint = getNextPoint();
                    if(getPoint()==prePoint) {
                        updatePoint(nextPoint);
                    }
                    
                    if (getStatus() == ORDER_RECEIVED || getStatus() == SERVE) {
                        request.printTaxiRunningInfo(this);
                    }
                    if (waitTime == 20000) {
                        waitTime = 0;
                        if (getStatus() == WAIT) {
                            updateStatus(STOP);
                        }
                    }
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    break;

                case ORDER_RECEIVED:

                    running(getPoint(), startingPoint);
                    try {
                        long sleepTime = initialSystemTime + taxiTime + 1000 - gv.getTime();
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    }
                    catch (Exception e) {}
                    taxiTime += 1000;
                    if (getStatus() == ORDER_RECEIVED) {
                        updateStatus(SERVE);
                    }
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    break;

                default:
                    break;

                }

            }
        }
        catch (Throwable e) {
            System.exit(0);
        }

    }

}
