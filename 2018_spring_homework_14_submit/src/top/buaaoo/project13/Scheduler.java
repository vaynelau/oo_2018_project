package top.buaaoo.project13;

public class Scheduler{
    /*
     * OVERVIEW: 调度器类，负责执行请求，更新电梯状态，检查同质请求，可捎带请求等;
     * 表示对象：RequestQueue reqQueue, Elevator elevator;
     * 抽象函数：AF(c) = {reqQueue, elevator} where c.reqQueue == reqQueue, c.elevator == elevator;
     * 不变式：(c.reqQueue != null) && (c.elevator != null);
     */
    
    private RequestQueue reqQueue;
    private Elevator elevator;
    
    /**
     * @REQUIRES: None;
     * @MODIFIES: None;
     * @EFFECTS: ((reqQueue != null) && (elevator != null)) ==> (\result == true);
     *           (!((reqQueue != null) && (elevator != null)) ==> (\result == false);
     */
    public boolean repOK() {
        return ((reqQueue != null) && (elevator != null));
    }
      
    
    /**
     * @REQUIRES: requestQueue != null && elevator != null;
     * @MODIFIES: this;
     * @EFFECTS: this != null && this.elevator == elevator && this.reqQueue == requestQueue;
     */
    Scheduler(RequestQueue requestQueue,Elevator elevator) {
        this.elevator = elevator;
        this.reqQueue = requestQueue;
    }
    
    
    
    /**
     * @REQUIRES: 1<=currentFloor<=10 && 1<=targetFloor<=10;
     * @MODIFIES: this;
     * @EFFECTS: (targetFloor > currentFloor) ==> (this.elevator.currentStatus.equals("UP"));
     *           (targetFloor < currentFloor) ==> (this.elevator.currentStatus.equals("DOWN"));
     *           (targetFloor == currentFloor) ==> (this.elevator.currentStatus.equals("STILL"));
     */
    void updateStatus(int currentFloor, int targetFloor) {
        String status;
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
     * @REQUIRES: request != null && this.elevator.getFloor() == request.getFloor();
     * @MODIFIES: this;request;System.out;
     * @EFFECTS: (this.elevator.currentTime == \old(this.elevator.currentTime) + 1) &&
     *           (request.getIsExecuted() == true) && System.out有请求执行结果输出;
     */
    void runStill(Request request) {
        elevator.setTime(elevator.getTime() + 1);
        request.setExecuted();
        System.out.println(elevator.toString(request));
        checkSameRequest(request, elevator.getTime());
    }
    
    
    
    /**
     * @REQUIRES: request != null && this.elevator.getFloor() != request.getFloor();
     * @MODIFIES: this;
     * @EFFECTS: (this.elevator.currentTime > \old(this.elevator.currentTime)) &&
     *           (this.elevator.getFloor() == request.getFloor());
     */
    void runUpOrDown(Request request){
        while (request.getFloor() != elevator.getFloor()) {
            if (elevator.getStatus().equals("UP")) {
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
     * @REQUIRES: req != null;
     * @MODIFIES: this;System.out;
     * @EFFECTS: (!reqQueue.isEmpty() && finishTime >= elevator.getTime()) ==> (\all int i;(i >= reqQueue.getFront() && i <= reqQueue.getRear() && reqQueue.get(i).getTime() <= finishTime && !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted() && reqQueue.get(i).getDirection().equals(req.getDirection()) && reqQueue.get(i).getFloor() == req.getFloor());(reqQueue.get(i).getIsSame() && System.out输出该同质请求));
     * (reqQueue.isEmpty() || finishTime < elevator.getTime()) ==> do nothing;
     */
    void checkSameRequest(Request req, double finishTime) {// finishTime为请求req执行完成的时间
        if(!reqQueue.isEmpty() && finishTime >= elevator.getTime()) { 
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
     * @REQUIRES: req != null;
     * @MODIFIES: this;System.out;
     * @EFFECTS: (\all int i;(i >= reqQueue.getFront()-1 && i <= reqQueue.getRear() && reqQueue.get(i).getTime() < elevator.getTime() && !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted() && reqQueue.get(i).getFloor() == elevator.getFloor() && (reqQueue.get(i).getDirection().equals("ER") || reqQueue.get(i) == req || reqQueue.get(i).getDirection().equals(elevator.getStatus())));(reqQueue.get(i).getIsExecuted() && System.out有提示信息输出));
     *           (\result == (\exist int i;(i >= reqQueue.getFront()-1 && i <= reqQueue.getRear() && reqQueue.get(i).getTime() < elevator.getTime() && !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted() && reqQueue.get(i).getFloor() == elevator.getFloor() && (reqQueue.get(i).getDirection().equals("ER") || reqQueue.get(i) == req || reqQueue.get(i).getDirection().equals(elevator.getStatus())))));
     */
    boolean checkPiggybacking(Request req) { // 检查是否可以捎带其他请求
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
     * @REQUIRES: req != null;
     * @MODIFIES: None;
     * @EFFECTS: (\exist int i;(i >= reqQueue.getFront() && i <= reqQueue.getRear() && reqQueue.get(i).getTime() < elevator.getTime() - 1 && !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted() && reqQueue.get(i).getDirection().equals("ER") && (elevator.getStatus().equals("UP") && reqQueue.get(i).getFloor() > elevator.getFloor() || elevator.getStatus().equals("DOWN") && reqQueue.get(i).getFloor() < elevator.getFloor()))) ==> (\result == this.reqQueue.get(i));
     * !(\exist int i;(i >= reqQueue.getFront() && i <= reqQueue.getRear() && reqQueue.get(i).getTime() < elevator.getTime() - 1 && !reqQueue.get(i).getIsSame() && !reqQueue.get(i).getIsExecuted() && reqQueue.get(i).getDirection().equals("ER") && (elevator.getStatus().equals("UP") && reqQueue.get(i).getFloor() > elevator.getFloor() || elevator.getStatus().equals("DOWN") && reqQueue.get(i).getFloor() < elevator.getFloor()))) ==> (\result == req);
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
