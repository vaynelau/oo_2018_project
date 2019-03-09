package top.buaaoo.project13;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequestQueueTest {
    private static int i;
    private RequestQueue requestQueue; 

    @Before
    public void setUp() throws Exception {
        requestQueue = new RequestQueue();
        System.out.println("-----test RequestQueue "+ i +" begins-----");
    }

    @After
    public void tearDown() throws Exception {
        requestQueue = null;
        System.out.println("-----test RequestQueue "+ i++ +" ends-----");
    } 
 
    @Test
    public void testRepOK() {
        assert requestQueue.repOK();
        requestQueue.setFront(-1);
        assert requestQueue.repOK()==false;
        requestQueue.setFront(0);
        requestQueue.setRear(-2);
        assert requestQueue.repOK()==false;
        requestQueue.setRear(0);
        requestQueue.setCount(-1);
        assert requestQueue.repOK()==false;
        requestQueue.setCount(1001);
        assert requestQueue.repOK()==false;
        requestQueue.setCount(0);
        requestQueue.setQueue(null);
        assert requestQueue.repOK()==false;
        
        
    }

    @Test
    public void testEnQueue() {
        Request request = new Request(1, 0, "UP");
        
        assert !requestQueue.isFull();
        assert requestQueue.getCount()==0 && requestQueue.getRear()==-1;
        assert requestQueue.enQueue(request);
        assert requestQueue.getCount()==1 && requestQueue.getRear()==0 && requestQueue.get(requestQueue.getRear())==request;
        
        for(int i = 0;i<999;i++) {
            assert requestQueue.enQueue(new Request(1, i, "UP"));
        }
        
        assert requestQueue.isFull();
        assert requestQueue.getCount()==1000 && requestQueue.getRear()==999;
        assert !requestQueue.enQueue(request);
        assert requestQueue.getCount()==1000 && requestQueue.getRear()==999;
        
        
    }

    @Test
    public void testDeQueue() {
        Request request = new Request(5, 0, "UP");
        Request request2 = new Request(8, 0, "UP");
        
        assert requestQueue.isEmpty();
        assert requestQueue.getCount()==0 && requestQueue.getFront()==0;
        assert requestQueue.deQueue()==null;
        assert requestQueue.getCount()==0 && requestQueue.getFront()==0;
        
        assert requestQueue.enQueue(request);
        assert requestQueue.enQueue(request2);
        for(int i = 0;i<998;i++) {
            assert requestQueue.enQueue(new Request(1, i, "UP"));
        }
        
        assert !requestQueue.isEmpty();
        assert requestQueue.getCount()==1000 && requestQueue.getFront()==0 && requestQueue.get(requestQueue.getFront())==request;
        assert requestQueue.deQueue()== request;
        assert requestQueue.getCount()==999 && requestQueue.getFront()==1;
        assert requestQueue.deQueue()== request2;
        assert requestQueue.getCount()==998 && requestQueue.getFront()==2;
        
    }

    @Test
    public void testIsEmpty() {
        assert requestQueue.getCount() == 0;
        assert requestQueue.isEmpty();
        
        requestQueue.enQueue(new Request(1, 1, "UP"));
        
        assert requestQueue.getCount() == 1;
        assert !requestQueue.isEmpty();
    }

    @Test
    public void testIsFull() {
        assert requestQueue.getCount() == 0;
        assert !requestQueue.isFull();

        for(int i = 0;i<1000;i++) {
            requestQueue.enQueue(new Request(1, i, "UP"));
        }
        
        assert requestQueue.getCount() == 1000;
        assert requestQueue.isFull();
    }

}
