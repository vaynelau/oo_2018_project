package top.buaaoo.project13;

public class Request {
	
	private static Boolean isFirst=true;  //isFirst代表是否有可能成为第一个有效输入
	private static long lastTime=0; //lastTime代表上一个有效请求的时间

	private int floor;
	private long time;
	private String direction;
	private boolean isSame; //标记该请求是否为同质请求
	private boolean isExecuted; //标记该请求是否已被（捎带）执行
	 
	Request(int floor ,long time, String direction) {
		this.floor = floor;
		this.time = time;
		//this.direction = new String(direction);
		this.direction = direction;
		isSame = false;
		isExecuted = false;
		
	}
	 
 
	static boolean checkFormat(int floor ,long time, String direction) {
		if(floor<1 || floor>10) {
			return false;
		}
		else if(floor==1 && "DOWN".equals(direction) || floor==10 && "UP".equals(direction)) {
			return false;
		}
		else if(time<0 || time>4294967295L) {
			return false;
		}
		else if(isFirst) {
			if(!(floor==1 && "UP".equals(direction) && time==0)) {
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
		if("ER".equals(direction)) {
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
	
	boolean getIsSame() {
		return isSame;
	}
	
	void setSame() {
		isSame = true;
	}
	
	boolean getIsExecuted() {
		return isExecuted;
	}
	
	void setExecuted() {
		isExecuted = true;
	}
	
}
