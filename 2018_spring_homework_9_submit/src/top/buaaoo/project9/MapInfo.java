package top.buaaoo.project9;

import java.awt.Point;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapInfo implements Constant {

    private static final String REGEX = "[0123]{80}";
    private static Pattern pattern = Pattern.compile(REGEX);

    volatile int[][] map = new int[MAP_SIZE][MAP_SIZE];
    static int[][] graph = GUIInfo.graph;
    
    /**
     * @REQUIRES: filePath!=null;
     * @MODIFIES: map,System.out;
     * @EFFECTS: normal_behavior:
     *           \all int i,j;0=<i<MAP_SIZE,0=<j<MAP_SIZE;0=<map[i][j]<=3;
     *           exception_behavior(Throwable e):
     *           System.out控制台输出提示信息
     *           process finished with exit code 0;
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
                    Matcher matcher = pattern.matcher(str);
                    if (!matcher.matches()) {
                        System.out.println("地图文件信息有误，程序退出");
                        System.exit(0);
                    }
                    String[] strArray = str.split("");
                    for (int j = 0; j < MAP_SIZE; j++) {
                        this.map[i][j] = Integer.parseInt(strArray[j]);
                    }
                } else {
                    System.out.println("地图文件信息有误，程序退出");
                    System.exit(0);
                }

            }
            if (scan.hasNextLine()) {
                System.out.println("地图文件信息有误，程序退出");
                System.exit(0);
            }
            scan.close();
        }
        catch (Throwable e) {
            System.out.println("地图文件信息有误，程序退出");
            System.exit(0);
        }
    }
    
    /**
     * @REQUIRES: point!=null && 0=<point.x<80 && 0=<point.y<80;
     * @MODIFIES: None;
     * @EFFECTS: \result==connected;
     *           \all int i;0=<i<4 && point is connected to the direction of i;connected[i]==true;
     *           \all int i;0=<i<4 && point is not connected to the direction of i;connected[i]==false;
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
     * @REQUIRES: 0=<x1<80;0=<y1<80;0=<x2<80;0=<y2<80;
     * @MODIFIES: None;
     * @EFFECTS: \result==(GUIInfo.graph[x1 * 80 + y1][x2 * 80 + y2] == 1);
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(graph);
     */
    public boolean isConnected(int x1, int y1, int x2, int y2) {
        
        synchronized (graph) {
            if (GUIInfo.graph[x1 * 80 + y1][x2 * 80 + y2] == 1) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @REQUIRES: 0=<x1<80;0=<y1<80;0=<x2<80;0=<y2<80;
     * @MODIFIES: None;
     * @EFFECTS: (x1,y1) is next and connected to (x2,y2) in the map originally ==> \result == true;
     *           (x1,y1) is not next or connected to (x2,y2) in the map originally ==> \result == false;
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

}
