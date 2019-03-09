package top.buaaoo.project5;

public class FloorRequestHandler implements Runnable {

    private static Elevator[] elev;
    private Request req;
    private volatile static int count = 0;
    volatile static RequestQueue FR_ReqQueue = new RequestQueue();

    public FloorRequestHandler(Elevator[] elev, Request req) {
        FloorRequestHandler.elev = elev;
        this.req = req;
    }

    public synchronized boolean distribute() {
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
            if (!req.isDistributed()) {
                req.setDistributed();
                // if(elev[min].reqQueue.isEmpty()) {
                //
                // elev[min].setTargetFloor(req.getFloor());
                // elev[min].updateStatus();
                // }
                elev[min].reqQueue.enQueue(req);
                elev[min].setBusy();
                // System.out.println("#" + min + " is selected(piggyback) by " + req);
                return true;
            }

        }
        else if (elev[1].isFree() || elev[2].isFree() || elev[3].isFree()) {
            freeElevatorDistribute(req);
            return true;
        }
        return false;
    }

    public static synchronized boolean freeElevatorDistribute(Request req) {
        int min = 0;
        long minMotion = Long.MAX_VALUE;
        try {
            Thread.sleep(1);
        }
        catch (InterruptedException e) {
        }
        if (req.isPiggyback(elev[1]) || req.isPiggyback(elev[2]) || req.isPiggyback(elev[3])) {
            return false;
        }
        else if (elev[1].isFree() || elev[2].isFree() || elev[3].isFree()) {
            for (int i = 1; i <= 3; i++) {
                if (elev[i].isFree() && minMotion > elev[i].getMotion()) {
                    min = i;
                    minMotion = elev[i].getMotion();
                }
            }
            while (!FR_ReqQueue.isEmpty()) {
                req = FR_ReqQueue.deQueue();
                // System.out.println(req + " deQueue");
                if (!req.isDistributed()) {
                    req.setDistributed();
                    elev[min].setTargetFloor(req.getFloor());
                    elev[min].updateStatus();

                    elev[min].reqQueue.enQueue(req);
                    elev[min].setBusy();
                    // System.out.println("#" + min + " is selected(free) by " + req);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void run() {
        countIncrease();

        // System.out.println("FloorRequestHandlerThread(" + req + ") is start.");
        while (!req.isDistributed()) {
            while (!req.isDistributed() && !req.isPiggyback(elev[1]) && !req.isPiggyback(elev[2])
                    && !req.isPiggyback(elev[3]) && !elev[1].isFree() && !elev[2].isFree() && !elev[3].isFree()) {
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                }
            }
            distribute();
        }
        // System.out.println("FloorRequestHandlerThread(" + req + ") is end.");
        countDecrease();
    }

    public synchronized static int getFRHandlerThreadNum() {
        return count;
    }

    public synchronized static void countIncrease() {
        count++;
    }

    public synchronized static void countDecrease() {
        count--;
    }
}
