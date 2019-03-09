package top.buaaoo.project11;


public class RequestScheduler implements Runnable, Constant {
    /**
     * Overview: 请求调度类，主要负责对合法请求的派单过程
     * 
     */
    
    
    private Request request;
    private static final Object lock = new Object();
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (request != null && lock != null);
     */
    public boolean repOK() {
        return request != null && lock != null;
    }
    
    
    /**
     * @REQUIRES: request != null;
     * @MODIFIES: \this.request;
     * @EFFECTS: \this.request == request;
     */
    public RequestScheduler(Request request) {
        this.request = request;
    }
    
    
    /**
     * @REQUIRES: \this != null;Main.requestQueue!=null;request!=null;lock != null;
     * @MODIFIES: Main.requestQueue;\this.request;
     * @EFFECTS: 未捕捉到异常 ==> 抢单窗口关闭后request被分配给指定的出租车并输出相关信息，无出租车可调度时通过输出文件进行相关提示;
     *           未捕捉到异常 ==> !Main.requestQueue.contains(request);
     *           捕捉到异常 ==> 程序退出;
     * @THREAD_EFFECTS: \locked(\this.lock);
     */
    public void run() {
        try {
            long sleepTime = request.time - gv.getTime();//
            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
            request.setUnDistributed();
            request.createOutputFile();
            request.printRequestInfo();
            Main.taxiGUI.RequestTaxi(request.srcPoint, request.dstPoint);
            sleepTime = request.time + 7500 - gv.getTime();//
            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
            request.setDistributed();
            Main.requestQueue.remove(request);
            long maxCredit = Long.MIN_VALUE;
            boolean isScrambling = false;
            Taxi taxi, dstTaxi = null;

            synchronized (lock) {
                for (int i = 0; i < 100; i++) {
                    if (request.isScramble(i)) {
                        taxi = Main.taxiQueue.get(i);
                        request.printTaxiInfo(taxi);
                        if ((taxi.getStatus() == WAIT || taxi.getStatus() == STOP) && taxi.getCredit() > maxCredit) {
                            maxCredit = taxi.getCredit();
                            isScrambling = true;
                        }
                    }

                }
                if (!isScrambling) {
                    request.printNoTaxi();
                    return;
                }

                int minDistance = Integer.MAX_VALUE;
                for (int i = 0; i < 100; i++) {
                    taxi = Main.taxiQueue.get(i);
                    if (request.isScramble(i) && taxi.getCredit() == maxCredit
                            && (taxi.getStatus() == WAIT || taxi.getStatus() == STOP)) {
                        int distance = GUIInfo.getDistance(taxi.getTaxiNum(), taxi.getCurrentPoint(), request.srcPoint);
                        if (minDistance > distance) {
                            minDistance = distance;
                            dstTaxi = taxi;
                        }
                    }

                }
                if (dstTaxi != null) {
                    dstTaxi.updateStatus(ORDER_RECEIVED, request);
                } else {
                    request.printNoTaxi();
                }
                
            }

        }
        catch (Throwable e) {
            System.exit(0);
        }

    }

}
