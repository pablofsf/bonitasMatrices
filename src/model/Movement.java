package model;

public class Movement {
	int probability;
	int[] pos;
	
	public Movement(double probability, int row, int col, int head){
		this.probability = (int) (probability*100);
		pos = new int[3];
		pos[0] = row;
		pos[1] = col;
		pos[2] = head;
	}
	
	public int relativeProb(){
		return probability;
	}
	
	public int[] getPos(){
		return pos;
	}
}
