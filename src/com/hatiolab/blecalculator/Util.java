package com.hatiolab.blecalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.location.Location;

import com.hatiolab.blecalculator.model.Beacon;
import com.hatiolab.blecalculator.model.Position;
import com.hatiolab.blecalculator.model.Scene;

public class Util {
	static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
//	public static Location getLocationWithCenterOfGravity(Location beaconA, Location beaconB, Location beaconC, double distanceA, double distanceB, double distanceC) {
//
//	    //Every meter there are approx 4.5 points
//	    double METERS_IN_COORDINATE_UNITS_RATIO = 0.01;
//
//	    //http://stackoverflow.com/a/524770/663941
//	    //Find Center of Gravity
//	    double cogX = (beaconA.getLatitude() + beaconB.getLatitude() + beaconC.getLatitude()) / 3;
//	    double cogY = (beaconA.getLongitude() + beaconB.getLongitude() + beaconC.getLongitude()) / 3;
//	    Location cog = new Location("Cog");
//	    cog.setLatitude(cogX);
//	    cog.setLongitude(cogY);
//
//
//	    //Nearest Beacon
//	    Location nearestBeacon;
//	    double shortestDistanceInMeters;
//	    if (distanceA < distanceB && distanceA < distanceC) {
//	        nearestBeacon = beaconA;
//	        shortestDistanceInMeters = distanceA;
//	    } else if (distanceB < distanceC) {
//	        nearestBeacon = beaconB;
//	        shortestDistanceInMeters = distanceB;
//	    } else {
//	        nearestBeacon = beaconC;
//	        shortestDistanceInMeters = distanceC;
//	    }
//
//	    //http://www.mathplanet.com/education/algebra-2/conic-sections/distance-between-two-points-and-the-midpoint
//	    //Distance between nearest beacon and COG
//	    double distanceToCog = Math.sqrt(Math.pow(cog.getLatitude() - nearestBeacon.getLatitude(),2)
//	            + Math.pow(cog.getLongitude() - nearestBeacon.getLongitude(),2));
//
//	    //Convert shortest distance in meters into coordinates units.
//	    double shortestDistanceInCoordinationUnits = shortestDistanceInMeters * METERS_IN_COORDINATE_UNITS_RATIO;
//
//	    //http://math.stackexchange.com/questions/46527/coordinates-of-point-on-a-line-defined-by-two-other-points-with-a-known-distance?rq=1
//	    //On the line between Nearest Beacon and COG find shortestDistance point apart from Nearest Beacon
//
//	    double t = shortestDistanceInCoordinationUnits/distanceToCog;
//
//	    Location pointsDiff = new Location("PointsDiff");
//	    pointsDiff.setLatitude(cog.getLatitude() - nearestBeacon.getLatitude());
//	    pointsDiff.setLongitude(cog.getLongitude() - nearestBeacon.getLongitude());
//
//	    Location tTimesDiff = new Location("tTimesDiff");
//	    tTimesDiff.setLatitude( pointsDiff.getLatitude() * t );
//	    tTimesDiff.setLongitude(pointsDiff.getLongitude() * t);
//
//	    //Add t times diff with nearestBeacon to find coordinates at a distance from nearest beacon in line to COG.
//
//	    Location userLocation = new Location("UserLocation");
//	    userLocation.setLatitude(nearestBeacon.getLatitude() + tTimesDiff.getLatitude());
//	    userLocation.setLongitude(nearestBeacon.getLongitude() + tTimesDiff.getLongitude());
//
//	    return userLocation;
//	}
	
	public static double[] moveNode(ArrayList<Position> positionArr, double dA, double dB, double dC) {
		double xa = positionArr.get(0).getCenterX();
		double ya = positionArr.get(0).getCenterY();
		double xb = positionArr.get(1).getCenterX();
		double yb = positionArr.get(1).getCenterY();
		double xc = positionArr.get(2).getCenterX();
		double yc = positionArr.get(2).getCenterX();
		double ra = dA;
		double rb = dB;
		double rc = dC;
		double x, y;
		
		
		double xaSq = xa*xa, xbSq = xb*xb, xcSq = xc*xc, yaSq = ya*ya, ybSq = yb*yb, ycSq = yc*yc, raSq = ra*ra, rbSq = rb*rb, rcSq = rc*rc;
		
		double numerator1 = (xb-xa) * (xcSq + ycSq - rcSq) + (xa - xc) * (xbSq + ybSq - rbSq) + (xc - xb) * (xaSq + yaSq - raSq);
		double denominator1 = 2 * (yc * (xb - xa) + yb * (xa - xc) + ya * (xc - xb));
		y = numerator1 / denominator1;
		
		double numerator2 = rbSq - raSq + xaSq - xbSq + yaSq - ybSq - 2 * (ya - yb) * y;
		double denominator2 = 2 * (xa - xb);
		x = numerator2 / denominator2;
		double[] result = {x, y};
		
		if(x == Double.NaN || x == Double.NEGATIVE_INFINITY || y == Double.NaN || y == Double.NEGATIVE_INFINITY) {
			System.out.println("asdfads");
		}
		
		return result;
		
		
		
//		double xa = nodeA[0];
//		double ya = nodeA[1];
//		double xb = nodeB[0];
//		double yb = nodeB[1];
//		double xc = nodeC[0];
//		double yc = nodeC[1];
//		double ra = dA;
//		double rb = dB;
//		double rc = dC;
//		
//		double W = ra*ra - rb*rb - xa*xa - ya*ya + xb*xb + yb*yb;
//		double Z = rb*rb - rc*rc - xb*xb - yb*yb + xc*xc + yc*yc;
//		
//		double moveX = (W*(yc-yb) - Z*(yb-ya)) / (2 * ((xb-xa)*(yc-yb) - (xc-xb)*(yb-ya)));
//		double moveY = (W - 2*moveX*(xb-xa)) / (2*(yb-ya));
//	    //y2 is a second measure of y to mitigate errors
//		double moveY2 = (Z - 2*moveX*(xc-xb)) / (2*(yc-yb));
//		moveY = (moveY + moveY2) / 2;
//		double[] result = {moveX, moveY};
//		
//		if(moveX == Double.NaN || moveX == Double.NEGATIVE_INFINITY) {
//			System.out.println("asdf");
//		}
//		if(moveY == Double.NaN || moveY == Double.NEGATIVE_INFINITY) {
//			System.out.println("asdf");
//		}
////		
////		double[] result = {decimalScale(String.valueOf(moveX), 3), decimalScale(String.valueOf(moveY), 3)};
////		
//		return result;
		
		
		
//		double xa = nodeA[0];
//		double ya = nodeA[1];
//		double xb = nodeB[0];
//		double yb = nodeB[1];
//		double xc = nodeC[0];
//		double yc = nodeC[1];
//		double ra = dA;
//		double rb = dB;
//		double rc = dC;
//
//		double S = (Math.pow(xc, 2.) - Math.pow(xb, 2.) + Math.pow(yc, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(rc, 2.)) / 2.0;
//		double T = (Math.pow(xa, 2.) - Math.pow(xb, 2.) + Math.pow(ya, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(ra, 2.)) / 2.0;
//		double y = ((T * (xb - xc)) - (S * (xb - xa))) / (((ya - yb) * (xb - xc)) - ((yc - yb) * (xb - xa)));
//		double x = ((y * (ya - yb)) - T) / (xb - xa);
//
//		
//		
//		
//		
////		double moveX = (dA * dA - dB * dB + nodeB[0] * nodeB[0]) / (2 * nodeB[0]);
////		double moveY = (nodeC[0] * nodeC[0] + nodeC[1] * nodeC[1] + dA * dA * dC * dC - 2*moveX*nodeC[0]) / (2*nodeC[1]);
////		double moveZ = Math.sqrt(Math.abs(dA * dA - moveX * moveX - moveY * moveY));
////		
//		double[] result = {decimalScale(String.valueOf(x), 3), decimalScale(String.valueOf(y), 3)};
//		
//		return result;
	}
	
	public static Double decimalScale(String decimal, int loc) {
		BigDecimal bd = new BigDecimal(decimal);
		BigDecimal result;

		result = bd.setScale(loc, BigDecimal.ROUND_HALF_EVEN);

		return result.doubleValue();
	}
	
	public static double calculateDistance(int txPower, double rssi) {
		if (rssi == 0) {
			return -1.0; // if we cannot determine distance, return -1.
		}

		double ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			return accuracy;
		}
	}
	
	public static Beacon recordParser(BluetoothDevice device, int rssi, byte[] scanRecord, ArrayList<Scene> sceneBeacon) {
        Beacon param = new Beacon();
		int startByte = 2;
	    boolean patternFound = false;
	    while (startByte <= 5) {
	        if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
	                ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
	            patternFound = true;
	            break;
	        }
	        startByte++;
	    }
	    
	    if (patternFound) {
	        //Convert to hex String
	        byte[] uuidBytes = new byte[16];
	        System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
	        String hexString = Util.bytesToHex(uuidBytes);
	        
	        //Here is your UUID
	        String uuid =  hexString.substring(0,8) + "-" + 
	                hexString.substring(8,12) + "-" + 
	                hexString.substring(12,16) + "-" + 
	                hexString.substring(16,20) + "-" + 
	                hexString.substring(20,32);
	        
	        //Here is your Major value
	        int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);
	        
	        //Here is your Minor value
	        int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);
	        int txPower = (int) scanRecord[startByte + 24];
	        
	        param.setDeviceAddress(device.getAddress());
	        param.setUuid(uuid);
	        param.setMajor(major);
	        param.setMinor(minor);
	        param.setTxPower(txPower);
	        param.setRssi(rssi);
	        
	        
	       for(int i = 0 ; i < sceneBeacon.size() ; i++) {
	    	   if(device.getAddress().equals(sceneBeacon.get(i).getBeaconId())) {
	    		   param.setBeaconX(sceneBeacon.get(i).getLeft());
	    		   param.setBeaconY(sceneBeacon.get(i).getTop());

    		    return param;
	    	   }
	       }
	    }
	    return null;
	}
	
	public static double mean(ArrayList<Double> array) {
		double sum = 0.0;
		for (int i = 0; i < array.size(); i++)
			sum += array.get(i);
		return sum / array.size();
	}
	
	public static double standardDeviation(ArrayList<Double> array) {
		if (array.size() < 2) return Double.NaN;
	    double sum = 0.0;
	    double sd = 0.0;
	    double diff;
	    double meanValue = mean(array);
	    for (int i = 0; i < array.size(); i++) {
	      diff = array.get(i) - meanValue;
	      sum += diff * diff;
	    }
	    sd = Math.sqrt(sum / (array.size()));
	    return sd;
	}
	
	public static int avgFilter(int count, int prevAvg, int value) {
		int alpha = (count - 1) / count;
		int avg = alpha * prevAvg + (1 - alpha) * value;
		
		return avg;
	}
	
	public static int lpfilter(double alpha, int prevValue, int currentX) {
		int value = (int) (alpha * prevValue + (1 - alpha) * currentX);
		return value;
	}
	
	public static ArrayList<Beacon> descendingSort(ArrayList<Beacon> beaconSetting) {
		Beacon param = new Beacon();
		for(int i = 0 ; i < beaconSetting.size() ; i++) {
			for(int j = 0 ; j < beaconSetting.size() - 1 ; j++) {
				
				if(Util.calculateDistance(beaconSetting.get(j).getTxPower(), beaconSetting.get(j).getRssi()) < Util.calculateDistance(beaconSetting.get(j + 1).getTxPower(), beaconSetting.get(j + 1).getRssi())) {
					param = beaconSetting.get(j);
					beaconSetting.set(j, beaconSetting.get(j + 1));
					beaconSetting.set(j + 1, param);
				}
			}
		}
		return beaconSetting;
	}
	
	
	public static ArrayList<Position> combination(Beacon[] elements, int K){
		// get the length of the array
		// e.g. for {'A','B','C','D'} => N = 4 
		int N = elements.length;
		ArrayList<Position> beforePosition = new ArrayList<Position>();
		
		if(K > N){
			System.out.println("Invalid input, K > N");
			return beforePosition;
		}
		// calculate the possible combinations
		// e.g. c(4,2)
		c(N,K);
		
		// get the combination by index 
		// e.g. 01 --> AB , 23 --> CD
		int combination[] = new int[K];
		
		// position of current index
		//  if (r = 1)				r*
		//	index ==>		0	|	1	|	2
		//	element ==>		A	|	B	|	C
		int r = 0;		
		int index = 0;
		
		while(r >= 0){
			// possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
			// possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"
			
			// for r = 0 ==> index < (4+ (0 - 2)) = 2
			if(index <= (N + (r - K))){
					combination[r] = index;
					
				// if we are at the last position print and increase the index
				if(r == K-1){

					//do something with the combination e.g. add to list or print
					beforePosition.add(calPosition(combination, elements));
					index++;				
				}
				else{
					// select index for next position
					index = combination[r]+1;
					r++;										
				}
			}
			else{
				r--;
				if(r > 0)
					index = combination[r]+1;
				else
					index = combination[0]+1;	
			}			
		}
		
		return beforePosition;
//		averageOfPositions(beforePosition);
	}
	
	public static int c(int n, int r){
		int nf=fact(n);
		int rf=fact(r);
		int nrf=fact(n-r);
		int npr=nf/nrf;
		int ncr=npr/rf; 
		
		System.out.println("C("+n+","+r+") = "+ ncr);

		return ncr;
	}
	
	public static int fact(int n)
	{
		if(n == 0)
			return 1;
		else
			return n * fact(n-1);
	}

	public static Position calPosition(int[] combination, Beacon[] elements){

		ArrayList<Integer> rssi = new ArrayList<Integer>();
		ArrayList<Integer> txPower = new ArrayList<Integer>();
		ArrayList<Position> positionArr = new ArrayList<Position>();
		for(int z = 0 ; z < combination.length ; z++){
//			output += elements[combination[z]].getDeviceAddress() + " / ";
			Position param = new Position();
			rssi.add(elements[combination[z]].getRssi());
			txPower.add(elements[combination[z]].getTxPower());
			param.setCenterX(elements[combination[z]].getBeaconX());
			param.setCenterY(elements[combination[z]].getBeaconY());
			positionArr.add(param);
		}
		
		
		
		
		
//		double xa = positionArr.get(0).getCenterX();
//		double ya = positionArr.get(0).getCenterY();
//		double xb = positionArr.get(1).getCenterX();
//		double yb = positionArr.get(1).getCenterY();
//		double xc = positionArr.get(2).getCenterX();
//		double yc = positionArr.get(2).getCenterX();
		
		
		Location beaconA = new Location("beaconA");
		Location beaconB = new Location("beaconB");
		Location beaconC = new Location("beaconC");
		beaconA.setLatitude(positionArr.get(0).getCenterX());
		beaconA.setLongitude(positionArr.get(0).getCenterY());
		beaconB.setLatitude(positionArr.get(1).getCenterX());
		beaconB.setLongitude(positionArr.get(1).getCenterY());
		beaconC.setLatitude(positionArr.get(2).getCenterX());
		beaconC.setLongitude(positionArr.get(2).getCenterY());
		
		
//		Location location = Util.getLocationWithCenterOfGravity(beaconA, beaconB, beaconC, Util.calculateDistance(txPower.get(0), rssi.get(0)), Util.calculateDistance(txPower.get(1), rssi.get(1)), Util.calculateDistance(txPower.get(2), rssi.get(2)));
		
		
		double[] position = Util.moveNode(positionArr, Util.calculateDistance(txPower.get(0), rssi.get(0)), Util.calculateDistance(txPower.get(1), rssi.get(1)), Util.calculateDistance(txPower.get(2), rssi.get(2)));
		
		Position result = new Position();
//		result.setCenterX(location.getLatitude());
//		result.setCenterY(location.getLongitude());
		result.setCenterX(position[0]);
		result.setCenterY(position[1]);
		
		
		return result;
		
//		System.out.println(output);
//		Log.d("combination+++++++++++++", output);
	}
	
	public static Position averageOfPositions(ArrayList<Position> beforePosition) {
		double sumX = 0;
		double sumY = 0;

		Position result = new Position();
		if(beforePosition.size() > 0) {
			for(int i = 0 ; i < beforePosition.size() ; i++) {
				sumX += beforePosition.get(i).getCenterX();
				sumY += beforePosition.get(i).getCenterY();
			}
			
			double avgX = sumX / beforePosition.size();
			double avgY = sumY / beforePosition.size();
			
			result.setCenterX(avgX);
			result.setCenterY(avgY);
			
		}
		return result;
		
		
//		txtLocation.setText("X : " + sumX / beforePosition.size() + " / Y : " + sumY / beforePosition.size());
//		sceneFirebase.setPosition(avgX, avgY);
	}
}
