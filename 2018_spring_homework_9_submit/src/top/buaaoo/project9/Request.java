package top.buaaoo.project9;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.Arrays;

public class Request implements Constant {
    
    static int count = 0;
    int num;
    long time;
    Point srcPoint, dstPoint;
    volatile boolean[] scrambleTaxi = new boolean[TAXI_NUM];
    volatile boolean distributed, simulate;
    PrintWriter outputFile;
    
    /**
     * @REQUIRES: 0=<num<=Integer.MAX_VALUE;0=<time<=Long.MAX_VALUE;
     *            0=<srcPoint.x<80;0=<srcPoint.y<80;0=<dstPoint.x<80;0=<dstPoint.y<80;
     * @MODIFIES: \this.num;\this.time;\this.srcPoint;\this.dstPoint;\this.distributed;\this.simulate;scrambleTaxi;
     * @EFFECTS: \this.num == count-1;
                 \this.time == time;
                 \this.srcPoint == srcPoint;
                 \this.dstPoint == dstPoint;
                 \this.distributed == true;
                 \this.simulate == false;
                 \all int i;0=<i<TAXI_NUM;scrambleTaxi[i] == false;
     */
    public Request(int num, long time, Point srcPoint, Point dstPoint) {
        this.num = count++;
        this.time = time;
        this.srcPoint = srcPoint;
        this.dstPoint = dstPoint;
        this.distributed = true;
        this.simulate = false;
        Arrays.fill(scrambleTaxi, false);
    }
    
    
    /**
     * @REQUIRES: 0=<time<=Long.MAX_VALUE;
     * @MODIFIES: \this.num;\this.time;\this.srcPoint;\this.dstPoint;\this.distributed;\this.simulate;
     * @EFFECTS: \this.num == count-1;
                 \this.time == time;
                 \this.srcPoint == new Point(1, 1);
                 \this.dstPoint == new Point(10, 10);
                 \this.distributed == true;
                 \this.simulate == true;
     */
    public Request(long time) {
        this.num = count++;
        this.time = time;
        this.srcPoint = new Point(1, 1);
        this.dstPoint = new Point(10, 10);
        this.distributed = true;
        this.simulate = true;
    }

    /**
     * @REQUIRES: request!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (time == request.time && srcPoint.equals(request.srcPoint) && dstPoint.equals(request.dstPoint));
     * 
     */
    public boolean equals(Request request) {
        if (time == request.time && srcPoint.equals(request.srcPoint) && dstPoint.equals(request.dstPoint)) {
            return true;
        }
        return false;
    }
    
    /**
     * @REQUIRES: 0=<taxiNum<TAXI_NUM;
     * @MODIFIES: None;
     * @EFFECTS: \result == scrambleTaxi[taxiNum];
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized boolean isScramble(int taxiNum) {
        return scrambleTaxi[taxiNum];
    }
    
    /**
     * @REQUIRES: 0=<taxiNum<TAXI_NUM;
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
     * @REQUIRES: 0=<taxiNum<TAXI_NUM;;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出参与抢单的出租车的相关信息;
     */
    public void printTaxiInfo(Taxi taxi) {
        outputFile.println("taxies participating in grabbing the order:");
        outputFile.println("taxi number: " + taxi.taxiNum);
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
     * @REQUIRES: None;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出没有汽车可以派单的信息;
     */
    public void printNoTaxi() {
        outputFile.println("no taxi available!");
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();
    }
    
    /**
     * @REQUIRES: 0=<taxiNum<TAXI_NUM;;
     * @MODIFIES: outputFile;
     * @EFFECTS: 向outputFile中输出被派单的出租车的相关信息;
     */
    public void printTaxiDispatched(Taxi taxi) {
        outputFile.println("the taxi being dispatched:");
        outputFile.println("taxi number: " + taxi.taxiNum);
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
     * @REQUIRES: 0=<taxiNum<TAXI_NUM;;
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
