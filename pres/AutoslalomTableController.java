package pres;

import javax.swing.*;
import java.io.File;

public class AutoslalomTableController {
    private final AutoslalomTableModel tableModel;
    private AutoslalomTableView tableView;
    private int[] board;

    public AutoslalomTableController(AutoslalomTableModel tableModel, AutoslalomTableView tableView) {
        this.tableModel = tableModel;
        this.tableView = tableView;
    }

    public void setBoard(int[] board) {
        this.board = board;
    }

    public void updateCells() {
        for (int i = 0; i < board.length; i++) {
            int j = 7 - 1 - i;
            if(getPosition((File)(tableModel.getValueAt(j,0))) != board[i]){
                tableModel.setValueAt(getFile(i, board[i]), j, 0);
            }
        }
    }

    private int getPosition (File file){
        return Integer.parseInt(file.getName().substring(file.getName().length() - 5).substring(0,1));
    }

    private File getFile(int r, int n){
//        System.out.println(r+" : "+n);
        return new File("res\\board" + r + "\\board" + r + "_" + n + ".png");
    }
}
