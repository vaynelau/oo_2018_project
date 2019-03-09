package top.buaaoo.project10;

import java.awt.Point;
import java.util.Vector;

public class Taxi implements Runnable, Constant {
    /**
     * Overview: 出租车线程类，保存出租车的各项状态信息，线程执行出租车的正常运行
     * 
     */
    
    
    private volatile long credit;
    private long waitTime, taxiTime, initialSystemTime;
    private boolean serveFirst = false;

    private int taxiNum;
    private volatile Point currentPoint, lastPoint, startingPoint, destinationPoint;
    private volatile int currentStatus;
    private MapInfo mapInfo;
    private TaxiGUI taxiGUI;
    private Request request;

    /**
     * @REQUIRES: 0<=num<100;point!=null && 0<=point.x<80 &&
     *            0<=point.y<80;0<=status<=3;mapInfo!=null;taxiGUI!=null;
     * @MODIFIES: \this.taxiNum;\this.currentPoint;\this.currentStatus;waitTime;\this.credit;\this.mapInfo;\this.taxiGUI;
     * @EFFECTS: \this.taxiNum == num; \this.currentPoint == point;
     *           \this.currentStatus == status; waitTime == 0; \this.credit == 0L;
     *           \this.mapInfo == mapInfo; \this.taxiGUI == taxiGUI;
     */
    public Taxi(int num, Point point, int status, MapInfo mapInfo, TaxiGUI taxiGUI) {
        this.taxiNum = num;
        this.currentPoint = point;
        this.lastPoint = point;
        this.currentStatus = status;
        waitTime = 0;
        this.credit = 0L;
        this.mapInfo = mapInfo;
        this.taxiGUI = taxiGUI;

    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == ((taxiNum>=0 && taxiNum<100)&&(currentPoint!=null && 0<=currentPoint.x&& 
     * currentPoint.x<80 && 0<=currentPoint.y && currentPoint.y<80)&&(lastPoint!=null &&
     *  0<=lastPoint.x&& lastPoint.x<80 && 0<=lastPoint.y && lastPoint.y<80)&&
     *  (0<=currentStatus&& currentStatus<=3)&&(mapInfo!=null && taxiGUI!=null)&&(waitTime>=0 && credit>=0));
     */
    public boolean repOK() {
        if(!(taxiNum>=0 && taxiNum<100)) {
            return false;
        }
        if(!(currentPoint!=null && 0<=currentPoint.x&& currentPoint.x<80 && 0<=currentPoint.y && currentPoint.y<80)) {
            return false;
        }
        if(!(lastPoint!=null && 0<=lastPoint.x&& lastPoint.x<80 && 0<=lastPoint.y && lastPoint.y<80)) {
            return false;
        }
        if(!(0<=currentStatus&& currentStatus<=3)) {
            return false;
        }
        if(!(mapInfo!=null && taxiGUI!=null)){
            return false;
        }
        if(!(waitTime>=0 && credit>=0)) {
            return false;
        }
        return true;
    }
    
    
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.taxiNum;
     */
    public int getTaxiNum() {
        return this.taxiNum;
    }
    
    
    
    
    /**
     * @REQUIRES: mapInfo!=null;MapInfo.graph!=null;MapInfo.flowMap!=null;
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
        int[] flow = MapInfo.getFlow(point);

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
            if (nextPoint != null) {
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
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == lastPoint;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized Point getLastPoint() {
        return lastPoint;
    }

    /**
     * @REQUIRES: point != null && 0<=point.x<80 && 0<=point.y<80;MapInfo.flowMap!=null;
     * @MODIFIES: \this.lastPoint;\this.currentPoint;MapInfo.flowMap;
     * @EFFECTS: \this.lastPoint == \old(\this.currentPoint);\this.currentPoint ==
     *           point;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    private synchronized void updatePoint(Point point) {
        this.lastPoint = this.currentPoint;
        this.currentPoint = point;
        MapInfo.addFlow(lastPoint.x, lastPoint.y,currentPoint.x, currentPoint.y);
    }

    /**
     * @REQUIRES: point != null && 0<=point.x<80 && 0<=point.y<80;
     * @MODIFIES: \this.lastPoint;\this.currentPoint;
     * @EFFECTS: \this.lastPoint == point;\this.currentPoint == point;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void setPoint(Point point) {
        this.lastPoint = point;
        this.currentPoint = point;

    }

    /**
     * @REQUIRES: 0<=credit<=Integer.MAX_VALUE;
     * @MODIFIES: \this.credit;
     * @EFFECTS: \this.credit == credit;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    private synchronized void updateCredit(long credit) {
        this.credit = credit;
    }

    /**
     * @REQUIRES: 0<=credit<=Integer.MAX_VALUE;
     * @MODIFIES: \this.credit;
     * @EFFECTS: \this.credit == credit;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void setCredit(long credit) {
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
     * @REQUIRES: 0<=status<=3;
     * @MODIFIES: request;startingPoint;destinationPoint;request.outputFile;\this.currentStatus;serveFirst;
     * @EFFECTS: \this.currentStatus == status; (status == ORDER_RECEIVED || status
     *           == SERVE) ==> (request != null && startingPoint == request.srcPoint
     *           && destinationPoint == request.dstPoint && request.outputFile !=
     *           null);status==SERVE ==>serveFirst == true; 
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void setStatus(int status) {
        if (status == ORDER_RECEIVED || status == SERVE) {
            long currentTime = (long) Math.ceil(gv.getTime() / 100.0) * 100;
            request = new Request(currentTime);
            startingPoint = request.srcPoint;
            destinationPoint = request.dstPoint;
            request.createOutputFile();
            request.printRequestInfo();
            request.printTaxiDispatched(this);
            if (status == SERVE) {
                serveFirst = true;
            }

        }
        this.currentStatus = status;
    }

    /**
     * @REQUIRES: 0<=status<=3;
     * @MODIFIES: waitTime;\this.currentStatus;
     * @EFFECTS: \this.currentStatus == status; status == WAIT && currentStatus !=
     *           WAIT ==> waitTime == 0;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    private synchronized void updateStatus(int status) {
        if (status == WAIT && currentStatus != WAIT) {
            waitTime = 0;
        }
        this.currentStatus = status;
    }

    /**
     * @REQUIRES: 0<=status<=3;request != null;
     * @MODIFIES: \this.request;startingPoint;destinationPoint;\this.currentStatus;
     * @EFFECTS: \this.currentStatus == status; status == ORDER_RECEIVED ==>
     *           (this.request == request && startingPoint == request.srcPoint &&
     *           destinationPoint == request.dstPoint);
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
     * @REQUIRES: srcPoint!= null && 0<=srcPoint.x<80 &&
     *            0<=srcPoint.y<80;dstPoint!=null && 0<=dstPoint.x<80 &&
     *            0<=dstPoint.y<80;mapInfo !=null;taxiGUI!=null;request!=null;
     * @MODIFIES: taxiTime;
     * @EFFECTS: taxiTime == ((gv.getTime()-initialSystemTime)/100)*100;
     */
    private void running(Point srcPoint, Point dstPoint) {
        if (srcPoint.equals(dstPoint)) {
            return;
        }
        Vector<Point> shortestPath = GUIInfo.getShortestPath(srcPoint, dstPoint);
        long sleepTime;
        for (Point p : shortestPath) {
            Point nowPoint = getPoint();
            Point nextPoint = p;
            int light = Light.getLight(nowPoint);
            if ((light == 1 || light == 2) && !isPassable(nowPoint, nextPoint, light)) {
                try {
                    sleepTime = Light.getRemainingTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                        taxiTime += sleepTime;
                    }
                }
                catch (Exception e) {}
            }
            if (!mapInfo.isConnected(nowPoint.x, nowPoint.y, nextPoint.x, nextPoint.y)) {
                break;
            }
            try {
                sleepTime = initialSystemTime + taxiTime + 500 - gv.getTime();
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
            catch (Exception e) {}
            updatePoint(p);
            taxiTime += 500;
            taxiGUI.setTaxiStatus(taxiNum, p, getStatus());
            request.printTaxiRunningInfo(this);
        }
        if (!getPoint().equals(dstPoint)) {
            running(getPoint(), dstPoint);
        }
    }

    /**
     * @REQUIRES: Main.requestQueue!=null;
     * @MODIFIES: credit;
     * @EFFECTS: credit == \old(credit) + 抢单次数;
     */
    private void searchRequest() {
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
     * @REQUIRES: nowPoint!= null && 0<=nowPoint.x<80 &&
     *            0<=nowPoint.y<80;nextPoint!=null && 0<=nextPoint.x<80 &&
     *            0<=dnextPoint.y<80;light==1|| light==2;
     * @MODIFIES: None;
     * @EFFECTS: 可以通过红绿灯==>\result==true;
     *          不可以通过红绿灯==>\result==false;
     */
    private boolean isPassable(Point nowPoint, Point nextPoint, int light) {
        Point lastPoint = getLastPoint();

        if (lastPoint == nowPoint) {
            lastPoint = new Point(nowPoint.x - 1, nowPoint.y);
        }
        if (!lastPoint.equals(nextPoint) && light == 2) {
            if (lastPoint.x == nowPoint.x && nowPoint.x == nextPoint.x) {
                return false;
            }
            if (lastPoint.x - 1 == nowPoint.x && nowPoint.y - 1 == nextPoint.y) {
                return false;
            }
            if (lastPoint.x + 1 == nowPoint.x && nowPoint.y + 1 == nextPoint.y) {
                return false;
            }
        }

        if (!lastPoint.equals(nextPoint) && light == 1) {
            if (lastPoint.y == nowPoint.y && nowPoint.y == nextPoint.y) {
                return false;
            }
            if (lastPoint.y + 1 == nowPoint.y && nowPoint.x - 1 == nextPoint.x) {
                return false;
            }
            if (lastPoint.y - 1 == nowPoint.y && nowPoint.x + 1 == nextPoint.x) {
                return false;
            }
        }

        return true;
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: taxiTime;initialSystemTime;
     * @EFFECTS: 未捕捉到异常 ==> initialSystemTime==方法开始时的系统时间;出租车正常运行;
     *           捕捉到异常 ==> 程序退出;
     */
    public void run() {

        try {
            taxiTime = 0;
            initialSystemTime = System.currentTimeMillis();
            long sleepTime;
            while (true) {
                switch (getStatus()) {
                case STOP:
                    sleepTime = initialSystemTime + taxiTime + 1000 - gv.getTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                    taxiTime += 1000;
                    if (getStatus() == STOP) {
                        updateStatus(WAIT);
                    }
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    break;

                case SERVE:
                    if (serveFirst) {
                        serveFirst = false;
                        request.printTaxiRunningInfo(this);
                    }
                    running(getPoint(), destinationPoint);
                    updateCredit(getCredit() + 3);
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    updateStatus(STOP);
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    break;

                case WAIT:
                    Point nowPoint = getPoint();
                    searchRequest();
                    Point nextPoint = getNextPoint();
                    int light = Light.getLight(nowPoint);
                    if ((light == 1 || light == 2) && !isPassable(nowPoint, nextPoint, light)) {
                        sleepTime = Light.getRemainingTime();
                        if (sleepTime > 0) {
                            taxiTime += sleepTime;
                            Thread.sleep(sleepTime);
                        }
                        if (!mapInfo.isConnected(nowPoint.x, nowPoint.y, nextPoint.x, nextPoint.y)) {
                            break;
                        }
                    }
                    sleepTime = initialSystemTime + taxiTime + 500 - gv.getTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                    taxiTime += 500;
                    waitTime += 500;
                    updatePoint(nextPoint);
                    //MapInfo.addFlow(nowPoint.x, nowPoint.y, nextPoint.x, nextPoint.y);
                    taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                    if (waitTime >= 20000) {
                        waitTime = 0;
                        if (getStatus() == WAIT) {
                            updateStatus(STOP);
                            taxiGUI.setTaxiStatus(taxiNum, getPoint(), getStatus());
                        }
                    }
                    break;

                case ORDER_RECEIVED:
                    request.printTaxiRunningInfo(this);
                    running(getPoint(), startingPoint);
                    sleepTime = initialSystemTime + taxiTime + 1000 - gv.getTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                    taxiTime += 1000;
                    updateStatus(SERVE);
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
