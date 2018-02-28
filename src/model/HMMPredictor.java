package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import control.EstimatorInterface;

public class HMMPredictor implements EstimatorInterface {

	private int rows,cols,head;
	private double[][] T,O;
	public HMMPredictor(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
		this.head = 4;
		T = new double[rows*cols*head][rows*cols*head];
		//Each of the Os matrices is represented by an array [rows*cols*head].
		//As we have [rows*cols +1] Os, we store the info in a matrix.
		O = new double[rows*cols +1][rows*cols*head];
		generateT(T);
		generateOs(O);
	}
	@Override
	public int getNumRows() {
		return this.rows;
	}
	
	private int mapT(int row, int col, int head){
		return col*4 + row*this.cols*4 + head;
	}
	private int mapO(int row, int col){
		return col + row*this.cols;
	}
	
	/*private int move(int row, int col, int h){
		if(h == 0)
			row = row - 1;//return moveNorth(row,col);
		if(h == 1)
			col = col + 1;
		if(h == 2)
			row = row + 1;
		if(h == 3)
			col = col - 1;
		return mapT(row,col,h);
	}*/
	private int moveNorth(int row, int col){
		return col*4 + (row-1)*this.cols*4;
	}
	private int moveEast(int row, int col){
		return (col+1)*4 + row*this.cols*4 + 1;
	}
	private int moveSouth(int row, int col){
		return col*4 + (row+1)*this.cols*4 + 2;
	}
	private int moveWest(int row, int col){
		return (col-1)*4 + row*this.cols*4 + 3;
	}
	private int faceNorth(int row, int col){
		return mapT(row,col,0);
	}
	private int faceEast(int row, int col){
		return mapT(row,col,1);
	}
	private int faceSouth(int row, int col){
		return mapT(row,col,2);
	}
	private int faceWest(int row, int col){
		return mapT(row,col,3);
	}
	
	/*
	 * if(i == 0){
					//Left top corner
					if(j == 0){
						//Facing wall
						row[moveEast(i,j)] = row[moveSouth(i,j)] = 0.5;
						T[faceNorth(i,j)] = T[faceWest(i,j)] = row;
						//Facing not wall
						
						row[moveEast(i,j)] = 0.3;
						row[moveSouth(i,j)] = 0.7;
						T[faceSouth(i,j)] = row;
						
						row[moveSouth(i,j)] = 0.3;
						row[moveEast(i,j)] = 0.7;
						T[faceEast(i,j)] = row;
					}
					//Right top corner
					if(j == cols - 1){
						row[moveSouth(i,j)] = row[moveWest(i,j)] = 0.5;
						T[faceNorth(i,j)] = T[faceEast(i,j)] = row;
						
						row[moveWest(i,j)] = 0.7;
						row[moveSouth(i,j)] = 0.3;
						T[faceWest(i,j)] = row;
						
						row[moveWest(i,j)] = 0.3;
						row[moveSouth(i,j)] = 0.7;
						T[faceSouth(i,j)] = row;
					}
					
				}
				else 
					if(i == rows - 1){
						//Bottom left corner
						if(j == 0){
							row[moveEast(i,j)] = row[moveNorth(i,j)] = 0.5;
							T[faceSouth(i,j)] = T[faceWest(i,j)] = row;
							
							row[moveEast(i,j)] = 0.3;
							row[moveNorth(i,j)] = 0.7;
							T[faceNorth(i,j)] = row;
							
							row[moveNorth(i,j)] = 0.3;
							row[moveEast(i,j)] = 0.7;
							T[faceEast(i,j)] = row;
						}
						if(j == cols - 1){
							row[moveWest(i,j)] = row[moveNorth(i,j)] = 0.5;
							T[faceSouth(i,j)] = T[faceEast(i,j)] = row;
							
							row[moveWest(i,j)] = 0.3;
							row[moveNorth(i,j)] = 0.7;
							T[faceNorth(i,j)] = row;
							
							row[moveNorth(i,j)] = 0.3;
							row[moveWest(i,j)] = 0.7;
							T[faceWest(i,j)] = row;
						}
					}
	 */
	private void generateCorners(double[][] T){
		double[] row = new double[rows*cols*head];
		int i,j;
		
		//Top left
		i = 0;
		j = 0;
	
		row[moveEast(i,j)] = row[moveSouth(i,j)] = 0.5;
		System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);
		System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);
		
		row[moveEast(i,j)] = 0.3;
		row[moveSouth(i,j)] = 0.7;
		System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
		
		row[moveSouth(i,j)] = 0.3;
		row[moveEast(i,j)] = 0.7;
		System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);		
		
		row[moveEast(i,j)] = row[moveSouth(i,j)] = 0;
		//Top right
		j = cols - 1;
		
		row[moveSouth(i,j)] = row[moveWest(i,j)] = 0.5;
		System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);
		System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
		
		row[moveWest(i,j)] = 0.7;
		row[moveSouth(i,j)] = 0.3;
		System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);
		
		row[moveWest(i,j)] = 0.3;
		row[moveSouth(i,j)] = 0.7;
		System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
		

		row[moveWest(i,j)] = row[moveSouth(i,j)] = 0;
		//Bottom right
		i = rows - 1;
		
		row[moveWest(i,j)] = row[moveNorth(i,j)] = 0.5;
		System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
		System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
		
		row[moveWest(i,j)] = 0.3;
		row[moveNorth(i,j)] = 0.7;
		System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);
		
		row[moveNorth(i,j)] = 0.3;
		row[moveWest(i,j)] = 0.7;
		System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);
		
		
		row[moveWest(i,j)] = row[moveNorth(i,j)] = 0;
		//Bottom left
		j = 0;
		
		row[moveEast(i,j)] = row[moveNorth(i,j)] = 0.5;
		System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
		System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);
		
		row[moveEast(i,j)] = 0.3;
		row[moveNorth(i,j)] = 0.7;
		System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);
		
		row[moveNorth(i,j)] = 0.3;
		row[moveEast(i,j)] = 0.7;
		System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
		
	}
	private void generateWalls(double[][] T){
		int i,j;
		double[] row = new double[rows*cols*head];
		
		i = 0;
		for(j = 1; j < cols - 1; j++){
			row[moveEast(i,j)] = row[moveSouth(i,j)] = row[moveWest(i,j)] = 0.33;
			System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);
			
			row[moveWest(i,j)] = row[moveSouth(i,j)] = 0.15;
			row[moveEast(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
			
			row[moveWest(i,j)] = row[moveEast(i,j)] = 0.15;
			row[moveSouth(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
			
			row[moveEast(i,j)] = row[moveSouth(i,j)] = 0.15;
			row[moveWest(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);

			row[moveEast(i,j)] = row[moveSouth(i,j)] = row[moveWest(i,j)] = 0;
		}
		
		i = rows -1;
		for(j = 1; j < cols - 1; j++){
			row[moveEast(i,j)] = row[moveNorth(i,j)] = row[moveWest(i,j)] = 0.33;
			System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
			
			row[moveWest(i,j)] = row[moveNorth(i,j)] = 0.15;
			row[moveEast(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
			
			row[moveWest(i,j)] = row[moveEast(i,j)] = 0.15;
			row[moveNorth(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);
			
			row[moveEast(i,j)] = row[moveNorth(i,j)] = 0.15;
			row[moveWest(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);

			row[moveEast(i,j)] = row[moveNorth(i,j)] = row[moveWest(i,j)] = 0;
		}
		
		j = 0;
		for(i = 1; i < cols - 1; i++){
			row[moveEast(i,j)] = row[moveSouth(i,j)] = row[moveNorth(i,j)] = 0.33;
			System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);
			
			row[moveNorth(i,j)] = row[moveSouth(i,j)] = 0.15;
			row[moveEast(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
			
			row[moveNorth(i,j)] = row[moveEast(i,j)] = 0.15;
			row[moveSouth(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
			
			row[moveEast(i,j)] = row[moveSouth(i,j)] = 0.15;
			row[moveNorth(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);

			row[moveEast(i,j)] = row[moveSouth(i,j)] = row[moveNorth(i,j)] = 0;
		}
		
		j = cols - 1;
		for(i = 1; i < cols - 1; i++){
			row[moveWest(i,j)] = row[moveSouth(i,j)] = row[moveNorth(i,j)] = 0.33;
			System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
			
			row[moveNorth(i,j)] = row[moveSouth(i,j)] = 0.15;
			row[moveWest(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);
			
			row[moveNorth(i,j)] = row[moveWest(i,j)] = 0.15;
			row[moveSouth(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
			
			row[moveWest(i,j)] = row[moveSouth(i,j)] = 0.15;
			row[moveNorth(i,j)] = 0.7;
			System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);

			row[moveWest(i,j)] = row[moveSouth(i,j)] = row[moveNorth(i,j)] = 0;
		}
	}
	private void generateT(double[][] T){
		//double[][] cornerAux = new double[head][rows*cols*head];
		double[] row = new double[rows*cols*head];
		int i,j;
		
		generateCorners(T);
		generateWalls(T);
		
		
		
		//Middle points
		for(i  = 1; i < rows - 1; i++){	
			for(j = 1; j < cols - 1; j++){
				//This initializes row to a zeros vector. Maybe could be done at the end of every if				
				row[moveEast(i,j)] = row[moveSouth(i,j)] = row[moveWest(i,j)] = 0.1;
				row[moveNorth(i,j)] = 0.7;
				System.arraycopy(row,0,T[faceNorth(i,j)],0,row.length);
				
				row[moveNorth(i,j)] = row[moveWest(i,j)] = row[moveSouth(i,j)] = 0.1;
				row[moveEast(i,j)] = 0.7;
				System.arraycopy(row,0,T[faceEast(i,j)],0,row.length);
				
				row[moveNorth(i,j)] = row[moveEast(i,j)] = row[moveWest(i,j)] = 0.1;
				row[moveSouth(i,j)] = 0.7;
				System.arraycopy(row,0,T[faceSouth(i,j)],0,row.length);
				
				row[moveNorth(i,j)] = row[moveEast(i,j)] = row[moveSouth(i,j)] = 0.1;
				row[moveWest(i,j)] = 0.7;
				System.arraycopy(row,0,T[faceWest(i,j)],0,row.length);
				
				row[moveNorth(i,j)] = row[moveEast(i,j)] = row[moveSouth(i,j)] = row[moveWest(i,j)] = 0;
			}
		}
	}
	
	private void generateOs(double[][] O){
		int diffX, diffY;
		//First two done to iterate through the O matrices
		for(int Oi = 0; Oi < rows; Oi++){
			for(int Oj = 0; Oj < cols; Oj++){
				//Now we start checking each matrix
				for(int i = 0; i < rows; i++){
					for(int j = 0; j < cols; j++){
						//h fors could be removed if we considered it properly in the multiplication
						diffX = Oi - i;
						diffY = Oj - j;
						if(diffX == 0 && diffY == 0){
							for(int h = 0; h < head; h++){
								O[mapO(Oi,Oj)][mapT(i,j,h)] = 0.1;
							}
						} else
							if(diffX <= 1 && diffX >= -1 && diffY <= 1 && diffY >= -1){
								for(int h = 0; h < head; h++){
									O[mapO(Oi,Oj)][mapT(i,j,h)] = 0.05;
								}
							} else 
								if(diffX <= 2 && diffX >= -2 && diffY <= 2 && diffY >= -2){
									for(int h = 0; h < head; h++){
										O[mapO(Oi,Oj)][mapT(i,j,h)] = 0.025;
									}
								}else
									for(int h = 0; h < head; h++){
										O[mapO(Oi,Oj)][mapT(i,j,h)] = 0;
									}
					}
				}
			}
		}
		
		//Here we initialize the sensor error matrix
		List<Object> Olist; 
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				for(int h = 0; h < head; h++){
					Olist = DoubleStream.of(O[mapO(i,j)]).boxed().collect(Collectors.toList());
					O[rows*cols][mapT(i,j,h)] = 0.9 - 0.05*Collections.frequency(Olist, new Double(0.05))/4 - 0.025*Collections.frequency(Olist, new Double(0.025))/4;  
				}
			}
		}
	}

	@Override
	public int getNumCols() {
		return this.cols;
	}

	@Override
	public int getNumHead() {
		return this.head;
	}

	/*
	 * should trigger one step of the estimation, i.e., true position, sensor reading and 
	 * the probability distribution for the position estimate should be updated one step
	 * after the method has been called once.
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	/*
	 * returns the currently known true position i.e., after one simulation step
	 * of the robot as (x,y)-pair.
	 */
	@Override
	public int[] getCurrentTruePosition() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * returns the currently available sensor reading obtained for the true position 
	 * after the simulation step 
	 */
	@Override
	public int[] getCurrentReading() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * returns the currently estimated (summed) probability for the robot to be in position
	 * (x,y) in the grid. The different headings are not considered, as it makes the 
	 * view somewhat unclear.
	 */
	@Override
	public double getCurrentProb(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * returns the entry of your observation matrix corresponding to the probability 
	 * of getting the sensor reading r expressed as 
	 * position (rX, rY) given the state (x, y, h). 
	 * See page 579, about how the observation matrices O_r are built, 
	 * i.e. O_r(i,i) = P( r | X = i). 
	 * If rX or rY (or both) are -1, the method should return the probability for 
	 * the sensor to return "nothing" given the robot is in pose (or state) (x, y, h).
	 */
	@Override
	public double getOrXY(int rX, int rY, int x, int y, int h) {
		if(rX == -1 || rY == -1){
			return O[rows*cols][mapT(x,y,h)];
		}
		else{
			return O[mapO(rX,rY)][mapT(x,y,h)];
		}
	}

	/*
	 * returns the probability entry (Tij) of your transition model T to go from pose 
	 * i = (x, y, h) to pose j = (nX, nY, nH)
	 */	
	@Override
	public double getTProb(int x, int y, int h, int nX, int nY, int nH) {
		return T[mapT(x,y,h)][mapT(nX,nY,nH)];
	}
	
	
	/*public double[][] getO(){
		return O;
	}
	
	public double[][] getT(){
		return T;
	}
	
	public static void printArray(double matrix[][]) {
	    for (double[] row : matrix) 
	        System.out.println(Arrays.toString(row));       
	}
	static public void main(String args[]){
		HMMPredictor pred = new HMMPredictor(3, 3);
		//double[][] O = pred.getO();
		double[][] T = pred.getT();
		printArray(T);
		System.out.print(T);
	}*/
}
