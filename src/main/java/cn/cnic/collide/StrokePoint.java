package cn.cnic.collide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.26 026 16:33:09
 */
public class StrokePoint extends JPanel implements MouseListener, MouseMotionListener {

    Cue cue;
    public double r = 0, theta = 0;
    private int xMouse, yMouse;

    public StrokePoint() {
        setPreferredSize(new Dimension(200, 200));
        addMouseListener(this);
        addMouseMotionListener(this);
        xMouse = 100;
        yMouse = 100;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int h = getHeight();
        int w = getWidth();
        int len = Math.min(h, w);
        g.setColor(Color.WHITE);
        g.fillOval(0, 0, len, len);
        g.setColor(Color.red);
        double dr = Math.sqrt((xMouse - len / 2) * (xMouse - len / 2) + (yMouse - len / 2) * (yMouse - len / 2));
        if (dr > len / 2) {
            double theta = Math.asin((xMouse - len / 2) / dr);
            if ((yMouse - len / 2) < 0) {
                theta = Math.PI - theta;
            }
            yMouse = (int) (len / 2 * Math.cos(theta)) + len / 2;
            xMouse = (int) (len / 2 * Math.sin(theta)) + len / 2;
        }
        int lx1 = xMouse - 10;
        int ly1 = yMouse;
        int lx2 = xMouse + 10;
        int ly2 = yMouse;
        g.drawLine(lx1, ly1, lx2, ly2);
        lx1 = xMouse;
        ly1 = yMouse - 10;
        lx2 = xMouse;
        ly2 = yMouse + 10;
        g.drawLine(lx1, ly1, lx2, ly2);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
        int h = getHeight();
        int w = getWidth();
        int len = Math.min(h, w);
        double r = (double) len / 2;
        double dr = Math.sqrt((xMouse - r) * (xMouse - r) + (yMouse - r) * (yMouse - r));
        double theta = Math.acos((xMouse - r) / dr);
        if ((r - yMouse) < 0) {
            theta = 2 * Math.PI - theta;
        }
        dr = Math.min(dr, r);
        double ballr = cue.white.d / 2;
        this.r = dr / r * ballr;
        this.theta = theta;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

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

    }

    public static void main(String[] args) {
        StrokePoint strokePoint = new StrokePoint();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("台球");
            frame.setPreferredSize(new Dimension(200, 200));
            frame.setContentPane(strokePoint);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
