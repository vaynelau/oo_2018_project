package top.buaaoo.project5;

public class Request {

    private int floor;
    private int elevNumber;
    private double time;
    private String direction;
    private boolean isSame; // 标记该请求是否为同质请求
    private boolean isExecuted; // 标记该请求是否已被（捎带）执行
    private boolean isDistributed;

    Request(String str, double t) {
        String[] strSplit = str.split("[,#\\)]");
        time = t;
        isSame = false;
        isExecuted = false;
        isDistributed = false;
        if (str.charAt(1) == 'F') {
            floor = Integer.parseInt(strSplit[1]);
            direction = strSplit[2];
            elevNumber = 0; // 表示该请求为楼层内请求

        }
        else {
            floor = Integer.parseInt(strSplit[3]);
            direction = "ER"; // 表示该请求为电梯内请求
            elevNumber = Integer.parseInt(strSplit[2]);
        }
    }

    public synchronized boolean isPiggyback(Elevator elev) { // 检查该请求是否可以被捎带

        if (direction.equals("ER") && elev.getCurrentStatus().equals("UP") && floor > elev.getFloor() && floor <= 20) {
            return true;
        }
        if (direction.equals("ER") && elev.getCurrentStatus().equals("DOWN") && floor < elev.getFloor() && floor >= 1) {
            return true;
        }
        if (direction.equals("UP") && elev.getCurrentStatus().equals("UP") && floor > elev.getFloor()
                && floor <= elev.getTargetFloor()) {
            return true;
        }
        if (direction.equals("DOWN") && elev.getCurrentStatus().equals("DOWN") && floor < elev.getFloor()
                && floor >= elev.getTargetFloor()) {
            return true;
        }

        return false;
    }

    boolean checkFormat() {
        if (floor < 1 || floor > 20) {
            return false;
        }
        else if (floor == 1 && direction.equals("DOWN") || floor == 20 && direction.equals("UP")) {
            return false;
        }
        return true;
    }

    public String toString() {
        String str;
        if (direction.equals("ER")) {
            str = String.format("ER,#%d,%d,%.1f", elevNumber, floor, time);
        }
        else {
            str = String.format("FR,%d,%s,%.1f", floor, direction, time);
        }
        return str;
    }

    int getFloor() {
        return floor;
    }

    int getElevNum() {
        return elevNumber;
    }

    double getTime() {
        return time;
    }

    String getDirection() {
        return direction;
    }

    boolean isSameRequest() {
        return isSame;
    }

    void setSameRequest() {
        isSame = true;
    }

    boolean isExecuted() {
        return isExecuted;
    }

    void setExecuted() {
        isExecuted = true;
    }

    synchronized boolean isDistributed() {
        return isDistributed;
    }

    synchronized void setDistributed() {
        isDistributed = true;
    }

}
