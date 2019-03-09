package top.buaaoo.project11;

import java.awt.Point;
import java.util.ArrayList;


public class TraceableTaxi extends Taxi {
    /**
     * Overview: 可追踪出租车线程类，保存出租车的各项状态信息，线程执行出租车的正常运行过程
     * INHERIT: Taxi
     * 
     */
    private ArrayList<Record> records;
    private int preIndex;
    private int nextIndex;
    
    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == ((taxiNum>=0 && taxiNum<100)&&(currentPoint!=null && 0<=currentPoint.x&& 
     * currentPoint.x<80 && 0<=currentPoint.y && currentPoint.y<80)&&(lastPoint!=null &&
     *  0<=lastPoint.x&& lastPoint.x<80 && 0<=lastPoint.y && lastPoint.y<80)&&
     *  (0<=currentStatus&& currentStatus<=3)&&(mapInfo!=null && taxiGUI!=null && records!=null)
     *  &&(waitTime>=0 && credit>=0) && preIndex >=-1 && next>=0);
     */
    public boolean repOK() {
        if(!(taxiNum>=0 && taxiNum<100)) {
            return false;
        }
        if(!(currentPoint!=null && 0<=currentPoint.x&& currentPoint.x<80 && 0<=currentPoint.y && currentPoint.y<80)) {
            return false;
        }
        if(!(lastPoint!=null && 0<=lastPoint.x&& lastPoint.x<80 && 0<=lastPoint.y && lastPoint.y<80)) {
            return false;
        }
        if(!(0<=currentStatus&& currentStatus<=3)) {
            return false;
        }
        if(!(mapInfo!=null && taxiGUI!=null && records!=null)){
            return false;
        }
        if(!(waitTime>=0 && credit>=0)) {
            return false;
        }
        if(!(preIndex>=-1 && nextIndex>=0)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
    /**
     * @REQUIRES: 0<=num<100;point!=null && 0<=point.x<80 &&
     *            0<=point.y<80;0<=status<=3;mapInfo!=null;taxiGUI!=null;
     * @MODIFIES: \this.taxiNum;\this.currentPoint;\this.currentStatus;\this.waitTime;\this.credit;\this.mapInfo;\this.taxiGUI;
     * @EFFECTS: \this.taxiNum == num; \this.currentPoint == point;
     *           \this.currentStatus == status; \this.waitTime == 0; \this.credit == 0L;
     *           \this.mapInfo == mapInfo; \this.taxiGUI == taxiGUI;
     */
    public TraceableTaxi(int num, Point point, int status, MapInfo mapInfo, TaxiGUI taxiGUI) {
        super(num, point, status, mapInfo, taxiGUI);
        records = new ArrayList<Record>();
        preIndex = -1;
        nextIndex = 0;
    }
    
    
    /**
     * @REQUIRES: request!=null && orderPoint!=null && 0<=orderPoint.x<80 && 0<=orderPoint.y<80;drivingPath!=null;records!=null;
     * @MODIFIES: \this.records;
     * @EFFECTS: \this.records.size()==\old(\this.records.size())+1;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void recordServeInfo() {
        records.add(new Record(request, orderPoint, drivingPath));
        
    }
    
    /**
     * @REQUIRES: \this != null;records!=null;
     * @MODIFIES: None;
     * @EFFECTS: \result == records.size();
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized int serveCount() {
        return records.size();
    }
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (nextIndex>=0 && nextIndex < records.size());
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized boolean hasNext() {
        return (nextIndex>=0 && nextIndex < records.size());
    }
    
    
    /**
     * @REQUIRES: \this != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == (preIndex>=0 && preIndex < records.size());
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized boolean hasPrevious() {
        return (preIndex>=0 && preIndex < records.size());
    }
    
    
    
    /**
     * @REQUIRES: records!=null;
     * @MODIFIES: \this.nextIndex;\this.preIndex;System.out;
     * @EFFECTS: (nextIndex>=0 && nextIndex < records.size())==> 
     *           (nextIndex == \old(nextIndex)+1 && preIndex == \old(preIndex)+1 && System.out输出Record信息);
     *           (!(nextIndex>=0 && nextIndex < records.size()))==> System.out输出错误提示信息信息;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void next() {
        if(hasNext()) {
            records.get(nextIndex).printRecord();
            nextIndex++;
            preIndex++;
        }else {
            System.out.println("the next element does not exist.");
        }
    }
    
    /**
     * @REQUIRES: records!=null;
     * @MODIFIES: \this.nextIndex;\this.preIndex;
     * @EFFECTS: (preIndex>=0 && preIndex < records.size())==> 
     *           (nextIndex == \old(nextIndex)-1 && preIndex == \old(preIndex)-1 && System.out输出Record信息);
     *           (!(nextIndex>=0 && nextIndex < records.size()))==> System.out输出错误提示信息信息;
     * @THREAD_EFFECTS: \locked();
     */
    public void previous() {
        if(hasPrevious()) {
            records.get(preIndex).printRecord();
            preIndex--;
            nextIndex--;
        }else {
            System.out.println("the previous element does not exist.");
        }
    }
    
}
