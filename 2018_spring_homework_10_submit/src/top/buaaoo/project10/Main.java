package top.buaaoo.project10;

import java.util.Vector;

public class Main implements Constant {
    /**
     * Overview: 程序的主入口，初始化出租车队列和请求队列，地图，以及gui相关信息，类中的main方法负责启动输入线程
     * 
     */
    
    static final Vector<Request> requestQueue = new Vector<Request>();
    static final Vector<Taxi> taxiQueue = new Vector<Taxi>(TAXI_NUM);
    static final TaxiGUI taxiGUI = new TaxiGUI();
    static final MapInfo mapInfo = new MapInfo();
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == (requestQueue != null && taxiQueue != null && taxiGUI != null && mapInfo != null);
     */
    public boolean repOK() {
        return requestQueue != null && taxiQueue != null && taxiGUI != null && mapInfo != null;
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: 未捕捉到异常 ==> 输入处理线程启动;
     *           捕捉到异常 ==> 程序退出;
     */
    public static void main(String[] args) {
        try {
            InputHandler inputHandler = new InputHandler(requestQueue);
            Thread thread = new Thread(inputHandler);
            thread.start();

        }
        catch (Throwable e) {
            System.exit(0);
        }

    }

}
