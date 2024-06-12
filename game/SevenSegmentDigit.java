package game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class SevenSegmentDigit
extends JPanel{

    private final int width;
    private final int height;
    private StartEventListener startEventListener;
    private PlusOneEventListener plusOneEventListener;

    private int score;
    private int digit;
    private boolean visible;

    private static final boolean[][] digitsStates = {
        // TOP, TOP-LEFT, MID, BOTTOM-LEFT, BOTTOM, TOP-RIGHT, BOTTOM-RIGHT
        {true, true, false, true, true, true, true}, //0
        {false, false, false, false, false, true, true}, //1
        {true, false, true, true, true, true, false}, //2
        {true, false, true, false, true, true, true}, //3
        {false, true, true, false, false, true, true}, //4
        {true, true, true, false, true, false, true}, //5
        {true, true, true, true, true, false, true}, //6
        {true, false, false, false, false, true, true}, //7
        {true, true, true, true, true, true, true}, //8
        {true, true, true, false, true, true, true}, //9

    };

    public SevenSegmentDigit() {
        this.digit = 0;
        this.visible = false;
        this.width = 70; // 70px x 124px
        this.height = 124;
        this.setOpaque(false);
        this.setSize(width, height);
    }


    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(visible){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            int thickness = this.width / 6;
            if(digitsStates[digit][0]) // TOP
                drawHorizontalSegment(g2d, thickness, 0, thickness, this.width - 2 * thickness);
            if(digitsStates[digit][1]) // TOP-LEFT
                drawVerticalSegment(g2d, 0, thickness, thickness, (this.height - 3 * thickness) / 2);
            if(digitsStates[digit][2]) // MID
                drawHorizontalSegment(g2d, thickness, (this.height - thickness) / 2, thickness, this.width - 2 * thickness);
            if(digitsStates[digit][3]) // BOTTOM-LEFT
                drawVerticalSegment(g2d, 0, (this.height + thickness) / 2, thickness, (this.height - 3 * thickness) / 2);
            if(digitsStates[digit][4]) // BOTTOM
                drawHorizontalSegment(g2d, thickness, this.height - thickness, thickness, this.width - 2 * thickness);
            if(digitsStates[digit][5]) // TOP-RIGHT
                drawVerticalSegment(g2d, this.width - thickness, thickness, thickness, (this.height - 3 * thickness) / 2);
            if(digitsStates[digit][6]) // BOTTOM-RIGHT
                drawVerticalSegment(g2d, this.width - thickness, (this.height + thickness) / 2, thickness, (this.height - 3 * thickness) / 2);
        }
    }

    private void drawVerticalSegment(Graphics2D graphic, int x, int y, int thickness, int length) {
        double offset = thickness / 2.0;
        Path2D.Double outline = new Path2D.Double();
        outline.moveTo(x, y + offset);
        outline.lineTo(x + offset, y);
        outline.lineTo(x + thickness - offset, y);
        outline.lineTo(x + thickness, y + offset);
        outline.lineTo(x + thickness, y + length - offset);
        outline.lineTo(x + thickness - offset, y + length);
        outline.lineTo(x + offset, y + length);
        outline.lineTo(x, y + length - offset);
        outline.closePath();
        graphic.fill(outline);
    }

    private void drawHorizontalSegment(Graphics2D graphic, int x, int y, int thickness, int length) {
        double offset = thickness / 2.0;
        Path2D.Double outline = new Path2D.Double();
        outline.moveTo(x + offset, y);
        outline.lineTo(x + length - offset, y);
        outline.lineTo(x + length, y + offset);
        outline.lineTo(x + length - offset, y + thickness);
        outline.lineTo(x + offset, y + thickness);
        outline.lineTo(x, y + offset);
        outline.closePath();
        graphic.fill(outline);
    }

    public void start(){
        this.visible = false;
        this.digit = 0;
    }

    public void plusOne(){
        if(!visible){
            GameThread.getInstance().getBoard().sevenSegmentNonZerosPlus();
        }
        this.visible = true;
        this.digit++;
        if(this.digit == 10){
            this.digit = 0;
            plusOneEventListener.onPlusEvent(
                    new PlusOneEvent(this)
            );
        }
    }

    public void setPlusOneEventListener(PlusOneEventListener plusOneEventListener) {
        this.plusOneEventListener = plusOneEventListener;
    }
}
