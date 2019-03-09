package top.buaaoo.project6;

import java.io.File;
import java.util.ArrayList;

public class MonitoringJob {

    File file;
    String task;
    String jobStr;
    FileInfo fileInfo;

    ArrayList<FileInfo> snapShot;
    ArrayList<MonitoringJob> childMonitoringJobs = new ArrayList<MonitoringJob>();

    public MonitoringJob(File monitoringObject, String monitoringTask, String job) {
        this.file = monitoringObject;
        this.task = monitoringTask;
        this.jobStr = job;
        if (!SafeFile.isDirectory(monitoringObject))
            this.fileInfo = new FileInfo(monitoringObject);
    }

    public File getObject() {
        return this.file;
    }

    public String getTask() {
        return this.task;
    }

    public String toString() {
        return this.jobStr;
    }

    public void updateChildJobs(File path) {
        childMonitoringJobs = new ArrayList<MonitoringJob>();
        searchChildFiles(path);

    }

    public void searchChildFiles(File path) {

        File[] list = SafeFile.listFiles(path);
        if (list != null && list.length != 0) {
            for (File f : list) {
                if (SafeFile.isDirectory(f)) {
                    searchChildFiles(f);
                }
                else {
                    MonitoringJob monitoringJob = new MonitoringJob(f, getTask(), this.toString());
                    childMonitoringJobs.add(monitoringJob);
//                    System.out.println(monitoringJob);
                    monitoringJob.updateSnapShot(f);
                }
            }
        }

    }
    
    

    public void updateSnapShot(File file) {
        snapShot = new ArrayList<FileInfo>();
        if(!SafeFile.exists(file)) {
            return;
        }
        File parentFile = SafeFile.getParentFile(file);
        File[] list = SafeFile.listFiles(parentFile);
        for (File f : list) {
            if (!SafeFile.isDirectory(f)) {
                FileInfo fInfo = new FileInfo(f);
                snapShot.add(fInfo); // 注意文件本身也被加入快照
            }
        }
    }
    
    public void updateAllSnapShot(File path) {
        snapShot = new ArrayList<FileInfo>();
        if(!SafeFile.exists(path)) {
            return;
        }
        searchChildFilesInfo(path);
    }
    
    
    public void searchChildFilesInfo(File path) {
        File[] list = SafeFile.listFiles(path);
        if (list != null && list.length != 0) {
            for (File f : list) {
                if (SafeFile.isDirectory(f)) {
                    searchChildFilesInfo(f);
                }
                else {
                    FileInfo fInfo = new FileInfo(f);
                    snapShot.add(fInfo); // 注意文件本身也被加入快照
                }
            }
        }
    }

}
