package top.buaaoo.project13;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ALS_SchedulerTest {

    private static int i;
    private ALS_Scheduler scheduler;
    private RequestQueue requestQueue;
    private Elevator elevator;
    
    @Before
    public void setUp() throws Exception {
        requestQueue = new RequestQueue();
        elevator = new Elevator();
        scheduler = new ALS_Scheduler(requestQueue, elevator);
        System.out.println("-----test ALS_Scheduler "+ i +" begins-----");
    }

    @After
    public void tearDown() throws Exception {
        elevator = null;
        System.out.println("-----test ALS_Scheduler "+ i++ +" ends-----");
    }
    @Test
    public void testRepOK() {
        assert scheduler.getStatus().equals("STILL");
        assert scheduler.repOK();

        scheduler.setStatus("UP");
        assert scheduler.getStatus().equals("UP");
        assert scheduler.repOK();
        
        scheduler.setStatus("DOWN");
        assert scheduler.getStatus().equals("DOWN");
        assert scheduler.repOK();
        
        scheduler.setStatus("DO");
        assert scheduler.getStatus().equals("DO");
        assert !scheduler.repOK();
        
        scheduler.setStatus(null);
        assert !scheduler.repOK();
        
        scheduler = new ALS_Scheduler(null, elevator);
        assert !scheduler.repOK();
        
        scheduler = new ALS_Scheduler(requestQueue, null);
        assert !scheduler.repOK();
        
        
    }

    @Test
    public void testALS_Scheduler() {
        scheduler = null;
        scheduler = new ALS_Scheduler(requestQueue, elevator);
        assert scheduler != null && scheduler.repOK();

        scheduler = null;
        scheduler = new ALS_Scheduler(new RequestQueue(), elevator);
        assert scheduler != null && scheduler.repOK();
        
        scheduler = null;
        scheduler = new ALS_Scheduler(requestQueue, new Elevator());
        assert scheduler != null && scheduler.repOK();
        
        scheduler = null;
        scheduler = new ALS_Scheduler(new RequestQueue(), new Elevator());
        assert scheduler != null && scheduler.repOK();
    }

    @Test
    public void testUpdateStatus() {
        scheduler.updateStatus(1, 10);
        assert scheduler.getStatus().equals("UP");
        assert elevator.getStatus().equals("UP");
        
        scheduler.updateStatus(5, 6);
        assert scheduler.getStatus().equals("UP");
        assert elevator.getStatus().equals("UP");
        
        scheduler.updateStatus(10, 1);
        assert scheduler.getStatus().equals("DOWN");
        assert elevator.getStatus().equals("DOWN");
        
        scheduler.updateStatus(5, 4);
        assert scheduler.getStatus().equals("DOWN");
        assert elevator.getStatus().equals("DOWN");
        
        scheduler.updateStatus(1, 1);
        assert scheduler.getStatus().equals("STILL");
        assert elevator.getStatus().equals("STILL");
        
        scheduler.updateStatus(10, 10);
        assert scheduler.getStatus().equals("STILL");
        assert elevator.getStatus().equals("STILL");
        
        scheduler.updateStatus(5, 5);
        assert scheduler.getStatus().equals("STILL");
        assert elevator.getStatus().equals("STILL");
    }

    @Test
    public void testRunStill() {
        assert elevator.getTime()== 0;
        assert elevator.getFloor()==1;
        requestQueue.enQueue(new Request(1, 0, "UP"));
        Request request = requestQueue.deQueue();
        assert !request.getIsExecuted();
        scheduler.runStill(request);
        assert elevator.getTime()==1;
        assert request.getIsExecuted();
        
        requestQueue.enQueue(new Request(1, 0, "DOWN"));
        requestQueue.enQueue(new Request(1, 0, "ER"));
        requestQueue.enQueue(new Request(1, 0, "UP"));
        request = requestQueue.deQueue();
        assert !request.getIsExecuted();
        scheduler.runStill(request);
        assert request.getIsExecuted();
        assert elevator.getTime()==2;
        
        request = requestQueue.deQueue();
        assert !request.getIsExecuted();
        scheduler.runStill(request);
        assert request.getIsExecuted();
        assert elevator.getTime()==3;
        
        request = requestQueue.deQueue();
        assert !request.getIsExecuted();
        scheduler.runStill(request);
        assert request.getIsExecuted();
        assert elevator.getTime()==4;
    }

    @Test
    public void testRunUpOrDown() {
        assert elevator.getTime()==0;
        assert elevator.getFloor()==1;
        requestQueue.enQueue(new Request(10, 0, "ER"));
        requestQueue.enQueue(new Request(5, 0, "UP"));
        requestQueue.enQueue(new Request(2, 0, "DOWN"));
        Request request = requestQueue.deQueue();
        scheduler.setStatus("UP");
        elevator.setStatus("UP"); 
        scheduler.runUpOrDown(request);
        System.out.println(elevator.getFloor()+elevator.getTime());
        assert elevator.getFloor()==10;
        assert elevator.getTime()==6.5;
        request = requestQueue.deQueue();
        assert request.getIsExecuted();
        
        request = requestQueue.deQueue();
        scheduler.setStatus("DOWN");
        elevator.setStatus("DOWN");
        scheduler.runUpOrDown(request);
        assert elevator.getFloor()==2;
        assert elevator.getTime()==11.5;
        
        
    }
 
    @Test
    public void testCheckSameRequest() { 
        scheduler.checkSameRequest(new Request(5, 0, "UP"), 3);
        requestQueue.enQueue(new Request(5, 0, "UP"));
        requestQueue.enQueue(new Request(5, 1, "UP"));
        requestQueue.enQueue(new Request(5, 2, "UP"));
        requestQueue.enQueue(new Request(5, 2, "UP"));
        requestQueue.enQueue(new Request(5, 2, "UP"));
        
        Request request = new Request(5, 2, "UP");
        request.setExecuted();
        requestQueue.enQueue(request);
        
        requestQueue.enQueue(new Request(5, 2, "DOWN"));
        requestQueue.enQueue(new Request(5, 2, "ER")); 
        requestQueue.enQueue(new Request(6, 2, "UP")); 
        requestQueue.enQueue(new Request(4, 2, "UP")); 

        request = new Request(5, 2, "UP");
        request.setSame();
        requestQueue.enQueue(request); 
        
        
        requestQueue.enQueue(new Request(5, 3, "UP"));
        requestQueue.enQueue(new Request(5, 4, "UP"));
        requestQueue.enQueue(new Request(5, 4, "UP"));
        requestQueue.enQueue(new Request(2, 5, "DOWN"));
        
        scheduler.checkSameRequest(new Request(5, 0, "UP"), 3);
        assert requestQueue.deQueue().getIsSame();
        assert requestQueue.deQueue().getIsSame();
        assert requestQueue.deQueue().getIsSame();
        assert requestQueue.deQueue().getIsSame();
        assert requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        assert requestQueue.deQueue().getIsSame();
        assert requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        assert !requestQueue.deQueue().getIsSame();
        
    }

    @Test
    public void testCheckPiggybacking() {
        
        assert requestQueue.getFront()==0;
        assert !scheduler.checkPiggybacking(new Request(5, 0, "UP")); 
        
        elevator.setStatus("UP"); 
        scheduler.setStatus("UP");
        elevator.setFloor(4);
        elevator.setTime(3); 
        requestQueue.enQueue(new Request(5, 0, "ER"));
        requestQueue.enQueue(new Request(8, 0, "ER"));
        requestQueue.enQueue(new Request(4, 0, "DOWN"));
        requestQueue.enQueue(new Request(4, 0, "UP"));
        requestQueue.enQueue(new Request(4, 0, "ER"));
        
        Request request = requestQueue.deQueue();
        assert scheduler.checkPiggybacking(request);
        assert !request.getIsExecuted(); 
        assert !requestQueue.deQueue().getIsExecuted();
        assert !requestQueue.deQueue().getIsExecuted();
        assert requestQueue.deQueue().getIsExecuted();
        assert requestQueue.deQueue().getIsExecuted();
        
        elevator.setFloor(4);
        elevator.setTime(4);
        requestQueue.enQueue(new Request(4, 3, "UP"));
        request = new Request(4, 3, "ER");
        request.setSame();
        requestQueue.enQueue(request); 
        request = new Request(4, 3, "UP");
        request.setExecuted(); 
        requestQueue.enQueue(request); 
        requestQueue.enQueue(new Request(4, 4, "ER"));
        requestQueue.enQueue(new Request(4, 4, "DOWN"));
        requestQueue.enQueue(new Request(4, 4, "UP"));
        
        request = requestQueue.deQueue();
        assert scheduler.checkPiggybacking(request);
        assert request.getIsExecuted();
        assert !requestQueue.deQueue().getIsExecuted();
        assert requestQueue.deQueue().getIsExecuted();
        assert !requestQueue.deQueue().getIsExecuted();
        assert !requestQueue.deQueue().getIsExecuted();
        assert !requestQueue.deQueue().getIsExecuted();
    }

    @Test
    public void testChangeMainRequest() {
        
        Request request = new Request(5, 0, "UP");
        assert scheduler.changeMainRequest(request) == request;
         
        elevator.setFloor(5);
        elevator.setTime(3);
        elevator.setStatus("UP"); 
        scheduler.setStatus("UP");
        requestQueue.enQueue(request);
        request = new Request(4, 0, "ER");
        request.setSame();
        requestQueue.enQueue(request); 
        request = new Request(4, 0, "UP");
        request.setExecuted(); 
        requestQueue.enQueue(request);
        requestQueue.enQueue(new Request(1, 0, "UP"));
        requestQueue.enQueue(new Request(2, 0, "DOWN"));
        requestQueue.enQueue(new Request(3, 0, "ER"));
        requestQueue.enQueue(new Request(4, 1, "ER"));
        requestQueue.enQueue(new Request(5, 1, "ER"));
        requestQueue.enQueue(new Request(7, 1, "UP"));
        requestQueue.enQueue(new Request(7, 1, "DOWN"));
        request = requestQueue.deQueue();
        assert scheduler.changeMainRequest(request) == request;
        
        requestQueue.enQueue(new Request(7, 1, "ER"));
        assert scheduler.changeMainRequest(request) != request; 
        
        elevator.setTime(5); 
        elevator.setFloor(7);
        requestQueue.enQueue(new Request(9, 4, "UP"));
        requestQueue.enQueue(new Request(9, 4, "DOWN"));
        requestQueue.enQueue(new Request(9, 4, "ER"));
        request = new Request(7, 1, "ER");
        assert scheduler.changeMainRequest(request) == request;
        
        requestQueue = new RequestQueue();
        scheduler = new ALS_Scheduler(requestQueue, elevator);
        elevator.setStatus("DOWN"); 
        scheduler.setStatus("DOWN");
        requestQueue.enQueue(new Request(8, 3, "ER"));
        requestQueue.enQueue(new Request(7, 3, "ER"));
        request = new Request(7, 1, "ER");
        assert scheduler.changeMainRequest(request) == request;
        
        requestQueue.enQueue(new Request(4, 3, "ER"));
        assert scheduler.changeMainRequest(request) != request;
         
        elevator.setStatus("STILL"); 
        scheduler.setStatus("STILL");
        request = new Request(7, 1, "ER");
        assert scheduler.changeMainRequest(request) == request;
    } 

}
