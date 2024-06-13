package game;
import pres.*;

import java.util.ArrayList;
import java.util.List;

public class GameThread
extends Thread{

    private final List<TickEventListener> tickEventListeners;
    private boolean state;
    private final float difficulty = 2.5F;
    private final int defaultSpeed;
    private int difficultyMultiplier;
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
        this.defaultSpeed = 600;
        this.difficultyMultiplier = 0;
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
                    this.difficultyMultiplier = 0;
                }
                Thread.sleep((int)(this.defaultSpeed - (this.difficulty * this.difficultyMultiplier)));
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
    }

    public void increaseDifficulty() {
        ++this.difficultyMultiplier;
        System.out.println(this.difficultyMultiplier);
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
        updateCells();
    }

    public void updateCells(){
        autoslalomTableController.updateCells();
    }
}
