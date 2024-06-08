package pres;
import game.*;

import javax.swing.*;
import java.awt.event.KeyAdapter;

public class Window
extends JFrame {
    public Board board;

    public Window(){
        this.setTitle("Autoslalom");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(720, 540);
        this.board = new Board();
        this.addKeyListener(board);
    }
}
