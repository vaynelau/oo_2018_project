package top.buaaoo.project6;

import java.io.File;

public class ModifiedTrigger implements Runnable {

    MonitoringJob job;

    public ModifiedTrigger(MonitoringJob job) {
        this.job = job;
        initialize();
    }

    public void checkModified(MonitoringJob job) {
        File file = job.getObject();
        if (SafeFile.exists(file)) {
            File parentFile = SafeFile.getParentFile(file);
            File[] list = SafeFile.listFiles(parentFile);
            for (File f : list) {
                if (SafeFile.isDirectory(f)) {
                    continue;
                }
                FileInfo fInfo = new FileInfo(f);
                if (fInfo.equalsExceptLastModify(job.fileInfo)) {
                    System.out.println(
                            SafeFile.getAbsolutePath(job.file) + " is modified to " + SafeFile.getAbsolutePath(f));
                    if (job.task.equals("record-summary")) {
                        OutputHandler.writeSummary("modified");
                        job.file = f;
                        job.fileInfo = fInfo;
                    }
                    else if (job.task.equals("record-detail")) {
                        OutputHandler.writeDetail("modified",job.fileInfo, fInfo);
                        job.file = f;
                        job.fileInfo = fInfo;
                    }
                    break;
                }
            }

        }

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

    public void run() {

        try {

            while (true) {
                try {
                    Thread.sleep(1000);
                }
                catch (Exception e) {
                }

                File file = job.getObject();
                if (!SafeFile.isDirectory(file)) {
                    checkModified(job);
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
                        checkModified(childJob);
                        File childFile = childJob.getObject();
                        if (!SafeFile.exists(childFile)) {
                            System.out.println(childJob + ": the monitoring object "
                                    + SafeFile.getAbsolutePath(childFile) + " is lost.");
                            continue;
                        }
                        // 更新子监控的快照(不需要)
                    }
                    job.updateChildJobs(file);// 更新所有的子监控文件，同时更新快照
                }
            }

        }
        catch (Exception e) {
        }

    }

}
