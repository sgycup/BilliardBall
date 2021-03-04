package cn.cnic.collide;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.23 023 13:41:31
 */
public class Table {
    /**
     * 斯诺克(司诺克)台球桌尺寸：3820*2035*850mm
     * 美式落袋台球桌尺寸：2810*1530*850mm
     * 花式九球台球桌尺寸：2850*1580*850mm
     */
    public double length = 2.54, width = 1.27;
    // 滑动摩擦系数，滚动摩擦系数，库边摩擦系数
    public double mu = 0.15, Mu = 0.0005, muc = 0.15;
    // 库边弹性系数
    public double k = 1e4;
    // 袋角半径，角袋口内沿最短距离为10.5厘米（±1毫米），腰袋袋口比角袋宽1.5厘米
    public double cornerR = 0.05, cornerLen = 0.085, middleLen = 0.1;
    // 开球线
    public double serveline = 0.25 * length;
    // 置球点
    public double spotx = 0.75 * length, spoty = width / 2;
    // 边距
    public double margin = 0.2;
    // 袋口位置
    public double[] pocketx, pockety;

    public Ball[] balls;
    public Cue cue;

    public Table(int n) {
        balls = new Ball[n];
        for (int i = 0; i < balls.length; i++) {
            balls[i] = new Ball(this);
        }
        length -= balls[0].d;
        width -= balls[0].d;
        balls = new Ball[n];
        for (int i = 0; i < balls.length; i++) {
            balls[i] = new Ball(this);
        }
        spoty = width / 2;
        pocketx = new double[6];
        pockety = new double[6];
        // 左上
        pocketx[0] = -cornerLen / 2;
        pockety[0] = width + cornerLen / 2;
        // 中上
        pocketx[1] = length / 2;
        pockety[1] = width + cornerLen / 2;
        // 右上
        pocketx[2] = length + cornerLen / 2;
        pockety[2] = width + cornerLen / 2;
        // 左下
        pocketx[3] = -cornerLen / 2;
        pockety[3] = -cornerLen / 2;
        // 中下
        pocketx[4] = length / 2;
        pockety[4] = -cornerLen / 2;
        // 右下
        pocketx[5] = length + cornerLen / 2;
        pockety[5] = -cornerLen / 2;
    }

    public static Table create8Ball() {
        Table table = new Table(16);
        Ball white = table.balls[15];
        white.paint = Color.WHITE;
        Arrays.fill(white.x, table.serveline);
        Arrays.fill(white.y, table.width / 2);
        int k = 0;
        Ball ball = table.balls[k];
        Arrays.fill(ball.x, table.spotx);
        Arrays.fill(ball.y, table.spoty);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i + 1; j++) {
                ball = table.balls[k];
                int d = k + (i + 1);
                int u = k + (i + 2);
                double x = ball.x[0] + ball.d * Math.cos(Math.PI / 6);
                double yd = ball.y[0] - ball.d * Math.sin(Math.PI / 6);
                double yu = ball.y[0] + ball.d * Math.sin(Math.PI / 6);
                Ball balld = table.balls[d];
                Arrays.fill(balld.x, x);
                Arrays.fill(balld.y, yd);
                Ball ballu = table.balls[u];
                Arrays.fill(ballu.x, x);
                Arrays.fill(ballu.y, yu);
                k++;
            }
        }
        table.balls[4].paint = Color.BLACK;
        HashSet<Integer> set = new HashSet<>();
        Random random = new Random();
        do {
            int num = random.nextInt(15);
            if (num != 4) {
                set.add(num);
            }
        } while (set.size() != 7);
        for (int i = 0; i < 15; i++) {
            if (i == 4) continue;
            if (set.contains(i)) {
                table.balls[i].paint = Color.RED;
            } else {
                table.balls[i].paint = Color.YELLOW;
            }
        }
        Cue cue = new Cue();
        cue.table = table;
        cue.white = white;
        table.cue = cue;
        return table;
    }

    public void play(double time) {
        double cost = 0;
        while (true) {
            for (Ball ball: balls) {
                ball.solveEquation();
            }
            cost += balls[0].dt;
            if (cost >= time) {
                break;
            }
            if (canNextShoot()) {
                for (Ball ball: balls) {
                    ball.stepForward();
                }
                break;
            }
            for (Ball ball: balls) {
                ball.stepForward();
            }
        }
    }

    public boolean canNextShoot() {
        boolean nextShoot = true;
        for (Ball ball: balls) {
            double[] x = ball.x;
            double[] y = ball.y;
            double dt = ball.dt;
            double vx = (x[2] - x[1]) / dt;
            double vy = (y[2] - y[1]) / dt;
            double ax = (x[2] - 2 * x[1] + x[0]) / dt / dt;
            double ay = (y[2] - 2 * y[1] + y[0]) / dt / dt;
            nextShoot &= Math.abs(vx) < 1e-5;
            nextShoot &= Math.abs(vy) < 1e-5;
            nextShoot &= Math.abs(ax) < 1e-3;
            nextShoot &= Math.abs(ay) < 1e-3;
        }
        if (nextShoot) {
            for (Ball ball: balls) {
                ball.wz[1] = 0;
            }
        }
        return nextShoot;
    }

    public void display(BufferedImage screen) {
        Graphics graphics = screen.getGraphics();
        int height = screen.getHeight();
        int width = screen.getWidth();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0,0, width, height);
        graphics.setColor(Color.BLACK);
        double d = balls[0].d;
        int offsetx = (int)((margin / (length + d + margin * 2)) * width);
        int offsety = (int)((margin / (this.width + d + margin * 2)) * height);
        graphics.drawRect(offsetx, offsety, width - offsetx * 2, height - offsety * 2);

        // 左下
        double x1 = cornerLen / 2 / Math.cos(Math.PI / 4) - cornerR * (1 - Math.tan(Math.PI / 8));
        double y1 = 0;
        double x2 = pocketx[3] - cornerLen / 2;
        double y2 = pockety[3] + cornerLen / 2;
        int start1 = 90;
        int arc1 = 45;
        int start2 = -135;
        int arc2 = 90;
        double lx1 = x1 + cornerR * (1 - Math.cos(Math.PI / 4));
        double ly1 = -cornerR * (1 - Math.cos(Math.PI / 4));
        double lx2 = pocketx[3] + cornerLen / 2 * Math.cos(Math.PI / 4);
        double ly2 = pockety[3] - cornerLen / 2 * Math.sin(Math.PI / 4);

        drawPocket(graphics, height, width, x1, y1, x2, y2, start1, arc1, start2, arc2, lx1, ly1, lx2, ly2, cornerLen);

        double[] l1 = CoordinateConversion.symmetryXY(lx1, ly1);
        double[] l2 = CoordinateConversion.symmetryXY(lx2, ly2);
        double[] c1 = CoordinateConversion.pan(x1, y1, new double[]{-cornerR, cornerR});
        double[] c2 = CoordinateConversion.pan(x2, y2, new double[]{-cornerLen / 2, cornerLen / 2});
        c1 = CoordinateConversion.symmetryXY(c1[0], c1[1]);
        c2 = CoordinateConversion.symmetryXY(c2[0], c2[1]);
        c1 = CoordinateConversion.pan(c1[0], c1[1], new double[]{cornerR, -cornerR});
        c2 = CoordinateConversion.pan(c2[0], c2[1], new double[]{cornerLen / 2, -cornerLen / 2});
        int[] a1 = CoordinateConversion.arcSymmetryXY(start1, arc1);
        int[] a2 = CoordinateConversion.arcSymmetryXY(start2, arc2);

        drawPocket(graphics, height, width, c1[0], c1[1], c2[0], c2[1], a1[0], a1[1], a2[0], a2[1], l1[0], l1[1], l2[0], l2[1], cornerLen);

        // 左上
        y1 = this.width + d - y1 + 2 * cornerR;
        y2 = this.width + d - y2 + cornerLen;
        ly1 = this.width + d - ly1;
        ly2 = this.width + d - ly2;
        int[] aa1 = CoordinateConversion.arcSymmetryX(start1, arc1);
        start1 = aa1[0];
        arc1 = aa1[1];
        int[] aa2 = CoordinateConversion.arcSymmetryX(start2, arc2);
        start2 = aa2[0];
        arc2 = aa2[1];
        drawPocket(graphics, height, width, x1, y1, x2, y2, start1, arc1, start2, arc2, lx1, ly1, lx2, ly2, cornerLen);

        c1[1] = this.width + d - c1[1] + 2 * cornerR;
        c2[1] = this.width + d - c2[1] + cornerLen;
        l1[1] = this.width + d - l1[1];
        l2[1] = this.width + d - l2[1];
        a1 = CoordinateConversion.arcSymmetryX(a1[0], a1[1]);
        a2 = CoordinateConversion.arcSymmetryX(a2[0], a2[1]);
        drawPocket(graphics, height, width, c1[0], c1[1], c2[0], c2[1], a1[0], a1[1], a2[0], a2[1], l1[0], l1[1], l2[0], l2[1], cornerLen);

        // 右上角
        x1 = this.length + d - x1 - 2 * cornerR;
        x2 = this.length + d - x2 - cornerLen;
        lx1 = this.length + d - lx1;
        lx2 = this.length + d - lx2;
        aa1 = CoordinateConversion.arcSymmetryY(start1, arc1);
        start1 = aa1[0];
        arc1 = aa1[1];
        aa2 = CoordinateConversion.arcSymmetryY(start2, arc2);
        start2 = aa2[0];
        arc2 = aa2[1];
        drawPocket(graphics, height, width, x1, y1, x2, y2, start1, arc1, start2, arc2, lx1, ly1, lx2, ly2, cornerLen);

        c1[0] = this.length + d - c1[0] - 2 * cornerR;
        c2[0] = this.length + d - c2[0] - cornerLen;
        l1[0] = this.length + d - l1[0];
        l2[0] = this.length + d - l2[0];
        a1 = CoordinateConversion.arcSymmetryY(a1[0], a1[1]);
        a2 = CoordinateConversion.arcSymmetryY(a2[0], a2[1]);
        drawPocket(graphics, height, width, c1[0], c1[1], c2[0], c2[1], a1[0], a1[1], a2[0], a2[1], l1[0], l1[1], l2[0], l2[1], cornerLen);

        // 右下角
        y1 = this.width + d - y1 + 2 * cornerR;
        y2 = this.width + d - y2 + cornerLen;
        ly1 = this.width + d - ly1;
        ly2 = this.width + d - ly2;
        aa1 = CoordinateConversion.arcSymmetryX(start1, arc1);
        start1 = aa1[0];
        arc1 = aa1[1];
        aa2 = CoordinateConversion.arcSymmetryX(start2, arc2);
        start2 = aa2[0];
        arc2 = aa2[1];
        drawPocket(graphics, height, width, x1, y1, x2, y2, start1, arc1, start2, arc2, lx1, ly1, lx2, ly2, cornerLen);

        c1[1] = this.width + d - c1[1] + 2 * cornerR;
        c2[1] = this.width + d - c2[1] + cornerLen;
        l1[1] = this.width + d - l1[1];
        l2[1] = this.width + d - l2[1];
        a1 = CoordinateConversion.arcSymmetryX(a1[0], a1[1]);
        a2 = CoordinateConversion.arcSymmetryX(a2[0], a2[1]);
        drawPocket(graphics, height, width, c1[0], c1[1], c2[0], c2[1], a1[0], a1[1], a2[0], a2[1], l1[0], l1[1], l2[0], l2[1], cornerLen);

        // 下中
        x1 = pocketx[4] + middleLen / 2;
        y1 = 0;
        x2 = pocketx[4] - middleLen / 2;
        y2 = pockety[4] + middleLen / 2;
        start1 = 90;
        arc1 = 90;
        start2 = -90;
        arc2 = 90;
        lx1 = x1;
        ly1 = -cornerR;
        lx2 = x1;
        ly2 = pockety[4];
        drawPocket(graphics, height, width, x1, y1, x2, y2, start1, arc1, start2, arc2, lx1, ly1, lx2, ly2, middleLen);

        double sx1 = 2 * pocketx[4] - (pocketx[4] + middleLen / 2) - cornerR * 2;
        double sy1 = 0;
        double sx2 = 2 * pocketx[4] - (pocketx[4] - middleLen / 2) - middleLen;
        double sy2 = pockety[4] + middleLen / 2;
        aa1 = CoordinateConversion.arcSymmetryY(start1, arc1);
        int sstart1 = aa1[0];
        int sarc1 = aa1[1];
        aa2 = CoordinateConversion.arcSymmetryY(start2, arc2);
        int sstart2 = aa2[0];
        int sarc2 = aa2[1];
        double slx1 = 2 * pocketx[4] - x1;
        double sly1 = -cornerR;
        double slx2 = 2 * pocketx[4] - x1;
        double sly2 = pockety[4];
        drawPocket(graphics, height, width, sx1, sy1, sx2, sy2, sstart1, sarc1, sstart2, sarc2, slx1, sly1, slx2, sly2, middleLen);

        // 上中
        y1 = this.width + d - y1 + 2 * cornerR;
        y2 = this.width + d - y2 + middleLen;
        ly1 = this.width + d - ly1;
        ly2 = this.width + d - ly2;
        aa1 = CoordinateConversion.arcSymmetryX(start1, arc1);
        start1 = aa1[0];
        arc1 = aa1[1];
        aa2 = CoordinateConversion.arcSymmetryX(start2, arc2);
        start2 = aa2[0];
        arc2 = aa2[1];
        drawPocket(graphics, height, width, x1, y1, x2, y2, start1, arc1, start2, arc2, lx1, ly1, lx2, ly2, middleLen);

        sy1 = this.width + d - sy1 + 2 * cornerR;
        sy2 = this.width + d - sy2 + middleLen;
        sly1 = this.width + d - sly1;
        sly2 = this.width + d - sly2;
        aa1 = CoordinateConversion.arcSymmetryX(sstart1, sarc1);
        sstart1 = aa1[0];
        sarc1 = aa1[1];
        aa2 = CoordinateConversion.arcSymmetryX(sstart2, sarc2);
        sstart2 = aa2[0];
        sarc2 = aa2[1];
        drawPocket(graphics, height, width, sx1, sy1, sx2, sy2, sstart1, sarc1, sstart2, sarc2, slx1, sly1, slx2, sly2, middleLen);

        for (Ball ball: balls) {
            ball.display(screen);
        }

        if (cue != null) {
            cue.display(screen);
        }
    }

    private void drawPocket(Graphics graphics, int height, int width, double x1, double y1, double x2, double y2
            , int start1, int arc1, int start2, int arc2, double lx1, double ly1, double lx2, double ly2, double pockteLen) {
        int sx1 = xOnScreen(x1, width);
        int sy1 = yOnScreen(y1, height);
        int sdx1 = xlenOnScreen(cornerR * 2, width);
        int sdy1 = ylenOnScreen(cornerR * 2, height);
        int sx2 = xOnScreen(x2, width);
        int sy2 = yOnScreen(y2, height);
        int sdx2 = xlenOnScreen(pockteLen, width);
        int sdy2 = ylenOnScreen(pockteLen, height);
        graphics.drawArc(sx1, sy1, sdx1, sdy1, start1, arc1);
        graphics.drawArc(sx2, sy2, sdx2, sdy2, start2, arc2);
        int slx1 = xOnScreen(lx1, width);
        int sly1 = yOnScreen(ly1, height);
        int slx2 = xOnScreen(lx2, width);
        int sly2 = yOnScreen(ly2, height);
        graphics.drawLine(slx1, sly1, slx2, sly2);
    }

    private int xlenOnScreen(double xlen, int width) {
        double d = balls[0].d;
        return (int)(xlen / (length + d + margin * 2) * width);
    }

    private int ylenOnScreen(double ylen, int height) {
        double d = balls[0].d;
        return (int)(ylen / (this.width + d + margin * 2) * height);
    }

    private int xOnScreen(double x, int width) {
        double d = balls[0].d;
        return (int)((margin + x) / (length + d + margin * 2) * width);
    }

    private int yOnScreen(double y, int height) {
        double d = balls[0].d;
        return height - (int)((margin + y) / (this.width + d + margin * 2) * height);
    }

    public static void main(String[] args) {
        Table table = Table.create8Ball();
//        double vx = 10;
//        double wy = -vx * table.balls[0].m * table.balls[0].d / 2 / table.balls[0].J;
//        Arrays.fill(table.balls[15].x, 0);
//        Arrays.fill(table.balls[15].y, table.width);
//        Arrays.fill(table.balls[14].x, 0);
//        Arrays.fill(table.balls[14].y, 0);
//        Arrays.fill(table.balls[13].x, table.length);
//        Arrays.fill(table.balls[13].y, 0);
//        Arrays.fill(table.balls[12].x, table.length);
//        Arrays.fill(table.balls[12].y, table.width);
//        Arrays.fill(table.balls[11].x, table.length / 2);
//        Arrays.fill(table.balls[11].y, 0);
//        Arrays.fill(table.balls[10].x, table.length / 2);
//        Arrays.fill(table.balls[10].y, table.width);
//        table.balls[15].stroke(new double[]{0.1, 5}, new double[]{0, 0, 0});
//        for (int i = 0; i < 15; i++) {
//            table.balls[i].pot = true;
//        }
    }
}
