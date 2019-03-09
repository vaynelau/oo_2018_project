package top.buaaoo.project6;

import java.io.File;

public class FileInfo {
    File file;
    String name, path;
    long size, lastModify;

    public FileInfo(File file) {
        this.file = file;
        if (!SafeFile.isDirectory(file)) {
            name = SafeFile.getName(file);
            size = SafeFile.length(file);
            lastModify = SafeFile.lastModified(file);
            path = SafeFile.getAbsolutePath(SafeFile.getParentFile(file));

        }

    }

    public boolean equals(FileInfo fInfo) {
        // System.out.println(name);
        // System.out.println(path);
        // System.out.println(fInfo.name);
        // System.out.println(fInfo.path);
        if (name.equals(fInfo.name) && path.equals(fInfo.path) && size == fInfo.size
                && lastModify == fInfo.lastModify) {
            return true;
        }
        return false;

    }

    public boolean equalsExceptName(FileInfo fInfo) {
        if (!name.equals(fInfo.name) && path.equals(fInfo.path) && size == fInfo.size
                && lastModify == fInfo.lastModify) {
            return true;
        }
        return false;
    }

    public boolean equalsExceptPath(FileInfo fInfo) {
        if (name.equals(fInfo.name) && !path.equals(fInfo.path) && size == fInfo.size
                && lastModify == fInfo.lastModify) {
            return true;
        }
        return false;
    }

    public boolean equalsExceptLastModify(FileInfo fInfo) {
        if (name.equals(fInfo.name) && path.equals(fInfo.path) && lastModify != fInfo.lastModify) {
            return true;
        }
        return false;
    }

    public boolean equalsExceptSizeAndLastModify(FileInfo fInfo) {
        if (name.equals(fInfo.name) && path.equals(fInfo.path) && size != fInfo.size
                && lastModify != fInfo.lastModify) {
            return true;
        }
        return false;
    }
}
