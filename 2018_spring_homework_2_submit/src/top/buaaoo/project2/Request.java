package top.buaaoo.project2;

public class Request {
	
	private static int flag=0;  //flag代表是否有第一个有效输入
	private static long lastTime=0;//lastTime代表上一个有效请求的时间

	private int floor;
	private String direction;
	private long time;
	private boolean validity;
	
	
	Request(int n, String str, long t) {
		floor = n;
		direction = str;
		time = t;
		validity = true;
	}
	
	Request(int n, long t) {
		floor = n;
		direction = "IN"; //表示该请求为电梯内请求
		time = t;
		validity = true;
	}
	
	boolean checkElevatorRequestFormat() {
		if(!(floor>=1 && floor<=10)) {
			return false;
		}
		else if(!(time>=0 && time<= 4294967295L)) {
			return false;
		}
		else if(flag == 0) {
			if(time != 0L) {
				return false;
			}
			else {
				flag = 1;
			}
		}
		else {
			if(time < lastTime) {
				return false;
			}
			else {
				lastTime = time;
			}
		}
		
		return true;
	}
	
	boolean checkFloorRequestFormat() {
		if(!(floor>1 && floor<10 || floor==1 && direction.equals("UP") || floor==10 && direction.equals("DOWN"))) {
			return false;
		}
		else if(!(time>=0 && time<= 4294967295L)) {
			return false;
		}
		else if(flag == 0) {
			if(time != 0L) {
				return false;
			}
			else {
				flag = 1;
			}
		}
		else {
			if(time < lastTime) {
				return false;
			}
			else {
				lastTime = time;
			}
		}
		
		return true;
	}
	
	
	int getFloor() {
		return floor;

	}
	
	long getTime() {
		return time;

	}
	
	String getDirection() {
		return direction;
	}
	
	void setInvalid() {
		validity = false;
	}
	
	boolean getValidity() {
		return validity;
	}
}
