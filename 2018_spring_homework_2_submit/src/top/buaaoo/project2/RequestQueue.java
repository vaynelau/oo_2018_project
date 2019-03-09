package top.buaaoo.project2;

public class RequestQueue {
	
	private static Request[] requestQueue = new Request[210];
	private static int front = 0;
	private static int rear = -1;
	private static int count = 0;
	
	static boolean enQueue(Request r) {
		if(!isFull()) {
			requestQueue[++rear] = r;
			count++;
			return true;
		}
		return false;
	}
	
	static Request deQueue() {
		if(!isEmpty()) {
			Request r = requestQueue[front++];
			count--;
			return r;
		}
		return null;
	}
	
	static boolean isEmpty() {
		return (count==0);
	}
	
	static boolean isFull() {
		return (count==200);
	}
												
	static void checkValidity(Request r, double finishTime) {//finishTime为请求r执行完成的时间
		for(int i=front; i<=rear && requestQueue[i].getTime()<=finishTime; i++) {
			if(requestQueue[i].getDirection().equals(r.getDirection()) && requestQueue[i].getFloor()==r.getFloor()) {
				requestQueue[i].setInvalid();
			}
		}
	}
	
}
