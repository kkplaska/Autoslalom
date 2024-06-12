package pres;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AutoslalomTableView
extends JPanel {
    private JTable table;
    private final TableModel tableModel;
    private DefaultTableCellRenderer imageCellRenderer;

    public AutoslalomTableView(TableModel tableModel) {
        this.tableModel = tableModel;
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1280, 805));
        prepareImageCellRenderer();
        prepareTable();
        this.add(table, BorderLayout.CENTER);
    }

    private void prepareTable(){
        this.table = new JTable(tableModel);
        this.table.setSize(new Dimension(1280, 805));
        this.table.setIntercellSpacing(new Dimension(0, 0));
        this.table.setShowGrid(false);
        this.table.setFocusable(false);
        this.table.setRowSelectionAllowed(false);
        this.table.getColumnModel().getColumn(0).setWidth(924);
        this.table.getColumnModel().getColumn(0).setCellRenderer(imageCellRenderer);
    }

    private void prepareImageCellRenderer(){
        this.imageCellRenderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                File file = (File) value;
                try {
                    BufferedImage image = ImageIO.read(file);
                    label.setText("");
                    label.setIcon(new ImageIcon(image));
                    table.setRowHeight(row, image.getHeight());
                } catch (IOException ignore) {}
                return label;
            }
        };
    }

    public JTable getTable() {
        return table;
    }
}
