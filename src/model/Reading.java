package model;

public class Reading {
	int probability;
	int[] pos;
	
	public Reading(double probability, int row, int col){
		this.probability = (int) (probability*100);
		pos = new int[2];
		pos[0] = row;
		pos[1] = col;
	}
	
	public int relativeProb(){
		return probability;
	}
	
	public int[] getPos(){
		return pos;
	}
}
