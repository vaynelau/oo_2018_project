package top.buaaoo.project13;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FloorTest {

    private static int i;
    @SuppressWarnings("unused")
    private Floor floor; 

    @Before
    public void setUp() throws Exception {
        floor = new Floor();
        System.out.println("-----test Floor "+ i +" begins-----");
    }

    @After
    public void tearDown() throws Exception {
        floor = null;
        System.out.println("-----test Floor "+ i++ +" ends-----");
    }

    @Test
    public void test() {
        
    }

}
