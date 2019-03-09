package top.buaaoo.project7;

import java.awt.Point;
import java.util.Vector;

public class Main implements Constant{

    

    static Vector<Request> requestQueue = new Vector<Request>(300);
    static Vector<Taxi> taxiQueue = new Vector<Taxi>(100);
    static TaxiGUI taxiGUI = new TaxiGUI();
    static MapInfo mapInfo = new MapInfo();

    private static void taxiInitialize() {

        int x, y;// 横坐标与纵坐标
        Taxi taxi;
        Thread taxiThread;
        Point point;
        for (int i = 0; i < TAXI_NUM; i++) {
            x = (int) Math.floor(MAP_SIZE * Math.random());
            y = (int) Math.floor(MAP_SIZE * Math.random());
            point = new Point(x, y);
            taxiGUI.SetTaxiStatus(i, point, WAIT);
            taxi = new Taxi(i, point, WAIT, mapInfo, taxiGUI);
            taxiQueue.add(taxi);
            taxiThread = new Thread(taxi);
            taxiThread.start();

        }

        // taxiGUI.SetTaxiStatus(1, new Point(1, 1), 1);
        // gv.stay(1000);// 暂停1s
        //
        // gv.stay(1000);
        // taxiGUI.SetTaxiStatus(1, new Point(1, 3), 1);
        // gv.stay(1000);
        // taxiGUI.SetTaxiStatus(1, new Point(2, 3), 1);
        // gv.stay(1000);
        // taxiGUI.SetTaxiStatus(1, new Point(2, 4), 1);
        // gv.stay(1000);
        // taxiGUI.SetTaxiStatus(1, new Point(3, 4), 1);
        // gv.stay(1000);
        // taxiGUI.SetTaxiStatus(1, new Point(3, 5), 1);
        //
        //
        // taxiGUI.RequestTaxi(new Point(1, 1), new Point(3, 5));
    }

    public static void main(String[] args) {
        try {
            mapInfo.readMap("map.txt");// 在这里设置地图文件路径
            taxiGUI.loadMap(mapInfo.map, MAP_SIZE);
            taxiInitialize();
            
            TestThread test = new TestThread();
            Thread testThread =new Thread(test);
            testThread.start();
            
            InputHandler inputHandler = new InputHandler(requestQueue);
            Thread inputHandlerThread = new Thread(inputHandler);
            inputHandlerThread.start();

        }
        catch (Throwable e) {}

    }

}
