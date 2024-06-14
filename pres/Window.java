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
                try {
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
        tableController.setBoard(this.board);
        GameThread.getInstance().setAutoslalomTableController(tableController);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 4));
        {
            SevenSegmentDigit[] digits = new SevenSegmentDigit[]{
                    new SevenSegmentDigit(true),
                    new SevenSegmentDigit(),
                    new SevenSegmentDigit()
            };

            for (SevenSegmentDigit digit : digits) {
                digit.setMinusOneGapListener(e -> this.board.minusOneGap());
            }

            this.board.addStartEventListener(e -> {
                for (SevenSegmentDigit digit : digits) {
                    digit.start();
                }
            });

            this.board.setPlusOneEventListener(e -> {
                digits[0].plusOne();
                GameThread.getInstance().increaseDifficulty();
            });
            digits[0].setPlusOneEventListener(e -> digits[1].plusOne());
            digits[1].setPlusOneEventListener(e -> digits[2].plusOne());
            digits[2].setPlusOneEventListener(e -> this.board.reset());

            this.board.addResetEventListener(e -> {
                for (SevenSegmentDigit digit : digits) {
                    digit.reset();
                }
            });

            JPanel gap = new JPanel();
            gap.setSize(0,124);
            gap.setOpaque(false);

            scorePanel.setBounds(140,50,450,150);
            scorePanel.setOpaque(false);
            scorePanel.add(digits[2]);
            scorePanel.add(gap);
            scorePanel.add(digits[1]);
            scorePanel.add(digits[0]);

        }

        JLayeredPane layeredPane = this.getLayeredPane();
        layeredPane.setSize(this.getSize());
        layeredPane.add(tableView, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(imgPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(scorePanel, JLayeredPane.MODAL_LAYER);

    }
}
