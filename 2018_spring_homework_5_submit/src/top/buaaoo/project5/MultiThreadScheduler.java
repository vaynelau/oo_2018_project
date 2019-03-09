package top.buaaoo.project5;

public class MultiThreadScheduler extends ALS_Scheduler implements Runnable {

    private Elevator[] elev = new Elevator[4];
    private Thread elev1Thread, elev2Thread, elev3Thread;
    volatile boolean stop = false;
    // private static int threadPriority = Thread.MAX_PRIORITY;

    MultiThreadScheduler(RequestQueue requestQueue) {
        super(requestQueue);
        new Floor();
        elev[1] = new Elevator(1);
        elev[2] = new Elevator(2);
        elev[3] = new Elevator(3);
        elev1Thread = new Thread(elev[1]);
        elev2Thread = new Thread(elev[2]);
        elev3Thread = new Thread(elev[3]);
        elev1Thread.start();
        elev2Thread.start();
        elev3Thread.start();
    }

    @Override
    public void run() {

        while (!reqQueue.isEmpty() || !stop) {
            if (reqQueue.isEmpty()) {
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                }
            }
            else {
                Request req = reqQueue.deQueue();
                if (isSameRequest(req)) {
                    OutputHandler.printSameRequest(req);
                }
                else if (req.getDirection().equals("ER")) {
                    int n = req.getElevNum();
                    elev[n].setPressed(req.getFloor());
                    if (elev[n].isFree()) {

                        elev[n].setTargetFloor(req.getFloor());
                        elev[n].updateStatus();
                    }
                    elev[n].reqQueue.enQueue(req);
                    elev[n].setBusy();
                }
                else {
                    Floor.setPressed(req.getFloor(), req.getDirection());

                    int min = 0;
                    long minMotion = Long.MAX_VALUE;
                    // try {
                    // Thread.sleep(1);
                    // }
                    // catch (InterruptedException e) {
                    // }
                    if (req.isPiggyback(elev[1]) || req.isPiggyback(elev[2]) || req.isPiggyback(elev[3])) {
                        for (int i = 1; i <= 3; i++) {
                            if (req.isPiggyback(elev[i]) && minMotion > elev[i].getMotion()) {
                                min = i;
                                minMotion = elev[i].getMotion();
                            }
                        }
                        elev[min].reqQueue.enQueue(req);
                        elev[min].setBusy();
                        // System.out.println("#" + min + " is selected(piggyback) by " + req);

                    }
                    else if (elev[1].isFree() || elev[2].isFree() || elev[3].isFree()) {
                        for (int i = 1; i <= 3; i++) {
                            if (elev[i].isFree() && minMotion > elev[i].getMotion()) {
                                min = i;
                                minMotion = elev[i].getMotion();
                            }
                        }
                        elev[min].setTargetFloor(req.getFloor());
                        elev[min].updateStatus();
                        elev[min].reqQueue.enQueue(req);
                        elev[min].setBusy();
                        // System.out.println("#" + min + " is selected(free) by " + req);
                    }
                    else {
                        FloorRequestHandler.FR_ReqQueue.enQueue(req);
                        // System.out.println(req + " enQueue");
                        FloorRequestHandler fRHandler = new FloorRequestHandler(elev, req);
                        // if (!fRHandler.distribute()) {
                        Thread fRHandlerThread = new Thread(fRHandler);
                        // fRHandlerThread.setPriority(threadPriority);
                        // threadPriority = (threadPriority > Thread.MIN_PRIORITY) ? threadPriority - 1
                        // : threadPriority;
                        fRHandlerThread.start();
                        // try {
                        // Thread.sleep(1); // 使得fRHandlerThread能够马上得以执行
                        // }
                        // catch (InterruptedException e) {
                        // }

                    }

                }
                // try {
                // Thread.sleep(1);
                // }
                // catch (InterruptedException e) {
                // }
            }

        }
        // try {
        // elev1Thread.join();
        // }
        // catch (InterruptedException e) {
        // }
        // try {
        // elev2Thread.join();
        // }
        // catch (InterruptedException e) {
        // }
        // try {
        // elev3Thread.join();
        // }
        // catch (InterruptedException e) {
        // }

        // System.out.println("schedulerThread is end.");
    }

    boolean isSameRequest(Request req) {
        if (req.getElevNum() == 0 && Floor.isPressed(req.getFloor(), req.getDirection())) {
            return true;
        }
        if (req.getElevNum() != 0 && elev[req.getElevNum()].isPressed(req.getFloor())) {
            return true;
        }
        return false;
    }

    public synchronized void stopThread() {
        this.stop = true;
        elev[1].stopThread();
        elev[2].stopThread();
        elev[3].stopThread();
    }

}
