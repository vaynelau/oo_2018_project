package top.buaaoo.project10;

import java.awt.Point;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;

public class MapInfo implements Constant {
    /**
     * Overview: 地图信息类，主要负责读入文件中的地图信息，并进行实时的流量统计和读取工作
     * 
     */
    
    
    private int[][] map = new int[MAP_SIZE][MAP_SIZE];
    private static final int[][] graph = GUIInfo.graph;
    private static final HashMap<String, Integer> flowMap = new HashMap<String, Integer>();

    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == (map != null && graph != null && flowMap != null);
     */
    public boolean repOK() {
        return map != null && graph != null && flowMap != null;
    }

    /**
     * @REQUIRES: filePath!=null;map!=null;Main.taxiGUI!=null;
     * @MODIFIES: map;System.out;
     * @EFFECTS: 地图文件正确 ==>\all int i,j;0<=i<MAP_SIZE,0<=j<MAP_SIZE;0<=map[i][j]<=3;
     *           地图文件不存在或错误==> System.out控制台输出提示信息,程序结束；
     */
    public void readMap(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("地图文件不存在,程序退出");
            System.exit(0);
        }
        try {
            Scanner scan = new Scanner(file);
            for (int i = 0; i < MAP_SIZE; i++) {
                if (scan.hasNextLine()) {
                    String str = scan.nextLine().replaceAll("[ \\t]", "");
                    Matcher matcher = pMAP.matcher(str);
                    if (!matcher.matches()) {
                        System.out.println("地图文件有误，程序退出");
                        System.exit(0);
                    }
                    String[] strArray = str.split("");
                    for (int j = 0; j < MAP_SIZE; j++) {
                        this.map[i][j] = Integer.parseInt(strArray[j]);
                    }
                } else {
                    System.out.println("地图文件有误，程序退出");
                    System.exit(0);
                }

            }
            if (scan.hasNextLine()) {
                System.out.println("地图文件有误，程序退出");
                System.exit(0);
            }
            scan.close();
            Main.taxiGUI.loadMap(map, MAP_SIZE);
        }
        catch (Throwable e) {
            System.out.println("地图文件有误，程序退出");
            System.exit(0);
        }
    }

    /**
     * @REQUIRES: point!=null && 0<=point.x<80 && 0<=point.y<80;graph!=null;GUIGV.guiInfo.map!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result==connected; (\all int i;0<=i<4 && point is connected to the
     *           direction of i;connected[i]==true); (\all int i;0<=i<4 && point is
     *           not connected to the direction of i;connected[i]==false);
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(graph);
     */
    public boolean[] isConnected(Point point) {

        boolean[] connected = new boolean[4];
        Arrays.fill(connected, false);
        int x = point.x;
        int y = point.y;
        synchronized (graph) {
            if (x > 0 && (GUIGV.guiInfo.map[x - 1][y] == 2 || GUIGV.guiInfo.map[x - 1][y] == 3)) {
                connected[UP] = true;
            }
            if (x < 79 && (GUIGV.guiInfo.map[x][y] == 2 || GUIGV.guiInfo.map[x][y] == 3)) {
                connected[DOWN] = true;
            }
            if (y > 0 && (GUIGV.guiInfo.map[x][y - 1] == 1 || GUIGV.guiInfo.map[x][y - 1] == 3)) {
                connected[LEFT] = true;
            }
            if (y < 79 && (GUIGV.guiInfo.map[x][y] == 1 || GUIGV.guiInfo.map[x][y] == 3)) {
                connected[RIGHT] = true;
            }
        }
        return connected;
    }

    /**
     * @REQUIRES: 0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;graph!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result==(graph[x1 * 80 + y1][x2 * 80 + y2] == 1);
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(graph);
     */
    public boolean isConnected(int x1, int y1, int x2, int y2) {

        synchronized (graph) {
            if (graph[x1 * 80 + y1][x2 * 80 + y2] == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @REQUIRES: 0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;map!=null;
     * @MODIFIES: None;
     * @EFFECTS: (x1,y1) is next and connected to (x2,y2) in the map originally ==>
     *           \result == true; (x1,y1) is not next or connected to (x2,y2) in the
     *           map originally ==> \result == false;
     */
    public boolean isConnectedFirst(int x1, int y1, int x2, int y2) {

        if (x1 == x2 && y1 + 1 == y2 && (map[x1][y1] == 1 || map[x1][y1] == 3)) {
            return true;
        }
        if (x1 == x2 && y2 + 1 == y1 && (map[x2][y2] == 1 || map[x2][y2] == 3)) {
            return true;
        }
        if (y1 == y2 && x1 + 1 == x2 && (map[x1][y1] == 2 || map[x1][y1] == 3)) {
            return true;
        }
        if (y1 == y2 && x2 + 1 == x1 && (map[x2][y2] == 2 || map[x2][y2] == 3)) {
            return true;
        }
        return false;
    }
    
    
    /**
     * @REQUIRES: -1<=x1<=80;-1<=y1<=80;-1<=x2<=80;-1<=y2<=80;
     * @MODIFIES: None;
     * @EFFECTS: \result==("" + x1 + "," + y1 + "," + x2 + "," + y2);
     */
    private static String Key(int x1, int y1, int x2, int y2) {
        return "" + x1 + "," + y1 + "," + x2 + "," + y2;
    }
    
    /**
     * @REQUIRES: 0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;flowMap!=null;
     * @MODIFIES: flowMap;
     * @EFFECTS: flowMap.get(Key(x1, y1, x2, y2))>=0;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(flowMap);
     */
    public static void addFlow(int x1, int y1, int x2, int y2) {
        long currentTime = System.currentTimeMillis();
        synchronized (flowMap) {
            int count = flowMap.get(Key(x1, y1, x2, y2)) == null ? 0 : flowMap.get(Key(x1, y1, x2, y2));
            flowMap.put(Key(x1, y1, x2, y2), count + 1);
            flowMap.put(Key(x2, y2, x1, y1), count + 1);
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    gv.stay(currentTime + 500 - System.currentTimeMillis());
                    synchronized (flowMap) {
                        int count = flowMap.get(Key(x1, y1, x2, y2));
                        if (count > 0) {
                            flowMap.put(Key(x1, y1, x2, y2), count - 1);
                            flowMap.put(Key(x2, y2, x1, y1), count - 1);
                        }
                    }
                }
                catch (Throwable e) {}
            }
        }).start();

    }
    
    /**
     * @REQUIRES: 0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;flowMap!=null;0<=count<=Integer.MAX_VALUE;
     * @MODIFIES: flowMap;
     * @EFFECTS: flowMap.get(Key(x1, y1, x2, y2))>=0;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(flowMap);
     */
    public static void setFlow(int x1, int y1, int x2, int y2, int count) {
        long currentTime = System.currentTimeMillis();
        synchronized (flowMap) {
            flowMap.put(Key(x1, y1, x2, y2), count);
            flowMap.put(Key(x2, y2, x1, y1), count);

        }
        if(count != 0) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        gv.stay(currentTime + 500 - System.currentTimeMillis());
                        synchronized (flowMap) {
                            int flow = flowMap.get(Key(x1, y1, x2, y2)) - count;
                            flowMap.put(Key(x1, y1, x2, y2), flow > 0 ? flow : 0);
                            flowMap.put(Key(x2, y2, x1, y1), flow > 0 ? flow : 0);
                        }
                    }
                    catch (Throwable e) {}
                }
            }).start();

        }
        
    }
    /**
     * @REQUIRES: -1<=x1<=80;-1<=y1<=80;-1<=x2<=80;-1<=y2<=80;flowMap!=null;
     * @MODIFIES: None;
     * @EFFECTS: (flowMap.get(Key(x1, y1, x2, y2)) == null) ==> \result==0;
     *           (flowMap.get(Key(x1, y1, x2, y2)) != null) ==> flowMap.get(Key(x1, y1, x2, y2));;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(flowMap);
     */
    public static int getFlow(int x1, int y1, int x2, int y2) {
        synchronized (flowMap) {
            return flowMap.get(Key(x1, y1, x2, y2)) == null ? 0 : flowMap.get(Key(x1, y1, x2, y2));
        }

    }

    /**
     * @REQUIRES: p != null && 0<=p.x<80 && 0<=p.y<80;flowMap!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result == flow;
     */
    public static int[] getFlow(Point p) {
        int[] flow = new int[4];
        flow[UP] = getFlow(p.x, p.y, p.x - 1, p.y);
        flow[DOWN] = getFlow(p.x, p.y, p.x + 1, p.y);
        flow[LEFT] = getFlow(p.x, p.y, p.x, p.y - 1);
        flow[RIGHT] = getFlow(p.x, p.y, p.x, p.y + 1);
        return flow;
    }

}
