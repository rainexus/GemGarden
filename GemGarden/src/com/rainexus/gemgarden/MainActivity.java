package com.rainexus.gemgarden;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private ImageAdapter mImageAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int gridviewWidth = 0;
		if (metrics.heightPixels > metrics.widthPixels)
			gridviewWidth = metrics.widthPixels /9;
		else
			gridviewWidth = metrics.heightPixels /9;
		gridviewWidth *= 4;
		
		mImageAdapter = new ImageAdapter(this,
	    		R.drawable.ic_customer1, CustomerDB.GetCustomerStrList(), gridviewWidth,
	    			ImageAdapter.DeFaultGridHeight, CustomerDB.globalMainActivityTextSize);
		
		GridView gridview = (GridView) findViewById(R.id.activity_main_gridview);
	    gridview.setAdapter(mImageAdapter);
	    gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startPriceListActivity(position);
			}
	    });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
			if (CustomerDB.globalMainActivityTextSize > 10) {
				--CustomerDB.globalMainActivityTextSize;
				Refresh();
			}
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
			if (CustomerDB.globalMainActivityTextSize < 80) {
				++CustomerDB.globalMainActivityTextSize;
				Refresh();
			}
			return true;
		} 
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int id = item.getItemId(); 
	    switch (id) {
	    	case R.id.action_customer_history: {
	    		startFileListActivity();
	    		return true;
	    	}
	    	case R.id.action_customer_operate: {
	    		startCustomerOperateActivity();
	    		return true;
	    	}
	    	case R.id.action_customer_exit: {
	    		System.exit(0);
	    		return true;
	    	}
	    	case R.id.action_customer_info: {
	    		Info();
	    		return true;
	    	}
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Refresh();
	}

	private void startCustomerOperateActivity() {
		Intent intent = new Intent(this, CustomerOperateActivity.class);
	    startActivity(intent);
	}
	
	private void startPriceListActivity(int position) {
		Intent intent = new Intent(this, ProductListActivity.class);
		intent.putExtra("position", position);
	    startActivity(intent);
	}
	
	private void startFileListActivity() {
		Intent intent = new Intent(this, FileListActivity.class);
	    startActivity(intent);
	}
	
	private void Info() {
		new AlertDialog.Builder(MainActivity.this)  
		.setTitle("Version 1.0.003\nCopyright by Rain")  
		.setIcon(R.drawable.ic_launcher)  
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog,  
                    int whichButton) {
            }
        })
		.show();
	}
	
	private void Refresh() {
		mImageAdapter.SetTextSize(CustomerDB.globalMainActivityTextSize);
		mImageAdapter.Reset(CustomerDB.GetCustomerStrList());
		mImageAdapter.notifyDataSetChanged();
	}
}
