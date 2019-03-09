package top.buaaoo.project7;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class OrderDistribute implements Runnable,Constant {

    Request request;

    public OrderDistribute(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        try {
            try {
                long sleepTime = request.time- Tools.getTime();//
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
            catch (Exception e) {}
            request.setUnDistributed();
            request.outputFile = new PrintWriter(new BufferedWriter(new FileWriter("request" + request.num + ".txt")));
            request.printRequestInfo();
            Main.taxiGUI.RequestTaxi(request.srcPoint, request.dstPoint);
            try {
                long sleepTime = request.time + 3000 - Tools.getTime();//
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
            catch (Exception e) {}
            request.setDistributed();
            long maxCredit = Long.MIN_VALUE;
            boolean isScrambling = false;
            Taxi taxi, dstTaxi = null;
            for (int i = 0; i < 100; i++) {
                if (request.isScrambling(i)) {
                    taxi = Main.taxiQueue.get(i);
                    request.printTaxiInfo(taxi);
                    if ((taxi.getStatus() == WAIT || taxi.getStatus() == STOP) && taxi.getCredit() > maxCredit) {
                        maxCredit = taxi.getCredit();
                        isScrambling = true;
                    }
                }

            }
            if (!isScrambling) {
                // System.out.println("no taxi available!");//
                request.printNoTaxi();
                return;
            }

            int minDistance = Integer.MAX_VALUE;
            for (int i = 0; i < 100; i++) {
                taxi = Main.taxiQueue.get(i);
                if (request.isScrambling(i) && taxi.getCredit() == maxCredit
                        && (taxi.getStatus() == WAIT || taxi.getStatus() == STOP)) {
                    int distance = GUIInfo.getDistance(taxi.getPoint(), request.srcPoint);
                    if (minDistance > distance) {
                        minDistance = distance;
                        dstTaxi = taxi;
                    }
                }

            }
            if (dstTaxi != null) {
                dstTaxi.updateStatus(ORDER_RECEIVED, request);
                // System.out.println("the order is received by taxi " + dstTaxi.taxiNum);
                request.printTaxiDispatched(dstTaxi);
            } else {
                // System.out.println("no taxi available!");//
                request.printNoTaxi();
                return;
            }

        }
        catch (Throwable e) {}

    }

}
