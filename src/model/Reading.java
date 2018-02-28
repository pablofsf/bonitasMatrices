package model;

public class Reading {
	double probability;
	int[] pos;
	
	public Reading(double probability, int row, int col){
		this.probability = (probability*100);
		pos = new int[2];
		pos[0] = row;
		pos[1] = col;
	}
	
	public double relativeProb(){
		return probability;
	}
	
	public int[] getPos(){
		return pos;
	}
}
