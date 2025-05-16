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
    private int roadsides;
    private int roadsideCounter;
    private int gaps;
    private int tickEventCounter;
    private final List<ResetEventListener> resetEventListeners;
    private final List<StartEventListener> startEventListeners;
    private PlusOneEventListener plusOneEventListener;
    private final GameThread gameThread;


    public Board() {
        this.board = new int[7];
        board[0] = 0b010; // Pozycja początkowa samochodu
        this.roadsides = 0;
        this.roadsideCounter = 0;

        this.resetEventListeners = new ArrayList<>();
        this.startEventListeners = new ArrayList<>();

        this.gameThread = GameThread.getInstance();
        this.gameThread.reset(this);
        this.tickEventCounter = 0;

        this.gameThread.addTickEventListener(
                e -> {
                    detectCollision();
                    if(roadsideCounter % 3 == 0) {
                        roadsides |= 0b1000000;
                        roadsideCounter = 0;
                    }
                    roadsides >>= 1;
                    if(gaps == 0) {
                        if(tickEventCounter % 4 == 0) {
                            generateObstacleRow();
                            tickEventCounter = 0;
                        } else {
                            generateEmptyRow();
                        }
                    } else if(gaps == 1) {
                        if(tickEventCounter % 3 == 0) {
                            generateObstacleRow();
                            tickEventCounter = 0;
                        } else {
                            generateEmptyRow();
                        }
                    } else if (gaps == 2) {
                        if(tickEventCounter % 2 == 0) {
                            generateObstacleRow();
                            tickEventCounter = 0;
                        } else {
                            generateEmptyRow();
                        }
                    } else { // gaps == 3
                        generateObstacleRow();
                    }
                    tickEventCounter++;
                    roadsideCounter++;
                }
        );
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
        this.startEventListeners.add(listener);
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

    public int getRoadsides() {
        return roadsides;
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
        System.out.println("START!");
        roadsides = 0b0100100;
        roadsideCounter = 1;
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
            case 's':{
                start();
                synchronized (gameThread) {
                    gameThread.notify();
                }
                break;
            }
            case 'a': {
                if(board[0] != 0b100){
                    board[0] <<= 1;
                    gameThread.updateCells();
                }
                break;
            }
            case 'd': {
                if(board[0] != 0b001){
                    board[0] >>= 1;
                    gameThread.updateCells();
                }
                break;
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
