package top.buaaoo.project5;

//import java.util.Arrays;

public class Floor {

    private static boolean[][] button = new boolean[22][2];

    public Floor() {
        for (int i = 0; i < 22; i++) {
            button[i][0] = false;
            button[i][1] = false;
        }
        // Arrays.fill(button, false);
    }

    static synchronized boolean isFree() {
        for (int i = 1; i <= 20; i++) {
            if (button[i][0] || button[i][1]) {
                return false;
            }
        }
        return true;
    }

    static synchronized boolean isPressed(int floor, String direction) {
        if (direction.equals("UP")) {
            return button[floor][0];
        }
        else {
            return button[floor][1];
        }
    }

    static synchronized void setPressed(int floor, String direction) {
        if (direction.equals("UP")) {
            button[floor][0] = true;
        }
        else {
            button[floor][1] = true;
        }

    }

    static synchronized void setUnpressed(int floor, String direction) {
        if (direction.equals("UP")) {
            button[floor][0] = false;
        }
        else {
            button[floor][1] = false;
        }
    }

    static void makeFloorRequest(String str, double T, RequestQueue reqQueue) {
        Request FR = new Request(str, T); // 实例化楼层请求FR
        if (FR.checkFormat() == true) {
            reqQueue.enQueue(FR); // 将FR加入队列
        }
        else {
            OutputHandler.printInvalidRequest(str, T);
        }
    }

}
