package pres;
import game.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Window
extends JFrame {
    private final Board board;

    public Window(){
        this.setTitle("Autoslalom");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1296, 844);
//        this.setSize(1280, 805);
        this.setResizable(false);
        this.board = new Board();
        this.addKeyListener(board);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        JPanel imgPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage image;
                try {;
                    image = ImageIO.read(new File("res\\backgroundAlpha.png"));
                    g.drawImage(image,0,0, this);
                } catch (IOException ignore) {
                    System.out.println("Background load fail!");
                    System.exit(2);
                }
            }
        };
        imgPanel.setSize(new Dimension(1280, 805));
        imgPanel.setOpaque(false);

        AutoslalomTableModel tableModel = new AutoslalomTableModel();
        AutoslalomTableView tableView = new AutoslalomTableView(tableModel);
        AutoslalomTableController tableController = new AutoslalomTableController(tableModel, tableView);
        tableController.setBoard(this.board.getBoard());
        GameThread.getInstance().setAutoslalomTableController(tableController);

        JLayeredPane layeredPane = this.getLayeredPane();
        layeredPane.setSize(this.getSize());
        layeredPane.add(tableView, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(imgPanel, JLayeredPane.PALETTE_LAYER);

    }
}
