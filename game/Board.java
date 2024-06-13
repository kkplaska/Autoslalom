package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class Board
implements KeyListener{
    // Samochód board[0]
    // Przeszkody board[1] - board[6]
    // 0b110 = 6
    // 0b101 = 5
    // 0b100 = 4 <- lewy pas
    // 0b011 = 3
    // 0b010 = 2 <- środkowy pas
    // 0b001 = 1 <- prawy pas
    // 0b000 = 0

    private final int[] board;
    private int gaps;
    private int tickEventCounter;
    private final List<ResetEventListener> resetEventListeners;
    private final List<StartEventListener> startEventListeners;
    private PlusOneEventListener plusOneEventListener;
    private final GameThread gameThread;


    public Board() {
        this.board = new int[7];
        board[0] = 0b010; // Pozycja początkowa samochodu

        this.resetEventListeners = new ArrayList<ResetEventListener>();
        this.startEventListeners = new ArrayList<StartEventListener>();

        this.gameThread = GameThread.getInstance();
        gameThread.setBoard(this);
        tickEventCounter = 0;


        gameThread.addTickEventListener(
                e -> {
                    detectCollision();
                    if(gaps == 0) {
                        if(tickEventCounter % 5 == 0) {
                            generateObstacleRow();
                            tickEventCounter = 0;
                        } else {
                            generateEmptyRow();
                        }
                    } else if(gaps == 1) {
                        if(tickEventCounter % 4 == 0) {
                            generateObstacleRow();
                            tickEventCounter = 0;
                        } else {
                            generateEmptyRow();
                        }
                    } else if (gaps == 2) {
                        if(tickEventCounter % 3 == 0) {
                            generateObstacleRow();
                            tickEventCounter = 0;
                        } else {
                            generateEmptyRow();
                        }
                    } else { // sevenSegmentNonZeros == 3
                        if(tickEventCounter % 2 == 0) {
                            generateObstacleRow();
                            tickEventCounter = 0;
                        } else {
                            generateEmptyRow();
                        }
                    }
                    tickEventCounter++;
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
        System.out.println(sb);
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

    public void minusOneGap (){
        this.gaps++;
    }

    public void addStartEventListener (StartEventListener listener){
        startEventListeners.add(listener);
    }

    public void addResetEventListener(ResetEventListener listener) {
        this.resetEventListeners.add(listener);
    }

    public void setPlusOneEventListener(PlusOneEventListener listener) {
        this.plusOneEventListener = listener;
    }

    public int[] getBoard() {
        return board;
    }

    public void detectCollision(){
        if((board[0] & board[1]) != 0){
            System.out.println("Collision detected!");
            this.reset();
        } else if (board[1] != 0) {
            this.plusOneEventListener.onPlusEvent(new PlusOneEvent(this));
        }
    }

    public void start(){
        board[0] = 2;
        for (int i = 1; i < board.length; i++) {
            board[i] = 0;
        }
        StartEvent startEvent = new StartEvent(this);
        for(StartEventListener listener : startEventListeners){
            listener.onStartEvent(startEvent);
        }
    }

    public void reset(){
        gaps = 0;
        tickEventCounter = 0;
        ResetEvent resetEvent = new ResetEvent(this);
        for (ResetEventListener listener : resetEventListeners) {
            listener.onResetEvent(resetEvent);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 's' -> {
                start();
                synchronized (gameThread) {
                    gameThread.notify();
                }}
            case 'a' -> {
                if(board[0] != 0b100){
                    board[0] <<= 1;
                    gameThread.updateCells();
                }
            }
            case 'd' -> {
                if(board[0] != 0b001){
                    board[0] >>= 1;
                    gameThread.updateCells();
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
