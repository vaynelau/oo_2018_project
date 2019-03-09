package top.buaaoo.project7;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {// 常用工具
    public static int MAXNUM = 1000000;

    public static long getTime() {// 获得当前系统时间
        // Requires:无
        // Modifies:无
        // Effects:返回long类型的以毫秒计的系统时间
        return System.currentTimeMillis();
    }

    @SuppressWarnings("static-access")
    public static void stay(long time) {
        // Requires:long类型的以毫秒计的休眠时间
        // Modifies:无
        // Effects:使当前线程休眠time的时间
        try {
            Thread.currentThread().sleep(time);
        }
        catch (InterruptedException e) {}
    }

    public static void printTime() {
        // Requires:无
        // Modifies:System.out
        // Effects:在屏幕上打印HH:mm:ss:SSS格式的当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        System.out.println(sdf.format(new Date(getTime())));
    }

    public static String getFormatTime() {
        // Requires:无
        // Modifies:无
        // Effects:返回String类型的HH:mm:ss格式的时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(getTime()));
    }
}
