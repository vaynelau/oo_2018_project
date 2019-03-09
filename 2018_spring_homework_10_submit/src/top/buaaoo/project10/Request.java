package top.buaaoo.project10;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class Request implements Constant {
    /**
     * Overview: 请求类，主要保存请求的各项信息，向文件中输出请求的相关调度信息
     * 
     */
    
    private static int count = 0;
    private static final Object lock = new Object();
    long time;
    Point srcPoint, dstPoint;
    private volatile boolean[] scrambleTaxi = new boolean[TAXI_NUM];
    private volatile boolean distributed, simulate;
    private PrintWriter outputFile;

    /**
     * @REQUIRES: 0<=num<=Integer.MAX_VALUE;0<=time<=Long.MAX_VALUE;
     *            srcPoint!=null;0<=srcPoint.x<80;0<=srcPoint.y<80;dstPoint!=null;0<=dstPoint.x<80;0<=dstPoint.y<80;
     * @MODIFIES: \this.time;\this.srcPoint;\this.dstPoint;\this.distributed;\this.simulate;scrambleTaxi;
     * @EFFECTS: \this.time == time; \this.srcPoint == srcPoint; \this.dstPoint ==
     *           dstPoint; \this.distributed == true; \this.simulate == false; \all
     *           int i;0<=i<TAXI_NUM;scrambleTaxi[i] == false;
     */
    public Request(int num, long time, Point srcPoint, Point dstPoint) {
        this.time = time;
        this.srcPoint = srcPoint;
        this.dstPoint = dstPoint;
        this.distributed = true;
        this.simulate = false;
        Arrays.fill(scrambleTaxi, false);
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == ((0<=time&& time<=Long.MAX_VALUE&&count >= 0)&&
     *           (srcPoint!=null && 0<=srcPoint.x&& srcPoint.x<80 && 0<=srcPoint.y
     *           && srcPoint.y<80)&& (dstPoint!=null && 0<=dstPoint.x&&
     *           dstPoint.x<80 && 0<=dstPoint.y && dstPoint.y<80)&& (distributed ==
     *           true|| distributed == false)&& (simulate == true|| simulate ==
     *           false)&& (scrambleTaxi!=null && lock!=null));
     */
    public boolean repOK() {
        if (!(0 <= time && time <= Long.MAX_VALUE && count >= 0)) {
            return false;
        }
        if (!(srcPoint != null && 0 <= srcPoint.x && srcPoint.x < 80 && 0 <= srcPoint.y && srcPoint.y < 80)) {
            return false;
        }
        if (!(dstPoint != null && 0 <= dstPoint.x && dstPoint.x < 80 && 0 <= dstPoint.y && dstPoint.y < 80)) {
            return false;
        }
        if (!(distributed == true || distributed == false)) {
            return false;
        }
        if (!(simulate == true || simulate == false)) {
            return false;
        }
        if (!(scrambleTaxi != null && lock != null)) {
            return false;
        }
        return true;
    }

    /**
     * @REQUIRES: 0<=time<=Long.MAX_VALUE;
     * @MODIFIES: \this.time;\this.srcPoint;\this.dstPoint;\this.distributed;\this.simulate;
     * @EFFECTS: \this.time == time; \this.srcPoint.x == 1 && \this.srcPoint.y == 1;
     *           \this.dstPoint.x == 10 && \this.dstPoint.y == 10; \this.distributed
     *           == true; \this.simulate == true;
     */
    public Request(long time) {
        this.time = time;
        this.srcPoint = new Point(1, 1);
        this.dstPoint = new Point(10, 10);
        this.distributed = true;
        this.simulate = true;
    }

    /**
     * @REQUIRES: request!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (time == request.time &&
     *           srcPoint.equals(request.srcPoint) &&
     *           dstPoint.equals(request.dstPoint));
     * 
     */
    public boolean equals(Request request) {
        if (time == request.time && srcPoint.equals(request.srcPoint) && dstPoint.equals(request.dstPoint)) {
            return true;
        }
        return false;
    }

    /**
     * @REQUIRES: 0<=taxiNum<TAXI_NUM;
     * @MODIFIES: None;
     * @EFFECTS: \result == scrambleTaxi[taxiNum];
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized boolean isScramble(int taxiNum) {
        return scrambleTaxi[taxiNum];
    }

    /**
     * @REQUIRES: 0<=taxiNum<TAXI_NUM;
     * @MODIFIES: scrambleTaxi;
     * @EFFECTS: scrambleTaxi[taxiNum]==true;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void setScramble(int taxiNum) {
        scrambleTaxi[taxiNum] = true;
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == distributed;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized boolean isDistributed() {
        return distributed;
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: distributed;
     * @EFFECTS: distributed == true;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void setDistributed() {
        distributed = true;
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: distributed;
     * @EFFECTS: distributed == false;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void setUnDistributed() {
        distributed = false;
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: outputFile;count;
     * @EFFECTS: 未发生异常 ==>outputFile!=null&&count==\old(count)+1;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(lock);
     */
    public void createOutputFile() {
        synchronized (lock) {
            try {
                outputFile = new PrintWriter(new BufferedWriter(new FileWriter("request" + count + ".txt")));
                count++;
            }
            catch (Exception e) {}
        }

    }

    /**
     * @REQUIRES: outputFile!=null;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出请求相关的信息;
     */
    public void printRequestInfo() {
        outputFile.println("request information:");
        outputFile.println("time: " + time);
        outputFile.println("starting point: [" + srcPoint.x + "," + srcPoint.y + "]");
        outputFile.println("destination point: [" + dstPoint.x + "," + dstPoint.y + "]");
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();

    }

    /**
     * @REQUIRES: 0<=taxiNum<TAXI_NUM;outputFile!=null;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出参与抢单的出租车的相关信息;
     */
    public void printTaxiInfo(Taxi taxi) {
        outputFile.println("taxies participating in grabbing the order:");
        outputFile.println("taxi number: " + taxi.getTaxiNum());
        Point point = taxi.getPoint();
        outputFile.println("current point: [" + point.x + "," + point.y + "]");
        switch (taxi.getStatus()) {
        case STOP:
            outputFile.println("current status: STOP");
            break;
        case WAIT:
            outputFile.println("current status: WAIT");
            break;
        case SERVE:
            outputFile.println("current status: SERVE");
            break;
        case ORDER_RECEIVED:
            outputFile.println("current status: ORDER_RECEIVED");
            break;

        default:
            break;
        }
        outputFile.println("current credit: " + taxi.getCredit());
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();

    }

    /**
     * @REQUIRES: outputFile!=null;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出没有汽车可以派单的信息;
     */
    public void printNoTaxi() {
        outputFile.println("no taxi available!");
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();
    }

    /**
     * @REQUIRES: 0<=taxiNum<TAXI_NUM;outputFile!=null;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出被派单的出租车的相关信息;
     */
    public void printTaxiDispatched(Taxi taxi) {
        outputFile.println("the taxi being dispatched:");
        outputFile.println("taxi number: " + taxi.getTaxiNum());
        Point point = taxi.getPoint();
        outputFile.println("current point: [" + point.x + "," + point.y + "]");
        if (simulate) {
            outputFile.println("dispatch time: " + (long) Math.ceil(System.currentTimeMillis() / 100.0) * 100);
        } else {
            outputFile.println("dispatch time: " + (System.currentTimeMillis() / 100) * 100);
        }

        outputFile.println("-------------------------------------------------------");
        outputFile.flush();
    }

    /**
     * @REQUIRES: 0<=taxiNum<TAXI_NUM;outputFile!=null;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出出租车的运行信息;
     */
    public void printTaxiRunningInfo(Taxi taxi) {
        outputFile.println("taxi running information:");
        if (taxi.getPoint().equals(srcPoint)) {
            outputFile.println("the taxi arrived at the passenger location.");
        } else if (taxi.getPoint().equals(dstPoint)) {
            outputFile.println("the taxi arrived at the destination.");
        }
        Point point = taxi.getPoint();
        outputFile.println("current point: [" + point.x + "," + point.y + "]");
        outputFile.println("current time: " + (long) Math.ceil(System.currentTimeMillis() / 100.0) * 100);
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();
    }

}
