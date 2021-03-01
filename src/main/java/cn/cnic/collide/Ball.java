package cn.cnic.collide;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.23 023 13:24:27
 */
public class Ball {

    public Paint paint;
    public String text;
    public Font font;
    // 是否落袋
    public boolean pot = false;
    // 坐标
    public double[] x = new double[3], y = new double[3];
    public double z = 0;
    // 角速度
    public double[] wx = new double[2], wy = new double[2], wz = new double[2];
    // 中式八球的台球尺寸：直径57.15mm（允许误差+/-0.05mm），重量156g-170g。
    public double d = 57.15e-3, m = 160e-3;
    // 转动惯量
    public double J = 2.0 / 5 * m * Math.pow(d / 2, 2);
    // 杨氏模量
    public double k = 1e6;
    // 重力加速度
    public double g = 9.8;
    // 时步
    public double dt = 1e-4;
    // 摩阻系数，球之间
    public double mu = 0.005;

    private Table table;

    private CoordinateConversion cc;

    public Ball(Table table) {
        this.table = table;
        cc = new CoordinateConversion(table);
    }

    public void stroke(double[] v0, double[] w0) {
        x[1] = x[0] + v0[0] * dt;
        y[1] = y[0] + v0[1] * dt;
        wx[0] = w0[0];
        wy[0] = w0[1];
        wz[0] = w0[2];
    }

    private void checkPot() {
        for (Ball ball: table.balls) {
            double x = ball.x[1];
            double y = ball.y[1];
            double dt = ball.dt;
            // 大于两度时掉落
            if (ball.z > Math.PI / 90) {
                ball.pot = true;
                ball.z = 0;
            }
            if (x < -table.cornerLen || x > table.length + table.cornerLen
                    || y < -table.cornerLen || y > table.width + table.cornerLen) {
                ball.pot = true;
                ball.z = 0;
            }
            double l = 0;
            for (int i = 0, n = table.pocketx.length; i < n; i++) {
                double dr = Math.sqrt((x - table.pocketx[i]) * (x - table.pocketx[i]) + (y - table.pockety[i]) * (y - table.pockety[i]));
                if (dr < table.cornerLen / 2) {
                    l = table.cornerLen / 2 - dr;
                    break;
                }
            }
            ball.z += ball.m * ball.g * l * dt;
        }
    }

    public void solveEquation() {
        checkPot();
        if (pot) {
            return;
        }
        // 平动
        double Fx = 0, Fy = 0;
        double Mx = 0, My = 0, Mz = 0;
        for (Ball ball : table.balls) {
            if (ball.pot || this.equals(ball)) {
                continue;
            }
            double x2 = ball.x[1];
            double y2 = ball.y[1];
            double x1 = x[1];
            double y1 = y[1];
            double dis = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
            // 判断是否发生碰撞
            if (dis > d) {
                continue;
            }
            // 碰撞合外力
            double lx = x2 - x1;
            lx -= d * lx / dis;
            double fx = k * lx / m;
            Fx += fx;
            double ly = y2 - y1;
            ly -= d * ly / dis;
            double fy = k * ly / m;
            Fy += fy;
            // 碰撞合外力矩
            double f = Math.sqrt(fx * fx + fy * fy);
            double wx2 = ball.wx[0];
            double wx1 = wx[0];
            double dwx = wx2 + wx1;
            if (Math.abs(dwx) > 1e-3) {
                Mx += -Math.signum(wx1) * d / 2 * f * mu;
            }
            double wy2 = ball.wy[0];
            double wy1 = wy[0];
            double dwy = wy2 + wy1;
            if (Math.abs(dwy) > 1e-3) {
                My += -Math.signum(wy1) * d / 2 * f * mu;
            }
            double wz2 = ball.wz[0];
            double wz1 = wz[0];
            double dwz = wz2 + wz1;
            if (Math.abs(dwz) > 1e-3) {
                Mz += -Math.signum(wz1) * d / 2 * f * mu;
            }
        }
        // 摩阻
        double vx = (x[1] - x[0]) / dt;
        double vy = (y[1] - y[0]) / dt;
        double vxr = vx - wy[0] * d / 2;
        double vyr = vy + wx[0] * d / 2;
        double vr = Math.sqrt(vxr * vxr + vyr * vyr);
        double f, fx = 0, fy = 0;
        if (Math.abs(vr) > 1e-5) {
            double theta = Math.acos(vxr / vr);
            if (vyr < 0) {
                theta = 2 * Math.PI - theta;
            }
            f = table.mu * g;
            fx = -f * Math.cos(theta);
            fy = -f * Math.sin(theta);
        }

        // 摩阻力矩
        My += -fx * d / 2;
        Mx += fy * d / 2;
        // 库边
        double fsx = 0, fsy = 0;
        double deltaLen = (table.cornerR + d / 2) * Math.tan(Math.PI / 8);
        double cornerLen = (table.cornerLen - d) * Math.cos(Math.PI / 4);
        if (x[1] < 0) {
            if (y[1] >= (deltaLen + cornerLen) && y[1] <= (table.width - deltaLen - cornerLen)) {
                fsx = -table.k * x[1] / m;
                if (Math.abs(vy - wz[0] * d / 2) > 1e-3) {
                    fsy = -Math.signum(vy - wz[0] * d / 2) * table.muc * Math.abs(fsx);
                    // 库边力矩
                    Mz += fsy * d / 2;
                }
            } else {
                // 袋口
                double[] coord = {x[1], y[1], 1};
                // 2个边袋角
                if (y[1] < (deltaLen + cornerLen)) {
                    double[] coordNew = cc.forwardPoint(coord, 0);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 0);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                } else {
                    double[] coordNew = cc.forwardPoint(coord, 1);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 1);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
            }
        }
        if (x[1] > table.length) {
            if (y[1] >= (deltaLen + cornerLen) && y[1] <= (table.width - deltaLen - cornerLen)) {
                fsx = -table.k * (x[1] - table.length) / m;
                if (Math.abs(vy + wz[0] * d / 2) > 1e-3) {
                    fsy = -Math.signum(vy + wz[0] * d / 2) * table.muc * Math.abs(fsx);
                    Mz += fsy * d / 2;
                }
            } else {
                // 袋口
                double[] coord = {x[1], y[1], 1};
                // 2个边袋角
                if (y[1] < (deltaLen + cornerLen)) {
                    double[] coordNew = cc.forwardPoint(coord, 5);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 5);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                } else {
                    double[] coordNew = cc.forwardPoint(coord, 4);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 4);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
            }
        }
        boolean left = x[1] >= cornerLen + deltaLen && x[1] <= (table.length - table.middleLen) / 2 - table.cornerR;
        boolean right = x[1] >= (table.length + table.middleLen) / 2 + table.cornerR && x[1] <= table.length - cornerLen - deltaLen;
        if (y[1] < 0) {
            if (left || right) {
                fsy = -table.k * y[1] / m;
                if (Math.abs(vx + wz[0] * d / 2) > 1e-3) {
                    fsx = -Math.signum(vx + wz[0] * d / 2) * table.muc * Math.abs(fsy);
                    // 库边力矩
                    Mz += fsx * d / 2;
                }
            } else {
                // 袋口
                double[] coord = {x[1], y[1], 1};
                // 2个中袋角
                if (x[1] > (table.length - table.middleLen) / 2 - table.cornerR && x[1] < table.length / 2) {
                    double[] coordNew = cc.forwardPoint(coord, 8);
                    double[] force = middleForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 8);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
                if (x[1] > table.length / 2 && x[1] < (table.length + table.middleLen) / 2 + table.cornerR) {
                    double[] coordNew = cc.forwardPoint(coord, 9);
                    double[] force = middleForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 9);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
                if (x[1] < cornerLen + deltaLen) {
                    double[] coordNew = cc.forwardPoint(coord, 7);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 7);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
                if (x[1] > table.length - cornerLen - deltaLen) {
                    double[] coordNew = cc.forwardPoint(coord, 6);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 6);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
            }
        }
        if (y[1] > table.width) {
            if (left || right) {
                fsy = -table.k * (y[1] - table.width) / m;
                if (Math.abs(vx - wz[0] * d / 2) > 1e-3) {
                    fsx = -Math.signum(vx - wz[0] * d / 2) * table.muc * Math.abs(fsy);
                    Mz += -fsx * d / 2;
                }
            } else {
                // 袋口
                double[] coord = {x[1], y[1], 1};
                // 2个中袋角
                if (x[1] > (table.length - table.middleLen) / 2 - table.cornerR && x[1] < table.length / 2) {
                    double[] coordNew = cc.forwardPoint(coord, 11);
                    double[] force = middleForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 11);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
                if (x[1] > table.length / 2 && x[1] < (table.length + table.middleLen) / 2 + table.cornerR) {
                    double[] coordNew = cc.forwardPoint(coord, 10);
                    double[] force = middleForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 10);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
                if (x[1] < cornerLen + deltaLen) {
                    double[] coordNew = cc.forwardPoint(coord, 2);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 2);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
                if (x[1] > table.length - cornerLen - deltaLen) {
                    double[] coordNew = cc.forwardPoint(coord, 3);
                    double[] force = cornerForce(coordNew[0], coordNew[1]);
                    force = cc.backforwForce(force, 3);
                    fsx += force[0];
                    fsy += force[1];
                    Mz += force[2];
                }
            }
        }
        double F = Math.sqrt((Fx + fx + fsx) * (Fx + fx + fsx) + (Fy + fy + fsy) * (Fy + fy + fsy));
        // 更新
        if (Math.abs(vr) > 1e-5 && F > 1e-3) {
            // 平动
            x[2] = (Fx + fx + fsx) * dt * dt + 2 * x[1] - x[0];
            y[2] = (Fy + fy + fsy) * dt * dt + 2 * y[1] - y[0];
        } else {
            // 纯滚动
            x[2] += wy[0] * d / 2 * dt;
            x[0] = x[1];
            y[2] += -wx[0] * d / 2 * dt;
            y[0] = y[1];
        }
        // 旋转
        double w = Math.sqrt(wx[0] * wx[0] + wy[0] * wy[0]);
        double M = 0;
        if (Math.abs(w) > 1e-3) {
            double theta = Math.acos(wy[0] / w);
            if (wy[0] < 0) {
                theta = 2 * Math.PI - theta;
            }
            M = table.Mu * g;
            Mx += -M * Math.cos(theta);
            My += -M * Math.sin(theta);
        }
        if (Math.abs(wz[0]) > 1e-3) {
            Mz += -Math.signum(wz[0]) * table.Mu * g;
        }
        Mx *= m;
        My *= m;
        Mz *= m;
        wx[1] = wx[0] + Mx / J * dt;
        wy[1] = wy[0] + My / J * dt;
        wz[1] = wz[0] + Mz / J * dt;
        if (Math.abs(M) < 1e-3) {
            wx[1] = 0;
            wy[1] = 0;
        }
        if (Math.abs(Mz) < 1e-3) {
            wz[1] = 0;
        }
    }

    public double[] cornerForce(double x, double y) {
        double[] result = new double[3];
        double deltaLen = (table.cornerR + d / 2) * Math.tan(Math.PI / 8);
        double cornerLen = (table.cornerLen - d) * Math.cos(Math.PI / 4);
        double xc = -(table.cornerR + d / 2);
        double yc = deltaLen + cornerLen;
        double rc = Math.sqrt((xc - x) * (xc - x) + (yc - y) * (yc - y));
        double fsx = 0, fsy = 0, Mz = 0;
        double c = cornerLen * Math.sin(Math.PI / 4);
        if (rc < (table.cornerR + d / 2)) {
            if (Math.abs(y - yc) <= Math.abs(x - xc)) {
                double dr = (table.cornerR + d / 2) - rc;
                fsx = table.k * (x - xc) / rc * dr / m;
                fsy = table.k * (y - yc) / rc * dr / m;
                Mz += -fsx * d / 2;
                Mz += -fsy * d / 2;
            }
        } else if (y - x - c > 0) {
            double dx = (y - x - c) / 2;
            double dy = dx;
            fsx = table.k * dx / m;
            fsy = -table.k * dy / m;
            Mz += -fsx * d / 2;
            Mz += -fsy * d / 2;
        }
        result[0] = fsx;
        result[1] = fsy;
        result[2] = Mz;
        return result;
    }

    public double[] middleForce(double x, double y) {
        double[] result = new double[3];
        double xc = -table.cornerR - table.middleLen / 2;
        double yc = -table.cornerR - d / 2;
        double rc = Math.sqrt((xc - x) * (xc - x) + (yc - y) * (yc - y));
        double fsx = 0, fsy = 0, Mz = 0;
        if (rc < table.cornerR + d / 2) {
            if (y > yc) {
                double dr = table.cornerR + d / 2 - rc;
                fsx = table.k * (x - xc) / rc * dr / m;
                fsy = table.k * (y - yc) / rc * dr / m;
                Mz += fsx * d / 2;
                Mz += -fsy * d / 2;
            } else {
                fsx = table.k * (x - xc) / m;
            }
        }
        result[0] = fsx;
        result[1] = fsy;
        result[2] = Mz;
        return result;
    }

    public void stepForward() {
        arrayShift(x);
        arrayShift(y);
        arrayShift(wx);
        arrayShift(wy);
        arrayShift(wz);
    }

    public void display(BufferedImage screen) {
        if (pot) {
            return;
        }
        Graphics graphics = screen.getGraphics();
        int height = screen.getHeight();
        int width = screen.getWidth();
        double tw = table.length + table.margin * 2 + d;
        double th = table.width + table.margin * 2 + d;
        int bw = (int) (d / tw * width);
        int bh = (int) (d / th * height);
        int sx = (int) ((x[1] + table.margin) / tw * width);
        int sy = (int) ((th - y[1] - table.margin - d) / th * height);
        if (paint != null) {
            Graphics2D g = (Graphics2D) graphics;
            g.setPaint(paint);
            g.fillOval(sx, sy, bw, bh);
        } else {
            graphics.setColor(Color.WHITE);
            graphics.drawOval(sx, sy, bw, bh);
        }
        if (text != null) {
            graphics.setFont(font);
            FontMetrics fontMetrics = graphics.getFontMetrics(font);
            int h = fontMetrics.getHeight();
            int w = fontMetrics.stringWidth(text);
            graphics.setColor(Color.WHITE);
            sx = sx + bw / 2 - w / 2 - 1;
            sy = sy - bh / 2 + h / 2 + 1;
            bw = w + 2;
            bh = h + 2;
            graphics.fillRect(sx, sy, bw, bh);
        }
    }

    private void arrayShift(double[] x) {
        int n = x.length - 1;
        for (int i = 0; i < n; i++) {
            x[i] = x[i + 1];
        }
    }

    public static void main(String[] args) {
        Table table = new Table(1);
        Arrays.fill(table.balls[0].x, table.length / 2);
        Arrays.fill(table.balls[0].y, table.width / 2);
        table.balls[0].stroke(new double[2], new double[]{0, -800, 0});
        for (int i = 0; i < 100; i++) {
            table.play(table.balls[0].dt * 100);
            System.out.println(table.balls[0].x[1]);
        }

    }
}
