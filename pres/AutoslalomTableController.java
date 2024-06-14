package pres;

import game.Board;
import java.io.File;

public class AutoslalomTableController {
    private final AutoslalomTableModel tableModel;
    private final AutoslalomTableView tableView;
    private Board board;

    public AutoslalomTableController(AutoslalomTableModel tableModel, AutoslalomTableView tableView) {
        this.tableModel = tableModel;
        this.tableView = tableView;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void updateCells() {
        for (int i = 0; i < this.board.getBoard().length; i++) {
            int j = 7 - 1 - i;
            boolean roadside;
            if(i == 0){
                roadside = (this.board.getRoadsides() & 1) == 1;
            } else {
                roadside = (this.board.getRoadsides() >> (i - 1) & 1) == 1;
            }
            String comparer = roadside ? this.board.getBoard()[i] + "R" : this.board.getBoard()[i] + "N";
//            System.out.println(i + ": " + getPosition((File)(tableModel.getValueAt(j, 0))) + " " + comparer + " " + !getPosition((File) (tableModel.getValueAt(j, 0))).equals(comparer));

            if(!getPosition((File) (tableModel.getValueAt(j, 0))).equals(comparer)){
                tableModel.setValueAt(getFile(i, this.board.getBoard()[i], roadside), j, 0);
            }
        }
//        System.out.println();
    }

    private String getPosition (File file){
        return file.getName().substring(file.getName().length() - 6).substring(0,2);
    }

    private File getFile(int r, int n, boolean roadside){
        return roadside ?
                new File("res\\board" + r + "\\board" + r + "_" + n + "R.png")
                : new File("res\\board" + r + "\\board" + r + "_" + n + "N.png");
    }
}
