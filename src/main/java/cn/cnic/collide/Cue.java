package cn.cnic.collide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.25 025 22:53:06
 */
public class Cue implements MouseListener, MouseMotionListener {

    public double maxStrength = 100;
    public double maxCueAngle = Math.toRadians(85);// 85度
    public double maxVelocity = 5;

    public Table table;
    public Ball white;
    public StrokePoint point;
    public Strengther strengther;
    public CueAngle cueAngler;
    private int xMouse, yMouse, height, width;
    private boolean reset = false;
    // 力度与球杆角度
    public double strength = 100, cueAngle = 0;
    // 击球点位置（中心为原点的半径与角度）
    public double strokeR = 0, strokeAngle = 0;

    public void display(BufferedImage screen) {
        if (white.pot) {
            return;
        }
        Graphics graphics = screen.getGraphics();
        int height = screen.getHeight();
        int width = screen.getWidth();
        this.height = height;
        this.width = width;
        graphics.setColor(Color.WHITE);
        double d = white.d;
        double tw = table.length + table.margin * 2 + d;
        double th = table.width + table.margin * 2 + d;
        int sx = (int) ((white.x[1] + table.margin + d / 2) / tw * width);
        int sy = (int) ((th - white.y[1] - table.margin - d / 2) / th * height);
        graphics.drawLine(sx, sy, xMouse, yMouse);
        int bw = (int) (d / tw * width);
        int bh = (int) (d / th * height);
        graphics.drawOval(xMouse - bw / 2, yMouse - bh / 2, bw, bh);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (reset) {
            reset = false;
            return;
        }
        strokeR = point.r;
        strokeAngle = point.theta;
        strength = strengther.strength;
        cueAngle = cueAngler.cueAngle;
        double d = white.d;
        double tw = table.length + table.margin * 2 + d;
        double th = table.width + table.margin * 2 + d;
        double xw = white.x[1];
        double yw = white.y[1];
        double x = (double) xMouse / width * tw - table.margin - d / 2;
        double y = th - (double) yMouse / height * th - table.margin - d / 2;
        double r = Math.sqrt((y - yw) * (y - yw) + (x - xw) * (x - xw));
        double theta = Math.acos((x - xw) / r);
        if ((y - yw) < 0) {
            theta = 2 * Math.PI - theta;
        }
        if (r == 0) {
            theta = 0;
        }
        double v = strength / maxStrength * maxVelocity;
        double w = white.m * v * strokeR / white.J;
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        v = v * Math.cos(cueAngle);
        double vx = v * cos;
        double vy = v * sin;
        cos = Math.cos(theta - Math.PI / 2);
        sin = Math.sin(theta - Math.PI / 2);

        // 求解转速在自身坐标系中各分量的值
        double coss = Math.cos(strokeAngle);
        double sins = Math.sin(strokeAngle);
        double cosc = Math.cos(cueAngle);
        double sinc = Math.sin(cueAngle);

        double wxp = -w * sins;
        double wyp = 0;
        double wz = w * coss;
        double wxpp = wxp;
        double wypp = wz * sinc;
        double wzp = wz * cosc;

        double wx = wxpp * cos - wypp * sin;
        double wy = wxpp * sin + wypp * cos;
        wz = wzp;

        white.stroke(new double[]{vx, vy}, new double[]{wx, wy, wz});
    }

    @Override
    public void mousePressed(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
        if (white.pot || reset) {
            white.pot = false;
            reset = true;
            double d = white.d;
            double tw = table.length + table.margin * 2 + d;
            double th = table.width + table.margin * 2 + d;
            double x = (double) xMouse / width * tw - table.margin - d / 2;
            double y = th - (double) yMouse / height * th - table.margin - d / 2;
            if (x < 0) x = 0;
            if (x > table.length) x = table.length;
            if (y < 0) y = 0;
            if (y > table.width) y = table.width;
            Arrays.fill(white.x, x);
            Arrays.fill(white.y, y);
            Arrays.fill(white.wx, 0);
            Arrays.fill(white.wy, 0);
            Arrays.fill(white.wz, 0);
        }
    }
}
