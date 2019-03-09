package top.buaaoo.project6;

class TestThread implements Runnable {

    public void testcase1() {
        /*
         * 请在此处输入测试内容
         * 
         */
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
        }
    }

    public void testcase2() {
        /*
         * 请在此处输入测试内容
         * 
         */
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
        }
    }

    public void testcase3() {
        /*
         * 请在此处输入测试内容
         * 
         */
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
        }
    }

    public void run() {

        try {
            Thread.sleep(1500);
        }
        catch (Exception e) {
        }

        testcase1();
        testcase2();
        testcase3();
        /*
         * 可以新建其他的testcase，但必须符合上述格式，保留sleep语句
         */
    }
}
