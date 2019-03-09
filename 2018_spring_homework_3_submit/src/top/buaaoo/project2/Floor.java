package top.buaaoo.project2;

public class Floor {
	
	static void makeFloorRequest(String str, RequestQueue reqQueue) {
		Request FR = new Request(str); //实例化楼层请求FR
		if(FR.checkFormat()==true) {
			reqQueue.enQueue(FR); //将FR加入队列
		}
		else {
			System.out.println("INVALID["+str+"]");
		}
	}
	
}
