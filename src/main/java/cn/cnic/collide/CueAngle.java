package cn.cnic.collide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.27 027 4:24:29
 */
public class CueAngle extends JPanel implements MouseListener{

    public double cueAngle;
    private int xMouse, yMouse;

    public CueAngle() {
        setPreferredSize(new Dimension(200, 200));
        addMouseListener(this);
        xMouse = 199;
        yMouse = 199;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int h = getHeight();
        int w = getWidth();
        int len = Math.min(h, w);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, len, len);
        g.setColor(Color.BLACK);
        g.drawArc(-len, 0, len * 2, len * 2, 0, 85);
        int x0 = (int)(len * Math.cos(Math.toRadians(85)));
        int y0 = (int)(len * Math.sin(Math.toRadians(85)));
        y0 = len - y0;
        g.drawLine(0, len, x0, y0);
        g.drawLine(0, len, len, len);
        double dy = yMouse - len;
        double dx = xMouse;
        double dr = Math.sqrt(dx * dx + dy * dy);
        double theta = Math.acos(dx / dr);
        theta = Math.min(theta, Math.toRadians(85));
        cueAngle = theta;
        int x = (int)(len * Math.cos(theta));
        int y = (int)(len * Math.sin(theta));
        y = len - y;
        g.drawLine(0, len, x, y);
        g.drawString("角度：" + ((int)Math.toDegrees(theta)), len * 3 / 4, len / 5);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
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

    public static void main(String[] args) {
        CueAngle angle = new CueAngle();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("台球");
            frame.setPreferredSize(new Dimension(350, 350));
            frame.setContentPane(angle);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
