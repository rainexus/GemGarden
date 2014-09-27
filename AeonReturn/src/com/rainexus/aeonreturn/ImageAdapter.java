package com.rainexus.aeonreturn;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	public final static int DeFaultGridWitth = 350;
	public final static int DeFaultGridHeight = 300;
	protected int mTextSize = 0;
    protected Context mContext;
    protected int mIconID;    
    protected ArrayList<String> mStrList;
    protected int mGridWitth;
    protected int mGridHeight;

    public ImageAdapter(Context c, int iconID, ArrayList<String> strList, int gridWitth, int gridHeight, int textSize) {
        mContext = c;
        mIconID = iconID;
        mStrList = strList;
        mGridWitth = gridWitth;
        mGridHeight = gridHeight;
        mTextSize = textSize;
    }

    public void Reset(ArrayList<String> strList) {
    	 mStrList = strList;
    }
    
    public void SetTextSize(int textSize) {
    	mTextSize = textSize;
    	CustomerDB.WriteToXml();
    }
    
    public int getCount() {
        return mStrList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	LinearLayout linearLayout = new LinearLayout(mContext);
    	linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    	linearLayout.setLayoutParams(new GridView.LayoutParams(mGridWitth, mGridHeight));
    	linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		
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