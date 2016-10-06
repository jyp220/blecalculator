package com.hatiolab.blecalculator.widget;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hatiolab.blecalculator.model.Scene;

public class SceneFirebase {
	private Firebase myFirebaseRef;
	private String childName;
	
	public SceneFirebase(String name) {
		
		myFirebaseRef = new Firebase("https://bluetoothscan.firebaseio.com/");
		childName = name;
//		myFirebaseRef.child(childName).addValueEventListener(new ValueEventListener() {
//	           @Override
//	           public void onDataChange(DataSnapshot snapshot) {
//	        	   if(snapshot.getValue() != null) {
//		        	   String text = snapshot.getValue().toString();
//	        	   }
//	           }
//	           @Override public void onCancelled(FirebaseError error) { }
//	      });
	}
	
	public Firebase getFirebase() {
		return myFirebaseRef;
	}
	
//	public void setValue(double pitch, double roll, double yaw, String uuid) {
//		myFirebaseRef.child("260").child("locations").child(uuid).child("lastUpdateTime").setValue(System.currentTimeMillis());
//		myFirebaseRef.child("260").child("locations").child(uuid).child("updateInterval").setValue(50000);
//		
//		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("pitch").setValue(pitch);
//		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("roll").setValue(roll);
//		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("yaw").setValue(yaw);
//		
////		myFirebaseRef.child("260").child("locations").child("bbbbb").child("lastUpdateTime").setValue(System.currentTimeMillis());
////		
////		myFirebaseRef.child("260").child("locations").child("bbbbb").child("props").child("pitch").setValue(pitch);
//////		myFirebaseRef.child("260").child("locations").child("bbbbb").child("props").child("roll").setValue(roll);
//////		myFirebaseRef.child("260").child("locations").child("bbbbb").child("props").child("yaw").setValue(yaw);
//	}
	
	public void setPosition(double x, double y, double pitch, double roll, double yaw, String uuid) {
		myFirebaseRef.child("260").child("locations").child(uuid).child("lastUpdateTime").setValue(System.currentTimeMillis());
		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("center").child("x").setValue(x);
		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("center").child("y").setValue(y);
		
		myFirebaseRef.child("260").child("locations").child(uuid).child("updateInterval").setValue(50000);
		
		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("pitch").setValue(pitch);
		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("roll").setValue(roll);
		myFirebaseRef.child("260").child("locations").child(uuid).child("props").child("yaw").setValue(yaw);
	}
	
	public void setStandardDeviation(String address, double avg, double distance, double standardDeviation) {
		myFirebaseRef.child(address).child("average").setValue(avg);
		myFirebaseRef.child(address).child("distance").setValue(distance);
		myFirebaseRef.child(address).child("standardDeviation").setValue(standardDeviation);
	}
}
