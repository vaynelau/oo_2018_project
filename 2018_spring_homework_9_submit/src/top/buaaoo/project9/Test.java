package top.buaaoo.project9;

import java.awt.Point;

public class Test implements Runnable, Constant {
    
    
    /**
     * @REQUIRES: 0=<taxiNum<TAXI_NUM;
     * @MODIFIES: System.out;
     * @EFFECTS: normal_behavior:
     *           System.out输出出租车的相关信息;
     *           exception_behavior(Throwable e):
     *           do nothing;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
     */
    public static synchronized void searchTaxiNum(int taxiNum) {

        try {
            if (taxiNum < 0 || taxiNum >= TAXI_NUM) {
                System.out.println("incorrect taxi number!");
                return;
            }
            Taxi taxi = Main.taxiQueue.get(taxiNum);
            System.out.println("taxi " + taxiNum + " information:");
            System.out.println("current time: " + System.currentTimeMillis());
            Point point = taxi.getPoint();
            System.out.println("current point: [" + point.x + "," + point.y + "]");

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
     * 0代表STOP,1代表SERVE,2代表WAIT,3代表ORDER_RECEIVED;
     * @REQUIRES: 0=<status<4;
     * @MODIFIES: System.out;
     * @EFFECTS: normal_behavior:
     *           System.out输出所有处于该状态的出租车的编号;
     *           exception_behavior(Throwable e):
     *           do nothing;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked();
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
                    System.out.print("taxi " + taxi.taxiNum+" ");
                }
            }
            System.out.println();
            System.out.println("-----------------------------------------------------");
        }
        catch (Throwable e) {}

    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: normal_behavior:
     *           System.out输出出租车的相关信息;
     *           exception_behavior(Throwable e):
     *           process finished with exit code 0;
     */
    public void run() {
        try {
            
        }
        catch (Throwable e) {
            System.exit(0);
        }

    }

}
