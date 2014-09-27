package com.rainexus.aeonreturn;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CustomerOperateActivity extends Activity {
	private ImageAdapterWithCheck mImageAdapterWithCheck = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_edit);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		double gridviewWidthUnit = 0;
		if (metrics.heightPixels > metrics.widthPixels)
			gridviewWidthUnit = metrics.widthPixels /10;
		else
			gridviewWidthUnit = metrics.heightPixels /10;
		
		mImageAdapterWithCheck = new ImageAdapterWithCheck(this,
					R.drawable.ic_customer1, CustomerDB.GetCustomerStrList(),
					(int)(gridviewWidthUnit * 3), (int)(gridviewWidthUnit * 3), CustomerDB.globalCustomerOperateActivityTextSize);
		
		GridView gridview = (GridView) findViewById(R.id.activity_customer_edit_gridview);
	    gridview.setAdapter(mImageAdapterWithCheck);
	    gridview.setNumColumns((int)(metrics.widthPixels / 3.3 / gridviewWidthUnit));
	    gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	    		final CustomerInfo customerInfo = CustomerDB.GetCustomerInfoList().get(position);
	    		LayoutInflater factory = LayoutInflater.from(CustomerOperateActivity.this);  
	    		final View textEntryView = factory.inflate(R.layout.input_customer_pop, null);  
	    		final EditText editText = (EditText) textEntryView.findViewById(R.id.input_customer_pop_price_editTextName); 
	    		editText.setText(customerInfo.GetName());
	    		editText.setSelection(editText.getText().length());
	    		
	    		new AlertDialog.Builder(CustomerOperateActivity.this)  
	    		.setTitle("Edit customer name")  
	    		.setIcon(R.drawable.ic_launcher)  
	    		.setView(textEntryView)  
	    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {
                    	String newName = editText.getText().toString().trim();
                    	if (!customerInfo.GetName().equals(newName) && !CheckCustomerName(newName)) {
                    		return;
                    	}
                    	
                    	customerInfo.SetName(newName);
                    	CustomerDB.WriteToXml();
                    	Refresh();
                    }
                })  
	    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {
                    }  
                })  
	    		.show();
			}
	    });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
			if (CustomerDB.globalCustomerOperateActivityTextSize > 10) {
				--CustomerDB.globalCustomerOperateActivityTextSize;
				Refresh();
			}
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
			if (CustomerDB.globalCustomerOperateActivityTextSize < 80) {
				++CustomerDB.globalCustomerOperateActivityTextSize;
				Refresh();
			}
			return true;
		} 
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.customer_operate_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_customer_new: {
	    		LayoutInflater factory = LayoutInflater.from(this);  
	    		final View textEntryView = factory.inflate(R.layout.input_customer_pop, null);  
	    		final EditText editText = (EditText) textEntryView.findViewById(R.id.input_customer_pop_price_editTextName);
	    		
	    		new AlertDialog.Builder(CustomerOperateActivity.this)  
	    		.setTitle("Input customer name")  
	    		.setIcon(R.drawable.ic_launcher)  
	    		.setView(textEntryView)
	    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                    	String newName = editText.getText().toString().trim();
                    	if (CheckCustomerName(newName)) {
                    		CustomerDB.AddCustomerInfo(new CustomerInfo(newName,  new ArrayList<ProductInfo>()));
                    		CustomerDB.WriteToXml();
                        	Refresh();
                    	}
                    }  
                })  
	    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {
                    }
                })  
	    		.show();
	    		
	    		return true;
	    	}
	    	case R.id.action_customer_remove: {
	    		if (mImageAdapterWithCheck.GetChecks().size() != 0) {
		    		ArrayList<String> strList = new ArrayList<String>();
		    		Iterator<Integer> it = mImageAdapterWithCheck.GetChecks().iterator();
		    		while (it.hasNext()) {
		    			int fposition = it.next();
			    		CustomerInfo customerInfo = CustomerDB.GetCustomerInfoList().get(fposition);
		    			strList.add(customerInfo.GetName());
		    		}
		    		
		    		for (int i=0; i<strList.size(); ++i) {
		    			CustomerDB.RemoveCustomerInfo(strList.get(i));
		    		}
		    		CustomerDB.WriteToXml();
		    		Refresh();
	    		}
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
	
	private void Refresh() {
		mImageAdapterWithCheck.Reset(CustomerDB.GetCustomerStrList());
		mImageAdapterWithCheck.SetTextSize(CustomerDB.globalCustomerOperateActivityTextSize);
    	mImageAdapterWithCheck.notifyDataSetChanged();
	}
	
	private boolean CheckCustomerName(String customerName) {
		if (customerName.length() == 0) {
			Toast.makeText(CustomerOperateActivity.this, "Input nothing!", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			ArrayList<String> customerStrList = CustomerDB.GetCustomerStrList();
			for (int i = 0; i<customerStrList.size(); ++i) {
				if (customerStrList.get(i).equals(customerName)) {
					Toast.makeText(CustomerOperateActivity.this, customerName + " exists!", Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		}
		
		return true;
	}
}
