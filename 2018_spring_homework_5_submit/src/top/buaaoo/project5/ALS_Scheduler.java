package top.buaaoo.project5;

public class ALS_Scheduler extends Scheduler {

    Request mainReq;

    ALS_Scheduler(RequestQueue requestQueue) {
        super(requestQueue);
    }

    void work() { // 执行调度过程

        elevator = new Elevator();
        while (!reqQueue.isEmpty()) {
            Request request = reqQueue.deQueue();
            if (!request.isExecuted() && !request.isSameRequest()) {

                mainReq = request;

                if (mainReq.getFloor() > elevator.getFloor()) {
                    status = "UP";
                } else if (mainReq.getFloor() < elevator.getFloor()) {
                    status = "DOWN";
                } else {
                    status = "STILL";
                }
                elevator.updateStatus(status);
                elevator.updateTime(Math.max(elevator.getCurrentTime(), mainReq.getTime()));

                if (status.equals("STILL")) {
                    elevator.updateTime(elevator.getCurrentTime() + 1);
                    mainReq.setExecuted();
                    System.out.println(elevator.toString(mainReq));
                    checkSameRequest(mainReq, elevator.getCurrentTime());
                    continue;
                }

                do {
                    while (mainReq.getFloor() != elevator.getFloor()) {
                        if (status.equals("UP")) {
                            elevator.updateFloor(elevator.getFloor() + 1);
                        } else {
                            elevator.updateFloor(elevator.getFloor() - 1);
                        }
                        elevator.updateTime(elevator.getCurrentTime() + 0.5);

                        if (checkPiggybacking(elevator, mainReq)) {
                            elevator.updateTime(elevator.getCurrentTime() + 1);
                        }
                    }
                    request = mainReq;
                    mainReq = changeMainRequest(elevator, mainReq);

                } while (mainReq != request);
            }
        }
    }

    boolean checkPiggybacking(Elevator elev, Request req) { // 检查是否可以捎带其他请求
        boolean flag = false;
        for (int i = reqQueue.front - 1; i <= reqQueue.rear && reqQueue.queue[i].getTime() < elev.getCurrentTime(); i++) {
            boolean bool = !reqQueue.queue[i].isSameRequest() && !reqQueue.queue[i].isExecuted()
                    && reqQueue.queue[i].getFloor() == elev.getFloor() && (reqQueue.queue[i].getDirection().equals("ER")
                            || reqQueue.queue[i] == req || reqQueue.queue[i].getDirection().equals(elev.getCurrentStatus()));
            if (bool) {
                reqQueue.queue[i].setExecuted();
                System.out.println(elev.toString(reqQueue.queue[i]));
                checkSameRequest(reqQueue.queue[i], elev.getCurrentTime() + 1);// 需要进一步验证
                flag = true;
            }
        }
        return flag;
    }

    Request changeMainRequest(Elevator elev, Request req) { // 检查是否可以更换主请求
        for (int i = reqQueue.front; i <= reqQueue.rear && reqQueue.queue[i].getTime() < elev.getCurrentTime() - 1; i++) {
            boolean bool = !reqQueue.queue[i].isSameRequest() && !reqQueue.queue[i].isExecuted()
                    && reqQueue.queue[i].getDirection().equals("ER")
                    && (elev.getCurrentStatus().equals("UP") && reqQueue.queue[i].getFloor() > elev.getFloor()
                            || elev.getCurrentStatus().equals("DOWN") && reqQueue.queue[i].getFloor() < elev.getFloor());
            if (bool) {
                return reqQueue.queue[i];
            }
        }
        return req;
    }

}
