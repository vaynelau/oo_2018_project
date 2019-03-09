package top.buaaoo.project6;

import java.io.File;

public class RenamedTrigger implements Runnable {

    MonitoringJob job;

    public RenamedTrigger(MonitoringJob job) {
        this.job = job;
        initialize();
    }

    public boolean isNew(FileInfo fileInfo, MonitoringJob job) {
        int length = job.snapShot.size();
        for (int i = 0; i < length; i++) {
            if (job.snapShot.get(i).equals(fileInfo)) {
                return false;
            }
        }
        return true;
    }

    public void initialize() {
        File file = job.getObject();
        if (!SafeFile.isDirectory(file)) {
            job.updateSnapShot(file);
        }
        else {
            job.updateChildJobs(file);
        }

    }

    public void checkRenamed(MonitoringJob job) {
        File file = job.getObject();
        if (!SafeFile.exists(file)) {
            File parentFile = SafeFile.getParentFile(file);
            File[] list = SafeFile.listFiles(parentFile);
            for (File f : list) {
                if (SafeFile.isDirectory(f)) {
                    continue;
                }
                FileInfo fInfo = new FileInfo(f);
                if (fInfo.equalsExceptName(job.fileInfo) && isNew(fInfo, job)) {
                    System.out.println(
                            SafeFile.getAbsolutePath(job.file) + " is renamed to " + SafeFile.getAbsolutePath(f));// 进行重命名监控任务处理
                    if (job.task.equals("record-summary")) {
                        OutputHandler.writeSummary("renamed");
                        job.file = f;
                        job.fileInfo = fInfo;
                    }
                    else if (job.task.equals("record-detail")) {
                        OutputHandler.writeDetail("renamed",job.fileInfo, fInfo);
                        job.file = f;
                        job.fileInfo = fInfo;
                    }
                    else if (job.task.equals("recover")) {
                        SafeFile.rename(SafeFile.getAbsolutePath(f), job.fileInfo.name);
                    }

                    break;
                }
            }

        }

    }

    public void run() {

        try {

            while (true) {
                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {
                }

                File file = job.getObject();
                if (!SafeFile.isDirectory(file)) {
                    checkRenamed(job);
                    file = job.getObject();
                    if (!SafeFile.exists(file)) {
                        System.out.println(
                                job + ": the monitoring object " + SafeFile.getAbsolutePath(file) + " is lost.");
                        break;
                    }
                    job.updateSnapShot(file);
                    job.file = file;
                    job.fileInfo = new FileInfo(file);
                }
                else {
                    for (MonitoringJob childJob : job.childMonitoringJobs) {
                        checkRenamed(childJob);
                        File childFile = childJob.getObject();
                        if (!SafeFile.exists(childFile)) {
                            System.out.println(childJob + ": the monitoring object "
                                    + SafeFile.getAbsolutePath(childFile) + " is lost.");
                            continue;
                        }
                        // 更新子监控的快照(不需要?)
                    }
                    job.updateChildJobs(file);// 更新所有的子监控文件
                }
            }

        }
        catch (Exception e) {
        }

    }

}
