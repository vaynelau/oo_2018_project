package top.buaaoo.project2;

public class RequestQueue {
	
	Request[] queue;
	int front;
	int rear;
	int count;
	
	RequestQueue() {
		queue = new Request[210];
		front = 0;
		rear = -1;
		count = 0;
	}
	
	boolean enQueue(Request r) {
		if(!isFull()) {
			queue[++rear] = r;
			count++;
			return true;
		}
		return false;
	}
	
	Request deQueue() {
		if(!isEmpty()) {
			Request r = queue[front++];
			count--;
			return r;
		}
		return null;
	}
	
	boolean isEmpty() {
		return (count==0);
	}
	
	boolean isFull() {
		return (count==200);
	}
	
	
}
