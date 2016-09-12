package com.hatiolab.blecalculator;

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
}
