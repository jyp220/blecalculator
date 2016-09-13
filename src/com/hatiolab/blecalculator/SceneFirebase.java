package com.hatiolab.blecalculator;

import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class SceneFirebase {
	private Firebase myFirebaseRef;
	
	public SceneFirebase(String childName, final TextView textView) {
		
		myFirebaseRef = new Firebase("https://bluetoothscan.firebaseio.com/");
		
		myFirebaseRef.child(childName).addValueEventListener(new ValueEventListener() {
	           @Override
	           public void onDataChange(DataSnapshot snapshot) {
	        	   if(snapshot.getValue() != null) {
		        	   String text = snapshot.getValue().toString();
		        	   textView.setText(text);
	        	   }
	           }
	           @Override public void onCancelled(FirebaseError error) { }
	      });
	}
	
	public void setValue(double pitch, double roll, double yaw) {
//		myFirebaseRef.child("260").child("locations").child("aaaaa").child("lastUpdateTime").setValue(System.currentTimeMillis());
//		
////		myFirebaseRef.child("260").child("locations").child("aaaaa").child("props").child("pitch").setValue(pitch);
////		myFirebaseRef.child("260").child("locations").child("aaaaa").child("props").child("roll").setValue(roll);
//		myFirebaseRef.child("260").child("locations").child("aaaaa").child("props").child("yaw").setValue(yaw);
		
		myFirebaseRef.child("260").child("locations").child("bbbbb").child("lastUpdateTime").setValue(System.currentTimeMillis());
		
		myFirebaseRef.child("260").child("locations").child("bbbbb").child("props").child("pitch").setValue(pitch);
//		myFirebaseRef.child("260").child("locations").child("bbbbb").child("props").child("roll").setValue(roll);
//		myFirebaseRef.child("260").child("locations").child("bbbbb").child("props").child("yaw").setValue(yaw);
	}
}
