package top.buaaoo.project2;

import java.text.DecimalFormat;

public class Scheduler {
	
	private double currentTime;
	private DecimalFormat doubleFormat;
	

	Scheduler() {
		currentTime = 0;
		doubleFormat = new DecimalFormat("0.0");
	}
	
	
	void work() {
		
		Elevator elevator = new Elevator();
		
		while (!RequestQueue.isEmpty()) {
			
			Request request = RequestQueue.deQueue();
			
			if(request.getValidity()==true) {
				
				if(currentTime >= request.getTime()) {
					currentTime = currentTime + Math.abs(request.getFloor() - elevator.getFloor())*0.5+1;
				}
				else {
					currentTime = request.getTime() + Math.abs(request.getFloor() - elevator.getFloor())*0.5+1;
				}
				
				
				if(request.getFloor() > elevator.getFloor()) {
					System.out.println("("+request.getFloor()+",UP,"+doubleFormat.format(currentTime-1)+")");
				}
				else if(request.getFloor() < elevator.getFloor()) {
					System.out.println("("+request.getFloor()+",DOWN,"+doubleFormat.format(currentTime-1)+")");
				}
				else {
					System.out.println("("+request.getFloor()+",STILL,"+doubleFormat.format(currentTime)+")");
				}
				
				RequestQueue.checkValidity(request, currentTime);
				elevator.updateFloor(request.getFloor());
				
			}
			else {
				System.out.println("#Ignored same request.");
			}
			
		}
	
	}

	
}
