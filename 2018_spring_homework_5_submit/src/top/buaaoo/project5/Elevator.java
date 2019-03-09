package top.buaaoo.project5;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

public class Elevator implements ElevatorInterface, Runnable {

    private volatile int currentFloor, targetFloor;
    private volatile double currentTime;
    private volatile String currentStatus;
    private volatile int elevNumber;
    private volatile long motion;
    private volatile boolean[] button = new boolean[22];
    volatile RequestQueue reqQueue;
    volatile boolean stop = false;
    volatile boolean elevButton = false;
    volatile boolean floorButton = false;
    volatile boolean free = true;

    private DecimalFormat doubleFormat = new DecimalFormat("0.0");

    Elevator() {
        currentFloor = 1;

        currentTime = 0;
        currentStatus = "STILL";
    }

    Elevator(int n) {
        currentFloor = 1;
        targetFloor = 1;
        currentTime = 0;
        currentStatus = "WFS";
        elevNumber = n;
        motion = 0;
        Arrays.fill(button, false);
        reqQueue = new RequestQueue();
    }

    synchronized int getTargetFloor() {
        return targetFloor;
    }

    synchronized void setTargetFloor(int floor) {
        targetFloor = floor;
    }

    synchronized long getMotion() {
        return motion;
    }

    synchronized boolean isFree() {
        // if (!reqQueue.isEmpty()) {
        // return false;
        // }
        return free;
    }

    synchronized void setFree() {
        free = true;
    }

    synchronized void setBusy() {
        free = false;
    }

    synchronized boolean isPressed(int floor) {
        return button[floor];
    }

    synchronized void setPressed(int floor) {
        button[floor] = true;
    }

    synchronized void setUnpressed(int floor) {
        button[floor] = false;
    }

    static void makeElevatorRequest(String str, double time, RequestQueue queue) {
        Request ER = new Request(str, time); // 实例化楼层请求ER
        if (ER.checkFormat() == true) {
            queue.enQueue(ER); // 将ER加入队列
        }
        else {
            OutputHandler.printInvalidRequest(str, time);
        }
    }

    public synchronized int getFloor() {
        return currentFloor;
    }

    public synchronized void updateFloor(int floor) {
        currentFloor = floor;
    }

    public synchronized double getCurrentTime() {
        return currentTime;
        // long time = new Date().getTime();
        // return (time - Main.initialTime)/1000.0;
    }

    public synchronized void updateTime(double time) {
        currentTime = time;
    }

    public synchronized String getCurrentStatus() {

        return currentStatus;
    }

    public synchronized void updateStatus(String str) {
        currentStatus = str;
    }

    public synchronized void updateStatus() {
        if (targetFloor > currentFloor) {
            currentStatus = "UP";
        }
        else if (targetFloor < currentFloor) {
            currentStatus = "DOWN";
        }
        else {
            currentStatus = "STILL";
        }
    }

    @Override
    public synchronized String toString() {
        String str = String.format("#%d,%d,%s,%d,%.1f", elevNumber, currentFloor, currentStatus, motion, currentTime);
        return str;
    }

    public String toString(Request req) {
        String str = "[" + req + "]/(" + currentFloor + "," + currentStatus + "," + doubleFormat.format(currentTime)
                + ")";
        return str;
    }

    @Override
    public void run() {

        Request mainReq, req;

        while (!stop || !reqQueue.isEmpty() || FloorRequestHandler.getFRHandlerThreadNum() != 0 || !Floor.isFree()) {
            if (reqQueue.isEmpty()) {
                // updateStatus("WFS");
                setFree();
                // try {
                // Thread.sleep(1);
                // }
                // catch (InterruptedException e) {
                // }
            }
            else {
                setBusy();
                req = reqQueue.deQueue();
                if (!req.isExecuted() && !req.isSameRequest()) {

                    mainReq = req;
                    targetFloor = mainReq.getFloor();

                    if (targetFloor > currentFloor) {
                        updateStatus("UP");
                    }
                    else if (targetFloor < currentFloor) {
                        updateStatus("DOWN");
                    }
                    else {
                        updateStatus("STILL");
                    }

                    updateTime(Math.max(currentTime, mainReq.getTime()));

                    if (getCurrentStatus().equals("STILL")) {
                        try {
                            long time = (long) ((currentTime + 6) * 1000 + Main.initialTime - new Date().getTime());
                            if (time > 0) {
                                Thread.sleep(time);
                            }
                        }
                        catch (InterruptedException e) {
                        }

                        updateTime(currentTime + 6);
                        mainReq.setExecuted();
                        OutputHandler.printExecutionResult(mainReq, this);
                        if (mainReq.getDirection().equals("ER")) {
                            setUnpressed(currentFloor);
                        }
                        else {
                            Floor.setUnpressed(currentFloor, mainReq.getDirection());
                        }

                        // checkSameRequest(mainReq, currentTime);
                        continue;
                    }

                    do {
                        while (targetFloor != currentFloor) {
                            try {
                                long time = (long) ((currentTime + 3) * 1000 + Main.initialTime - new Date().getTime());
                                if (time > 0) {
                                    Thread.sleep(time);
                                }
                            }
                            catch (InterruptedException e) {
                            }

                            updateTime(currentTime + 3);
                            motion += 1;

                            if (currentStatus.equals("UP")) {
                                updateFloor(currentFloor + 1);
                            }
                            else {
                                updateFloor(currentFloor - 1);
                            }
                            elevButton = false;
                            floorButton = false;
                            if (checkPiggybacking(mainReq)) {

                                try {
                                    long time = (long) ((currentTime + 6) * 1000 + Main.initialTime - new Date().getTime());
                                    if (time > 0) {
                                        Thread.sleep(time);
                                    }
                                }
                                catch (InterruptedException e) {
                                }
                                updateTime(currentTime + 6);
                                if (elevButton) {
                                    setUnpressed(currentFloor);
                                }
                                if (floorButton) {
                                    Floor.setUnpressed(currentFloor, currentStatus);
                                }
                                if (targetFloor == currentFloor && !mainReq.getDirection().equals("ER")) {
                                    Floor.setUnpressed(currentFloor, mainReq.getDirection());
                                }
                            }
                        }
                        req = mainReq;
                        mainReq = changeMainRequest(mainReq);

                    } while (mainReq != req);
                }
            }

        }
//        System.out.println("elev" + elevNumber + "Thread is end.");
    }

    synchronized boolean checkPiggybacking(Request req) { // 检查是否可以捎带其他请求
        boolean flag = false;
        for (int i = reqQueue.front - 1; i <= reqQueue.rear && reqQueue.queue[i].getTime() < currentTime; i++) {
            boolean bool = !reqQueue.queue[i].isSameRequest() && !reqQueue.queue[i].isExecuted()
                    && reqQueue.queue[i].getFloor() == currentFloor && (reqQueue.queue[i].getDirection().equals("ER")
                            || reqQueue.queue[i] == req || reqQueue.queue[i].getDirection().equals(getCurrentStatus()));
            if (bool) {
                reqQueue.queue[i].setExecuted();
                OutputHandler.printExecutionResult(reqQueue.queue[i], this);
                if (reqQueue.queue[i].getDirection().equals("ER")) {
                    elevButton = true;
                }
                else if (reqQueue.queue[i].getDirection().equals(getCurrentStatus())) {
                    floorButton = true;
                }
                // checkSameRequest(reqQueue.queue[i], elev.getTime() + 1);// 需要进一步验证
                flag = true;
            }
        }
        return flag;
    }

    synchronized Request changeMainRequest(Request req) { // 检查是否可以更换主请求
        for (int i = reqQueue.front; i <= reqQueue.rear && reqQueue.queue[i].getTime() < currentTime - 6; i++) {
            boolean bool = !reqQueue.queue[i].isSameRequest() && !reqQueue.queue[i].isExecuted()
                    && reqQueue.queue[i].getDirection().equals("ER")
                    && (currentStatus.equals("UP") && reqQueue.queue[i].getFloor() > currentFloor
                            || currentStatus.equals("DOWN") && reqQueue.queue[i].getFloor() < currentFloor);
            if (bool) {
                return reqQueue.queue[i];
            }
        }
        return req;
    }

    public synchronized void stopThread() {
        this.stop = true;

    }

}
