package com.hatiolab.blecalculator;

import java.math.BigDecimal;

import com.hatiolab.blecalculator.model.Beacon;

import android.bluetooth.BluetoothDevice;

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
	
	public static double[] moveNode(int[] nodeA, int[] nodeB, int[] nodeC, double dA, double dB, double dC) {
		double xa = nodeA[0];
		double ya = nodeA[1];
		double xb = nodeB[0];
		double yb = nodeB[1];
		double xc = nodeC[0];
		double yc = nodeC[1];
		double ra = dA;
		double rb = dB;
		double rc = dC;

		double S = (Math.pow(xc, 2.) - Math.pow(xb, 2.) + Math.pow(yc, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(rc, 2.)) / 2.0;
		double T = (Math.pow(xa, 2.) - Math.pow(xb, 2.) + Math.pow(ya, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(ra, 2.)) / 2.0;
		double y = ((T * (xb - xc)) - (S * (xb - xa))) / (((ya - yb) * (xb - xc)) - ((yc - yb) * (xb - xa)));
		double x = ((y * (ya - yb)) - T) / (xb - xa);

		
		
		
		
//		double moveX = (dA * dA - dB * dB + nodeB[0] * nodeB[0]) / (2 * nodeB[0]);
//		double moveY = (nodeC[0] * nodeC[0] + nodeC[1] * nodeC[1] + dA * dA * dC * dC - 2*moveX*nodeC[0]) / (2*nodeC[1]);
//		double moveZ = Math.sqrt(Math.abs(dA * dA - moveX * moveX - moveY * moveY));
//		
		double[] result = {decimalScale(String.valueOf(x), 3), decimalScale(String.valueOf(y), 3)};
		
		return result;
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
	
	
	
	public static Beacon recordParser(BluetoothDevice device, byte[] scanRecord) {
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
	        
	    }
	    return param;
	}
}
