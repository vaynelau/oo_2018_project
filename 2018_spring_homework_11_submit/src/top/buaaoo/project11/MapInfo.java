package top.buaaoo.project11;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;


public class MapInfo implements Constant {
    /**
     * Overview: 地图信息类，主要负责读入文件中的地图信息，并进行实时的流量统计和读取工作;
     * 
     */

    
    private int[][] map = new int[MAP_SIZE][MAP_SIZE];
    static final int[][] graph = GUIInfo.graph;
    static final int[][] initGraph = GUIInfo.initGraph;
    private static final HashMap<String, Integer> flowMap = new HashMap<String, Integer>();

    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (map != null && graph != null && flowMap != null && initGraph != null);
     */
    public boolean repOK() {
        return map != null && graph != null && flowMap != null && initGraph != null;
    }

    /**
     * @REQUIRES: \this != null;filePath!=null;map!=null;Main.taxiGUI!=null;
     * @MODIFIES: \this.map;System.out;
     * @EFFECTS: 地图文件正确 ==>(\all int i,j;0<=i<MAP_SIZE,0<=j<MAP_SIZE;0<=map[i][j]<=3);
     *           地图文件不存在或错误==> System.out控制台输出提示信息,程序结束；
     */
    public void readMap(String filePath) {

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("地图文件不存在,程序退出");
                System.exit(0);
            }
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
     * @REQUIRES: \this != null;point!=null && 0<=point.x<80 && 0<=point.y<80;initGraph!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result==count && 0<=count<=4;
     */
    public int connectedEdges(int x, int y) {
        int count = 0;
        if (x > 0 && isConnectedFirst(x, y, x - 1, y)) {
            count++;
        }
        if (x < 79 && isConnectedFirst(x, y, x + 1, y)) {
            count++;
        }
        if (y > 0 && isConnectedFirst(x, y, x, y - 1)) {
            count++;
        }
        if (y < 79 && isConnectedFirst(x, y, x, y + 1)) {
            count++;
        }
        return count;
    }

    
    /**
     * @REQUIRES: \this != null;0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;graph!=null;initGraph!=null;0<=taxiNum<100;
     * @MODIFIES: None;
     * @EFFECTS: 0 <= taxiNum < 30 ==> \result==(initGraph[x1 * 80 + y1][x2 * 80 + y2] == 1);
     *           30 <= taxiNum < 100 ==> \result==(graph[x1 * 80 + y1][x2 * 80 + y2] == 1);
     * @THREAD_EFFECTS: \locked(\this.graph);
     */
    public boolean isConnected(int taxiNum, int x1, int y1, int x2, int y2) {
        if(taxiNum < 30) {
            return isConnectedFirst(x1, y1, x2, y2);
        }
        synchronized (graph) {
            if (graph[x1 * 80 + y1][x2 * 80 + y2] == 1) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * @REQUIRES: \this != null;0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;graph!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result==(graph[x1 * 80 + y1][x2 * 80 + y2] == 1);
     * @THREAD_EFFECTS: \locked(\this.graph);
     */
    public boolean isConnectedNow(int x1, int y1, int x2, int y2) {
        synchronized (graph) {
            if (graph[x1 * 80 + y1][x2 * 80 + y2] == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @REQUIRES: \this != null;0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;initGraph!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result==(initGraph[x1 * 80 + y1][x2 * 80 + y2] == 1);
     */
    public boolean isConnectedFirst(int x1, int y1, int x2, int y2) {
        if (initGraph[x1 * 80 + y1][x2 * 80 + y2] == 1) {
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
     * @REQUIRES: 0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;flowMap!=null;graph!=null;
     * @MODIFIES: \this.flowMap;
     * @EFFECTS: \this.flowMap.get(Key(x1, y1, x2, y2))>=0;
     * @THREAD_EFFECTS: \locked(\this.flowMap);\locked(\this.graph);
     */
    public static void addFlow(int x1, int y1, int x2, int y2) {
        synchronized (graph) {
            if (graph[x1 * 80 + y1][x2 * 80 + y2] == 0) {
                return;
            }
        }
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
     * @REQUIRES: 0<=x1<80;0<=y1<80;0<=x2<80;0<=y2<80;flowMap!=null;0<=count<=1000;
     * @MODIFIES: \this.flowMap;
     * @EFFECTS: flowMap.get(Key(x1, y1, x2, y2))>=0;
     * @THREAD_EFFECTS: \locked(\this.flowMap);
     */
    public static void setFlow(int x1, int y1, int x2, int y2, int count) {
        long currentTime = System.currentTimeMillis();
        synchronized (flowMap) {
            flowMap.put(Key(x1, y1, x2, y2), count);
            flowMap.put(Key(x2, y2, x1, y1), count);

        }
        if (count != 0) {
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
     *           (flowMap.get(Key(x1, y1, x2, y2)) != null) ==> \result==flowMap.get(Key(x1, y1, x2, y2));
     * @THREAD_EFFECTS: \locked(\this.flowMap);
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
     * @THREAD_EFFECTS: \locked(flowMap);
     */
    public static int[] getFlow(Point p) {
        int[] flow = new int[4];
        synchronized (flowMap) {
            flow[UP] = getFlow(p.x, p.y, p.x - 1, p.y);
            flow[DOWN] = getFlow(p.x, p.y, p.x + 1, p.y);
            flow[LEFT] = getFlow(p.x, p.y, p.x, p.y - 1);
            flow[RIGHT] = getFlow(p.x, p.y, p.x, p.y + 1);
        }
        return flow;
    }

}
