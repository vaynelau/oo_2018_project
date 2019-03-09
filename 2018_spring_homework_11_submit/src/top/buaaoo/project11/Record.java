package top.buaaoo.project11;

import java.awt.Point;
import java.util.ArrayList;


public class Record {
    
    private Request request;
    private Point orderPoint;
    private ArrayList<Point> drivingPath;
    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == ((request != null && orderPoint != null && drivingPath != null)&&(0<= orderPoint.x&& orderPoint.x<80 && 0<=orderPoint.y && orderPoint.y<80));
     */
    public boolean repOK() {
        if(!(request != null && orderPoint != null && drivingPath != null)) {
            return false;
        }
        if(!(0<= orderPoint.x&& orderPoint.x<80 && 0<=orderPoint.y && orderPoint.y<80)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * @REQUIRES: request != null && orderPoint != null && drivingPath != null;0<= orderPoint.x&& orderPoint.x<80 && 0<=orderPoint.y && orderPoint.y<80;
     * @MODIFIES: \this.request;\this.orderPoint;\this.drivingPath;
     * @EFFECTS: \this.request == request;
        \this.orderPoint == orderPoint;
        \this.drivingPath == drivingPath;
     */
    public Record(Request request, Point orderPoint,ArrayList<Point> drivingPath) {
        this.request = request;
        this.orderPoint = orderPoint;
        this.drivingPath = drivingPath;
    }
    
    
    /**
     * @REQUIRES: \this != null;request != null && orderPoint != null && drivingPath != null;0<= orderPoint.x&& orderPoint.x<80 && 0<=orderPoint.y && orderPoint.y<80;
     * @MODIFIES: System.out;
     * @EFFECTS: (request != null && orderPoint != null && drivingPath != null) ==> System.out输出记录信息
     */
    public void printRecord() {
        System.out.println("------------------------------------------------------------");
        System.out.println("request time: " + request.time);
        System.out.println("starting point: [" + request.srcPoint.x + "," + request.srcPoint.y + "]");
        System.out.println("destination point: [" + request.dstPoint.x + "," + request.dstPoint.y + "]");
        System.out.println("order point: [" + orderPoint.x + "," + orderPoint.y + "]");
        System.out.println("driving path:");
        
        for (Point point : drivingPath) {
            System.out.print("["+point.x+","+point.y+"]->");
        }
        System.out.println();
        System.out.println("------------------------------------------------------------");
    }
    
}
