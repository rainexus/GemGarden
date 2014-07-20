package com.rainexus.gemgarden;

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

public class ProductListActivity extends Activity {
	static private int mPosition = ~0;
	private ImageAdapter mImageAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_price_list);
		
		Intent intent = getIntent();
		mPosition = intent.getIntExtra("position", mPosition);
		if (mPosition == ~0)
			throw new RuntimeException("position");
		
		mImageAdapter = new ImageAdapter(this,
	    		R.drawable.ic_flower,
	    		CustomerDB.GetCustomerInfoList().get(mPosition).GetProductPriceCountStrList(), ImageAdapter.DeFaultGridWitth, ImageAdapter.DeFaultGridHeight);
		GridView gridview = (GridView) findViewById(R.id.activity_price_list_gridview);
		gridview.setColumnWidth(ImageAdapter.DeFaultGridWitth);
	    gridview.setAdapter(mImageAdapter);

	    gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int fposition = position;
				final CustomerInfo customerInfo = CustomerDB.GetCustomerInfoList().get(mPosition);
	    		final ProductInfo priceInfo = customerInfo.GetProductPriceInfoList().get(fposition);
	    		LayoutInflater factory = LayoutInflater.from(ProductListActivity.this);  
	    		final View textEntryView = factory.inflate(R.layout.input_product_count_pop, null);  
	    		final EditText editText = (EditText) textEntryView.findViewById(R.id.input_count_pop_count_editTextName); 
	    		if (priceInfo.GetProductCount() > 0)
	    			editText.setText(Integer.toString(priceInfo.GetProductCount()));
	    		editText.setSelection(editText.getText().length());
	    		
	    		new AlertDialog.Builder(ProductListActivity.this)  
	    		.setTitle("Edit Quantity")  
	    		.setIcon(R.drawable.ic_launcher)  
	    		.setView(textEntryView)  
	    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {
                    	String input = editText.getText().toString().trim();
                    	if (input.length() == 0) {
                    		Toast.makeText(ProductListActivity.this, "Input is invalid!", Toast.LENGTH_SHORT).show();
                    		return;
                    	}
                    	
                    	int count = Integer.parseInt(input);
                    	priceInfo.SetProductCount(count);
                    	if (!customerInfo.CheckMaxKind(ProductListActivity.this)) {
                    		priceInfo.SetProductCount(0);
                    	}
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
	    
	    setTitle(CustomerDB.GetCustomerInfoList().get(mPosition).GetGeneralStr());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.product_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_product_operate: {
	    		startPriceOperateActivity();
	    		return true;
	    	}
	    	case R.id.action_product_preview: {
	    		startReportActivity();
	    		return true;
	    	}
	    	case R.id.action_product_trash: {
	    		TrashOrder();
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
	
	private void startPriceOperateActivity() {
		Intent intent = new Intent(this, ProductOperateActivity.class);
		if (mPosition == ~0)
			throw new RuntimeException("position");
		intent.putExtra("position", mPosition);
	    startActivity(intent);
	}
	
	private void startReportActivity() {
		if (CustomerDB.GetCustomerInfoList().get(mPosition).GetTotalCount() == 0) {
			Toast.makeText(ProductListActivity.this, "The order is empty!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(this, PreviewActivity.class);
		if (mPosition == ~0)
			throw new RuntimeException("position");
		intent.putExtra("position", mPosition);
	    startActivity(intent);
	}
	
	private void Refresh() {
		setTitle(CustomerDB.GetCustomerInfoList().get(mPosition).GetGeneralStr());
		mImageAdapter.Reset(CustomerDB.GetCustomerInfoList().get(mPosition).GetProductPriceCountStrList());
		mImageAdapter.notifyDataSetChanged();
	}
	
	private void TrashOrder() {
		CustomerDB.GetCustomerInfoList().get(mPosition).ClearOrder();
		Refresh();
	}
}
