package com.hatiolab.blecalculator;

import java.util.ArrayList;
import java.util.List;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Setting extends Activity implements OnItemSelectedListener, ValueEventListener {
	
	private Firebase myFirebaseRef;
	private Spinner spnScene, spnType, spnInterval;
	private Button btnData;
	private List<String> sceneList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Firebase.setAndroidContext(this);

		setContentView(R.layout.setting);

		spnScene = (Spinner) findViewById(R.id.spn_select_scene);
		spnType = (Spinner) findViewById(R.id.spn_select_type);
		spnInterval = (Spinner) findViewById(R.id.spn_select_intervavl);
		btnData = (Button)findViewById(R.id.intent_button);
		
		spnScene.setOnItemSelectedListener(this);

		
		// 파이어 베이스 주소 Set
		myFirebaseRef = new Firebase("https://bluetoothscan.firebaseio.com/");

		// 파이어 베이스 Value 가져오기
		myFirebaseRef.getRoot().addValueEventListener(this);
		
		btnData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.putExtra("scene",spnScene.getSelectedItem().toString());
				i.putExtra("type", spnType.getSelectedItem().toString());
				i.putExtra("interval", spnInterval.getSelectedItem().toString());
				startActivity(i);
			}
		});
	}
	
	@Override
	public void onDataChange(DataSnapshot snapshot) {
		// TODO Auto-generated method stub
		
		// 스피너의 목록이 없을 때에만 불러옴
		if(spnScene.getChildCount() == 0){
			for (DataSnapshot child : snapshot.getChildren()) {
				sceneList.add(child.getKey());
			}

			setSceneSpinnerAdapter(this);
		}
	}
	
	private void setSceneSpinnerAdapter(Activity context) {
		ArrayAdapter<String> sceneAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
				sceneList);
		sceneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnScene.setAdapter(sceneAdapter);
	}

	@Override
	public void onCancelled(FirebaseError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

}
