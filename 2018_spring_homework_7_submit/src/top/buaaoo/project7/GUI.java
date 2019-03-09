package top.buaaoo.project7;

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

    public Node(int _NO, Vector<Point> shortestPath) {
        // Requires:int类型的结点号,Vector<Integer>类型的最短路径信息
        // Modifies:创建一个新的node对象，修改了此对象的NO,shortestPath属性
        // Effects:创建了一个新的node对象并初始化
        this.NO = _NO;
        this.shortestPath = shortestPath;
    }
}

class GUIInfo {
    public int[][] map;
    static int[][] graph = new int[6405][6405];// 邻接矩阵
    int[][] D = new int[6405][6405];// 保存从i到j的最小路径值

    public void initMatrix() {// 初始化邻接矩阵
        // Requires:无
        // Modifies:graph[][]
        // Effects:对邻接矩阵赋初值
        int MAXNUM = Tools.MAXNUM;
        for (int i = 0; i < 6400; i++) {
            for (int j = 0; j < 6400; j++) {
                if (i == j)
                    graph[i][j] = 0;
                else
                    graph[i][j] = MAXNUM;
            }
        }
        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 80; j++) {
                if (map[i][j] == 1 || map[i][j] == 3) {
                    graph[i * 80 + j][i * 80 + j + 1] = 1;
                    graph[i * 80 + j + 1][i * 80 + j] = 1;
                }
                if (map[i][j] == 2 || map[i][j] == 3) {
                    graph[i * 80 + j][(i + 1) * 80 + j] = 1;
                    graph[(i + 1) * 80 + j][i * 80 + j] = 1;
                }
            }
        }
    }

    public void pointBFS(int root) {// 单点广度优先搜索
        // Requires:int类型的点号root
        // Modifies:D[][],System.out
        // Effects:对整个地图进行广度优先搜索，获得任意点到root之间的最短路信息，储存在D[][]中
        int[] offset = new int[] { 0, 1, -1, 80, -80 };
        Vector<Node> queue = new Vector<Node>();
        boolean[] view = new boolean[6405];
        for (int i = 0; i < 6400; i++) {
            for (int j = 0; j < 6400; j++) {
                if (i == j) {
                    D[i][j] = 0;
                } else {
                    D[i][j] = graph[i][j];// 赋初值
                }
            }
        }
        int x = root;// 开始进行单点bfs
        for (int i = 0; i < 6400; i++)
            view[i] = false;
        queue.add(new Node(x, 0));
        while (queue.size() > 0) {
            Node n = queue.get(0);
            view[n.NO] = true;
            for (int i = 1; i <= 4; i++) {
                int next = n.NO + offset[i];
                if (next >= 0 && next < 6400 && view[next] == false && graph[n.NO][next] == 1) {
                    view[next] = true;
                    queue.add(new Node(next, n.depth + 1));// 加入遍历队列
                    D[x][next] = n.depth + 1;
                    D[next][x] = n.depth + 1;
                }
            }
            queue.remove(0);// 退出队列
        }
        // 检测孤立点
        int count = 0;
        for (int i = 0; i < 6400; i++) {
            if (D[i][x] == Tools.MAXNUM) {
                count++;
            }
        }
        if (count > 0) {
            System.out.println("地图并不是连通的,程序退出");
            System.exit(1);
        }
    }

    public static synchronized Vector<Point> pointBFS(Point srcPoint, Point dstPoint) {// 单点广度优先搜索
        // Requires:int类型的点号src,dst
        // Modifies:shortestPath,System.out
        // Effects:对整个地图进行广度优先搜索，获得src到dst之间的最短路信息，储存在shortestPath中
        int[] offset = new int[] { 0, 1, -1, 80, -80 };
        Vector<Node> queue = new Vector<Node>();
        Vector<Point> shortestPath = new Vector<Point>();

        if (srcPoint.equals(dstPoint)) {
            return shortestPath;
        }

        Point point;
        int src = (int) srcPoint.getX() * 80 + (int) srcPoint.getY();
        int dst = (int) dstPoint.getX() * 80 + (int) dstPoint.getY();

        boolean[] view = new boolean[6405];
        for (int i = 0; i < 6400; i++) {
            view[i] = false;
        }

        view[src] = true;
        queue.add(new Node(src, shortestPath));
        while (queue.size() > 0) {
            Node n = queue.get(0);
            for (int i = 1; i <= 4; i++) {
                int next = n.NO + offset[i];
                if (next >= 0 && next < 6400 && view[next] == false && graph[n.NO][next] == 1) {
                    shortestPath = new Vector<Point>(n.shortestPath.size());
                    for (Point p : n.shortestPath) {
                        shortestPath.add(p);
                    }
                    point = new Point(next / 80, next % 80);
                    shortestPath.add(point);
                    if (next == dst) {
                        return shortestPath;
                    }
                    view[next] = true;
                    queue.add(new Node(next, shortestPath));// 加入遍历队列
                }
            }
            queue.remove(0);// 退出队列
        }
        System.out.println("地图并不是连通的,程序退出");
        System.exit(0);
        return shortestPath;
    }

    public static synchronized int getDistance(Point srcPoint, Point dstPoint) {// 单点广度优先搜索
        // Requires:Point类型的坐标srcPoint,dstPoint
        // Modifies:depth,System.out
        // Effects:对整个地图进行广度优先搜索，获得src到dst之间的最短路信息，储存在depth中
        if (srcPoint.equals(dstPoint)) {
            return 0;
        }

        int[] offset = new int[] { 0, 1, -1, 80, -80 };
        Vector<Node> queue = new Vector<Node>();
        int src = srcPoint.x * 80 + srcPoint.y;
        int dst = dstPoint.x * 80 + dstPoint.y;

        boolean[] view = new boolean[6405];
        for (int i = 0; i < 6400; i++) {
            view[i] = false;
        }

        view[src] = true;
        queue.add(new Node(src, 0));
        while (queue.size() > 0) {
            Node n = queue.get(0);
            for (int i = 1; i <= 4; i++) {
                int next = n.NO + offset[i];
                if (next >= 0 && next < 6400 && view[next] == false && graph[n.NO][next] == 1) {
                    if (next == dst) {
                        return n.depth + 1;
                    }
                    view[next] = true;
                    queue.add(new Node(next, n.depth + 1));// 加入遍历队列
                }
            }
            queue.remove(0);// 退出队列
        }
        System.out.println("地图并不是连通的,程序退出");
        System.exit(0);
        return 0;
    }

    public int distance(int x1, int y1, int x2, int y2) {// 使用bfs计算两点之间的距离
        // pointBFS(x1 * 80 + y1);
        // return D[x1 * 80 + y1][x2 * 80 + y2];
        return getDistance(new Point(x1, y1), new Point(x2, y2));
    }
}

class GUITaxi {
    public int x = 1;
    public int y = 1;
    public int status = -1;
}

class GUIGV {
    public static GUIInfo m = new GUIInfo();// 地图备份
    public static CopyOnWriteArrayList<GUITaxi> taxilist = new CopyOnWriteArrayList<GUITaxi>();// 出租车列表
    public static CopyOnWriteArrayList<Point> srclist = new CopyOnWriteArrayList<Point>();// 出发点列表
    /* GUI */
    public static JPanel drawboard;
    public static int[][] colormap;
    public static boolean redraw = false;
    public static int xoffset = 0;
    public static int yoffset = 0;
    public static int oldxoffset = 0;
    public static int oldyoffset = 0;
    public static int mousex = 0;
    public static int mousey = 0;
    public static double percent = 1.0;
    public static boolean drawstr = false;
}

class DrawBoard extends JPanel {// 绘图板类
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        Brush.draw(g2D);
    }
}

class Myform extends JFrame {// 我的窗体程序
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int left = 100;
    private int top = 100;
    private int width = 600;
    private int height = 600;

    public Myform() {
        super();
        /* 设置按钮属性 */
        // button1
        JButton button1 = new JButton();// 创建一个按钮
        button1.setBounds(10, 515, 100, 40);// 设置按钮的位置
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
        button2.setBounds(120, 515, 100, 40);// 设置按钮的位置
        button2.setText("放大");// 设置按钮的标题
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIGV.percent += 0.1;
                GUIGV.drawboard.repaint();
            }
        });
        // button3
        JButton button3 = new JButton();// 创建一个按钮
        button3.setBounds(230, 515, 100, 40);// 设置按钮的位置
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
        button4.setBounds(340, 515, 100, 40);
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
        check1.setBounds(450, 515, 200, 40);
        check1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIGV.drawstr = check1.isSelected();
                GUIGV.drawboard.repaint();
            }
        });
        /* 设置绘图区属性 */
        DrawBoard drawboard = new DrawBoard();// 创建新的绘图板
        drawboard.setBounds(10, 10, 500, 500);// 设置大小
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
        c.add(drawboard);
        setVisible(true);// 使窗体可见
        setAlwaysOnTop(true);// 设置窗体置顶
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置默认关闭方式
    }
}

class Brush {// 画笔
    public static int[][] colormap = new int[85][85];

    public static void draw(Graphics2D g) {
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
                taximap[gt.x][gt.y] = gt.status;
                if (gt.status == 1) {
                    colormap[gt.x][gt.y] = 1;// 路线染色
                }
            }
        }
        // synchronized (guigv.m.taxilist) {
        // for (taxiInfo i : guigv.m.taxilist) {
        // taximap[i.nowPoint.x][i.nowPoint.y] = 1;
        // if (i.state == STATE.WILL || i.state == STATE.RUNNING) {
        // taximap[i.nowPoint.x][i.nowPoint.y] = 2;
        // colormap[i.nowPoint.x][i.nowPoint.y] = 1;// 路线染色
        // }
        // }
        // }
        // ...

        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 80; j++) {
                if (j < 10) {
                    xoffset = -5;
                    yoffset = 3;
                } else {
                    xoffset = -7;
                    yoffset = 3;
                }
                g.setStroke(new BasicStroke(2));
                if (GUIGV.m.map[i][j] == 2 || GUIGV.m.map[i][j] == 3) {
                    if (drawcolor && colormap[i][j] == 1 && colormap[i + 1][j] == 1)
                        g.setColor(Color.RED);
                    else
                        g.setColor(Color.BLACK);
                    g.drawLine((int) ((j * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) ((i * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) ((j * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((i + 1) * factor + GUIGV.yoffset) * GUIGV.percent));
                }
                if (GUIGV.m.map[i][j] == 1 || GUIGV.m.map[i][j] == 3) {
                    if (drawcolor && colormap[i][j] == 1 && colormap[i][j + 1] == 1)
                        g.setColor(Color.RED);
                    else
                        g.setColor(Color.BLACK);
                    g.drawLine((int) ((j * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) ((i * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((j + 1) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) ((i * factor + GUIGV.yoffset) * GUIGV.percent));
                }
                int targetWidth;
                if (taximap[i][j] == 3) {
                    g.setColor(Color.GREEN);
                    targetWidth = 2;
                } else if (taximap[i][j] == 2) {
                    g.setColor(Color.RED);
                    targetWidth = 2;
                } else if (taximap[i][j] == 1) {
                    g.setColor(Color.BLUE);
                    targetWidth = 2;
                } else if (taximap[i][j] == 0) {
                    g.setColor(Color.YELLOW);
                    targetWidth = 2;
                } else {
                    g.setColor(Color.BLACK);
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
                g.fillOval(cleft, ctop, cwidth, cwidth);// 绘制点
                // 标记srclist中的点
                for (Point p : GUIGV.srclist) {
                    g.setColor(Color.RED);
                    int x = p.x;
                    int y = p.y;
                    g.drawLine((int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                    g.drawLine((int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                    g.drawLine((int) (((y + 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                    g.drawLine((int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x + 2) * factor + GUIGV.yoffset) * GUIGV.percent),
                            (int) (((y - 2) * factor + GUIGV.xoffset) * GUIGV.percent),
                            (int) (((x - 2) * factor + GUIGV.yoffset) * GUIGV.percent));
                }
                if (GUIGV.drawstr == true) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, (int) (8 * GUIGV.percent)));
                    g.drawString("" + i + "," + j, (int) ((j * factor + xoffset + GUIGV.xoffset) * GUIGV.percent),
                            (int) ((i * factor + yoffset + GUIGV.yoffset) * GUIGV.percent));
                }
            }
        }
    }
}

class ProcessForm extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JProgressBar progressBar = new JProgressBar();
    JLabel label1 = new JLabel("BFS进度", SwingConstants.CENTER);

    public ProcessForm() {
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

class DebugForm extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTextArea text1 = new JTextArea();

    public DebugForm() {
        super();
        getContentPane().add(text1);
        setBounds(0, 100, 500, 100);
        setAlwaysOnTop(true);
        setVisible(true);
    }
}

class TaxiGUI {// GUI接口类
    public void loadMap(int[][] map, int size) {
        GUIGV.m.map = new int[size + 5][size + 5];
        // 复制地图
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                GUIGV.m.map[i][j] = map[i][j];
            }
        }
        // 开始绘制地图,并每100ms刷新
        new Myform();
        Thread th = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    Tools.stay(100);
                    GUIGV.drawboard.repaint();
                }
            }
        });
        th.start();
        GUIGV.m.initMatrix();// 初始化邻接矩阵
    }

    public void SetTaxiStatus(int index, Point point, int status) {
        GUITaxi gt = GUIGV.taxilist.get(index);
        gt.x = point.x;
        gt.y = point.y;
        gt.status = status;
    }

    public void RequestTaxi(Point src, Point dst) {
        // 将src周围标红
//        guigv.srclist.add(src);
        // 计算最短路径的值,通过一个窗口弹出
//        int distance = guigv.m.distance(src.x, src.y, dst.x, dst.y);
//        debugform form1 = new debugform();
//        form1.text1.setText("从(" + src.x + "," + src.y + ")到(" + dst.x + "," + dst.y + ")的最短路径长度是" + distance);
    }

    public TaxiGUI() {
        // 初始化taxilist
        for (int i = 0; i < 101; i++) {
            GUITaxi gt = new GUITaxi();
            GUIGV.taxilist.add(gt);
        }
    }
}
