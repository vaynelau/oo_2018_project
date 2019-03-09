package top.buaaoo.project13;

import java.text.DecimalFormat;


public class Elevator{
    /*
     * OVERVIEW: 电梯类，负责保存，获取，更新电梯的属性信息。
     * 表示对象：int currentFloor, double currentTime, String currentStatus;
     * 抽象函数：AF(c) = {currentFloor, currentTime, currentStatus} where c.currentFloor == currentFloor, c.currentTime == currentTime, c.currentStatus == currentStatus;
     * 不变式：(c.currentFloor >= 1 && c.currentFloor <= 10) && (c.currentTime >=0) && (c.currentStatus != null && (c.currentStatus.equals("UP") || c.currentStatus.equals("DOWN") || c.currentStatus.equals("STILL")));
     */
    
    private int currentFloor;
    private double currentTime;
    private String currentStatus;
    
    
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: ((this.currentFloor >= 1 && this.currentFloor <= 10) && (this.currentTime >=0) && (this.currentStatus != null && (this.currentStatus.equals("UP") || this.currentStatus.equals("DOWN") || this.currentStatus.equals("STILL")))) ==> (\result == true);
     *           (!((this.currentFloor >= 1 && this.currentFloor <= 10) && (this.currentTime >=0) && (this.currentStatus != null && (this.currentStatus.equals("UP") || this.currentStatus.equals("DOWN") || this.currentStatus.equals("STILL"))))) ==> (\result == false);
     */ 
    public boolean repOK() {
        return ((currentFloor >= 1 && currentFloor <= 10) && (currentTime >=0) && (currentStatus != null && (currentStatus.equals("UP") || currentStatus.equals("DOWN") || currentStatus.equals("STILL"))));
    }
    
    
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: this;
     * @EFFECTS: (this != null) && (this.currentFloor == 1) && (this.currentTime == 0) && (this.currentStatus.equals("STILL"));
     */
    public Elevator() {
        currentFloor = 1;
        currentTime = 0;
        currentStatus = "STILL";
    }
    
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.currentFloor;
     */
    public int getFloor() {
        return currentFloor;
    }
    
    /**
     * @REQUIRES: 1<=floor<=10;
     * @MODIFIES: this;
     * @EFFECTS: this.currentFloor == floor;
     */
    public void setFloor(int floor) {
        currentFloor = floor;
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.currentTime;
     */
    public double getTime() {
        return currentTime;
    }

    /**
     * @REQUIRES: time>=0;
     * @MODIFIES: this;
     * @EFFECTS: this.currentTime == time;
     */
    public void setTime(double time) {
        currentTime = time;
    }
    
    /** 
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.currentStatus;
     */
    public String getStatus() {
        return currentStatus;
    }
    
    /**
     * @REQUIRES: status != null && (status.equals("UP") || status.equals("DOWN") || status.equals("STILL"));
     * @MODIFIES: this;
     * @EFFECTS: this.currentStatus == status;
     */
    public void setStatus(String status) {
        currentStatus = status;
    }
    
    
    
    /**
     * @REQUIRES: req != null;
     * @MODIFIES: None;
     * @EFFECTS: \result.equals("[" + req + "]/(" + this.currentFloor + "," + this.currentStatus + "," + this.doubleFormat.format(this.currentTime) + ")");
     */
    public String toString(Request req) {
        DecimalFormat doubleFormat = new DecimalFormat("0.0");
        String str = "[" + req + "]/(" + currentFloor + "," + currentStatus + "," + doubleFormat.format(currentTime) + ")";
        return str;
    }

}
