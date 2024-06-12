package pres;
import game.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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

        JLayeredPane layeredPane = this.getLayeredPane();
        layeredPane.setSize(this.getSize());

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
        {
            imgPanel.setSize(new Dimension(1280, 805));
            imgPanel.setOpaque(false);
        }


        DefaultTableModel defaultTableModel = new DefaultTableModel(new String[]{"IMGs"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] defaultIMGsFilePaths = {
                "res\\board6\\board6_000.png",
                "res\\board5\\board5_000.png",
                "res\\board4\\board4_000.png",
                "res\\board3\\board3_000.png",
                "res\\board2\\board2_000.png",
                "res\\board1\\board1_000.png",
                "res\\board0\\board0_010.png"
        };

        for (String imgFilePath : defaultIMGsFilePaths) {
            defaultTableModel.addRow(new Object[]{imgFilePath});
        }

        DefaultTableCellRenderer imageCellRenderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String filePath = (String) value;
                try {
                    BufferedImage image = ImageIO.read(new File(filePath));
                    label.setText("");
                    label.setIcon(new ImageIcon(image));
                    table.setRowHeight(row, image.getHeight());
                } catch (IOException ignore) {}
                return label;
            }
        };


        JTable table = new JTable(defaultTableModel);
        {
            table.setSize(new Dimension(1280, 805));
            table.setIntercellSpacing(new Dimension(0, 0));
            table.setShowGrid(false);
            table.setFocusable(false);
            table.setRowSelectionAllowed(false);
            table.getColumnModel().getColumn(0).setWidth(924);
            table.getColumnModel().getColumn(0).setCellRenderer(imageCellRenderer);
        }

        layeredPane.add(table, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(imgPanel, JLayeredPane.PALETTE_LAYER);

    }
}
