package game;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

interface SnkC {
    public static int PWIDTH=30;
    public static int PHEIGHT=20;
    public static int WIDTH=20;
}

public class Game {
    
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch(Exception e){}
        SnakeFrame f=new SnakeFrame();
        f.setVisible(true);
    }
    
}

abstract class Shape {
    protected int x;
    protected int y;
    
    public Shape(int x,int y){
        this.x=x;
        this.y=y;
    }
    
    public int getX(){return x;}
    public int getY(){return y;}
    public abstract void draw(Graphics g);
}
class Square extends Shape {
    public Square(int x,int y){
        super(x,y);
    }
    
    @Override
    public void draw(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect(SnkC.WIDTH*x,SnkC.WIDTH*y,SnkC.WIDTH,SnkC.WIDTH);
    }
}
class Food extends Shape {
    public Food(int x,int y){
        super(x,y);
    }
    
    @Override
    public void draw(Graphics g){
        g.setColor(Color.RED);
        g.fillOval(SnkC.WIDTH*x+SnkC.WIDTH/4,SnkC.WIDTH*y+SnkC.WIDTH/4,SnkC.WIDTH/2,SnkC.WIDTH/2);
    }
}
enum Direction{UP,DOWN,RIGHT,LEFT}
class Snake {
    private Direction dir=Direction.RIGHT;
    ArrayList<Square> Squares=new ArrayList<>();
    
    public Snake(){
        Square s=new Square(SnkC.PWIDTH/2,SnkC.PHEIGHT/2);
        Squares.add(s);
        for(int i=0;i<3;i++)
            grow();
    }
    
    public void draw(Graphics g){
        for(int i=0;i<Squares.size();i++)
            Squares.get(i).draw(g);
    }
    public void grow(){
        Square s;
        switch(dir){
            case UP:s=new Square(Squares.get(0).getX(),(Squares.get(0).getY()-1)==-1? SnkC.PHEIGHT-1:Squares.get(0).getY()-1); break;
            case DOWN:s=new Square(Squares.get(0).getX(),(Squares.get(0).getY()+1)==SnkC.PHEIGHT? 0:Squares.get(0).getY()+1); break;
            case RIGHT:s=new Square((Squares.get(0).getX()+1)==SnkC.PWIDTH? 0:Squares.get(0).getX()+1,Squares.get(0).getY()); break;
            default:s=new Square((Squares.get(0).getX()-1)==-1? SnkC.PWIDTH-1:Squares.get(0).getX()-1,Squares.get(0).getY()); break;
        }
        Squares.add(0,s);
    }
    public void move(){
        Squares.remove(Squares.size()-1);
        grow();
    }
    public boolean hasCollisionWith(int x,int y,int start){
        for(int i=start;i<Squares.size();i++)
            if(Squares.get(i).getX()==x&&Squares.get(i).getY()==y)
                return true;
        return false;
    }
    public void setDirection(Direction d){
        if(!(isOppositeDirection(d)))
            dir=d;
    }
    private boolean isOppositeDirection(Direction d){
        Direction opp;
        switch(dir){
            case UP:opp=Direction.DOWN; break;
            case DOWN:opp=Direction.UP; break;
            case RIGHT:opp=Direction.LEFT; break;
            default:opp=Direction.RIGHT; break;
        }
        return (d==opp);
    }
}
class SnakePanel extends JPanel {
    private Random rand=new Random();
    Snake snake=new Snake();
    Food food;
    
    public SnakePanel(){
        generateFood();
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        snake.draw(g);
        food.draw(g);
    }
    public void generateFood(){
        Food f=new Food(rand.nextInt(SnkC.PWIDTH),rand.nextInt(SnkC.PHEIGHT));
        while(snake.hasCollisionWith(f.getX(),f.getY(),0))
            f=new Food(rand.nextInt(SnkC.PWIDTH),rand.nextInt(SnkC.PHEIGHT));
        food=f;
    }
}
class SnakeFrame extends JFrame {
    private SnakePanel pnlSnake=new SnakePanel();
    private JPanel pnlBtm=new JPanel();
    private JLabel lblScore=new JLabel("Score: 0");
    private JLabel lblLevel=new JLabel("Level: 1");
    private javax.swing.Timer t;
    private int score=0;
    private int level=1;
    private int count=0;
    private int delay=100;
    
    public SnakeFrame(){
        init();
    }
    private void init(){
        setTitle("Snake 1.0");
        setLocation(100,100);
        setResizable(false);
        setDefaultCloseOperation(3);
        Container c=getContentPane();
        c.add(pnlSnake);
        c.add(pnlBtm,BorderLayout.SOUTH);
        lblScore.setPreferredSize(new Dimension(100,30));
        lblLevel.setPreferredSize(new Dimension(100,30));
        pnlBtm.add(lblScore);
        pnlBtm.add(lblLevel);
        pnlSnake.setBackground(Color.YELLOW);
        pnlSnake.setPreferredSize(new Dimension(SnkC.PWIDTH*SnkC.WIDTH,SnkC.PHEIGHT*SnkC.WIDTH));
        pack();
        
        pnlSnake.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_UP:pnlSnake.snake.setDirection(Direction.UP); break;
                    case KeyEvent.VK_DOWN:pnlSnake.snake.setDirection(Direction.DOWN); break;
                    case KeyEvent.VK_RIGHT:pnlSnake.snake.setDirection(Direction.RIGHT); break;
                    case KeyEvent.VK_LEFT:pnlSnake.snake.setDirection(Direction.LEFT); break;
                }
            }
        });
        pnlSnake.setFocusable(true);
        t=new javax.swing.Timer(delay,new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                pnlSnake.snake.move();
                if(pnlSnake.snake.hasCollisionWith(pnlSnake.food.getX(),pnlSnake.food.getY(),0))
                    eat();
                pnlSnake.repaint();
                if(pnlSnake.snake.hasCollisionWith(pnlSnake.snake.Squares.get(0).getX(),pnlSnake.snake.Squares.get(0).getY(),1)){
                    JOptionPane.showMessageDialog(null,"Game over!");
                    System.exit(0);
                }
            }
        });
        t.start();
    }
    public void eat(){
        pnlSnake.snake.grow();
        pnlSnake.generateFood();
        score+=(10*level);
        count++;
        if(count==5){
            count=0;
            level++;
            if(level<=10)
                t.setDelay(delay-((level-1)*10));
        }
        lblScore.setText("Score: "+score);
        lblLevel.setText("Level: "+level);
    }
}