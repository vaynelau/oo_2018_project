package top.buaaoo.project11;

import java.awt.Point;

public class Test implements Constant {
    /**
     * Overview: 实现了程序相关的几个查询接口
     * 
     */

    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == true;
     */
    public boolean repOK() {
        return true;
    }
    
    
    
    /**
     * @REQUIRES: 0<=taxiNum<TAXI_NUM;Main.taxiQueue!=null;
     * @MODIFIES: System.out;
     * @EFFECTS: 
     * 未捕捉到异常 ==> System.out输出出租车的相关信息;
     * 捕捉到异常 ==>do nothing;
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
            Point point = taxi.getCurrentPoint();
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
     * @REQUIRES: 0<=status<4;Main.taxiQueue!=null;
     * @MODIFIES: System.out;
     * @EFFECTS: 未捕捉到异常 ==> System.out输出所有处于该状态的出租车的编号;
     * 捕捉到异常 ==>do nothing;
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
                    System.out.print("taxi " + taxi.getTaxiNum()+" ");
                }
            }
            System.out.println();
            System.out.println("-----------------------------------------------------");
        }
        catch (Throwable e) {}

    }


}
