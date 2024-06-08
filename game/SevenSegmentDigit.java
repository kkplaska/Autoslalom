package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SevenSegmentDigit
extends JPanel{

    int num;
    boolean visible;

    public SevenSegmentDigit() {
        this.num = 0;
        this.visible = false;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
