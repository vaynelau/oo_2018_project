package top.buaaoo.project6;

import java.io.File;
import java.io.PrintWriter;

public class SafeFile {


    public static synchronized String getName(File file) {
        return file.getName();
    }

    public static synchronized long length(File file) {
        try {
            return file.length();
        }
        catch (Exception e) {
            return 0L;
        }

    }

    public static synchronized long lastModified(File file) { // 得到最后一次修改的时间
        try {
            return file.lastModified();
        }
        catch (Exception e) {
            return 0L;
        }
    }

    public static synchronized boolean rename(String pathName, String newName) { //注意pathName包括路径和文件名，newName不包括路径
        try {
            File file = new File(pathName);
            if (!SafeFile.exists(file)) {
                System.out.println("file renamed failed, the file does not exist.");
                return false;
            }
            if (SafeFile.isDirectory(file)) {
                System.out.println("file renamed failed, the input path is a directory.");
                return false;
            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("file renamed failed, the input path is not an absolute path.");
                return false;
            }
            if (!newName.matches("[^\\\\/\\*\\|\"\\?:<>\\s]+")) {
                System.out.println("file renamed failed, the input newName is illegal.");
                return false;
            }

            File dest = new File(SafeFile.getParentFile(file) + File.separator + newName);
            return file.renameTo(dest);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean move(String oldpath, String newpath) {//oldpath，newpath包括文件名
        try {
            File file = new File(oldpath);
            if (!SafeFile.exists(file)) {
                System.out.println("file move failed, the file does not exist.");
                return false;
            }
            if (SafeFile.isDirectory(file)) {
                System.out.println("file move failed, the old path is a directory.");
                return false;
            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("file move failed, the old path is not an absolute path.");
                return false;
            }

            File dest = new File(newpath);

            if (SafeFile.exists(dest)) {
                System.out.println("file move failed, the new file exists.");
                return false;
            }
            if (SafeFile.isDirectory(dest)) {
                System.out.println("file move failed, the new path is a directory.");
                return false;
            }
            if (!SafeFile.isAbsolute(dest)) {
                System.out.println("file move failed, the new path is not an absolute path.");
                return false;
            }
            if (!SafeFile.getName(dest).matches("[^\\\\/\\*\\|\"\\?:<>\\s]+")) {
                System.out.println("file move failed, the new file name is illegal.");
                return false;
            }

            return file.renameTo(dest);
        }
        catch (Exception e) {
            return false;
        }
    }

    /* 当且仅当不存在指定名称的文件时，不可分地创建一个新的空文件。 */
    public static synchronized boolean createNewFile(String pathName) {
        try {
            File file = new File(pathName);
            if (SafeFile.exists(file)) {
                System.out.println("create new file failed, the file exists.");
                return false;
            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("create new file failed, the input path is not an absolute path.");
                return false;
            }
            
            return file.createNewFile();
        }
        catch (Exception e) {
            return false;
        }
    }

    /* 创建此路径名指定的目录 */
    public static synchronized boolean mkdir(String pathName) {
        try {
            File file = new File(pathName);
            
            if (SafeFile.exists(file)) {
                System.out.println("mkdir failed, the file exists.");
                return false;
            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("mkdir failed, the input path is not an absolute path.");
                return false;
            }
            
            return file.mkdir();
        }
        catch (Exception e) {
            return false;
        }
    }

    /* 创建此路径名指定的目录，包括所有必需但不存在的父目录。 */
    public static synchronized boolean mkdirs(String pathName) {
        try {
            File file = new File(pathName);
            
            if (SafeFile.exists(file)) {
                System.out.println("mkdir failed, the file exists.");
                return false;
            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("mkdir failed, the input path is not an absolute path.");
                return false;
            }
            
            return file.mkdirs();
        }
        catch (Exception e) {
            return false;
        }
    }

    /* 删除此抽象路径名表示的文件或目录。如果此路径名表示一个目录，则该目录必须为空才能删除。 */
    public static synchronized boolean delete(String pathName) {//
        try {
            File file = new File(pathName);
            if (!SafeFile.exists(file)) {
                System.out.println("delete failed, the file does not exist.");
                return false;
            }
//            if (SafeFile.isDirectory(file)) {
//                System.out.println("delete failed, the input path is a directory.");
//                return false;
//            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("delete failed, the input path is not an absolute path.");
                return false;
            }
            return file.delete();
        }
        catch (Exception e) {
            return false;
        }
    }

    public static synchronized boolean setLastModified(String pathName, long time) {

        try {
            File file = new File(pathName);
            if (!SafeFile.exists(file)) {
                System.out.println("set lastModified failed, the file does not exist.");
                return false;
            }
//            if (SafeFile.isDirectory(file)) {
//                System.out.println("set lastModified failed, the path is a directory.");
//                return false;
//            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("set lastModified failed, the path is not an absolute path.");
                return false;
            }
            return file.setLastModified(time);
        }
        catch (Exception e) {
            return false;
        }

    }

    public static synchronized boolean write(String pathName, String str) {

        try {
            File file = new File(pathName);
            if (!SafeFile.exists(file)) {
                System.out.println("file write failed, the file does not exist.");
                return false;
            }
            if (SafeFile.isDirectory(file)) {
                System.out.println("file write failed, the path is a directory.");
                return false;
            }
            if (!SafeFile.isAbsolute(file)) {
                System.out.println("file write failed, the path is not an absolute path.");
                return false;
            }
            PrintWriter printWriter = new PrintWriter(file, "utf-8");
            printWriter.println(str);
            printWriter.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }

    public static synchronized String getAbsolutePath(File file) {
        try {
            return file.getAbsolutePath();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static synchronized boolean isAbsolute(File file) {
        return file.isAbsolute();
    }

    public static synchronized boolean exists(File file) { // 测试此抽象路径名表示的文件或目录是否存在。
        try {
            return file.exists();
        }
        catch (Exception e) {
            return false;
        }
    }

    public static synchronized boolean isDirectory(File file) { // 测试此抽象路径名表示的文件是否是一个目录。
        try {
            return file.isDirectory();
        }
        catch (Exception e) {
            return false;
        }
    }

    public static synchronized File[] listFiles(File path) {
        try {
            return path.listFiles();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static synchronized File getParentFile(File file) {
        try {
            return file.getParentFile();
        }
        catch (Exception e) {
            return null;
        }
    }

}
