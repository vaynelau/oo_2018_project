package top.buaaoo.project6;

public class Main {

    public static void main(String[] args) {

        try {
            
            OutputHandler.initialize();
            InputHandler.readMonitoringJobs();
            TestThread testThread = new TestThread();
            Thread t = new Thread(testThread);
            t.start();
//            System.out.println("mainThread is end.");
        }
        catch (Throwable e) {
            // e.printStackTrace();
            System.exit(0);
        }

    }

}
