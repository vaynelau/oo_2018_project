package top.buaaoo.project7;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

public class TestThread implements Runnable, Constant {

    public static synchronized void searchTaxiNumber(int taxiNum) {

        try {
            if (taxiNum < 0 || taxiNum > 79) {
                System.out.println("incorrect taxi number!");
                return;
            }
            Taxi taxi = Main.taxiQueue.get(taxiNum);
            System.out.println("taxi " + taxiNum + " information:");
            System.out.println("current time: " + System.currentTimeMillis());
            System.out.println("current point: " + taxi.getPoint());

            switch (taxi.getStatus()) {
            case STOP:
                System.out.println("current status: STOP");
                break;
            case WAIT:
                System.out.println("current status: WAIT");
                break;
            case SERVE:
                System.out.println("current status: SERVE");
                break;
            case ORDER_RECEIVED:
                System.out.println("current status: ORDER_RECEIVED");
                break;
            default:
                break;
            }
            System.out.println("-----------------------------------------------------");
        }
        catch (Throwable e) {}

    }

    /**
     * static final int STOP = 0; static final int SERVE = 1; static final int WAIT
     * = 2; static final int ORDER_RECEIVED = 3; 参数为数字0,1,2,3或对应的常量
     */
    public static synchronized void searchTaxiStatus(int status) {

        try {
            if (status < 0 || status > 3) {
                System.out.println("incorrect taxi status!");
                return;
            }
            switch (status) {
            case STOP:
                System.out.println("all taxies in status STOP:");
                break;
            case WAIT:
                System.out.println("all taxies in status WAIT:");
                break;
            case SERVE:
                System.out.println("all taxies in status SERVE:");
                break;
            case ORDER_RECEIVED:
                System.out.println("all taxies in status ORDER_RECEIVED:");
                break;
            default:
                break;
            }

            for (Taxi taxi : Main.taxiQueue) {
                if (taxi.getStatus() == status) {
                    System.out.println("taxi " + taxi.taxiNum);
                }
            }

            System.out.println("-----------------------------------------------------");
        }
        catch (Throwable e) {}

    }

    @Override
    public void run() {
        try {

            try {
                Thread.sleep(10000);
            }
            catch (Exception e) {}

            // searchTaxiNumber(10);
            // try {
            // Thread.sleep(1000);
            // }
            // catch (Exception e) {}
            // searchTaxiStatus(STOP);
            // searchTaxiStatus(WAIT);
            // searchTaxiStatus(SERVE);
            // searchTaxiStatus(ORDER_RECEIVED);
        }
        catch (Throwable e) {}
        

    }

}
