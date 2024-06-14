package OneFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class S30213P02 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Window::new);
    }
}

class Board
        implements KeyListener {
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

class GameThread
        extends Thread{

    private final List<TickEventListener> tickEventListeners;
    private boolean state;
    private final float difficulty;
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
        this.tickEventListeners = new ArrayList<>();
        this.state = true;
        this.difficulty = 0.5F;
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

    public void reset(Board board) {
        board.addResetEventListener(
                e -> {
                    System.out.println("RESET!");
                    interrupt();
                }
        );
    }

    public void increaseDifficulty() {
        ++this.difficultyMultiplier;
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

class MinusOneGapEvent
        extends EventObject {
    public MinusOneGapEvent(Object source) {
        super(source);
    }
}

interface MinusOneGapListener {
    void onMinusOneGap(MinusOneGapEvent minusOneGapEvent);
}

class PlusOneEvent
        extends EventObject {
    public PlusOneEvent(Object source) {
        super(source);
    }
}

interface PlusOneEventListener {
    void onPlusEvent(PlusOneEvent event);
}

class ResetEvent
        extends EventObject {
    public ResetEvent(Object source) {
        super(source);
    }
}

interface ResetEventListener {
    void onResetEvent(ResetEvent e);
}

class StartEvent
        extends EventObject {
    public StartEvent(Object source) {
        super(source);
    }
}

interface StartEventListener {
    void onStartEvent(StartEvent e);
}

class TickEvent
        extends EventObject {
    public TickEvent(Object source) {
        super(source);
    }
}

interface TickEventListener {
    void onTickEvent(TickEvent e);
}

class AutoslalomTableController {
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

class AutoslalomTableModel
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

class AutoslalomTableView
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
                File file = (File) value;
                try {
                    BufferedImage image = ImageIO.read(file);
                    this.setText("");
                    this.setIcon(new ImageIcon(image));
                    table.setRowHeight(row, image.getHeight());
                } catch (IOException ignore) {}
                return this;
            }
        };
    }
}

class SevenSegmentDigit
        extends JPanel{

    private final int width;
    private final int height;
    private boolean isFirst;
    private PlusOneEventListener plusOneEventListener;
    private MinusOneGapListener minusOneGapListener;

    private int digit;
    private boolean visible;

    private static final boolean[][] digitsStates = {
            // TOP, TOP-LEFT, MID, BOTTOM-LEFT, BOTTOM, TOP-RIGHT, BOTTOM-RIGHT
            {true, true, false, true, true, true, true}, //0
            {false, false, false, false, false, true, true}, //1
            {true, false, true, true, true, true, false}, //2
            {true, false, true, false, true, true, true}, //3
            {false, true, true, false, false, true, true}, //4
            {true, true, true, false, true, false, true}, //5
            {true, true, true, true, true, false, true}, //6
            {true, false, false, false, false, true, true}, //7
            {true, true, true, true, true, true, true}, //8
            {true, true, true, false, true, true, true}, //9

    };

    public SevenSegmentDigit() {
        this(false);
    }

    public SevenSegmentDigit(boolean isFirst) {
        this.digit = 0;
        this.visible = false;
        this.width = 70; // 70px x 124px
        this.height = 124;
        this.setOpaque(false);
        this.setSize(width, height);
        this.isFirst = isFirst;
    }


    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(visible){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            int thickness = this.width / 6;
            if(digitsStates[digit][0]) // TOP
                drawHorizontalSegment(g2d, thickness, 0, thickness, this.width - 2 * thickness);
            if(digitsStates[digit][1]) // TOP-LEFT
                drawVerticalSegment(g2d, 0, thickness, thickness, (this.height - 3 * thickness) / 2);
            if(digitsStates[digit][2]) // MID
                drawHorizontalSegment(g2d, thickness, (this.height - thickness) / 2, thickness, this.width - 2 * thickness);
            if(digitsStates[digit][3]) // BOTTOM-LEFT
                drawVerticalSegment(g2d, 0, (this.height + thickness) / 2, thickness, (this.height - 3 * thickness) / 2);
            if(digitsStates[digit][4]) // BOTTOM
                drawHorizontalSegment(g2d, thickness, this.height - thickness, thickness, this.width - 2 * thickness);
            if(digitsStates[digit][5]) // TOP-RIGHT
                drawVerticalSegment(g2d, this.width - thickness, thickness, thickness, (this.height - 3 * thickness) / 2);
            if(digitsStates[digit][6]) // BOTTOM-RIGHT
                drawVerticalSegment(g2d, this.width - thickness, (this.height + thickness) / 2, thickness, (this.height - 3 * thickness) / 2);
        }
    }

    private void drawVerticalSegment(Graphics2D graphic, int x, int y, int thickness, int length) {
        double offset = thickness / 2.0;
        Path2D.Double outline = new Path2D.Double();
        outline.moveTo(x, y + offset);
        outline.lineTo(x + offset, y);
        outline.lineTo(x + thickness - offset, y);
        outline.lineTo(x + thickness, y + offset);
        outline.lineTo(x + thickness, y + length - offset);
        outline.lineTo(x + thickness - offset, y + length);
        outline.lineTo(x + offset, y + length);
        outline.lineTo(x, y + length - offset);
        outline.closePath();
        graphic.fill(outline);
    }

    private void drawHorizontalSegment(Graphics2D graphic, int x, int y, int thickness, int length) {
        double offset = thickness / 2.0;
        Path2D.Double outline = new Path2D.Double();
        outline.moveTo(x + offset, y);
        outline.lineTo(x + length - offset, y);
        outline.lineTo(x + length, y + offset);
        outline.lineTo(x + length - offset, y + thickness);
        outline.lineTo(x + offset, y + thickness);
        outline.lineTo(x, y + offset);
        outline.closePath();
        graphic.fill(outline);
    }

    public void start(){
        this.digit = 0;
        if(isFirst) {
            this.visible = true;
        }
    }

    public void reset(){
        this.visible = false;
    }

    public void plusOne(){
        if(!visible || isFirst) {
            minusOneGapListener.onMinusOneGap(new MinusOneGapEvent(this));
            this.isFirst = false;
        }
        this.visible = true;
        this.digit++;
        if(this.digit == 10){
            this.digit = 0;
            plusOneEventListener.onPlusEvent(
                    new PlusOneEvent(this)
            );
        }
    }

    public void setPlusOneEventListener(PlusOneEventListener plusOneEventListener) {
        this.plusOneEventListener = plusOneEventListener;
    }

    public void setMinusOneGapListener(MinusOneGapListener minusOneGapListener) {
        this.minusOneGapListener = minusOneGapListener;
    }
}

class Window
        extends JFrame {
    private final Board board;

    public Window(){
        this.setTitle("Autoslalom");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1296, 844);
//        this.setSize(1280, 805);
        this.setResizable(false);
        this.board = new Board();
        this.addKeyListener(board);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        JPanel imgPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage image;
                try {
                    image = ImageIO.read(new File("res\\backgroundAlpha.png"));
                    g.drawImage(image,0,0, this);
                } catch (IOException ignore) {
                    System.out.println("Background load fail!");
                    System.exit(2);
                }
            }
        };
        imgPanel.setSize(new Dimension(1280, 805));
        imgPanel.setOpaque(false);

        AutoslalomTableModel tableModel = new AutoslalomTableModel();
        AutoslalomTableView tableView = new AutoslalomTableView(tableModel);
        AutoslalomTableController tableController = new AutoslalomTableController(tableModel, tableView);
        tableController.setBoard(this.board);
        GameThread.getInstance().setAutoslalomTableController(tableController);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 4));
        {
            SevenSegmentDigit[] digits = new SevenSegmentDigit[]{
                    new SevenSegmentDigit(true),
                    new SevenSegmentDigit(),
                    new SevenSegmentDigit()
            };

            for (SevenSegmentDigit digit : digits) {
                digit.setMinusOneGapListener(e -> this.board.minusOneGap());
            }

            this.board.addStartEventListener(e -> {
                for (SevenSegmentDigit digit : digits) {
                    digit.start();
                }
            });

            this.board.setPlusOneEventListener(e -> {
                digits[0].plusOne();
                GameThread.getInstance().increaseDifficulty();
            });
            digits[0].setPlusOneEventListener(e -> digits[1].plusOne());
            digits[1].setPlusOneEventListener(e -> digits[2].plusOne());
            digits[2].setPlusOneEventListener(e -> this.board.reset());

            this.board.addResetEventListener(e -> {
                for (SevenSegmentDigit digit : digits) {
                    digit.reset();
                }
            });

            JPanel gap = new JPanel();
            gap.setSize(0,124);
            gap.setOpaque(false);

            scorePanel.setBounds(140,50,450,150);
            scorePanel.setOpaque(false);
            scorePanel.add(digits[2]);
            scorePanel.add(gap);
            scorePanel.add(digits[1]);
            scorePanel.add(digits[0]);

        }

        JLayeredPane layeredPane = this.getLayeredPane();
        layeredPane.setSize(this.getSize());
        layeredPane.add(tableView, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(imgPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(scorePanel, JLayeredPane.MODAL_LAYER);
    }
}
