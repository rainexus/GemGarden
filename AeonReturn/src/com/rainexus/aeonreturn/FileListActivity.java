package com.rainexus.aeonreturn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FileListActivity extends Activity {
	ArrayList<String> mfileList = GetFileList();
	ImageAdapter mImageAdapter = new ImageAdapter(FileListActivity.this, mfileList);
	ArrayList<CheckBox> mCheckBoxList = new ArrayList<CheckBox>();
	HashSet<Integer> mChecks = new HashSet<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_list);
		
		ListView listview = (ListView) findViewById(R.id.file_list_listview);
		listview.setAdapter(mImageAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewImage(mfileList.get(position));
			}
	    });
	}
	
	void Refresh() {
		mfileList = GetFileList();
		mImageAdapter.Reset(mfileList);
		mImageAdapter.notifyDataSetChanged();
	}
	
	void TryShare(ArrayList<String> fileNames) {
		final ArrayList<String> ffileNames = fileNames;
		AlertDialog.Builder builder = new AlertDialog.Builder(FileListActivity.this);
	    builder.setTitle("This order has be saved, you can share now!");
	    builder.setIcon(R.drawable.ic_launcher);
	    builder.setItems(new CharSequence[]
	            {"Share Image", "Share File", "Share Image and File", "Cancel"},
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    switch (which) {
	                        case 0: {
	                        	Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	                        	ArrayList<Uri> uris = new ArrayList<Uri>();
	                        	for(int i=0; i<ffileNames.size(); i++) {
	                        		uris.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AeonReturn" + "/image/" + ffileNames.get(i) + ".jpg")));
	                        	}
	                            sharingIntent.setType("image/jpeg");
	                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uris);
	                            FileListActivity.this.startActivityForResult(Intent.createChooser(sharingIntent, "Share Image using"), 1234);
	                            break;
	                        }
	                        case 1: {
	                        	Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	                        	ArrayList<Uri> uris = new ArrayList<Uri>();
	                        	for(int i=0; i<ffileNames.size(); i++) {
	                        		uris.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AeonReturn" + "/csv/" + ffileNames.get(i) + ".csv")));
	                        	}
	                        	sharingIntent.setType("plain/text");
	                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uris);
	                            FileListActivity.this.startActivityForResult(Intent.createChooser(sharingIntent, "Share File using"), 1234);
	                            break;
	                        }
	                        case 2: {
	                        	Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	                        	ArrayList<Uri> uris = new ArrayList<Uri>();
	                        	for(int i=0; i<ffileNames.size(); i++) {
	                        		uris.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AeonReturn" + "/image/" + ffileNames.get(i) + ".jpg")));
	                        	}
	                        	for(int i=0; i<ffileNames.size(); i++) {
	                        		uris.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AeonReturn" + "/csv/" + ffileNames.get(i) + ".csv")));
	                        	}
	                        	sharingIntent.setType("plain/text");
	                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uris);
	                            FileListActivity.this.startActivityForResult(Intent.createChooser(sharingIntent, "Share Image and File using"), 1234); 
	                            break;
	                        }
	                        case 3: {
	                            break;
	                        }
	                    }
	                }
	            });
	    builder.create().show();
	}
	
	void TryDelete(ArrayList<String> fileNames) {
		final ArrayList<String> ffileNames = fileNames;
		new AlertDialog.Builder(FileListActivity.this)  
		.setTitle("Will be deleted permanently!")  
		.setIcon(R.drawable.ic_launcher)  
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog,  
                    int whichButton) {
            	for (int i=0; i<ffileNames.size(); ++i) {
            		{
            			File file = new File(Environment.getExternalStorageDirectory() + "/AeonReturn" + "/image/" + ffileNames.get(i) + ".jpg");
                    	file.delete();
            		}
            		
            		{
            			File file = new File(Environment.getExternalStorageDirectory() + "/AeonReturn" + "/csv/" + ffileNames.get(i) + ".csv");
                    	file.delete();
            		}
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
	
	void ViewImage(String imagefile) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setType("image/jpeg");
		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/AeonReturn" + "/image/" + imagefile + ".jpg")), "image/*");
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.file_list_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.action_file_select_all: {
		    	SelectAll();
	    		return true;
	    	}
	    	case R.id.action_file_share: {
	    		ArrayList<String> fileNames = new ArrayList<String>();
	    		Iterator<Integer> it = mImageAdapter.GetChecks().iterator();
	    		while (it.hasNext()) {
	    			int fposition = it.next();
	    			fileNames.add(mfileList.get(fposition));
	    		}
	    		
	    		if (fileNames.size() == 0) {
	    			Toast.makeText(FileListActivity.this, "None seleced!", Toast.LENGTH_SHORT).show();
	    			return true;
	    		}
	    		TryShare(fileNames);
	    		return true;
	    	}
	    	case R.id.action_file_delete: {
	    		ArrayList<String> fileNames = new ArrayList<String>();
	    		Iterator<Integer> it = mImageAdapter.GetChecks().iterator();
	    		while (it.hasNext()) {
	    			int fposition = it.next();
	    			fileNames.add(mfileList.get(fposition));
	    		}
	    		
	    		if (fileNames.size() == 0) {
	    			Toast.makeText(FileListActivity.this, "None seleced!", Toast.LENGTH_SHORT).show();
	    			return true;
	    		}
	    		TryDelete(fileNames);
	    		return true;
	    	}
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private ArrayList<String> GetFileList() {
		ArrayList<String> fileList = new ArrayList<String>();
		
		File dir = new File(Environment.getExternalStorageDirectory() + "/AeonReturn/csv/");
	    if (dir.exists()) {
	        File[] files = dir.listFiles();
	        for (int i = 0; i < files.length; ++i) {
	            File file = files[i];
	            if (!file.isDirectory()) {
	            	String filename = file.getName();
	            	String name = filename.substring(0,filename.lastIndexOf("."));
	            	fileList.add(name);
	            }
	        }
	    }
	    
	    return fileList;
	}
	
	public void SelectAll() {
    	boolean bSelectedAll = true;
    	for (int i=0; i<mCheckBoxList.size(); ++i) {
    		if (!mCheckBoxList.get(i).isChecked()) {
    			bSelectedAll = false;
    			break;
    		}
    	}
    	
    	if (bSelectedAll) {
    		mChecks.clear();
    		for (int i=0; i<mCheckBoxList.size(); ++i) {
    			mCheckBoxList.get(i).setChecked(false);
        	}
    	} else {
    		mChecks.clear();
    		for (int i=0; i<mCheckBoxList.size(); ++i) {
    			mCheckBoxList.get(i).setChecked(true);
    			mChecks.add(i);
        	}
    	}
    }
	
	private class ImageAdapter extends BaseAdapter {
		public final static int DeFaultGridHeight = 100;
	    protected Context mContext;
	    protected ArrayList<String> mStrList;

	    public ImageAdapter(Context c, ArrayList<String> strList) {
	        mContext = c;
	        mStrList = strList;
	    }
	    
	    public void Reset(ArrayList<String> strList) {
	    	mStrList = strList;
	    	mChecks.clear();
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
	    
	    public HashSet<Integer> GetChecks() {
			return mChecks;
		}

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	LinearLayout linearLayout = new LinearLayout(mContext);
	    	linearLayout.setOrientation(LinearLayout.HORIZONTAL);
	    	linearLayout.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeFaultGridHeight));
	    	linearLayout.setGravity(Gravity.CENTER_VERTICAL);

	    	CheckBox checkBox = new CheckBox(mContext);
	    	mCheckBoxList.add(checkBox);
	        checkBox.setLayoutParams(new ViewGroup.LayoutParams(
	                ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT));
	        linearLayout.addView(checkBox);
	        checkBox.setFocusable(false);
	        
	        final int fposition = position;
	        checkBox.setOnClickListener(new OnClickListener() {
	      	  @Override
	      	  public void onClick(View v) {
	      		if (((CheckBox) v).isChecked()) {
	      			mChecks.add(fposition);
	      		} else {
	      			mChecks.remove(fposition);
	      		}
	      	  }
	      	});

	        TextView textView = new TextView(mContext);
	        textView.setLayoutParams(new ViewGroup.LayoutParams(
	                ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT));
	        textView.setText(mStrList.get(position));
	        linearLayout.addView(textView);

	        return linearLayout;
	    }    
	}
}
