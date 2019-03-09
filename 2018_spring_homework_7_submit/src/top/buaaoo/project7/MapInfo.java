package top.buaaoo.project7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapInfo {

    private static final int MAP_SIZE = 80;
    private static final String REGEX = "[0123]{80}";
    private static Pattern pattern = Pattern.compile(REGEX);

    int[][] map = new int[MAP_SIZE][MAP_SIZE];

    public void readMap(String filePath) {// 读入地图信息
        // Requires:String类型的地图路径,System.in
        // Modifies:System.out,map[][]
        // Effects:从文件中读入地图信息，储存在map[][]中
        Scanner scan = null;
        File file = new File(filePath);
        if (file.exists() == false) {
            System.out.println("地图文件不存在,程序退出");
            System.exit(0);
            return;
        }
        try {
            scan = new Scanner(new File(filePath));
        }
        catch (FileNotFoundException e) {}
        for (int i = 0; i < MAP_SIZE; i++) {
            String[] strArray = null;
            String str = null;
            try {
                if (scan.hasNextLine()) {
                    str = scan.nextLine();
                    // added
                    str = str.replaceAll("[ \\t]", "");
                    Matcher matcher = pattern.matcher(str);
                    if (!matcher.matches()) {
                        System.out.println("地图文件信息有误，程序退出");
                        System.exit(0);
                    }
                    strArray = str.split("");
                }

            }
            catch (Exception e) {
                System.out.println("地图文件信息有误，程序退出");
                System.exit(0);
            }
            for (int j = 0; j < MAP_SIZE; j++) {
                try {
                    this.map[i][j] = Integer.parseInt(strArray[j]);
                }
                catch (Exception e) {
                    System.out.println("地图文件信息有误，程序退出");
                    System.exit(0);
                }
            }
        }
        // added
        if (scan.hasNextLine()) {
            System.out.println("地图文件信息有误，程序退出");
            System.exit(0);
        }

        scan.close();
    }
}
