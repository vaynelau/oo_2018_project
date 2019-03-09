package top.buaaoo.project7;

import java.awt.Point;
import java.util.Vector;

public class Taxi implements Runnable,Constant {


    volatile long credit;
    long waitTime, taxiTime, initialSystemTime;

    int taxiNum;
    volatile Point currentPoint, startingPoint, destinationPoint;
    volatile int status;
    MapInfo mapInfo;
    TaxiGUI taxiGUI;
    Request request;

    public Taxi(int num, Point point, int status, MapInfo mapInfo, TaxiGUI taxiGUI) {
        this.taxiNum = num;
        this.currentPoint = point;
        this.status = status;
        waitTime = 0;
        this.credit = 0L;
        this.mapInfo = mapInfo;
        this.taxiGUI = taxiGUI;

    }

    public Point getNextPoint() {
        int x, y, random;
        // 孤立点死循环问题
        x = (int) currentPoint.getX();
        y = (int) currentPoint.getY();
        while (true) {
            random = (int) Math.floor(Math.random() * 4);// 在0,1,2,3中产生1个随机数，代表上下左右4个方向
            switch (random) {
            case UP:
                if (x > 0 && (mapInfo.map[x - 1][y] == 2 || mapInfo.map[x - 1][y] == 3)) {
                    return new Point(x - 1, y);
                }

                break;
            case DOWN:
                if (x < 79 && (mapInfo.map[x][y] == 2 || mapInfo.map[x][y] == 3)) {
                    return new Point(x + 1, y);
                }
                break;
            case LEFT:
                if (y > 0 && (mapInfo.map[x][y - 1] == 1 || mapInfo.map[x][y - 1] == 3)) {
                    return new Point(x, y - 1);
                }
                break;
            case RIGHT:
                if (y < 79 && (mapInfo.map[x][y] == 1 || mapInfo.map[x][y] == 3)) {
                    return new Point(x, y + 1);
                }
                break;

            default:
                break;
            }

        }
    }

    public synchronized Point getPoint() {
        return currentPoint;
    }

    public synchronized void updatePoint(Point point) {
        this.currentPoint = point;
    }

    public synchronized void updateCredit(long credit) { 
        this.credit = credit;
    }

    public synchronized long getCredit() { 
        return credit;
    }

    public synchronized void updateStatus(int status) {
        if (status == WAIT) {
            waitTime = 0;
        }
        this.status = status;
    }

    public synchronized void updateStatus(int status, Request request) {
        if (status == ORDER_RECEIVED) {
            this.request = request;
            startingPoint = request.srcPoint;
            destinationPoint = request.dstPoint;
            this.status = status;
        }

    }

    public synchronized int getStatus() {
        return this.status;
    }

    public void running(Point srcPoint, Point dstPoint) {
        if (srcPoint.equals(dstPoint)) {
            return;
        }
        Vector<Point> shortestPath = GUIInfo.pointBFS(srcPoint, dstPoint);
        for (Point p : shortestPath) {
            try {
                long sleepTime = initialSystemTime + taxiTime + 200 - Tools.getTime();
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
            catch (Exception e) {}
            updatePoint(p);
            request.printTaxiRunningInfo(this);
            taxiTime += 200;
            taxiGUI.SetTaxiStatus(taxiNum, p, getStatus());
        }
        // System.out.println(dstPoint);
    }

    public void searchRequest() {
        int dx, dy;
        for (Request request : Main.requestQueue) {
            dx = request.srcPoint.x - currentPoint.x;
            dy = request.srcPoint.y - currentPoint.y;
            if (Math.abs(dx) < 3 && Math.abs(dy) < 3 && !request.isDistributed() && !request.isScrambling(taxiNum)) {
                request.setScrambling(taxiNum);
                updateCredit(credit + 1);
            }
        }
    }

    @Override
    public void run() {

        try {
            taxiTime = 0;
            initialSystemTime = Tools.getTime();

            while (true) {
                switch (getStatus()) {

                case STOP:
                    try {
                        long sleepTime = initialSystemTime + taxiTime + 1000 - Tools.getTime();
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    }
                    catch (Exception e) {}
                    taxiTime += 1000;

                    if (getStatus() == STOP) {
                        updateStatus(WAIT);
                    } else if (getStatus() == ORDER_RECEIVED) {
                        request.printTaxiRunningInfo(this);
                    }
                    taxiGUI.SetTaxiStatus(taxiNum, currentPoint, getStatus());
                    break;

                case SERVE:
                    running(currentPoint, destinationPoint);
                    updateCredit(getCredit() + 3);
                    updateStatus(STOP);
                    taxiGUI.SetTaxiStatus(taxiNum, currentPoint, getStatus());
                    break;

                case WAIT:
                    searchRequest();
                    Point nextPoint = getNextPoint();
                    try {
                        long sleepTime = initialSystemTime + taxiTime + 200 - Tools.getTime();
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    }
                    catch (Exception e) {}
                    taxiTime += 200;
                    waitTime += 200;
                    updatePoint(nextPoint);
                    if (getStatus() == ORDER_RECEIVED) {
                        request.printTaxiRunningInfo(this);
                    }
                    if (waitTime == 20000) {
                        waitTime = 0;
                        if (getStatus() == WAIT) { // 在Readme中说明
                            updateStatus(STOP);
                        }
                    }
                    taxiGUI.SetTaxiStatus(taxiNum, nextPoint, getStatus());

                    break;

                case ORDER_RECEIVED:

                    running(currentPoint, startingPoint);
                    try {
                        long sleepTime = initialSystemTime + taxiTime + 1000 - Tools.getTime();
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    }
                    catch (Exception e) {}
                    taxiTime += 1000;
                    updateStatus(SERVE);
                    taxiGUI.SetTaxiStatus(taxiNum, currentPoint, getStatus());
                    break;

                default:
                    break;

                }

            }
        }
        catch (Throwable e) {}

    }

}
