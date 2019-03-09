package top.buaaoo.project13;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainTest {

    private static int i;
    
    @SuppressWarnings("unused")
    private Main main; 

    @Before
    public void setUp() throws Exception {
        main = new Main();
        System.out.println("-----test RequestQueue "+ i +" begins-----");
    }

    @After
    public void tearDown() throws Exception {
        main = null;
        System.out.println("-----test RequestQueue "+ i++ +" ends-----");
    } 

    @Test
    public void test() {
        
    }

}
