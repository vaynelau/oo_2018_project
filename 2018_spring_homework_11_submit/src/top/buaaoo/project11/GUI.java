package top.buaaoo.project11;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

class gv {// 常用工具

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
        catch (Throwable e) {}
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

class Node {// 结点信息
    int NO;
    int depth;
    Vector<Point> shortestPath;

    public Node(int _NO, int _depth) {
        // Requires:int类型的结点号,int类型的深度信息
        // Modifies:创建一个新的node对象，修改了此对象的NO,depth属性
        // Effects:创建了一个新的node对象并初始化
        NO = _NO;
        depth = _depth;
    }

}

class GUIInfo implements Constant {
    public int[][] map;
    public static final int[][] graph = new int[MATRIX_SIZE][MATRIX_SIZE];// 邻接矩阵
    public static final int[][] initGraph = new int[MATRIX_SIZE][MATRIX_SIZE];// 邻接矩阵
    private static final int[] offset = new int[] { 0, 1, -1, 80, -80 };

    /**
     * @REQUIRES: \this != null;(\all int i,j;0<=i<MAP_SIZE&&0<=j<MAP_SIZE;0<=map[i][j]<=3);
     * @MODIFIES: \this.graph;\this.initGraph;
     * @EFFECTS: (\all int i,j;0<=i=j<6400;graph[i][j]==0);
     *           (\all int i,j;0<=i<6400 && 0<=j<6400 && i!=j;graph[i][j]==graph[i][j] && (graph[i][j]==1 ||
     *           graph[i][j]==gv.MAXNUM));
     *           (\all int i,j;0<=i<6400 && 0<=j<6400;graph[i][j]==initGraph[i][j]);
     */
    public void initMatrix() {
        int MAXNUM = gv.MAXNUM;
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (i == j) {
                    graph[i][j] = 0;
                    initGraph[i][j] = 0;
                }
                else {
                    graph[i][j] = MAXNUM;
                    initGraph[i][j] = MAXNUM;
                }
            }
        }
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (map[i][j] == 1 || map[i][j] == 3) {
                    graph[i * 80 + j][i * 80 + j + 1] = 1;
                    graph[i * 80 + j + 1][i * 80 + j] = 1;
                    initGraph[i * 80 + j][i * 80 + j + 1] = 1;
                    initGraph[i * 80 + j + 1][i * 80 + j] = 1;
                }
                if (map[i][j] == 2 || map[i][j] == 3) {
                    graph[i * 80 + j][(i + 1) * 80 + j] = 1;
                    graph[(i + 1) * 80 + j][i * 80 + j] = 1;
                    initGraph[i * 80 + j][(i + 1) * 80 + j] = 1;
                    initGraph[(i + 1) * 80 + j][i * 80 + j] = 1;
                }
            }
        }
    }

    /**
     * 对整个地图进行广度优先搜索，获得src到dst之间的最短路径同时总流量最小，储存在minFlowShortestPath中
     *           若地图不连通则程序输出相关信息并退出
     * @REQUIRES: 0<=src<6400;0<=dst<6400;0<=taxiNum<100;
     * @MODIFIES: System.out;
     * @EFFECTS: 地图联通 ==> \result == minFlowShortestPath;
     *           地图不连通==> System.out输出提示信息，程序结束;
     *           
     * @THREAD_EFFECTS: \locked(\this.graph);\locked(\this.initGraph);
     */
    public static Vector<Point> SPFA(int taxiNum, int src, int dst) {
        if (src == dst) {
            return null;
        }

        int[][] mapGraph;
        boolean[] inQueue = new boolean[6400];// inQueue[i]表示点i是否在队列中
        int[] distance = new int[6400];// 距离数组,表示点i到起点的距离
        long[] flow = new long[6400];// 流量数组,表示点i到起点的流量之和
        int[] nextPoint = new int[6400];// nextPoint[i]表示点i的下一个点
        Queue<Integer> queue = new LinkedList<>();

        if(taxiNum < 30) {
            mapGraph = initGraph;
        }else {
            mapGraph = graph;
        }
            
        for (int i = 0; i < 6400; i++) {
            distance[i] = Integer.MAX_VALUE;
            flow[i] = Long.MAX_VALUE;
        }

        distance[dst] = 0;
        flow[dst] = 0;
        queue.offer(dst);
        inQueue[dst] = true;

        synchronized (mapGraph) {
            int now, distTemp;
            long flowTemp;
            while (!queue.isEmpty()) {
                now = queue.poll();
                if (now == src) {
                    break;
                }
                for (int i = 1; i <= 4; i++) {
                    int next = now + offset[i];
                    if (next < 0 || next >= 6400 || mapGraph[now][next] != 1) {
                        continue;
                    }
                    distTemp = distance[now] + 1;
                    flowTemp = flow[now] + (long) MapInfo.getFlow(now / 80, now % 80, next / 80, next % 80);
                    if (distance[next] > distTemp || (distance[next] == distTemp && flow[next] > flowTemp)) {
                        nextPoint[next] = now;
                        distance[next] = distTemp;
                        flow[next] = flowTemp;
                        if (!inQueue[next]) {
                            queue.offer(next);
                            inQueue[next] = true;
                        }
                    }
                }
                inQueue[now] = false;
            }
        }

        if (distance[src] == Integer.MAX_VALUE) {
            System.out.println("地图并不是连通的,程序退出");
            System.exit(0);
        }
        int begin = src;
        Vector<Point> minFlowShortestPath = new Vector<Point>();
        while (begin != dst) {
            minFlowShortestPath.add(new Point(nextPoint[begin] / 80, nextPoint[begin] % 80));
            begin = nextPoint[begin];
        }

        return minFlowShortestPath;
    }

    /**
     * @REQUIRES: srcPoint!= null && 0<=srcPoint.x<80 &&
     *            0<=srcPoint.y<80;dstPoint!=null && 0<=dstPoint.x<80 &&
     *            0<=dstPoint.y<80;0<=taxiNum<100;
     * @MODIFIES: None;
     * @EFFECTS: \result == minFlowShortestPath;
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized static Vector<Point> getShortestPath(int taxiNum, Point srcPoint, Point dstPoint) {

        if (srcPoint.equals(dstPoint)) {
            return null;
        }
        int src = srcPoint.x * 80 + srcPoint.y;
        int dst = dstPoint.x * 80 + dstPoint.y;
        Vector<Point> minFlowShortestPath = SPFA(taxiNum, src, dst);
        return minFlowShortestPath;

    }

    /**
     * 对整个地图进行广度优先搜索，获得src到dst之间的最短路径长度，储存在n.depth中 若地图不连通则程序输出相关信息并退出
     * @REQUIRES: srcPoint!= null && 0<=srcPoint.x<80 &&
     *            0<=srcPoint.y<80;dstPoint!=null && 0<=dstPoint.x<80 &&
     *            0<=dstPoint.y<80;
     * @MODIFIES: System.out;
     * @EFFECTS: 地图联通 ==>\result == n.depth + 1;
     *           地图不连通==> System.out输出提示信息，程序结束;
     * @THREAD_EFFECTS: \locked();\locked(\this.graph);
     */
    public static synchronized int getDistance(int taxiNum, Point srcPoint, Point dstPoint) {
        if (srcPoint.equals(dstPoint)) {
            return 0;
        }

        Vector<Node> queue = new Vector<Node>();
        int src = srcPoint.x * 80 + srcPoint.y;
        int dst = dstPoint.x * 80 + dstPoint.y;
        int mapGraph[][];
        
        if(taxiNum < 30) {
            mapGraph = initGraph;
        }else {
            mapGraph = graph;
        }
        
        
        boolean[] view = new boolean[6405];
        for (int i = 0; i < 6400; i++) {
            view[i] = false;
        }
        
        view[src] = true;
        queue.add(new Node(src, 0));
        synchronized (mapGraph) {
            while (queue.size() > 0) {
                Node n = queue.get(0);
                for (int i = 1; i <= 4; i++) {
                    int next = n.NO + offset[i];
                    if (next >= 0 && next < 6400 && view[next] == false && mapGraph[n.NO][next] == 1) {
                        if (next == dst) {
                            return n.depth + 1;
                        }
                        view[next] = true;
                        queue.add(new Node(next, n.depth + 1));// 加入遍历队列
                    }
                }
                queue.remove(0);// 退出队列
            }

        }

        System.out.println("地图并不是连通的,程序退出");
        System.exit(0);
        return 0;
    }

}

class GUITaxi {
    public int x = 1;
    public int y = 1;
    public int status = -1;
    public int type = 0;// 0是普通出租车，1是特殊车
}

class GUIGV implements Constant {
    public static GUIInfo guiInfo = new GUIInfo();// 地图备份
    public static CopyOnWriteArrayList<GUITaxi> taxilist = new CopyOnWriteArrayList<GUITaxi>();// 出租车列表
    public static CopyOnWriteArrayList<Point> srclist = new CopyOnWriteArrayList<Point>();// 出发点列表
    public static HashMap<String, Integer> flowmap = new HashMap<String, Integer>();// 当前流量
    public static HashMap<String, Integer> memflowmap = new HashMap<String, Integer>();// 之前统计的流量
    /* GUI */
    public static JPanel drawboard;
    public static int[][] colormap;
    static final int[][] lightmap = new int[85][85];// 初始化红绿灯
    public static boolean redraw = false;
    public static int xoffset = 0;
    public static int yoffset = 0;
    public static int oldxoffset = 0;
    public static int oldyoffset = 0;
    public static int mousex = 0;
    public static int mousey = 0;
    public static double percent = 0.55;
    public static boolean drawstr = false;
    public static boolean drawflow = false;// 是否绘制流量信息

    private static String key(int x1, int y1, int x2, int y2) {// 生成唯一的Key
        return "" + x1 + "," + y1 + "," + x2 + "," + y2;
    }

    public static void addFlow(int x1, int y1, int x2, int y2) {// 增加一个道路流量
        synchronized (GUIGV.flowmap) {
            // 查询之前的流量数量
            int count = 0;
            count = GUIGV.flowmap.get(key(x1, y1, x2, y2)) == null ? 0 : GUIGV.flowmap.get(key(x1, y1, x2, y2));
            // 添加流量
            GUIGV.flowmap.put(key(x1, y1, x2, y2), count + 1);
            GUIGV.flowmap.put(key(x2, y2, x1, y1), count + 1);
        }
    }

    public static int getFlow(int x1, int y1, int x2, int y2) {// 查询流量信息
        synchronized (GUIGV.memflowmap) {
            return GUIGV.memflowmap.get(key(x1, y1, x2, y2)) == null ? 0 : GUIGV.memflowmap.get(key(x1, y1, x2, y2));
        }
    }

    @SuppressWarnings("unchecked")
    public static void clearFlow() {// 清空流量信息
        synchronized (GUIGV.flowmap) {
            synchronized (GUIGV.memflowmap) {
                GUIGV.memflowmap = (HashMap<String, Integer>) GUIGV.flowmap.clone();
                GUIGV.flowmap = new HashMap<String, Integer>();
            }
        }
    }
}

class DrawBoard extends JPanel {// 绘图板类

    private static final long serialVersionUID = 1L;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        brush.draw(g2D);
    }
}

class MyForm extends JFrame {// 我的窗体程序

    private static final long serialVersionUID = 1L;
    private int left = 820;
    private int top = 20;
    private int width = 1010;
    private int height = 1010;

    public MyForm() {
        super();
        /* 设置按钮属性 */
        // button1
        JButton button1 = new JButton();// 创建一个按钮
        button1.setBounds(10, 915, 100, 40);// 设置按钮的位置
        button1.setText("重置");// 设置按钮的标题
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIGV.xoffset = 0;
                GUIGV.yoffset = 0;
                GUIGV.oldxoffset = 0;
                GUIGV.oldyoffset = 0;
                GUIGV.percent = 1.0;
                GUIGV.drawboard.repaint();
            }
        });
        // button2
        JButton button2 = new JButton();// 创建一个按钮
        button2.setBounds(120, 915, 100, 40);// 设置按钮的位置
        button2.setText("放大");// 设置按钮的标题
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIGV.percent += 0.1;
                GUIGV.drawboard.repaint();
            }
        });
        // button3
        JButton button3 = new JButton();// 创建一个按钮
        button3.setBounds(230, 915, 100, 40);// 设置按钮的位置
        button3.setText("缩小");// 设置按钮的标题
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (GUIGV.percent > 0.1)
                    GUIGV.percent -= 0.1;
                GUIGV.drawboard.repaint();
            }
        });
        // button4
        JButton button4 = new JButton();
        button4.setBounds(340, 915, 100, 40);
        button4.setText("清除轨迹");
        button4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 清除colormap
                for (int i = 0; i < 85; i++) {
                    for (int j = 0; j < 85; j++) {
                        GUIGV.colormap[i][j] = 0;
                    }
                }
                GUIGV.drawboard.repaint();
            }
        });
        /* 设置复选框属性 */
        JCheckBox check1 = new JCheckBox("显示位置");
        check1.setBounds(450, 915, 80, 40);
        check1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIGV.drawstr = check1.isSelected();
                GUIGV.drawboard.repaint();
            }
        });
        JCheckBox check2 = new JCheckBox("显示流量");
        check2.setBounds(530, 915, 80, 40);
        check2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIGV.drawflow = check2.isSelected();
                GUIGV.drawboard.repaint();
            }
        });
        /* 设置绘图区属性 */
        DrawBoard drawboard = new DrawBoard();// 创建新的绘图板
        drawboard.setBounds(10, 10, 900, 900);// 设置大小
        drawboard.setBorder(BorderFactory.createLineBorder(Color.black, 1));// 设置边框
        drawboard.setOpaque(true);
        drawboard.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {// 按下鼠标
                GUIGV.redraw = true;
                GUIGV.mousex = e.getX();
                GUIGV.mousey = e.getY();
            }

            public void mouseReleased(MouseEvent e) {// 松开鼠标
                GUIGV.oldxoffset = GUIGV.xoffset;
                GUIGV.oldyoffset = GUIGV.yoffset;
                GUIGV.redraw = false;
            }

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        drawboard.addMouseMotionListener(new MouseMotionAdapter() {// 添加鼠标拖动
            public void mouseDragged(MouseEvent e) {
                if (GUIGV.redraw == true) {
                    GUIGV.xoffset = GUIGV.oldxoffset + e.getX() - GUIGV.mousex;
                    GUIGV.yoffset = GUIGV.oldyoffset + e.getY() - GUIGV.mousey;
                    GUIGV.drawboard.repaint();
                }
            }
        });
        drawboard.addMouseWheelListener(new MouseWheelListener() {// 添加鼠标滚轮
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() == 1) {// 滑轮向前
                    if (GUIGV.percent > 0.1)
                        GUIGV.percent -= 0.1;
                    GUIGV.drawboard.repaint();
                } else if (e.getWheelRotation() == -1) {// 滑轮向后
                    GUIGV.percent += 0.1;
                    GUIGV.drawboard.repaint();
                }
            }
        });
        GUIGV.drawboard = drawboard;// 获得一份drawboard的引用

        /* 设置窗体属性 */
        setTitle("实时查看");// 设置窗体标题
        setLayout(null);// 使用绝对布局
        setBounds(left, top, width, height);// 设置窗体位置大小

        /* 添加控件，显示窗体 */
        Container c = getContentPane();// 获得一个容器
        c.add(button1);// 添加button1
        c.add(button2);
        c.add(button3);
        c.add(button4);
        c.add(check1);
        c.add(check2);
        c.add(drawboard);
        setVisible(true);// 使窗体可见
        setAlwaysOnTop(true);// 设置窗体置顶
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置默认关闭方式
    }
}

class brush {// 画笔
    public static int[][] colormap = new int[85][85];

    public static void draw(Graphics2D graphics2D) {
        boolean drawcolor = true;
        int factor = (int) (35 * GUIGV.percent);
        int width = (int) (20 * GUIGV.percent);
        int xoffset = -5;
        int yoffset = 3;
        // 检索一遍出租车位置信息，将有出租车的位置标上1
        int[][] taximap = new int[85][85];
        // 获得colormap的引用
        GUIGV.colormap = colormap;
        // 设置出租车位置
        for (int i = 0; i < 80; i++)
            for (int j = 0; j < 80; j++) {
                taximap[i][j] = -1;
            }
        for (int i = 0; i < GUIGV.taxilist.size(); i++) {
            GUITaxi gt = GUIGV.taxilist.get(i);
            if (gt.status > -1) {
                // System.out.println("####"+gt.x+" "+gt.y);
                // 加入对type的判断,如果type=1，则taximap=4
                if (gt.type == 1) {
                    taximap[gt.x][gt.y] = 4;
                } else {
                    taximap[gt.x][gt.y] = gt.status;
                }
                if (gt.status == 1) {
                    colormap[gt.x][gt.y] = 1;// 路线染色
                }
            }
        }

        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 80; j++) {
                if (j < 10) {
                    xoffset = -5;
                    yoffset = 3;
                } else {
                    xoffset = -7;
                    yoffset = 3;
                }
                graphics2D.setStroke(new BasicStroke(2));
                graphics2D.setFont(new Font("Arial", Font.PLAIN, (int) (10 * GUIGV.percent)));
                if (GUIGV.guiInfo.map[i][j] == 2 || GUIGV.guiInfo.map[i][j] == 3) {
                    if (drawcolor && colormap[i][j] == 1 && colormap[i + 1][j] == 1)
                        graphics2D.setColor(Color.RED);
                    else
                        graphics2D.setColor(Color.BLACK);
                    int memj = (int) ((j * factor + GUIGV.xoffset) * GUIGV.percent);
                    graphics2D.drawLine(memj, (int) ((i * factor + GUIGV.yoffset) * GUIGV.percent), memj,
                            (int) (((i + 1) * factor + GUIGV.yoffset) * GUIGV.percent));
                    // 绘制道路流量
                    if (GUIGV.drawflow) {
                        graphics2D.setColor(Color.BLUE);
                        graphics2D.drawString("" + MapInfo.getFlow(i, j, i + 1, j), memj,
                                (int) (((i + 0.5) * factor + GUIGV.yoffset) * GUIGV.percent));
                    }
                }
                if (GUIGV.guiInfo.map[i][j] == 1 || GUIGV.guiInfo.map[i][j] == 3) {
                    if (drawcolor && colormap[i][j] == 1 && colormap[i][j + 1] == 1)
                        graphics2D.setColor(Color.RED);
                    else
                        graphics2D.setColor(Color.BLACK);
                    int memi = (int) ((i * factor + GUIGV.yoffset) * GUIGV.percent);
                    graphics2D.drawLine((int) ((j * factor + GUIGV.xoffset) * GUIGV.percent), memi,
                            (int) (((j + 1) * factor + GUIGV.xoffset) * GUIGV.percent), memi);
                    // 绘制道路流量
                    if (GUIGV.drawflow) {
                        graphics2D.setColor(Color.BLUE);
                        graphics2D.drawString("" + MapInfo.getFlow(i, j, i, j + 1),
                                (int) (((j + 0.5) * factor + GUIGV.xoffset) * GUIGV.percent), memi);
                    }
                }
                int targetWidth;
                // 加入对type的判断
                if (taximap[i][j] == 4) {
                    graphics2D.setColor(Color.MAGENTA);
                    targetWidth = 2;
                } else if (taximap[i][j] == 3) {
                    graphics2D.setColor(Color.GREEN);
                    targetWidth = 2;
                } else if (taximap[i][j] == 2) {
                    graphics2D.setColor(Color.RED);
                    targetWidth = 2;
                } else if (taximap[i][j] == 1) {
                    graphics2D.setColor(Color.BLUE);
                    targetWidth = 2;
                } else if (taximap[i][j] == 0) {
                    graphics2D.setColor(Color.YELLOW);
                    targetWidth = 2;
                } else {
                    graphics2D.setColor(Color.BLACK);
                    targetWidth = 1;
                }
                int cleft = (int) ((j * factor - width / 2 + GUIGV.xoffset) * GUIGV.percent);
                int ctop = (int) ((i * factor - width / 2 + GUIGV.yoffset) * GUIGV.percent);
                int cwidth = (int) (width * GUIGV.percent) * targetWidth;
                if (targetWidth > 1) {
                    cleft = cleft - (int) (cwidth / 4);
                    ctop = ctop - (int) (cwidth / 4);
                }
                // g.fillOval((int)((j*factor-width/2+guigv.xoffset)*guigv.percent),(int)((i*factor-width/2+guigv.yoffset)*guigv.percent),(int)(width*guigv.percent)*targetWidth,(int)(width*guigv.percent)*targetWidth);
                graphics2D.fillOval(cleft, ctop, cwidth, cwidth);// 绘制点

                // 绘制红绿灯
                if (GUIGV.lightmap[i][j] == 1) {// 东西方向为绿灯
                    graphics2D.setColor(Color.GREEN);
                    graphics2D.fillRect(cleft - cwidth / 4, ctop + cwidth / 4, cwidth / 2, cwidth / 8);
                    graphics2D.setColor(Color.RED);
                    graphics2D.fillRect(cleft + cwidth / 8, ctop - cwidth / 4, cwidth / 8, cwidth / 2);
                } else if (GUIGV.lightmap[i][j] == 2) {// 东西方向为红灯
                    graphics2D.setColor(Color.RED);
                    graphics2D.fillRect(cleft - cwidth / 4, ctop + cwidth / 4, cwidth / 2, cwidth / 8);
                    graphics2D.setColor(Color.GREEN);
                    graphics2D.fillRect(cleft + cwidth / 8, ctop - cwidth / 4, cwidth / 8, cwidth / 2);
                }

                // 标记srclist中的点
                for (Point p : GUIGV.srclist) {
                    graphics2D.setColor(Color.RED);
                    int x = p.x;
                    int y = p.y;
                    graphics2D.drawLine((int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                    graphics2D.drawLine((int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                    graphics2D.drawLine((int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                    graphics2D.drawLine((int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                }
                if (GUIGV.drawstr == true) {
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setFont(new Font("Arial", Font.PLAIN, (int) (8 * GUIGV.percent)));
                    graphics2D.drawString("" + i + "," + j,
                            (int) ((j * factor + xoffset + GUIGV.xoffset) * GUIGV.percent),
                            (int) ((i * factor + yoffset + GUIGV.yoffset) * GUIGV.percent));
                }
            }
        }
    }
}

class processform extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JProgressBar progressBar = new JProgressBar();
    JLabel label1 = new JLabel("BFS进度", SwingConstants.CENTER);

    public processform() {
        super();
        // 将进度条设置在窗体最北面
        getContentPane().add(progressBar, BorderLayout.NORTH);
        getContentPane().add(label1, BorderLayout.CENTER);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        // 设置窗体各种属性方法
        setBounds(100, 100, 100, 100);
        setAlwaysOnTop(true);// 设置窗体置顶
        setVisible(true);
    }
}

class debugform extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTextArea text1 = new JTextArea();

    public debugform() {
        super();
        getContentPane().add(text1);
        setBounds(0, 100, 500, 100);
        setAlwaysOnTop(true);
        setVisible(true);
    }
}

class TaxiGUI {// GUI接口类

    static int[][] graph = GUIInfo.graph;

    public TaxiGUI() {
        // 初始化taxilist
        for (int i = 0; i < 101; i++) {
            GUITaxi guiTaxi = new GUITaxi();
            GUIGV.taxilist.add(guiTaxi);
        }
    }

    public void loadMap(int[][] map, int size) {
        GUIGV.guiInfo.map = new int[size + 5][size + 5];
        //GUIGV.lightmap = new int[size + 5][size + 5];// 初始化红绿灯
        // 复制地图
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                GUIGV.guiInfo.map[i][j] = map[i][j];
            }
        }
        // 开始绘制地图,并每100ms刷新
        new MyForm();
        Thread th = new Thread(new Runnable() {
            public void run() {

                try {
                    int timewindow = 500;// 时间窗设置为500ms
                    int timecount = 0;// 计时
                    while (true) {
                        gv.stay(100);
                        timecount += 100;
                        if (timecount > timewindow) {
                            timecount = 0;
                            // 重新记录流量信息
                            GUIGV.clearFlow();
                        }
                        GUIGV.drawboard.repaint();
                    }
                }
                catch (Throwable e) {
                    System.out.println("发生异常，程序退出！");
                    System.exit(0);
                }

            }
        });
        th.start();
        GUIGV.guiInfo.initMatrix();// 初始化邻接矩阵
    }

    public void setTaxiStatus(int index, Point point, int status) {
        GUITaxi guiTaxi = GUIGV.taxilist.get(index);
        GUIGV.addFlow(guiTaxi.x, guiTaxi.y, point.x, point.y);// 统计流量
        guiTaxi.x = point.x;
        guiTaxi.y = point.y;
        guiTaxi.status = status;
    }
    
    public void SetTaxiType(int index, int type) {
        GUITaxi gt = GUIGV.taxilist.get(index);
        gt.type = type;
    }
    
    public void RequestTaxi(Point src, Point dst) {
        // 将src周围标红
        // GUIGV.srclist.add(src);
    }

    public void setLightStatus(Point p, int status) {// 设置红绿灯 status 0 没有红绿灯 1 东西方向为绿灯 2 东西方向为红灯
        GUIGV.lightmap[p.x][p.y] = status;
    }

    public void setRoadStatus(Point p1, Point p2, int status) {// status 0关闭 1打开
        synchronized (GUIInfo.graph) {
            int di = p1.x - p2.x;
            int dj = p1.y - p2.y;
            Point p = null;
            if (di == 0) {// 在同一水平线上
                if (dj == 1) {// p2-p1
                    p = p2;
                } else if (dj == -1) {// p1-p2
                    p = p1;
                } else {
                    return;
                }
                if (status == 0) {// 关闭
                    if (GUIGV.guiInfo.map[p.x][p.y] == 3) {
                        GUIGV.guiInfo.map[p.x][p.y] = 2;
                    } else if (GUIGV.guiInfo.map[p.x][p.y] == 1) {
                        GUIGV.guiInfo.map[p.x][p.y] = 0;
                    }
                    GUIInfo.graph[p.x * 80 + p.y][p.x * 80 + p.y + 1] = 0;
                    GUIInfo.graph[p.x * 80 + p.y + 1][p.x * 80 + p.y] = 0;
                    MapInfo.setFlow(p1.x, p1.y, p2.x, p2.y, 0);
                } else if (status == 1) {// 打开
                    if (GUIGV.guiInfo.map[p.x][p.y] == 2) {
                        GUIGV.guiInfo.map[p.x][p.y] = 3;
                    } else if (GUIGV.guiInfo.map[p.x][p.y] == 0) {
                        GUIGV.guiInfo.map[p.x][p.y] = 1;
                    }
                    GUIInfo.graph[p.x * 80 + p.y][p.x * 80 + p.y + 1] = 1;
                    GUIInfo.graph[p.x * 80 + p.y + 1][p.x * 80 + p.y] = 1;
                }
            } else if (dj == 0) {// 在同一竖直线上
                if (di == 1) {// p2-p1
                    p = p2;
                } else if (di == -1) {// p1-p2
                    p = p1;
                } else {
                    return;
                }
                if (status == 0) {// 关闭
                    if (GUIGV.guiInfo.map[p.x][p.y] == 3) {
                        GUIGV.guiInfo.map[p.x][p.y] = 1;
                    } else if (GUIGV.guiInfo.map[p.x][p.y] == 2) {
                        GUIGV.guiInfo.map[p.x][p.y] = 0;
                    }
                    GUIInfo.graph[p.x * 80 + p.y][(p.x + 1) * 80 + p.y] = 0;
                    GUIInfo.graph[(p.x + 1) * 80 + p.y][p.x * 80 + p.y] = 0;
                    MapInfo.setFlow(p1.x, p1.y, p2.x, p2.y, 0);
                } else if (status == 1) {// 打开
                    if (GUIGV.guiInfo.map[p.x][p.y] == 1) {
                        GUIGV.guiInfo.map[p.x][p.y] = 3;
                    } else if (GUIGV.guiInfo.map[p.x][p.y] == 0) {
                        GUIGV.guiInfo.map[p.x][p.y] = 2;
                    }
                    GUIInfo.graph[p.x * 80 + p.y][(p.x + 1) * 80 + p.y] = 1;
                    GUIInfo.graph[(p.x + 1) * 80 + p.y][p.x * 80 + p.y] = 1;
                }
            }
            return;
        }
    }

}
