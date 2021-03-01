package cn.cnic.collide;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.27 027 5:38:27
 */
public class Strengther extends JSlider implements ChangeListener {

    public double strength = 50;

    public Strengther() {
        addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        strength = ((JSlider)e.getSource()).getValue();
    }
}
