package top.buaaoo.project6;

import java.io.PrintWriter;

public class OutputHandler {

    private static PrintWriter summary, detail;
    private static long n1 = 0, n2 = 0, n3 = 0, n4 = 0;

    public synchronized static boolean initialize() {

        try {
            summary = new PrintWriter("summary.txt", "utf-8");
            detail = new PrintWriter("detail.txt", "utf-8");
        }
        catch (Exception e) {
            return false;
        }

        return true;

    }

    public synchronized static void writeSummary(String str) {

        switch (str) {
        case "renamed":
            n1++;

            break;
        case "modified":
            n2++;

            break;
        case "path-changed":
            n3++;

            break;
        case "size-changed":
            n4++;

            break;
        default:
            break;
        }
        summary.println("renamed: " + n1);
        summary.flush();
        summary.println("modified: " + n2);
        summary.flush();
        summary.println("path-changed: " + n3);
        summary.flush();
        summary.println("path-changed: " + n4);
        summary.flush();
        summary.println("--------------------------------------------------------");
        summary.flush();
    }

    public synchronized static void writeDetail(String str,FileInfo oldInfo, FileInfo newInfo) {
        detail.println("Trigger: "+str);
        detail.flush();
        detail.println("name: "+oldInfo.name + " -> " + newInfo.name);
        detail.flush();
        detail.println("path: "+oldInfo.path + " -> " + newInfo.path);
        detail.flush();
        detail.println("size: "+oldInfo.size + " -> " + newInfo.size);
        detail.flush();
        detail.println("lastModify: "+oldInfo.lastModify + " -> " + newInfo.lastModify);
        detail.println("--------------------------------------------------------");
        detail.flush();

    }

}
