package top.buaaoo.project2;

public class Request {
	
	private static Boolean isFirst=true;  //isFirst代表是否有可能成为第一个有效输入
	private static long lastTime=0; //lastTime代表上一个有效请求的时间

	private int floor;
	private long time;
	private String direction;
	private boolean isSame; //标记该请求是否为同质请求
	private boolean isExecuted; //标记该请求是否已被（捎带）执行
	
	Request(String str) {
		String[] strSplit = str.split("[,\\)]");
		
		floor = Integer.parseInt(strSplit[1]);
		isSame = false;
		isExecuted = false;
		
		if(str.charAt(1)=='F') {
			direction = strSplit[2];
			time = Long.parseLong(strSplit[3]);
		}
		else {
			direction = "ER";  //表示该请求为电梯内请求
			time = Long.parseLong(strSplit[2]);
		}
	}
	

	boolean checkFormat() {
		if(floor<1 || floor>10) {
			return false;
		}
		else if(floor==1 && direction.equals("DOWN") || floor==10 && direction.equals("UP")) {
			return false;
		}
		else if(time<0 || time>4294967295L) {
			return false;
		}
		else if(isFirst) {
			if(!(floor==1 && direction.equals("UP") && time==0)) {
				return false;
			}
			else {
				isFirst = false; 
			}
		}
		else {
			if(time<lastTime) {
				return false;
			}
			else {
				lastTime = time;
			}
		}
		return true;
	}
	
	public String toString() {
		String str;
		if(direction.equals("ER")) {
			str = "ER,"+floor+","+time;
		}
		else {
			str = "FR,"+floor+","+direction+","+time;
		}
		return str;
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
	
}
