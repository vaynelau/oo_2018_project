package top.buaaoo.project2;

import java.text.DecimalFormat;

public class Elevator implements ElevatorInterface {

	private int currentFloor;
	private double currentTime;
	private String currentStatus;
	
	private DecimalFormat doubleFormat = new DecimalFormat("0.0");
	
	Elevator() {
		currentFloor = 1;
		currentTime = 0;
		currentStatus = "STILL";
	}
	
	static void makeElevatorRequest(String str, RequestQueue reqQueue) {
		Request ER = new Request(str);  //实例化楼层请求ER
		if(ER.checkFormat()==true) {
			reqQueue.enQueue(ER); //将ER加入队列
		}
		else {
			System.out.println("INVALID["+str+"]");
		}
	}
	
	public int getFloor() {
		return currentFloor;
	}
	
	public void updateFloor(int floor) {
		currentFloor = floor;
	}
	
	public double getTime() {
		return currentTime;
	}
	
	public void updateTime(double time) {
		currentTime = time;
	}
	
	public String getStatus() {
		return currentStatus;
	}
	
	public void updateStatus(String str) {
		currentStatus = str;
	}
	
	public String toString() {
		String str;
		if(currentStatus.equals("STILL")) {
			str = "("+currentFloor+","+currentStatus+","+doubleFormat.format(currentTime)+")";
		}
		else {
			str = "("+currentFloor+","+currentStatus+","+doubleFormat.format(currentTime-1)+")";
		}
		return str;
	}
	
	public String toString(Request req) {
		String str = "["+req+"]/("+currentFloor+","+currentStatus+","+doubleFormat.format(currentTime)+")";
		return str;
	}
	
}
