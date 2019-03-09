package top.buaaoo.project7;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.Arrays;

public class Request implements Constant{


    int num;
    long time;
    Point srcPoint, dstPoint;
    volatile boolean[] scramblingTaxi = new boolean[110];
    volatile boolean distributed;
    PrintWriter outputFile;

    public Request(int num, long time, Point srcPoint, Point dstPoint) {
        this.num = num;
        this.time = time;
        this.srcPoint = srcPoint;
        this.dstPoint = dstPoint;
        this.distributed = true;
        Arrays.fill(scramblingTaxi, false);
    }

    public boolean equals(Request request) {
        if (time == request.time && srcPoint.equals(request.srcPoint) && dstPoint.equals(request.dstPoint)) {
            return true;
        }
        return false;
    }

    public synchronized boolean isScrambling(int taxiNum) {
        return scramblingTaxi[taxiNum];
    }

    public synchronized void setScrambling(int taxiNum) {
        scramblingTaxi[taxiNum] = true;
    }

    public synchronized boolean isDistributed() {
        return distributed;
    }

    public synchronized void setDistributed() {
        distributed = true;
    }
    public synchronized void setUnDistributed() {
        distributed = false;
    }

    public void printRequestInfo() {
        outputFile.println("request information:");
        outputFile.println("time: " + time);
        outputFile.println("starting point: " + srcPoint);
        outputFile.println("destination point: " + dstPoint);
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();

    }

    public void printTaxiInfo(Taxi taxi) {
        outputFile.println("taxies participating in grab the order:");
        outputFile.println("taxi number: " + taxi.taxiNum);
        outputFile.println("current point: " + taxi.getPoint());
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

    public void printNoTaxi() {
        outputFile.println("no taxi available!");
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();
    }

    public void printTaxiDispatched(Taxi taxi) {
        outputFile.println("the taxi being dispatched:");
        outputFile.println("taxi number: " + taxi.taxiNum);
        outputFile.println("current point: " + taxi.getPoint());
        outputFile.println("dispatch time: " + System.currentTimeMillis());
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();
    }

    public void printTaxiRunningInfo(Taxi taxi) {
        outputFile.println("taxi Running information:");
        if (taxi.getPoint().equals(srcPoint)) {
            outputFile.println("the taxi arrived at the passenger location.");
        } else if (taxi.getPoint().equals(dstPoint)) {
            outputFile.println("the taxi arrived at the destination.");
        }
        outputFile.println("current point: " + taxi.getPoint());
        outputFile.println("current time: " + (long) Math.ceil(System.currentTimeMillis() / 100.0) * 100);
        // outputFile.println("current time: " + System.currentTimeMillis());
        outputFile.println("-------------------------------------------------------");
        outputFile.flush();
    }

}
