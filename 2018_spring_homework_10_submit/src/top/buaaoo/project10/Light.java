package top.buaaoo.project10;

import java.awt.Point;
import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Light implements Runnable, Constant {
    /**
     * Overview: 红绿灯相关的线程类，主要负责读入文件中的红绿灯信息，并进行定时切换红绿灯的操作
     * 
     */

    private static final int[][] lightMap = GUIGV.lightmap;
    private static long systemTime;
    private static final long timeWindow = (int) Math.floor(Math.random() * 500) + 500;;

    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == (lightMap != null && systemTime >= 0 && systemTime <=
     *           Long.MAX_VALUE && timeWindow>=500 && timeWindow<1000);
     */
    public boolean repOK() {
        return lightMap != null && systemTime >= 0 && systemTime <= Long.MAX_VALUE && timeWindow >= 500
                && timeWindow < 1000;
    }

    /**
     * @REQUIRES: filePath != null;lightMap != null;
     * @MODIFIES: lightMap;System.out;
     * @EFFECTS: 红绿灯文件正确 ==>\all int i,j;0<=i<MAP_SIZE,0<=j<MAP_SIZE;0<=lightMap[i][j]<=2;
     *           为非十字或丁字路口设置红绿灯==>System.out控制台输出提示信息,不再该处设红绿灯;
     *           红绿灯文件不存在或错误==> System.out控制台输出提示信息,程序结束;
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(lightMap);
     */
    public void readLight(String filePath) {

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("红绿灯文件不存在，程序退出");
                System.exit(0);
            }
            int random, count;
            boolean[] connected;
            do {
                random = (int) Math.floor(Math.random() * 2) + 1;
            } while (!(random == 1 || random == 2));
            Scanner scan = new Scanner(file);
            systemTime = System.currentTimeMillis();
            synchronized (lightMap) {
                for (int i = 0; i < MAP_SIZE; i++) {
                    if (scan.hasNextLine()) {
                        String str = scan.nextLine().replaceAll("[ \\t]", "");
                        Matcher matcher = pLIGHT.matcher(str);
                        if (!matcher.matches()) {
                            System.out.println("红绿灯文件有误，程序退出");
                            System.exit(0);
                        }
                        String[] strArray = str.split("");
                        for (int j = 0; j < MAP_SIZE; j++) {
                            if (Integer.parseInt(strArray[j]) == 1) {
                                connected = Main.mapInfo.isConnected(new Point(i, j));
                                count = 0;
                                for (boolean b : connected) {
                                    if (b == true) {
                                        count++;
                                    }
                                }
                                if (count >= 3) {
                                    Main.taxiGUI.setLightStatus(new Point(i, j), random);
                                } else {
                                    System.out.println("(" + i + "," + j + ") set light failed.");
                                }
                            } else {
                                Main.taxiGUI.setLightStatus(new Point(i, j), 0);
                            }
                        }
                    } else {
                        System.out.println("红绿灯文件有误，程序退出");
                        System.exit(0);
                    }
                }
            }

            if (scan.hasNextLine()) {
                System.out.println("红绿灯文件有误，程序退出");
                System.exit(0);
            }
            scan.close();

        }
        catch (Throwable e) {
            System.out.println("红绿灯文件有误，程序退出");
            System.exit(0);
        }
    }
    
    
    /**
     * @REQUIRES: point!=null && 0<=point.x<80 && 0<=point.y<80;lightMap!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result == lightMap[point.x][point.y];
     * @THREAD_REQUIRES: None;
     * @THREAD_EFFECTS: \locked(lightMap);
     */
    public static int getLight(Point point) {
        synchronized (lightMap) {
            return lightMap[point.x][point.y];
        }
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == systemTime + timeWindow - \old(System.currentTimeMillis());
     */
    public static long getRemainingTime() {
        return systemTime + timeWindow - System.currentTimeMillis();
    }

    /**
     * @REQUIRES: None;
     * @MODIFIES: systemTime;lightMap;
     * @EFFECTS: 未捕捉到异常 ==> \all int i,j;0<=i<MAP_SIZE,0<=j<MAP_SIZE;0<=lightMap[i][j]<=2;
     *           未捕捉到异常 ==> (systemTime-\old(systemTime))%timeWindow==0;
     *           捕捉到异常 ==> 程序退出;
     */
    public void run() {

        try {

            while (true) {
                long sleepTime = systemTime + timeWindow - System.currentTimeMillis();
                gv.stay(sleepTime);
                systemTime += timeWindow;
                synchronized (lightMap) {
                    for (int i = 0; i < MAP_SIZE; i++) {
                        for (int j = 0; j < MAP_SIZE; j++) {
                            if (lightMap[i][j] == 1) {
                                Main.taxiGUI.setLightStatus(new Point(i, j), 2);
                            } else if (lightMap[i][j] == 2) {
                                Main.taxiGUI.setLightStatus(new Point(i, j), 1);
                            }
                        }
                    }
                }

            }
        }
        catch (Throwable e) {
            System.exit(0);
        }

    }

}
