package model;

public class Movement {
	double probability;
	int[] pos;
	
	public Movement(double probability, int row, int col, int head){
		this.probability =  (probability*100);
		pos = new int[3];
		pos[0] = row;
		pos[1] = col;
		pos[2] = head;
	}
	
	public double relativeProb(){
		return probability;
	}
	
	public int[] getPos(){
		return pos;
	}
}
