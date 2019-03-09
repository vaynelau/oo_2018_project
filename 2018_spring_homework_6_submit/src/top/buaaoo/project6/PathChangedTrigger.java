package top.buaaoo.project6;

import java.io.File;

public class PathChangedTrigger implements Runnable {

    MonitoringJob job;
    FileInfo childInfo;

    public PathChangedTrigger(MonitoringJob job) {
        this.job = job;
        initialize();
    }

    public void initialize() {
        File file = job.getObject();
        if (!SafeFile.isDirectory(file)) {
            File parentFile = SafeFile.getParentFile(file);
            job.updateAllSnapShot(parentFile);
        }
        else {
            job.updateAllSnapShot(file);
        }
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

    public void search(File path) {
        File[] list = SafeFile.listFiles(path);
        if (list != null && list.length != 0) {
            for (File f : list) {
                if (SafeFile.isDirectory(f)) {
                    search(f);
                }
                else {
                    FileInfo fInfo = new FileInfo(f);
                    if (fInfo.equalsExceptPath(job.fileInfo) && isNew(fInfo, job)) {
                        System.out.println(SafeFile.getAbsolutePath(job.file) + " is path-changed to "
                                + SafeFile.getAbsolutePath(f));
                        if (job.task.equals("record-summary")) {
                            OutputHandler.writeSummary("path-changed");
                            job.file = f;
                            job.fileInfo = fInfo;
                        }
                        else if (job.task.equals("record-detail")) {
                            OutputHandler.writeDetail("path-changed",job.fileInfo, fInfo);
                            job.file = f;
                            job.fileInfo = fInfo;
                        }
                        else if (job.task.equals("recover")) {
                            SafeFile.move(SafeFile.getAbsolutePath(f), SafeFile.getAbsolutePath(job.file));
                        }
                        break;
                    }
                }
            }
        }

    }

    public void search(File path, FileInfo fileInfo) {
        File[] list = SafeFile.listFiles(path);
        if (list != null && list.length != 0) {
            for (File f : list) {
                if (SafeFile.isDirectory(f)) {
                    search(f, fileInfo);
                }
                else {
                    FileInfo fInfo = new FileInfo(f);
                    if (fInfo.equalsExceptPath(fileInfo) && isNew(fInfo, job)) {
                        System.out.println(SafeFile.getAbsolutePath(fileInfo.file) + " is path-changed to "
                                + SafeFile.getAbsolutePath(f));// 进行重命名监控任务处理
                        if (job.task.equals("record-summary")) {
                            OutputHandler.writeSummary("path-changed");
                            childInfo = fInfo;
                        }
                        else if (job.task.equals("record-detail")) {
                            OutputHandler.writeDetail("path-changed",fileInfo, fInfo);
                            childInfo = fInfo;
                        }
                        else if (job.task.equals("recover")) {
                            SafeFile.move(SafeFile.getAbsolutePath(f), SafeFile.getAbsolutePath(fileInfo.file));
                        }
                        break;
                    }
                }
            }
        }

    }

    public void checkPathChanged(MonitoringJob job) {
        File file = job.getObject();
        if (!SafeFile.exists(file)) {
            File parentFile = SafeFile.getParentFile(file);
            File[] list = SafeFile.listFiles(parentFile);
            for (File f : list) {
                if (SafeFile.isDirectory(f)) {
                    search(f);
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
                    checkPathChanged(job);
                    file = job.getObject();
                    if (!SafeFile.exists(file)) {
                        System.out.println(
                                job + ": the monitoring object " + SafeFile.getAbsolutePath(file) + " is lost.");
                        break;
                    }
                    File parentFile = SafeFile.getParentFile(file);
                    job.updateAllSnapShot(parentFile);
                    job.file = file;
                    job.fileInfo = new FileInfo(file);
                }
                else {
                    for (FileInfo childFileInfo : job.snapShot) {
                        // int c=0;
                        // System.out.println(c++);
                        File childFile = childFileInfo.file;
                        childInfo = childFileInfo;
                        if (!SafeFile.exists(childFile)) {
                            search(file, childFileInfo);
                        }
                        childFile = childInfo.file;
                        if (!SafeFile.exists(childFile)) {
                            System.out.println(job + ": the monitoring object " + SafeFile.getAbsolutePath(childFile)
                                    + " is lost.");
                            continue;
                        }
                        // 更新子监控的快照(不需要?)
                    }
                    job.updateAllSnapShot(file);// 更新所有的子监控文件
                }
            }

        }
        catch (Exception e) {
        }

    }

}
