/* **********************************************************************************
* HMMPredictor.java: Simulate forward behaviour for robot localization				*
* This file is part of robotLocalisationHMM											*
* 																					*
* Copyright (C) 2017 Pablo Correa Gomez										        *
*                                                                                   *
*    This program is free software; you can redistribute it and/or modify           *
*    it under the terms of the GNU General Public License as published by           *
*    the Free Software Foundation; either version 3 of the License, or              *
*    (at your option) any later version.                                            *
*                                                                                   *
*    This program is distributed in the hope that it will be useful,                *
*    but WITHOUT ANY WARRANTY; without even the implied warranty of                 *
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                  *
*    GNU General Public License for more details.                                   *
*    ( http://www.fsf.org/licenses/gpl.txt )                                        *
********************************************************************************** */

package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import control.EstimatorInterface;

public class HMMPredictor implements EstimatorInterface {

	private int rows,cols,head;
	private double[] f,T[],O[];
	int[] pos;
	int[] sens;
	Random random;
	
	public HMMPredictor(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
		this.head = 4;
		
		T = new double[rows*cols*head][rows*cols*head];
		//Each of the Os matrices is represented by an array [rows*cols*head]. head could be avoided, but helps making multiplication simpler
		//As we have [rows*cols +1] Os, we store the info in a matrix.
		O = new double[rows*cols +1][rows*cols*head];
		generateT(T);
		generateOs(O);
		
		f = new double[rows*cols*head];
		Arrays.fill(f,1/((double)rows*(double)cols*(double)head));
		
        random = new Random();
        sens = new int[2];
		pos = new int[3];
		pos[0] = random.nextInt(rows);
		pos[1] = random.nextInt(cols);
		pos[2] = random.nextInt(head);
				
	}
	
	@Override
	public int getNumRows() {
		return this.rows;
	}
	@Override
	public int getNumCols() {
		return this.cols;
	}
	@Override
	public int getNumHead() {
		return this.head;
	}
	
	//Find the corresponding element in one of the arrays rows*cols*head
	private int mapT(int row, int col, int head){
		return col*4 + row*this.cols*4 + head;
	}
	//Find the proper matrix O
	private int mapO(int row, int col){
		if(row < 0 || col < 0)
			return cols*rows;
		else
			return col + row*this.cols;
	}

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
		
		//Top row
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
		
		//Bottom row
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
		
		//Left column
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
		
		//Right column
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
		double[] row = new double[rows*cols*head];
		
		generateCorners(T);
		generateWalls(T);
		
		//Middle points
		for(int i  = 1; i < rows - 1; i++){	
			for(int j = 1; j < cols - 1; j++){
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
						diffX = Math.abs(Oi - i);
						diffY = Math.abs(Oj - j);
						if(diffX == 0 && diffY == 0){
							for(int h = 0; h < head; h++){
								O[mapO(Oi,Oj)][mapT(i,j,h)] = 0.1;
							}
						} else
							if(diffX <= 1 && diffY <= 1){
								for(int h = 0; h < head; h++){
									O[mapO(Oi,Oj)][mapT(i,j,h)] = 0.05;
								}
							} else 
								if(diffX <= 2 && diffY <= 2){
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
		
		//Initialize the sensor error matrix
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

	private void move(){
		double[] probCurrentPos = T[mapT(pos[0],pos[1],pos[2])];
		Movement choosenMov;
		ArrayList<Movement> movs = new ArrayList<Movement>(4);
		
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				for(int h = 0; h < head; h++){
					if(probCurrentPos[mapT(i,j,h)] != 0){
						movs.add(new Movement(probCurrentPos[mapT(i,j,h)],i,j,h));
					}
				}
			}
		}
		
		//Choose movement from probabilities
		//Based on stack overflow code proposal
	    random = new Random();
        double index = random.nextDouble()*100;
        double sum = 0;
        int i=0;
        
        try{
        	while(sum < index ) {
        		sum = sum + movs.get(i++).relativeProb();
        	}
            choosenMov = movs.get(Math.max(0,i-1));
        }
        catch(IndexOutOfBoundsException e){
        	choosenMov = movs.get(movs.size()-1);
        }
        pos = choosenMov.getPos();
	}

	private void getSensorReading(){
		Reading chosenRead;
		ArrayList<Reading> posReading = new ArrayList<Reading>();
		double probability = 0;
		
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				probability = O[mapO(i,j)][mapT(pos[0],pos[1],pos[2])];
				
				if(probability != 0)
					posReading.add(new Reading(probability,i,j));
			}
		}
		probability = O[rows*cols][mapT(pos[0],pos[1],pos[2])];
		posReading.add(new Reading(probability,-1,-1));
		
		//Based on stack overflow code proposal
	    random = new Random();
        double index = random.nextDouble()*100;
        double sum = 0;
        int i=0;
        
        try{
        	while(sum < index ) {
        		sum = sum + posReading.get(i++).relativeProb();
        	}
            chosenRead = posReading.get(Math.max(0,i-1));
        }
        catch(IndexOutOfBoundsException e){
        	chosenRead = posReading.get(posReading.size() - 1);
        }
        sens = chosenRead.getPos();
	}
	
	private void posEstimate(){
		//What we need to do is ft+1 = O*T'*ft;
		double[] currentO = O[mapO(sens[0],sens[1])], newf = new double[rows*cols*head];
		double[][] OTaux = new double[rows*cols*head][rows*cols*head];
		double sumf = 0, alpha;
		
		for(int i = 0; i < rows*cols*head; i++){
			for(int j = 0; j < rows*cols*head; j++){
				OTaux[i][j] = T[j][i]*currentO[i];
			}
		}
		
		for(int i = 0; i < rows*cols*head; i++){
			newf[i] = 0;
			for(int j = 0; j < rows*cols*head; j++){
				newf[i] += OTaux[i][j]*f[j];
			}
			sumf += newf[i];
		}
		
		//Corrrect f values
		alpha = 1/sumf;
		for(int i = 0; i < rows*cols*head; i++){
			f[i] = alpha*newf[i];
		}
	}
	/*
	 * should trigger one step of the estimation, i.e., true position, sensor reading and 
	 * the probability distribution for the position estimate should be updated one step
	 * after the method has been called once.
	 */
	@Override
	public void update() {
		move();
		getSensorReading();
		posEstimate();

	}

	/*
	 * returns the currently known true position i.e., after one simulation step
	 * of the robot as (x,y)-pair.
	 */
	@Override
	public int[] getCurrentTruePosition() {
		int[] posXY = new int[2];
		posXY[0] = pos[0];
		posXY[1] = pos[1];
		
		return posXY;
	}

	/*
	 * returns the currently available sensor reading obtained for the true position 
	 * after the simulation step 
	 */
	@Override
	public int[] getCurrentReading() {
		return sens;
	}

	/*
	 * returns the currently estimated (summed) probability for the robot to be in position
	 * (x,y) in the grid. The different headings are not considered, as it makes the 
	 * view somewhat unclear.
	 */
	@Override
	public double getCurrentProb(int x, int y) {
		double probability = 0;
		
		for(int h = 0; h < head; h++){
			probability += f[mapT(x,y,h)];
		}
		return probability;
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
}
