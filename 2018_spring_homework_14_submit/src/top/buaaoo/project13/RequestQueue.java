package top.buaaoo.project13;

import java.util.ArrayList;

public class RequestQueue {
    /*
     * OVERVIEW: 请求队列类，构建并维护了一个容量为1000的请求队列
     * 表示对象：ArrayList<Request> queue, int front, int rear, int count;
     * 抽象函数：AF(c) = {queue, front, rear, count} where c.queue == queue, c.front == front, c.rear == rear, c.count == count;
     * 不变式：(c.front >= 0) && (c.rear >= -1) && (c.count >= 0 && c.count <= 1000) && (c.queue != null);
     */
    
    
    
    private ArrayList<Request> queue;
    private int front;
    private int rear;
    private int count;
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: ((front >= 0) && (rear >= -1) && (count >= 0 && count <= 1000) && (queue != null)) ==> (\result == true);
     *           (!((front >= 0) && (rear >= -1) && (count >= 0 && count <= 1000) && (queue != null))) ==> (\result == false);
     */
    public boolean repOK() {
        return ((front >= 0) && (rear >= -1) && (count >= 0 && count <= 1000) && (queue != null));
    }
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: this;
     * @EFFECTS: (this != null) && (this.front == 0) && (this.rear == -1) && (this.count == 0) && (this.queue != null);
     */
    public RequestQueue() {
        front = 0;
        rear = -1;
        count = 0;
        queue = new ArrayList<Request>(); 
    }
    
    
    
    
    /**
     * @REQUIRES: r != null;
     * @MODIFIES: this;
     * @EFFECTS: (!this.isFull()) ==> ((this.rear == (\old(this.rear)+1)) && (this.count == \old(this.count)+1) && (this.queue.size() == \old(this.queue).size()+1) && (this.queue.get(this.rear) == r) && (this.result == true));
     *           (this.isFull()) ==> (this.result == false);
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
     * @REQUIRES: None;
     * @MODIFIES: this;
     * @EFFECTS: (!this.isEmpty()) ==> ((this.front == (\old(this.front)+1)) && (this.count == \old(this.count)-1) && (this.result == this.queue.get(\old(this.front))));
     *           (this.isEmpty()) ==> (this.result == null);
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
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: (this.count == 0) ==> (this.result == true);
     *           (this.count != 0) ==> (this.result == false);
     */
    boolean isEmpty() {
        return (count == 0);
    }

    
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: (this.count == 1000) ==> (this.result == true);
     *           (this.count != 1000) ==> (this.result == false);
     */
    boolean isFull() {
        return (count == 1000);
    }
    
    
    /**
     * @REQUIRES: index >= 0 && index < queue.size();
     * @MODIFIES: None;
     * @EFFECTS: \result == this.queue.get(index);
     */
    Request get(int index) {
        return queue.get(index);
    }
    
    
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.front;
     */
    int getFront() {
        return front;
    }
    
    
 
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.rear;
     */
    int getRear() {
        return rear;
    }
    
 
    
}
