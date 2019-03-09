package top.buaaoo.project9;

import java.awt.Point;
import java.util.Vector;

public class Main implements Constant {

    static Vector<Request> requestQueue = new Vector<Request>();
    static Vector<Taxi> taxiQueue = new Vector<Taxi>(TAXI_NUM);
    static TaxiGUI taxiGUI = new TaxiGUI();
    static MapInfo mapInfo = new MapInfo();
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: taxiQueue;
     * @EFFECTS: 初始化100辆出租车的状态与位置位置，将出租车对象加入队列，创建并启动出租车线程线程
     *           taxiQueue.size() == 100
     */
    private static void taxiInit() {
        int x, y;// 横坐标与纵坐标
        Taxi taxi;
        Thread taxiThread;
        Point point;
        for (int i = 0; i < TAXI_NUM; i++) {
            x = (int) Math.floor(MAP_SIZE * Math.random());
            y = (int) Math.floor(MAP_SIZE * Math.random());
            point = new Point(x, y);
            taxiGUI.setTaxiStatus(i, point, WAIT);
            taxi = new Taxi(i, point, WAIT, mapInfo, taxiGUI);
            taxiQueue.add(taxi);
            taxiThread = new Thread(taxi);
            taxiThread.start();

        }
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: normal_behavior:
     *           初始化地图与出租车位置，创建并启动输入处理线程
     *           exception_behavior(Throwable e):
     *           process finished with exit code 0;
     */
    public static void main(String[] args) {
        try {

            mapInfo.readMap("map.txt");// 在这里设置地图文件路径
            taxiGUI.loadMap(mapInfo.map, MAP_SIZE);
            taxiInit();

            InputHandler inputHandler = new InputHandler(requestQueue);
            new Thread(inputHandler).start();

            // TestThread test = new TestThread();
            // Thread testThread = new Thread(test);
            // testThread.start();

        }
        catch (Throwable e) {
            System.exit(0);
        }

    }

}
