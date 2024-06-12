package game;
import pres.*;

import java.util.ArrayList;
import java.util.List;

public class GameThread
extends Thread{

    private final List<TickEventListener> tickEventListeners;
    private boolean state;
    private Board board;
    private int difficulty = 0;
    private AutoslalomTableController autoslalomTableController;

    // SINGLETON
    private static GameThread instance;
    public static GameThread getInstance(){
        if(instance == null){
            instance = new GameThread();
        }
        return instance;
    }

    private GameThread(){
        this.tickEventListeners = new ArrayList<TickEventListener>();
        this.state = true;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        while(true){
            try {
                if(state){
                    state = false;
                    synchronized (this) { this.wait(); }
                }
                Thread.sleep(500 - (100L * difficulty));
                tick();
            } catch (InterruptedException e) {
                System.out.println("KONIEC GRY");
                state = true;
            }
        }
    }

    public void setBoard(Board board) {
        board.addResetEventListener(
                new ResetEventListener(){
                    @Override
                    public void onResetEvent(ResetEvent e) {
                        System.out.println("RESET!");
                        interrupt();
                    }
                }
        );
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setAutoslalomTableController(AutoslalomTableController autoslalomTableController) {
        this.autoslalomTableController = autoslalomTableController;
    }

    public void addTickEventListener(TickEventListener listener){
        tickEventListeners.add(listener);
    }

    public void tick(){
        TickEvent tickEvent = new TickEvent(this);
        for(TickEventListener listener : tickEventListeners){
            listener.onTickEvent(tickEvent);
        }
        autoslalomTableController.updateCells();
    }
}
