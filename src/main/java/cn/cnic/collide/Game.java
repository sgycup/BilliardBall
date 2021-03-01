package cn.cnic.collide;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.28 028 10:40:36
 */
public class Game {
    public static void main(String[] args) {
        Table table = Table.create8Ball();
        final BufferedImage screen = new BufferedImage(800, 400, BufferedImage.TYPE_INT_RGB);
        table.display(screen);
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.drawImage(screen, 0, 0, null);
            }
        };
        panel.addMouseListener(table.cue);
        panel.addMouseMotionListener(table.cue);
        panel.setPreferredSize(new Dimension(820, 450));

        StrokePoint point = new StrokePoint();
        Strengther strength = new Strengther();
        CueAngle cueAngle = new CueAngle();
        table.cue.point = point;
        table.cue.strengther = strength;
        table.cue.cueAngler = cueAngle;
        point.cue = table.cue;

        JPanel main = new JPanel();
        main.setPreferredSize(new Dimension(820, 450));
        main.add(panel);
        main.add(point);
        main.add(strength);
        main.add(cueAngle);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("台球");
            frame.setPreferredSize(new Dimension(820, 850));
            frame.setContentPane(main);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                table.play(1e-2);
                table.display(screen);
                SwingUtilities.invokeLater(panel::repaint);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
