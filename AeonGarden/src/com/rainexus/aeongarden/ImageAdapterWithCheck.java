package com.rainexus.aeongarden;

import java.util.ArrayList;
import java.util.HashSet;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageAdapterWithCheck extends ImageAdapter {
	HashSet<Integer> mChecks = new HashSet<Integer>();
	
	public ImageAdapterWithCheck(Context c, int iconID, ArrayList<String> strList, int gridWitth, int gridHeight, int textSizeIncrease) {
		super(c, iconID, strList, gridWitth, gridHeight, textSizeIncrease);
		// TODO Auto-generated constructor stub
	}

	public HashSet<Integer> GetChecks() {
		return mChecks;
	}

	@Override
	public void Reset(ArrayList<String> strList) {
   	 super.Reset(strList);
   	 mChecks.clear();
   }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	LinearLayout linearLayout = new LinearLayout(mContext);
    	linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    	linearLayout.setLayoutParams(new GridView.LayoutParams(mGridWitth, mGridHeight));
    	linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		
		CheckBox checkBox = new CheckBox(mContext);
        checkBox.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(checkBox);
        checkBox.setFocusable(false);
        
        final int fposition = position;
        checkBox.setOnClickListener(new OnClickListener() {
      	  @Override
      	  public void onClick(View v) {
                      //is chkIos checked?
      		if (((CheckBox) v).isChecked()) {
      			mChecks.add(fposition);
      		} else {
      			mChecks.remove(fposition);
      		}
      	  }
      	});
	
    	ImageView imageView;
        imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(mIconID);
        linearLayout.addView(imageView);
        
        TextView textView = new TextView(mContext);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(mTextSize);
        textView.setText(mStrList.get(position));
        linearLayout.addView(textView);
        
        return linearLayout;
    }
}
