package top.buaaoo.project2;

public class Scheduler {
	
	RequestQueue reqQueue;
	Elevator elevator;
	double finishTime;
	String status;
	
	Scheduler(RequestQueue requestQueue) {
		elevator = new Elevator();
		reqQueue = requestQueue;
	}
	
	
	void work() {
		
		while (!reqQueue.isEmpty()) {
			Request request = reqQueue.deQueue();
			if(!request.isExecuted() && !request.isSameRequest()) {
				
				if(request.getFloor() > elevator.getFloor()) {
					status = "UP";
				}
				else if(request.getFloor() < elevator.getFloor()) {
					status = "DOWN";
				}
				else {
					status = "STILL";
				}
				finishTime = Math.max(elevator.getTime(), request.getTime()) + 
							 Math.abs(request.getFloor() - elevator.getFloor())*0.5+1;
				
				elevator.updateFloor(request.getFloor());
				elevator.updateTime(finishTime);
				elevator.updateStatus(status);
				request.setExecuted();
				System.out.println(elevator);
				checkSameRequest(request, finishTime);
			}
		}
	}
	
	void checkSameRequest(Request req, double finishTime) {//finishTime为请求req执行完成的时间
		for(int i=reqQueue.front; i<=reqQueue.rear && reqQueue.queue[i].getTime()<=finishTime; i++) {
			boolean bool = !reqQueue.queue[i].isSameRequest() && !reqQueue.queue[i].isExecuted() &&
							reqQueue.queue[i].getDirection().equals(req.getDirection()) &&
							reqQueue.queue[i].getFloor()==req.getFloor();
			if(bool) {
				reqQueue.queue[i].setSameRequest();
				System.out.println("#SAME["+reqQueue.queue[i]+"]");
			}
		}
	}
	
}
