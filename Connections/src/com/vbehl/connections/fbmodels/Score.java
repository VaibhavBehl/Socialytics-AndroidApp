package com.vbehl.connections.fbmodels;


/**
 * contains score data and is associated with a FbUser model in a map. May containt info about connections(like photo count, coversation count)
 * 
 * @author vbehl
 *
 */
public class Score {
	private int score;
	private int connectionDataPoint;
	
	public Score() {
		// TODO Auto-generated constructor stub
	}
	
	public Score(int score, int connectionDataPoint) {
		this.score = score;
		this.connectionDataPoint = connectionDataPoint;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getConnectionDataPoint() {
		return connectionDataPoint;
	}

	public void setConnectionDataPoint(int connectionDataPoint) {
		this.connectionDataPoint = connectionDataPoint;
	}

	public int compareTo(Score score) {
		if(this.score < score.score) {
			return -1;
		} else if(this.score > score.score) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + score;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Score other = (Score) obj;
		if (score != other.score)
			return false;
		return true;
	}
	
	
}
