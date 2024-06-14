package pres;

import javax.swing.table.AbstractTableModel;
import java.io.File;

public class AutoslalomTableModel
extends AbstractTableModel {

    private final File[] data;

    public AutoslalomTableModel() {
        this.data = new File[]{
                new File("res\\board6\\board6_0N.png"),
                new File("res\\board5\\board5_0N.png"),
                new File("res\\board4\\board4_0N.png"),
                new File("res\\board3\\board3_0N.png"),
                new File("res\\board2\\board2_0N.png"),
                new File("res\\board1\\board1_0N.png"),
                new File("res\\board0\\board0_2N.png")
        };
    }

    @Override
    public int getRowCount() {
        return 7;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        this.data[rowIndex] = (File) aValue;
    }
}
