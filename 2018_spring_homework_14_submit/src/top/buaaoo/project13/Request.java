package top.buaaoo.project13;

public class Request {
    /*
     * OVERVIEW: 请求类，负责记录请求的详细信息等;
     * 表示对象：int floor, long time, String direction, boolean isSame, boolean isExecuted;
     * 抽象函数：AF(c) = {floor, time, direction, isSame, isExecuted} where c.floor == floor, c.time == time, c.direction == direction, c.isSame == isSame, c.isExecuted == isExecuted;
     * 不变式：(c.floor >= 1 && c.floor <= 10) && (0<=c.time<=4294967295L) && (c.direction != null && (c.direction.equals("UP") || c.direction.equals("DOWN") || c.direction.equals("STILL")));
     */
    
    
	

	private int floor;
	private long time;
	private String direction;
	private boolean isSame; //标记该请求是否为同质请求
	private boolean isExecuted; //标记该请求是否已被（捎带）执行
	
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: ((floor >= 1 && floor <= 10) && (0<=time<=4294967295L) && (direction != null && (direction.equals("UP") || direction.equals("DOWN") || direction.equals("ER")))) ==> (\result == true);
     *           (!((floor >= 1 && floor <= 10) && (0<=time<=4294967295L) && (direction != null && (direction.equals("UP") || direction.equals("DOWN") || direction.equals("ER"))))) ==> (\result == false);
     */ 
    public boolean repOK() {
        return ((floor >= 1 && floor <= 10) && (time >=0) && (time<=4294967295L) && (direction != null && (direction.equals("UP") || direction.equals("DOWN") || direction.equals("STILL"))));
    }
    
    
    /**
     * @REQUIRES: (floor >= 1 && floor <= 10) && (0<=time<=4294967295L) && (direction != null && (direction.equals("UP") || direction.equals("DOWN") || direction.equals("ER")));
     * @MODIFIES: this;
     * @EFFECTS: (this != null) && (this.floor == floor) && (this.time == time) && (this.direction == direction) && (isSame == false) && (isExecuted == false);
     */
	Request(int floor ,long time, String direction) {
		this.floor = floor;
		this.time = time;
		this.direction = direction;
		isSame = false;
		isExecuted = false;
	}
	
	
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: "ER".equals(direction) ==> \result.equals("ER,"+floor+","+time);
     * !"ER".equals(direction) ==> \result.equals("FR,"+floor+","+direction+","+time);
     */
	public String toString() {
		if("ER".equals(direction)) {
			return ("ER,"+floor+","+time);
		}
		else {
			return ("FR,"+floor+","+direction+","+time);
		}
	}
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.floor;
     */ 
	int getFloor() {
		return floor;
	}
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.time;
     */
	long getTime() {
		return time;
	}
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.direction;
     */
	String getDirection() {
		return direction;
	}
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.isSame;
     */
	boolean getIsSame() {
		return isSame;
	}
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: this;
     * @EFFECTS: this.isSame == true;
     */
	void setSame() {
		isSame = true;
	}
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.isExecuted;
     */
	boolean getIsExecuted() {
		return isExecuted;
	}
	
	/**
     * @REQUIRES: None;
     * @MODIFIES: this;
     * @EFFECTS: this.isExecuted == true;
     */
	void setExecuted() {
		isExecuted = true;
	}
	
	
}
