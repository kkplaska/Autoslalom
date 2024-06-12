package game;
import pres.*;

import java.util.ArrayList;
import java.util.List;

public class GameThread
extends Thread{

    private List<TickEventListener> tickEventListeners;
    private Board board;
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
        tickEventListeners = new ArrayList<TickEventListener>();
    }

    @Override
    public void run() {
        super.run();
        while(true){
            try {
                Thread.sleep(1500);
                tick();
            } catch (InterruptedException e) {
                System.out.println("KONIEC GRY");
                System.exit(0);
            }
        }
    }

    public void setBoard(Board board) {
        board.setResetEventListener(
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

    public void setAutoslalomTableController(AutoslalomTableController autoslalomTableController) {
        this.autoslalomTableController = autoslalomTableController;
    }

    public void addTickEventListener(TickEventListener listener){
        tickEventListeners.add(listener);
    }
    public void removeTickEventListener(TickEventListener listener){
        tickEventListeners.remove(listener);
    }
    public void tick(){
        TickEvent tickEvent = new TickEvent(this);
        for(TickEventListener listener : tickEventListeners){
            listener.onTickEvent(tickEvent);
        }
        autoslalomTableController.updateCells();
    }
}
