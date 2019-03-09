package top.buaaoo.project13;

import java.text.DecimalFormat;


public class Elevator implements ElevatorInterface {
    /**
     * Overview: 电梯类，负责保存，获取，更新电梯的属性信息;
     * 
     */
    
    private static final DecimalFormat doubleFormat = new DecimalFormat("0.0");
    private int currentFloor = 1;
    private double currentTime = 0;
    private String currentStatus = "STILL";
    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: ((\this.currentFloor >= 1 && \this.currentFloor <= 10) && (\this.currentTime >=0 && \this.currentTime <= 4394967295L) && (\this.currentStatus != null && (\this.currentStatus.equals("UP") || \this.currentStatus.equals("DOWN") || \this.currentStatus.equals("STILL")))) ==> (\result == true);
     *           (!((\this.currentFloor >= 1 && \this.currentFloor <= 10) && (\this.currentTime >=0 && \this.currentTime <= 4394967295L) && (\this.currentStatus != null && (\this.currentStatus.equals("UP") || \this.currentStatus.equals("DOWN") || \this.currentStatus.equals("STILL"))))) ==> (\result == false);
     */ 
    public boolean repOK() {
        return ((currentFloor >= 1 && currentFloor <= 10) && (currentTime >=0 && currentTime <= 4394967295L) && (currentStatus != null && (currentStatus.equals("UP") || currentStatus.equals("DOWN") || currentStatus.equals("STILL"))));
    }
    
    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.currentFloor;
     */
    public int getFloor() {
        return currentFloor;
    }
    
    /**
     * @REQUIRES: \this != null && 1<=floor<=10;
     * @MODIFIES: \this.currentFloor;
     * @EFFECTS: \this.currentFloor == floor;
     */
    public void setFloor(int floor) {
        currentFloor = floor;
    }
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.currentTime;
     */
    public double getTime() {
        return currentTime;
    }

    /**
     * @REQUIRES: \this != null && 0<=time<=4394967295L;
     * @MODIFIES: \this.currentTime;
     * @EFFECTS: \this.currentTime == time;
     */
    public void setTime(double time) {
        currentTime = time;
    }
    
    /** 
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.currentStatus;
     */
    public String getStatus() {
        return currentStatus;
    }
    
    /**
     * @REQUIRES: \this != null && str != null && (str.equals("UP") || str.equals("DOWN") || str.equals("STILL"));
     * @MODIFIES: \this.currentStatus;
     * @EFFECTS: \this.currentStatus == str;
     */
    public void setStatus(String str) {
        currentStatus = str;
    }
    
    
    
    /**
     * @REQUIRES: \this != null && \this.currentStatus != null && req !=null;
     * @MODIFIES: None;
     * @EFFECTS: \this.result == "[" + req + "]/(" + \this.currentFloor + "," + \this.currentStatus + "," + \this.doubleFormat.format(\this.currentTime) + ")";
     */
    public String toString(Request req) {
        String str = "[" + req + "]/(" + currentFloor + "," + currentStatus + "," + doubleFormat.format(currentTime) + ")";
        return str;
    }

}
