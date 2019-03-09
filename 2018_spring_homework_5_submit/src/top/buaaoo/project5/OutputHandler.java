package top.buaaoo.project5;

import java.io.PrintWriter;
import java.util.Date;

public class OutputHandler {

    private static PrintWriter outputFile = null;

    public static void setPrintWriter(PrintWriter printWriter) {
        outputFile = printWriter;
    }

    public synchronized static void printInvalidRequest(String str, double time) {
        long currentTime = new Date().getTime();
        outputFile.printf("%d:INVALID[%s,%.1f]\r\n", currentTime, str, time);
        outputFile.flush();
        System.out.printf("%d:INVALID[%s,%.1f]\n", currentTime, str, time);
    }

    public synchronized static void printSameRequest(Request req) {
        long currentTime = new Date().getTime();
        outputFile.printf("#%d:SAME[%s]\r\n", currentTime, req.toString());//////////////////
        outputFile.flush();
        System.out.printf("#%d:SAME[%s]\n", currentTime, req.toString());
    }
    
    public synchronized static void printExecutionResult(Request req, Elevator elev) {
        long currentTime = new Date().getTime();
        outputFile.printf("%d:[%s]/(%s)\r\n", currentTime, req.toString(), elev.toString());
        outputFile.flush();
        System.out.printf("%d:[%s]/(%s)\n", currentTime, req.toString(), elev.toString());
        
    }
}
