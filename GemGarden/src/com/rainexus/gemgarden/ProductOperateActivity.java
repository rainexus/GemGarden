package com.rainexus.gemgarden;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class ProductOperateActivity extends Activity {
	static private int mPosition = ~0;
	private ImageAdapterWithCheck mImageAdapterWithCheck = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_price_operate);
		
		Intent intent = getIntent();
		mPosition = intent.getIntExtra("position", mPosition);
		if (mPosition == ~0)
			throw new RuntimeException("position");
		
		mImageAdapterWithCheck = new ImageAdapterWithCheck(this,
	    		R.drawable.ic_flower,
	    		CustomerDB.GetCustomerInfoList().get(mPosition).GetProductPriceStrList(), ImageAdapter.DeFaultGridWitth, ImageAdapter.DeFaultGridHeight);
		GridView gridview = (GridView) findViewById(R.id.activity_price_operate_gridview);
		gridview.setColumnWidth(ImageAdapter.DeFaultGridWitth);
	    gridview.setAdapter(mImageAdapterWithCheck);

	    gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LayoutInflater factory = LayoutInflater.from(ProductOperateActivity.this);
	    		final View textEntryView = factory.inflate(R.layout.input_product_pop, null);  
	    		final EditText priceEditText = (EditText) textEntryView.findViewById(R.id.input_product_pop_price_editTextName);
	    		final EditText nameEditText = (EditText) textEntryView.findViewById(R.id.input_product_pop_name_editTextNum);
	    		
	    		final ProductInfo priceInfo = CustomerDB.GetCustomerInfoList().get(mPosition).GetProductPriceInfoList().get(position);
	    		priceEditText.setText(Integer.toString(priceInfo.GetProductPrice()));
	    		priceEditText.setSelection(priceEditText.getText().length());
	    		nameEditText.setText(priceInfo.GetProductName());
	    		
	    		new AlertDialog.Builder(ProductOperateActivity.this)
	    		.setTitle("Input product information")
	    		.setIcon(R.drawable.ic_launcher)  
	    		.setView(textEntryView)
	    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {
                    	String name = nameEditText.getText().toString().trim();
                    	int price = Integer.parseInt(priceEditText.getText().toString().trim());
                    	if (!priceInfo.GetProductName().equals(name) && !CheckPrice(name, price)) {
                    		return;
                    	}
                    	
                    	priceInfo.SetProductName(name);
                		priceInfo.SetProductPrice(price);
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
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.product_operate_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_product_new: {
	    		LayoutInflater factory = LayoutInflater.from(this);
	    		final View textEntryView = factory.inflate(R.layout.input_product_pop, null);
	    		final EditText priceEditText = (EditText) textEntryView.findViewById(R.id.input_product_pop_price_editTextName);
	    		final EditText nameEditText = (EditText) textEntryView.findViewById(R.id.input_product_pop_name_editTextNum);
	    		
	    		new AlertDialog.Builder(ProductOperateActivity.this)
	    		.setTitle("Input product information")  
	    		.setIcon(R.drawable.ic_launcher)  
	    		.setView(textEntryView)  
	    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {
                    	String name = nameEditText.getText().toString().trim();
                    	String price = priceEditText.getText().toString().trim();
                    	if (name.length() == 0 || price.length() == 0) {
                    		Toast.makeText(ProductOperateActivity.this, "Input is invalid!", Toast.LENGTH_SHORT).show();
                    		return;
                    	}

                    	int nPrice = Integer.parseInt(price);
                    	if (CheckPrice(name, nPrice)) {
                    		CustomerDB.GetCustomerInfoList().get(mPosition).AddPriceInfo(new ProductInfo(name, nPrice));
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
	    	case R.id.action_product_remove: {
	    		if (mImageAdapterWithCheck.GetChecks().size() != 0) {
	    			CustomerInfo customerInfo = CustomerDB.GetCustomerInfoList().get(mPosition);
	    			ArrayList<ProductInfo> priceList = customerInfo.GetProductPriceInfoList();
	    			ArrayList<String> strList = new ArrayList<String>();
		    		Iterator<Integer> it = mImageAdapterWithCheck.GetChecks().iterator();
		    		while (it.hasNext()) {
		    			int fposition = it.next();
		    			strList.add(priceList.get(fposition).GetProductName());
		    		}
		    		
		    		for (int i=0; i<strList.size(); ++i) {
		    			customerInfo.RemovePriceInfo(strList.get(i));
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
	
	void Refresh() {
		CustomerDB.BroadCastPriceInfoList(CustomerDB.GetCustomerInfoList().get(mPosition).GetProductPriceInfoList());
		mImageAdapterWithCheck.Reset(CustomerDB.GetCustomerInfoList().get(mPosition).GetProductPriceStrList());
		mImageAdapterWithCheck.notifyDataSetChanged();
	}
	
	boolean CheckPrice(String name, int price) {
		ArrayList<ProductInfo> priceInfoList = CustomerDB.GetCustomerInfoList().get(mPosition).GetProductPriceInfoList();
		for (int i=0; i<priceInfoList.size(); ++i) {
			if (name.equals(priceInfoList.get(i).GetProductName())) {
				Toast.makeText(ProductOperateActivity.this, name + " exists!", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		return true;
	}
}
