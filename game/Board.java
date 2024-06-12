package game;

import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Board
implements KeyListener{
    // Samochód
    // board[0] - samochód
    // 100 = 4 <- lewy pas
    // 010 = 2 <- środkowy pas
    // 001 = 1 <- prawy pas

    // Przeszkody
    // board[1] - board[6]
    // 0b110 = 6
    // 0b101 = 5
    // 0b100 = 4
    // 0b011 = 3
    // 0b010 = 2
    // 0b001 = 1
    // 0b000 = 0

    private final int[] board;
    private SevenSegmentDigit[] sevenSegmentDigits;
    private int obstacleCounter;
    private int tickEventCounter;
    private ResetEventListener resetEventListener;
    private final GameThread gameThread;
    private TableModel autoslalomTableModel;


    public Board() {
        this.board = new int[7];
        board[0] = 0b010; // Pozycja początkowa samochodu

        this.sevenSegmentDigits = new SevenSegmentDigit[3];
        this.gameThread = GameThread.getInstance();
        gameThread.setBoard(this);
        obstacleCounter = 0;
        tickEventCounter = 0;

        gameThread.addTickEventListener(
                new TickEventListener() {
                    @Override
                    public void onTickEvent(TickEvent e) {
                        detectCollision();
                        if(obstacleCounter < 1) {
                            if(tickEventCounter % 4 == 0) {
                                generateObstacleRow();
                            } else {
                                generateEmptyRow();
                            }
                        } else if(obstacleCounter < 10) {
                            if(tickEventCounter % 3 == 0) {
                                generateObstacleRow();
                            } else {
                                generateEmptyRow();
                            }
                        } else if (obstacleCounter < 100) {
                            if(tickEventCounter % 2 == 0) {
                                generateObstacleRow();
                            } else {
                                generateEmptyRow();
                            }
                        } else {
                            generateObstacleRow();
                        }
                        consoleTrack();
                        tickEventCounter++;
                    }
                }
        );
    }

    private void consoleTrack(){
        StringBuilder sb = new StringBuilder();
        for (int i = 6; i > 0; i--) {
            switch (board[i]) {
                case 0b110 -> sb.append("|== |");
                case 0b101 -> sb.append("|= =|");
                case 0b100 -> sb.append("|=  |");
                case 0b011 -> sb.append("| ==|");
                case 0b010 -> sb.append("| = |");
                case 0b001 -> sb.append("|  =|");
                case 0b000 -> sb.append("|   |");
            }
            sb.append("\n");
        }
        switch (board[0]){
            case 0b100 -> sb.append("|A  |");
            case 0b010 -> sb.append("| A |");
            case 0b001 -> sb.append("|  A|");
        }
        sb.append("\n");
        System.out.println(sb.toString());
    }

    public void generateEmptyRow(){
        moveObstacles();
        board[board.length - 1] = 0b000;
    }

    public void generateObstacleRow(){
        int newObstacle = (int)(Math.random() * 6) + 1;
        if(((board[board.length - 1] & 0b1) == 0
                || (board[board.length - 1] & 0b100) == 0)
            && board[board.length - 1] != 0b000){
            while((newObstacle & 0b10) != 0){
                newObstacle = (int)(Math.random() * 6) + 1;
            }
        }
        moveObstacles();
        board[board.length - 1] = newObstacle;
    }

    public void moveObstacles(){
        for(int i = 2; i < board.length; i++){
            board[i - 1] = board[i];
        }
    }

    public void setResetEventListener(ResetEventListener resetEventListener) {
        this.resetEventListener = resetEventListener;
    }

    public int[] getBoard() {
        return board;
    }

    public void detectCollision(){
        if((board[0] & board[1]) != 0){
            System.out.println("Collision detected!");
            reset();
        } else if (board[1] != 0) {
            obstacleCounter++;
        }
    }

    public void reset(){
        obstacleCounter = 0;
        tickEventCounter = 0;
        ResetEvent resetEvent = new ResetEvent(this);
        resetEventListener.onResetEvent(resetEvent);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 's' -> {
                gameThread.start();
            }
            case 'a' -> {
                if(board[0] != 0b100){
                    board[0] <<= 1;
                }
            }
            case 'd' -> {
                if(board[0] != 0b001){
                    board[0] >>= 1;
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
