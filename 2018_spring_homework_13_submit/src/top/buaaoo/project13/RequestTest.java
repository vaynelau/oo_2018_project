package top.buaaoo.project13;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequestTest {

    private static int i;
    private Request request;

    @Before
    public void setUp() throws Exception {
        request = new Request(1, 0, "UP");
        System.out.println("-----test Request "+ i +" begins-----");
    }

    @After
    public void tearDown() throws Exception {
        request = null;
        System.out.println("-----test Request "+ i++ +" ends-----");
    }

    @Test
    public void testRequest() {
        request = null;
        request = new Request(1, 0, "UP");
        assert request != null;
        request = null;
        request = new Request(10, 12, "DOWN");
        assert request != null;
        request = null;
        request = new Request(5, 1, "UP");
        assert request != null;
        request = null;
        request = new Request(5, 4000000000L, "DOWN");
        assert request != null;
        request = null;
        request = new Request(1, 0, "ER");
        assert request != null;
        request = null;
        request = new Request(10, 0, "ER");
        assert request != null;
        request = null;
        request = new Request(5, 0, "ER");
        assert request != null;        
    }

    @Test
    public void testCheckFormat() {
         
        assert !Request.checkFormat(2, 1, "DOWN");
        assert !Request.checkFormat(2, 0, "DOWN");
        assert !Request.checkFormat(2, 0, "UP");
        assert !Request.checkFormat(2, 1, "UP");
         
        assert !Request.checkFormat(1, 0, "ER");
        assert !Request.checkFormat(1, 1, "ER");
        assert !Request.checkFormat(1, 2, "UP");
         
        
        assert Request.checkFormat(1, 0, "UP");
        assert !Request.checkFormat(1, 0, "DOWN");
        assert Request.checkFormat(1, 0, "ER");
        
        assert !Request.checkFormat(10, 0, "UP");
        assert Request.checkFormat(10, 0, "DOWN");
        assert Request.checkFormat(10, 0, "ER");
        
        assert Request.checkFormat(5, 0, "UP");
        assert Request.checkFormat(5, 0, "DOWN");
        assert Request.checkFormat(5, 0, "ER");
        
        assert !Request.checkFormat(0, 0, "UP");
        assert !Request.checkFormat(0, 0, "DOWN");
        assert !Request.checkFormat(0, 0, "ER");

        assert !Request.checkFormat(11, 0, "UP");
        assert !Request.checkFormat(11, 0, "DOWN");
        assert !Request.checkFormat(11, 0, "ER");
        
        assert !Request.checkFormat(5, -1, "UP");
        assert !Request.checkFormat(5, -1, "DOWN");
        assert !Request.checkFormat(5, -1, "ER");

        assert !Request.checkFormat(5, 4294967296L, "UP");
        assert !Request.checkFormat(5, 4294967296L, "DOWN");
        assert !Request.checkFormat(5, 4294967296L, "ER");
        
        assert Request.checkFormat(5, 1, "UP");
        assert Request.checkFormat(5, 1, "DOWN");
        assert Request.checkFormat(5, 1, "ER");
    
        assert !Request.checkFormat(5, 0, "UP");
        assert !Request.checkFormat(5, 0, "DOWN");
        assert !Request.checkFormat(5, 0, "ER");
        
    }
    

    @Test
    public void testToString() {
        assert new Request(1, 0, "UP").toString().equals("FR,1,UP,0");
        assert new Request(10, 12, "DOWN").toString().equals("FR,10,DOWN,12");
        assert new Request(10, 12, "ER").toString().equals("ER,10,12");
        assert new Request(1, 4000000000L, "ER").toString().equals("ER,1,4000000000");
        
        
    }

}
