package com.deekshit.test;

import java.util.Comparator;
public class NearestNeighbor implements Comparable<NearestNeighbor>{

	private double distance;
	private double latitude;
	private double longitude;
	private int popularity;
	public int getPopularity() {
		return popularity;
	}
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	@Override
	public int compareTo(NearestNeighbor o) {
		// TODO Auto-generated method stub
		return Comparators.DISTANCE.compare(this, o);
	}
	public static class Comparators {

        public static Comparator<NearestNeighbor> DISTANCE = new Comparator<NearestNeighbor>() {
            @Override
            public int compare(NearestNeighbor o1, NearestNeighbor o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        };
        public static Comparator<NearestNeighbor> POPULARITY = new Comparator<NearestNeighbor>() {
            @Override
            public int compare(NearestNeighbor o1, NearestNeighbor o2) {
                return Integer.compare(o1.popularity, o2.popularity);
            }
        };
        
    }
	
	

}
