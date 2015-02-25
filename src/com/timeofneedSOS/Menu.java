package com.timeofneedSOS;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Menu extends ListActivity{

	String[] values = new String[]{"Settings", "SOS Phone Numbers"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = values[position];
		
		if(item == "Settings"){
			startActivity(new Intent("com.sos.Settings"));
		} else if (item == "SOS Phone Numbers") {
			startActivity(new Intent("com.sos.PhoneNumbers"));
		}

		super.onListItemClick(l, v, position, id);
	}

}
