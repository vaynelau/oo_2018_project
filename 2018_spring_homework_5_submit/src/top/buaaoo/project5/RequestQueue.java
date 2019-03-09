package top.buaaoo.project5;

public class RequestQueue {

    volatile Request[] queue;
    volatile int front;
    volatile int rear;
    volatile int count;

    RequestQueue() {
        queue = new Request[510];
        front = 0;
        rear = -1;
        count = 0;
    }

    synchronized boolean enQueue(Request r) {
        if (!isFull()) {
            queue[++rear] = r;
            count++;
            return true;
        }
        return false;
    }

    synchronized Request deQueue() {
        if (!isEmpty()) {
            Request r = queue[front++];
            count--;
            return r;
        }
        return null;
    }

    synchronized boolean isEmpty() {
        return (count == 0);
    }

    synchronized boolean isFull() {
        return (count == 500);
    }

}
