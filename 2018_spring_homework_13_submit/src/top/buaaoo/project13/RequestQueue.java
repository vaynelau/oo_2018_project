package top.buaaoo.project13;

import java.util.ArrayList;

public class RequestQueue {
    /**
     * Overview: 请求队列类，构建了一个容量为1000的循环队列;
     * 
     */
    private ArrayList<Request> queue = new ArrayList<Request>(); 
    private int front = 0;
    private int rear = -1;
    private int count = 0;
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: ((front >= 0) && (rear >= -1) && (count >= 0 && count <= 1000) && (queue != null)) ==> (\result == true);
     *           (!((front >= 0) && (rear >= -1) && (count >= 0 && count <= 1000) && (queue != null))) ==> (\result == false);
     */
    public boolean repOK() {
        return ((front >= 0) && (rear >= -1) && (count >= 0 && count <= 1000) && (queue != null));
    }
    
    
    /**
     * @REQUIRES: \this != null && r != null;
     * @MODIFIES: \this.rear;\this.count;\this.queue;
     * @EFFECTS: (!\this.isFull()) ==> ((\this.rear == (\old(\this.rear)+1)) && (\this.count == \old(\this.count)+1) && (\this.queue.size() == \old(\this.queue).size()+1) && (\this.queue.get(\this.rear) == r) && (\this.result == true);
     *           (\this.isFull()) ==> (\this.result == false);
     */
    boolean enQueue(Request r) {
        if (!isFull()) {
            rear++;
            queue.add(r);
            count++;
            return true; 
        }
        return false;
    }
    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: \this.front;\this.count;\this.queue;
     * @EFFECTS: (!\this.isEmpty()) ==> ((\this.front == (\old(\this.front)+1)) && (\this.count == \old(\this.count)-1) && (\this.result == \this.queue.get(\old(\this.front))));
     *           (\this.isEmpty()) ==> (\this.result == null);
     */
    Request deQueue() {
        if (!isEmpty()) {
            Request r = queue.get(front);
            front++;
            count--;
            return r;
        }
        return null;
    }

    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: (\this.count == 0) ==> (\this.result == true);
     *           (\this.count != 0) ==> (\this.result == false);
     */
    boolean isEmpty() {
        return (count == 0);
    }
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: (\this.count == 1000) ==> (\this.result == true);
     *           (\this.count != 1000) ==> (\this.result == false);
     */
    boolean isFull() {
        return (count == 1000);
    }
    
    
    /**
     * @REQUIRES: \this != null && index >= 0 && index < queue.size();
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.queue.get(index);
     */
    Request get(int index) {
        return queue.get(index);
    }
    
    
    /**
     * @REQUIRES: \this != null && queue != null;
     * @MODIFIES: \this.queue;
     * @EFFECTS: \this.queue == queue;
     */
    void setQueue(ArrayList<Request> queue) {
        this.queue = queue;
    }
    
    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.front;
     */
    int getFront() {
        return front;
    }
    
    
    /**
     * @REQUIRES: \this != null && 0<=front<queue.size();
     * @MODIFIES: \this.front;
     * @EFFECTS: \this.front == front;
     */
    void setFront(int front) {
        this.front = front;
    }
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.rear;
     */
    int getRear() {
        return rear;
    }
    
    /**
     * @REQUIRES: \this != null && -1<=rear<queue.size();
     * @MODIFIES: \this.rear;
     * @EFFECTS: \this.rear == rear;
     */
    void setRear(int rear) {
        this.rear = rear;
    }
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.count;
     */
    int getCount() {
        return count;
    }
    
    /**
     * @REQUIRES: \this != null && 0<=count<=1000;
     * @MODIFIES: \this.count;
     * @EFFECTS: \this.count == count;
     */
    void setCount(int count) {
        this.count = count;
    }
    
}
