package top.buaaoo.project5;

public interface ElevatorInterface {
	int getFloor();
	void updateFloor(int floor);
	double getCurrentTime();
	void updateTime(double time);
	String getCurrentStatus();
	void updateStatus(String str);
}
