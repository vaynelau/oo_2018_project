package top.buaaoo.project13;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ElevatorTest {
    private static int i;
    private Elevator elevator;
    
    
    @Before
    public void setUp() throws Exception {
        elevator = new Elevator();
        System.out.println("-----test Elevator "+ i +" begins-----");
    }

    @After
    public void tearDown() throws Exception {
        elevator = null;
        System.out.println("-----test Elevator "+ i++ +" ends-----");
    }

    @Test
    public void testRepOK() {
        assert elevator.repOK();
        elevator.setFloor(0); 
        assert elevator.repOK()==false; 
        elevator.setFloor(11);
        assert elevator.repOK()==false; 
        elevator.setFloor(1);
        elevator.setTime(-1); 
        assert elevator.repOK()==false; 
        elevator.setTime(4394967296L); 
        assert elevator.repOK()==false; 
        elevator.setTime(0);
        elevator.setStatus(null);
        assert elevator.repOK()==false;  
        elevator.setStatus("UP");
        assert elevator.repOK();  
        elevator.setStatus("DOWN");
        assert elevator.repOK();  
        elevator.setStatus("DO");
        assert elevator.repOK()==false;  
        
    }

 

    @Test
    public void testToString() {
        assert elevator.toString(new Request(1, 0, "UP")).equals("[FR,1,UP,0]/(1,STILL,0.0)");
        assert elevator.toString(new Request(5, 1, "DOWN")).equals("[FR,5,DOWN,1]/(1,STILL,0.0)");
        assert elevator.toString(new Request(10, 2, "ER")).equals("[ER,10,2]/(1,STILL,0.0)");
        elevator.setFloor(5);
        assert elevator.toString(new Request(1, 0, "UP")).equals("[FR,1,UP,0]/(5,STILL,0.0)");
        assert elevator.toString(new Request(5, 1, "DOWN")).equals("[FR,5,DOWN,1]/(5,STILL,0.0)");
        assert elevator.toString(new Request(10, 2, "ER")).equals("[ER,10,2]/(5,STILL,0.0)");
        elevator.setFloor(10);
        assert elevator.toString(new Request(1, 0, "UP")).equals("[FR,1,UP,0]/(10,STILL,0.0)");
        assert elevator.toString(new Request(5, 1, "DOWN")).equals("[FR,5,DOWN,1]/(10,STILL,0.0)");
        assert elevator.toString(new Request(10, 2, "ER")).equals("[ER,10,2]/(10,STILL,0.0)");
        elevator.setStatus("DOWN"); 
        assert elevator.toString(new Request(1, 0, "UP")).equals("[FR,1,UP,0]/(10,DOWN,0.0)");
        assert elevator.toString(new Request(5, 1, "DOWN")).equals("[FR,5,DOWN,1]/(10,DOWN,0.0)");
        assert elevator.toString(new Request(10, 2, "ER")).equals("[ER,10,2]/(10,DOWN,0.0)");
        elevator.setStatus("UP");
        assert elevator.toString(new Request(1, 0, "UP")).equals("[FR,1,UP,0]/(10,UP,0.0)");
        assert elevator.toString(new Request(5, 1, "DOWN")).equals("[FR,5,DOWN,1]/(10,UP,0.0)");
        assert elevator.toString(new Request(10, 2, "ER")).equals("[ER,10,2]/(10,UP,0.0)");
        elevator.setTime(10);
        assert elevator.toString(new Request(1, 0, "UP")).equals("[FR,1,UP,0]/(10,UP,10.0)");
        assert elevator.toString(new Request(5, 1, "DOWN")).equals("[FR,5,DOWN,1]/(10,UP,10.0)");
        assert elevator.toString(new Request(10, 2, "ER")).equals("[ER,10,2]/(10,UP,10.0)");
        elevator.setTime(4394967295L);
        assert elevator.toString(new Request(1, 0, "UP")).equals("[FR,1,UP,0]/(10,UP,4394967295.0)");
        assert elevator.toString(new Request(5, 1, "DOWN")).equals("[FR,5,DOWN,1]/(10,UP,4394967295.0)");
        assert elevator.toString(new Request(10, 2, "ER")).equals("[ER,10,2]/(10,UP,4394967295.0)");
    }

}
