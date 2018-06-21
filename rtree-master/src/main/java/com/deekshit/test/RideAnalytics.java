package com.deekshit.test;

import static org.junit.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.github.davidmoten.grumpy.core.Position;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.Observable;
import rx.functions.Func1;
import javax.swing.*;
import java.awt.event.*;
public class RideAnalytics implements ActionListener{
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Coordinate co = new Coordinate();
		String filePath = "file/coordinates.csv";
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		Integer i = 1;
		int count = 0;
		try{
			ArrayList<Coordinate> al = new ArrayList<Coordinate>();
			List<NearestNeighbor> lnn = new ArrayList<NearestNeighbor>();
		
			br = new BufferedReader(new FileReader(filePath));
			while((line = br.readLine())!= null){
				Coordinate co = new Coordinate();
				String[] coordinates = line.split(splitBy);
				double dropOffLongitude =  Double.parseDouble(coordinates[2]);
				double dropOffLatitude = Double.parseDouble(coordinates[3]);
				int popularity = Integer.parseInt(coordinates[4]);
				co.setLongitude(dropOffLongitude);
				co.setLatitude(dropOffLatitude);
				co.setPopularity(popularity);
				al.add(co);
			}
			RTree<String, Point> tree = RTree.maxChildren(4).create();
			for (Coordinate coordinate : al) {
				tree = tree.add(i.toString(), Geometries.point(coordinate.getLongitude(),coordinate.getLatitude()));
				i++;
			}
					
			System.out.println("please enter distance");
			Scanner sc = new Scanner(System.in);
			final double distanceKm = sc.nextInt();
			System.out.println("please enter the longitude values:");
			double input_longitude = sc.nextDouble();
			System.out.println("please enter the latitude values:");
			double input_latitude = sc.nextDouble();
			sc.close();
			//Point point1 = Geometries.point(-73.98339081, 40.76172256);
			Point point1 = Geometries.point(input_longitude, input_latitude);
			final Position from = Position.create(point1.y(), point1.x());
	        List<Entry<String, Point>> list = search(tree, point1, distanceKm)
	                // get the result
	                .toList().toBlocking().single();
	        for (Entry<String, Point> entry : list) {
	        	NearestNeighbor nn = new NearestNeighbor();
	        	Point p = entry.geometry();
                Position position = Position.create(p.y(), p.x());
				double distfinal =from.getDistanceToKm(position);
				nn.setDistance(distfinal);
				nn.setLatitude(entry.geometry().x());
				nn.setLongitude(entry.geometry().y());
				lnn.add(nn);
			}
	        Collections.sort(lnn,NearestNeighbor.Comparators.DISTANCE);

	        for (NearestNeighbor entry : lnn) {
				//System.out.println("Distance:"+entry.getDistance()+" Latitude:"+entry.getLatitude()+" Longitude:"+entry.getLongitude());
	        	Double res_lat = BigDecimal.valueOf(entry.getLatitude()).setScale(6,RoundingMode.HALF_UP).doubleValue();
	        	Double res_long = BigDecimal.valueOf(entry.getLongitude()).setScale(6,RoundingMode.HALF_UP).doubleValue();
	        	System.out.println("Distance:"+entry.getDistance()+" Latitude:"+res_lat+" Longitude:"+res_long);
	        	if(count == 5){
	        		break;
	        	}
	        	else count++;
	        }  
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			if(br!=null){
				try{
					br.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		
	}

	 public static <T> Observable<Entry<String, Point>> search(RTree<String, Point> tree, Point lonLat, final double distanceKm) {
		 
		 
		 final Position from = Position.create(lonLat.y(), lonLat.x());
	        Rectangle bounds = createBounds(from, distanceKm);

	        return tree
	                // do the first search using the bounds
	                .search(bounds)
	                // refine using the exact distance
	                .filter(new Func1<Entry<String, Point>, Boolean>() {
	                    @Override
	                    public Boolean call(Entry<String, Point> entry) {
	                    	NearestNeighbor nn = new NearestNeighbor();
	                    	Point p = entry.geometry();
	                        Position position = Position.create(p.y(), p.x());
	                        double dist = from.getDistanceToKm(position);
	                        nn.setDistance(dist);
	                        nn.setLatitude(position.getLat());
	                        nn.setLongitude(position.getLon());
	                        return from.getDistanceToKm(position) < distanceKm;
	                    }
	                });
	}

	private static Rectangle createBounds(final Position from, final double distanceKm) {
	        // this calculates a pretty accurate bounding box. Depending on the
	        // performance you require you wouldn't have to be this accurate because
	        // accuracy is enforced later
	        Position north = from.predict(distanceKm, 0);
	        Position south = from.predict(distanceKm, 180);
	        Position east = from.predict(distanceKm, 90);
	        Position west = from.predict(distanceKm, 270);

	        return Geometries.rectangle(west.getLon(), south.getLat(), east.getLon(), north.getLat());
	    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
