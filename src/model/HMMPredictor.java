package model;

import control.EstimatorInterface;

public class HMMPredictor implements EstimatorInterface {

	private int rows,cols,head;
	private double[][] T,O[];
	public HMMPredictor(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
		this.head = 4;
		T = new double[rows*cols*head][rows*cols*head];
		O = new double[rows*cols +1][rows*cols*head][rows*cols*head];
		generateT(T);
		generateOs(O);
	}
	@Override
	public int getNumRows() {
		return this.rows;
	}
	
	private int mapT(int row, int col, int head){
		return col*4 + row*col*4 + head;
	}
	
	private int moveNorth(int row, int col){
		return col*4 + (row+1)*col*4;
	}
	private int moveEast(int row, int col){
		return (col+1)*4 + row*col*4 + 1;
	}
	private int moveSouth(int row, int col){
		return col*4 + (row-1)*col*4 + 2;
	}
	private int moveWest(int row, int col){
		return (col-1)*4 + row*col*4 + 3;
	}
	private int faceNorth(int row, int col){
		return col*4 + row*col*4;
	}
	private int faceEast(int row, int col){
		return col*4 + row*col*4 + 1;
	}
	private int faceSouth(int row, int col){
		return col*4 + row*col*4 + 2;
	}
	private int faceWest(int row, int col){
		return col*4 + row*col*4 + 3;
	}
	
	private void generateT(double[][] T){
		//double[][] cornerAux = new double[head][rows*cols*head];
		double[] row = new double[rows*cols*head];
		
		for(int i  = 0; i < rows; i++){	
			for(int j = 0; j < cols; j++){
				row = new double[rows*cols*head];
				//Left top corner
				if(i == 0 && j == 0){
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
				else 
				//Right top corner
				if(i == 0 && j == cols - 1 ){
					row[moveSouth(i,j)] = row[moveWest(i,j)] = 0.5;
					T[faceNorth(i,j)] = T[faceEast(i,j)] = row;
					
					row[moveWest(i,j)] = 0.7;
					row[moveSouth(i,j)] = 0.3;
					T[faceWest(i,j)] = row;
					
					row[moveWest(i,j)] = 0.3;
					row[moveSouth(i,j)] = 0.7;
					T[faceSouth(i,j)] = row;
				}
				else
				if(i == rows - 1 && j == 0){
					row[moveEast(i,j)] = row[moveNorth(i,j)] = 0.5;
					T[faceSouth(i,j)] = T[faceWest(i,j)] = row;
					
					row[moveEast(i,j)] = 0.3;
					row[moveNorth(i,j)] = 0.7;
					T[faceNorth(i,j)] = row;
					
					row[moveNorth(i,j)] = 0.3;
					row[moveEast(i,j)] = 0.7;
					T[faceEast(i,j)] = row;
				}
				else
				if(i == rows - 1 && j == cols - 1){
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
		}
	}
	
	private void generateOs(double[][][] O){
		
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
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * returns the probability entry (Tij) of your transition model T to go from pose 
	 * i = (x, y, h) to pose j = (nX, nY, nH)
	 */	
	@Override
	public double getTProb(int x, int y, int h, int nX, int nY, int nH) {
		return T[mapT(x,y,h)][mapT(nX,nY,nH)];
	}

}
