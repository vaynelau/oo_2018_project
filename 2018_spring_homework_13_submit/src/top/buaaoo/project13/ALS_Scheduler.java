package top.buaaoo.project13;

public class ALS_Scheduler{
    /**
     * Overview: 调度器类，负责对同质请求，可捎带请求的检查与执行同侧或非同层请求等;
     * 
     */
    
    private RequestQueue reqQueue;
    private Elevator elevator;
    private String status;
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: ((reqQueue != null) && (elevator != null) && (status != null && (status.equals("UP") || status.equals("DOWN") || status.equals("STILL")))) ==> (\result == true);
     *           (!((reqQueue != null) && (elevator != null) && (status != null && (status.equals("UP") || status.equals("DOWN") || status.equals("STILL"))))) ==> (\result == false);
     */
    public boolean repOK() {
        return ((reqQueue != null) && (elevator != null) && (status != null && (status.equals("UP") || status.equals("DOWN") || status.equals("STILL"))));
    }
      
    
    /**
     * @REQUIRES: requestQueue != null && elevator != null;
     * @MODIFIES: \this.elevator;\this.reqQueue;\this.status;
     * @EFFECTS: \this.elevator == elevator && \this.reqQueue == requestQueue && \this.status.equals("STILL");
     */
    ALS_Scheduler(RequestQueue requestQueue,Elevator elevator) {
        this.elevator = elevator;
        this.reqQueue = requestQueue;
        this.status = "STILL";
    }
    
    
    /** 
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == \this.status;
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * @REQUIRES: \this != null && str != null && (str.equals("UP") || str.equals("DOWN") || str.equals("STILL"));
     * @MODIFIES: \this.status;
     * @EFFECTS: \this.status == str;
     */
    public void setStatus(String str) {
        this.status = str;
    }
    
    /**
     * @REQUIRES: \this != null && 1<=currentFloor<=10 && 1<=targetFloor<=10;
     * @MODIFIES: \this.status;\this.elevator;
     * @EFFECTS: (targetFloor > currentFloor) ==> (\this.status.equals("UP") && \this.elevator.currentStatus.equals("UP"));
     *           (targetFloor < currentFloor) ==> (\this.status.equals("DOWN") && \this.elevator.currentStatus.equals("DOWN"));
     *           (targetFloor == currentFloor) ==> (\this.status.equals("STILL") && \this.elevator.currentStatus.equals("STILL"));
     * 
     */
    void updateStatus(int currentFloor, int targetFloor) {
        if (targetFloor > currentFloor) {
            status = "UP";
        } else if (targetFloor < currentFloor) {
            status = "DOWN";
        } else {
            status = "STILL";
        }
        elevator.setStatus(status);
    }
    
    
    /**
     * @REQUIRES: \this != null && request != null && \this.elevator.getFloor() == request.getFloor();
     * @MODIFIES: \this.elevator;\this.reqQueue;request;System.out;
     * @EFFECTS: (\this.elevator.currentTime == \old(\this.elevator.currentTime) + 1) &&
     *           (request.getIsExecuted() == true) && System.out有调度结果输出;
     */
    void runStill(Request request) {
        elevator.setTime(elevator.getTime() + 1);
        request.setExecuted();
        System.out.println(elevator.toString(request));
        checkSameRequest(request, elevator.getTime());
    }
    
    
    
    /**
     * @REQUIRES: \this != null && request != null && \this.elevator.getFloor() != request.getFloor();
     * @MODIFIES: \this.elevator;
     * @EFFECTS: (\this.elevator.currentTime > \old(\this.elevator.currentTime)) &&
     *           (\this.elevator.getFloor() == request.getFloor());
     */
    void runUpOrDown(Request request){
        while (request.getFloor() != elevator.getFloor()) {
            if (status.equals("UP")) {
                elevator.setFloor(elevator.getFloor() + 1);
            } else {
                elevator.setFloor(elevator.getFloor() - 1);
            }
            elevator.setTime(elevator.getTime() + 0.5);
 
            if (checkPiggybacking(request)) {
                elevator.setTime(elevator.getTime() + 1);
            } 
        }
    }
    
     
    
    /**
     * @REQUIRES: \this != null && \this.reqQueue != null && request != null && finishTime >= elevator.getTime();
     * @MODIFIES: \this.reqQueue;System.out;
     * @EFFECTS: (!reqQueue.isEmpty()) ==> (\all int i;(i >= reqQueue.getFront() && i <= reqQueue.getRear() &&
     *           reqQueue.get(i).getTime() <= finishTime &&
     *           !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted() && 
     *           reqQueue.get(i).getDirection().equals(req.getDirection()) &&
     *           reqQueue.get(i).getFloor() == req.getFloor());(reqQueue.get(i).getIsSame() && System.out有提示信息输出));
     */
    void checkSameRequest(Request req, double finishTime) {// finishTime为请求req执行完成的时间
        if(!reqQueue.isEmpty()) { 
            for (int i = reqQueue.getFront(); i <= reqQueue.getRear() && reqQueue.get(i).getTime() <= finishTime; i++) {
                boolean bool = !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted()
                        && reqQueue.get(i).getDirection().equals(req.getDirection())
                        && reqQueue.get(i).getFloor() == req.getFloor();
                if (bool) {
                    reqQueue.get(i).setSame();
                    System.out.println("#SAME[" + reqQueue.get(i) + "]");
                }
            }
        }
    }
    
    /**
     * @REQUIRES: \this != null && \this.reqQueue != null && \this.elevator != null && req != null;
     * @MODIFIES: \this.reqQueue;System.out;
     * @EFFECTS: (reqQueue.getFront()>0) ==> (\all int i;(i >= reqQueue.getFront()-1 && i <= reqQueue.getRear() &&
     *            reqQueue.get(i).getTime() < elevator.getTime() &&
     *           !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted()
     *           && reqQueue.get(i).getFloor() == elevator.getFloor() && (reqQueue.get(i).getDirection().equals("ER")
     *           || reqQueue.get(i) == req || reqQueue.get(i).getDirection().equals(elevator.getStatus())));(reqQueue.get(i).getIsExecuted() && System.out有提示信息输出));
     *           (reqQueue.getFront()>0) ==> (\result == (\exist int i;(i >= reqQueue.getFront()-1 && i <= reqQueue.getRear() &&
     *            reqQueue.get(i).getTime() < elevator.getTime() &&
     *           !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted()
     *           && reqQueue.get(i).getFloor() == elevator.getFloor() && (reqQueue.get(i).getDirection().equals("ER")
     *           || reqQueue.get(i) == req || reqQueue.get(i).getDirection().equals(elevator.getStatus())))));
     *           (reqQueue.getFront()<=0) ==> (\result == false); 
     */
    boolean checkPiggybacking(Request req) { // 检查是否可以捎带其他请求
        if(reqQueue.getFront()<=0) {
            return false;
        } 
        boolean flag = false; 
        for (int i = reqQueue.getFront() - 1; i <= reqQueue.getRear() && reqQueue.get(i).getTime() < elevator.getTime(); i++) {
            boolean bool = !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted() 
                    && reqQueue.get(i).getFloor() == elevator.getFloor() && (reqQueue.get(i).getDirection().equals("ER")
                            || reqQueue.get(i) == req || reqQueue.get(i).getDirection().equals(elevator.getStatus()));
            if (bool) {
                reqQueue.get(i).setExecuted();
                System.out.println(elevator.toString(reqQueue.get(i)));
                checkSameRequest(reqQueue.get(i), elevator.getTime() + 1);
                flag = true;
            }
        }
        return flag;
    }
     
    
    
    /**
     * @REQUIRES: \this != null && \this.reqQueue != null && \this.elevator != null && req != null;
     * @MODIFIES: None;
     * @EFFECTS: (\exist int i;(i >= reqQueue.getFront() && i <= reqQueue.getRear() &&
     *           reqQueue.get(i).getTime() < elevator.getTime() - 1 &&
     *           !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted()
     *           && reqQueue.get(i).getDirection().equals("ER")
     *           && (elevator.getStatus().equals("UP") && reqQueue.get(i).getFloor() > elevator.getFloor()
     *           || elevator.getStatus().equals("DOWN") && reqQueue.get(i).getFloor() < elevator.getFloor()))) ==> (\result == \this.reqQueue.get(i));
     *           !(\exist int i;(i >= reqQueue.getFront() && i <= reqQueue.getRear() &&
     *           reqQueue.get(i).getTime() < elevator.getTime() - 1 &&
     *           !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted()
     *           && reqQueue.get(i).getDirection().equals("ER")
     *           && (elevator.getStatus().equals("UP") && reqQueue.get(i).getFloor() > elevator.getFloor()
     *           || elevator.getStatus().equals("DOWN") && reqQueue.get(i).getFloor() < elevator.getFloor()))) ==> (\result == req);
     */
    Request changeMainRequest(Request req) { // 检查是否可以更换主请求
        for (int i = reqQueue.getFront(); i <= reqQueue.getRear() && reqQueue.get(i).getTime() < elevator.getTime() - 1; i++) {
            boolean bool = !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted()
                    && reqQueue.get(i).getDirection().equals("ER")
                    && (elevator.getStatus().equals("UP") && reqQueue.get(i).getFloor() > elevator.getFloor()
                            || elevator.getStatus().equals("DOWN") && reqQueue.get(i).getFloor() < elevator.getFloor());
            if (bool) { 
                return reqQueue.get(i); 
            }
        }
        return req;
    }

}
