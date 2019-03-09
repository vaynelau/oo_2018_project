package top.buaaoo.project2;

public class Elevator {

	private int currentFloor;
	
	Elevator() {
		currentFloor = 1;
	}
	
	int getFloor() {
		return currentFloor;
	}
	
	void updateFloor(int f) {
		currentFloor = f;
	}
	
	static void makeElevatorRequest(int f, long t) {
		
		Request ER = new Request(f, t);  //实例化楼层请求FR
		if(ER.checkElevatorRequestFormat()==true) {
			RequestQueue.enQueue(ER); //将ER加入队列
		}
		else {
			System.out.println("ERROR");
			System.out.println("#Invalid Input.");
		}

	}
	
}
