package top.buaaoo.project7;

import java.awt.Point;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHandler implements Runnable {

    private static final String REGEX = "\\[CR,\\((([0-9])|([1-7][0-9])),(([0-9])|([1-7][0-9]))\\),\\((([0-9])|([1-7][0-9])),(([0-9])|([1-7][0-9]))\\)\\]";
    private static Pattern pattern = Pattern.compile(REGEX);
    
    Vector<Request> requestQueue;
    
    public InputHandler(Vector<Request> requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {

        int x1, y1, x2, y2;
        long currentTime = 0;
        Request currentRequest;
        String inputStr;
        int count = 0;

        try {

            @SuppressWarnings("resource")
            Scanner scan = new Scanner(System.in);
            while (true) {
                inputStr = scan.nextLine();
                currentTime = Tools.getTime();
                currentTime = (long) Math.ceil(currentTime / 100.0) * 100;
                Matcher matcher = pattern.matcher(inputStr);
                if (!matcher.matches()) {
                    System.out.println("INVALID[" + inputStr + "]");
                    continue;
                }

                String[] strArray = inputStr.split("[\\(\\),]");
                x1 = Integer.parseInt(strArray[2]);
                y1 = Integer.parseInt(strArray[3]);
                x2 = Integer.parseInt(strArray[6]);
                y2 = Integer.parseInt(strArray[7]);

                if (x1 == x2 && y1 == y2) {
                    System.out.println("INVALID[" + inputStr + "]");
                    continue;
                }
                
                currentRequest = new Request(count,currentTime, new Point(x1, y1), new Point(x2, y2));
                boolean sameFlag = false;
                for (Request request : requestQueue) {
                    if (currentRequest.equals(request)) {
                        System.out.println("INVALID[" + inputStr + "]");
                        sameFlag = true;
                        break;
                    }
                }
                if(sameFlag) {
                    continue;
                }
                OrderDistribute orderDistribute = new OrderDistribute(currentRequest);
                Thread thread = new Thread(orderDistribute);
                thread.start();
                requestQueue.add(currentRequest);
                count++;
                
            }

        }
        catch (Throwable e) {}

    }

}
