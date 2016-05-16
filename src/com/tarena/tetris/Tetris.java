package com.tarena.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Tetris extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private int score;
	private int lines;
	private Cell[][] wall;
	private Tetromino tetromino;
	private Tetromino nextOne;
	
	public static BufferedImage background;
	public static BufferedImage overImage;
	public static BufferedImage pauseImage;
	public static BufferedImage startImage;
	public static BufferedImage T;
	public static BufferedImage S;
	public static BufferedImage I;
	public static BufferedImage L;
	public static BufferedImage J;
	public static BufferedImage O;
	public static BufferedImage Z;
	
	public static final int ROWS = 20;
	public static final int COLS = 10;
	
	static{
		try {
			background = ImageIO.read(Tetris.class.getResource("../img/tetris.png"));
			overImage = ImageIO.read(Tetris.class.getResource("../img/game-over.png"));
			pauseImage = ImageIO.read(Tetris.class.getResource("../img/pause.png"));
			startImage = ImageIO.read(Tetris.class.getResource("../img/game-start.png"));
			T = ImageIO.read(Tetris.class.getResource("../img/T.png"));
			I = ImageIO.read(Tetris.class.getResource("../img/I.png"));
			S = ImageIO.read(Tetris.class.getResource("../img/S.png"));
			Z = ImageIO.read(Tetris.class.getResource("../img/Z.png"));
			J = ImageIO.read(Tetris.class.getResource("../img/J.png"));
			L = ImageIO.read(Tetris.class.getResource("../img/L.png"));
			O = ImageIO.read(Tetris.class.getResource("../img/O.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g){
		if(gameStart){
			g.drawImage(startImage,0,0,null);
		}else{
			g.drawImage(background,0,0,null);
			g.translate(15,15);
			paintWall(g);
			paintTetromino(g);
			paintNextOne(g);
			paintScore(g);
		}
		if(pause){
			g.translate(-15,-15);
			g.drawImage(pauseImage,0,0,null);
		}
		if(gameOver){
			g.translate(-15,-15);
			g.drawImage(overImage,0,0,null);
		}
	}
	
	public void action(){
		wall = new Cell[ROWS][COLS];
//		startAction();
//		wall[2][2] = new Cell(2,2,T);
		tetromino = Tetromino.randomOne();
		nextOne = Tetromino.randomOne();
		
		KeyAdapter l = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_Q){
					System.exit(0);
				}
				if(gameOver){
					if(key == KeyEvent.VK_S){
						startAction();
						repaint();
					}
					return;
				}
				if(pause){
					if(key == KeyEvent.VK_C){
						continueAction();
						repaint();
					}
					return;
				}
				if(gameStart){
					if(key == KeyEvent.VK_ENTER){
						startAction();
						repaint();
					}
				}
				switch (key) {
				case KeyEvent.VK_DOWN:
					softDropAction();
					break;
				case KeyEvent.VK_RIGHT:
					moveRightAction();
					break;
				case KeyEvent.VK_LEFT:
					moveLeftAction();
					break;
				case KeyEvent.VK_SPACE:
					hardDropAction();
					break;
				case KeyEvent.VK_UP:
					rotateRightAction();
					break;
				case KeyEvent.VK_P:
					pauseAction();
					break;
				case KeyEvent.VK_Z:
					rotateLeftAction();
					break;
				}
				repaint();
			}
		};
		this.requestFocus();
		this.addKeyListener(l);
	}
	
	public static final int FONT_COLOR = 0x667799;
	public static final int FONT_SIZE = 30;

	private void paintScore(Graphics g){
		int x = 290;
		int y = 160;
		g.setColor(new Color(FONT_COLOR));
		Font font = g.getFont();
		font = new Font(font.getName(),font.getStyle(),FONT_SIZE);
		g.setFont(font);
		String str = "SCORT:" + score;
		g.drawString(str,x,y);
		y+=56;
		str = "LINES:" + lines;
		g.drawString(str,x,y);
		y+=56;
		str = "[P]Pause";
		if(pause){
			str = "[C]Continue";
		}
		if(gameOver){
			str = "[S]Start";
		}
		g.drawString(str,x,y);
	}
	
	public void paintNextOne(Graphics g){
		if(nextOne==null){
			return;
		}
		Cell[] cells = nextOne.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];
			int x = (cell.getCol()+10)*CELL_SIZE;
			int y = (cell.getRow()+1)*CELL_SIZE;
			g.drawImage(cell.getImage(),x+1,y+1,null);
		}
	}
	
	public void paintTetromino(Graphics g){
		if(tetromino==null){
			return;
		}
		Cell[] cells = tetromino.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];
			int x = cell.getCol()*CELL_SIZE;
			int y = cell.getRow()*CELL_SIZE;
			g.drawImage(cell.getImage(),x-1,y-1,null);
		}
		
	}
	
	public static final int CELL_SIZE = 26;
	
	private void paintWall(Graphics g){
		for(int row=0;row<wall.length;row++){
			Cell[] line = wall[row];
			//line代表墙上的每一行
			for(int col=0;col<line.length;col++){
				Cell cell = line[col];
				//cell代表墙上的每一个格子
				int x = col*CELL_SIZE;
				int y = row*CELL_SIZE;
				if(cell==null){
					g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
				}else{
					g.drawImage(cell.getImage(),x-1,y-1,null);
				}
			}
		}
	}
	
	private boolean outOfBounds(){
		Cell[] cells = tetromino.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];
			int col = cell.getCol();
			int row = cell.getRow();
			if(col<0||col>=COLS||row<0||row>=ROWS){
				return true;
			}
			
		}
		return false;
	}
	
	private boolean coincide(){
		Cell[] cells = tetromino.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];
			int row = cell.getRow();
			int col = cell.getCol();
			if(row>=0&&row<ROWS&&col>=0&&col<COLS&&wall[row][col]!=null){
				return true;
			}
		}
		return false;
	}
	
	public void moveRightAction(){
		tetromino.moveRight();
		if(outOfBounds()||coincide()){
			tetromino.moveLeft();
		}
	}
	public void moveLeftAction(){
		tetromino.moveLeft();
		if(outOfBounds()||coincide()){
			tetromino.moveRight();
		}
	}
	public void softDropAction(){
		if(canDrop()){
			tetromino.softDrop();
		}else{
			landIntoWall();
			destroyLines();
			checkGameOverAction();
			tetromino=nextOne;
			nextOne = Tetromino.randomOne();
		}
	}
	
	private static int[] scoreTable = {0,1,10,50,100};
	
	private void destroyLines(){
		int lines = 0;
		for(int row=0;row<wall.length;row++){
			if(fullCells(row)){
				deleteRow(row);
				lines++;
			}
		}
		this.score += scoreTable[lines];
		this.lines += lines;
	}
	
	private void deleteRow(int row){
		for(int i=row;i>=1;i--){
			System.arraycopy(wall[i-1], 0, wall[i], 0, COLS);
		}
		Arrays.fill(wall[0],null);
	}
	
	private boolean fullCells(int row){
		Cell[] line = wall[row];
		for(Cell cell:line){
			if(cell==null){
				return false;
			}
		}
		return true;
	}
	
	private void landIntoWall(){
		Cell[] cells = tetromino.cells;
		for(Cell cell:cells){
			int row = cell.getRow();
			int col = cell.getCol();
//			cell.setImage(O);
			wall[row][col]=cell;
		}
	}
	
	private boolean canDrop(){
		Cell[] cells = tetromino.cells;
		for(Cell cell:cells){
			int row = cell.getRow();
			int col = cell.getCol();
			if(row==ROWS-1){
				return false;
			}
			if(row+1>=0&&row+1<ROWS&&col>=0&&col<=COLS&&wall[row+1][col]!=null){
				return false;
			}
		}
		return true;
	}
	
	public void hardDropAction(){
		while(canDrop()){
			tetromino.softDrop();
		}
		landIntoWall();
		destroyLines();
		checkGameOverAction();
		tetromino = nextOne;
		nextOne = Tetromino.randomOne();
	}
	
	public void rotateRightAction() {
		tetromino.rotateRight();
		if(outOfBounds()||coincide()) {
			tetromino.rotateLeft();
		}
	}
	public void rotateLeftAction() {
		tetromino.rotateLeft();
		if(outOfBounds()||coincide()){
			tetromino.rotateRight();
		}
	}

	private Timer timer;
	private boolean pause;
	private boolean gameOver;
	private boolean gameStart = true;
	private long interval = 600;
	
	public void startAction(){
		pause = false;
		gameOver = false;
		gameStart = false;
		score = 0;
		lines = 0;
		clearWall();
		tetromino = Tetromino.randomOne();
		nextOne = Tetromino.randomOne();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
			}
		}, interval,interval);
	}
	private void clearWall(){
		for(Cell[] line:wall){
			Arrays.fill(line, null);
		}
	}
	public void pauseAction(){
		timer.cancel();
		pause = true;
	}
	public void continueAction(){
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
			}
		}, interval,interval);
		pause = false;
	}
	public void checkGameOverAction(){
		if(wall[0][4]!=null){
			gameOver = true;
			timer.cancel();
		}
	}
	
}
