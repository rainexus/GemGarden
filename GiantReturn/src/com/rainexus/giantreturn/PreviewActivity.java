package com.rainexus.giantreturn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PreviewActivity extends Activity {
	 public static final String TAG = "ImgDisplayActivity";
	    private ImageView imgDisPlay;
	    Matrix fullMatrix = new Matrix();
	    Matrix oriMatrix = new Matrix();
	    LinearLayout layout;
	    GestureDetector gestureDetector = null;
	    ArrayList<ProductInfo> mPriceInfoList = null;
	    CustomerInfo mCustomerInfo = null;
	    int mPosition = ~0;
	    String fileName = null;
	    static Bitmap template = null;
	    static Bitmap newb = null;
	    String date = null;
	                                                                                                            
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_preview);
	        
	        Intent intent = getIntent();
			mPosition = intent.getIntExtra("position", mPosition);
			if (mPosition == ~0)
				throw new RuntimeException("position");
			
			mCustomerInfo = CustomerDB.GetCustomerInfoList().get(mPosition);
			mPriceInfoList  = mCustomerInfo.GetProductPriceInfoList();

	        layout = (LinearLayout) findViewById(R.id.flayout_img_display);
	        
	        imgDisPlay = (ImageView) findViewById(R.id.img_display);
	        //imgDisPlay.setImageResource(R.drawable.template);
	        createBitmap();
	        imgDisPlay.setOnTouchListener(new TouchListener());
	       
	        oriMatrix.set(imgDisPlay.getImageMatrix());
	        
	        ViewTreeObserver vto = layout.getViewTreeObserver(); 
	        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
	            @Override 
	            public void onGlobalLayout() {
	                int imageWidth = imgDisPlay.getDrawable().getIntrinsicWidth();
	    	        int layoutWidth = layout.getMeasuredWidth();  
	    	        float scaleWidth = ((float)layoutWidth)/imageWidth;	       
	    	        fullMatrix.set(oriMatrix);
	    	        fullMatrix.postScale(scaleWidth, scaleWidth);
	    	        imgDisPlay.setImageMatrix(fullMatrix);
	            } 
	        });
	        
	        gestureDetector = new GestureDetector(PreviewActivity.this, new GestureListener());
	    }
	    
	    private void createBitmap() {
	    	if (template == null)
	    		template = BitmapFactory.decodeResource(this.getResources(), R.drawable.im_template);
	    	if (newb == null)
	    		newb  = Bitmap.createBitmap( template.getWidth(), template.getHeight(), Config.ARGB_4444);
	    	
	    	Canvas canvasTemp = new Canvas(newb);
	    	Paint p = new Paint();
	    	canvasTemp.drawBitmap(template, 0, 0, p);
	    	
	    	p.setColor(Color.BLACK);
	        p.setTextSize(28);
	        p.setStrokeWidth(4);
	        p.setTypeface(Typeface.DEFAULT_BOLD);
	        
	        int nStartX = template.getWidth() / 7;
	        int nEndX = template.getWidth() - template.getWidth() / 7;
	        
	        int nStartY =  (int) (template.getHeight() / 6.5);
	        int nEndY =  template.getHeight() - template.getHeight() / 7;
	        int nStepY = (nEndY - nStartY) / 22;
	        
	        nEndY -= nStepY/2;
	        nEndY -= nStepY;
	        nStartY += nStepY;
	        nStartY += nStepY;
	        nStartY += nStepY/2;
	        
	        int nFirstY = nStartY;
	        int nLastY = nFirstY;
	        for (int y = nStartY; y <= nEndY; y += nStepY) {
	        	canvasTemp.drawLine(nStartX, y, nEndX, y, p);
	        	nLastY = y;
	        }
	        
	        // 1, 4, 2, 2, 2
	        int nStepX = (nEndX - nStartX) / 11;
	        // no.
	        canvasTemp.drawLine(nStartX, nFirstY, nStartX, nLastY, p);
	        
	        // des
	        canvasTemp.drawLine(nStartX + nStepX, nFirstY, nStartX + nStepX, nLastY, p);
	        
	        // price
	        canvasTemp.drawLine(nStartX + nStepX * 5, nFirstY, nStartX + nStepX * 5, nLastY, p);
	        
	        // quality
	        canvasTemp.drawLine(nStartX + nStepX * 7, nFirstY, nStartX + nStepX * 7, nLastY, p);
	        
	        // amount
	        canvasTemp.drawLine(nStartX + nStepX * 9, nFirstY, nStartX + nStepX * 9, nLastY, p);
	        
	        // right
	        canvasTemp.drawLine(nEndX, nFirstY, nEndX, nLastY, p);
	        
	        // customer
	        date = (DateFormat.format("dd-MM-yyyy kk:mm:ss", new java.util.Date()).toString());
	        String info = mCustomerInfo.GetGeneralStrWithDate(date);
	        fileName = info;
	        fileName = fileName.replace(' ', '_');
	        fileName = fileName.replace(',', '_');
	        
	        canvasTemp.drawText("DELIVERY RETURN", nStartX, nFirstY - nStepY/5 - nStepY - nStepY, p);
	        canvasTemp.drawText("To: " + mCustomerInfo.GetName(), nStartX, nFirstY - nStepY/5 - nStepY, p);
	        canvasTemp.drawText("Date: " + date, nStartX, nFirstY - nStepY/5 , p);
	        
	        // cols
	        /*
	        canvasTemp.drawText("NO.", nStartX + nStepX / 4, nFirstY + nStepY - nStepY/10 , p);
	        canvasTemp.drawText("Description", nStartX + nStepX + (nStepX*4) / 4, nFirstY + nStepY - nStepY/10, p);
	        canvasTemp.drawText("Price(RM)", nStartX + nStepX*5 + (nStepX*2) / 8, nFirstY + nStepY - nStepY/10, p);
	        canvasTemp.drawText("Quantity", nStartX + nStepX*7 + (nStepX*2) / 5, nFirstY + nStepY - nStepY/10, p);
	        canvasTemp.drawText("Amount(RM)", nStartX + nStepX*9 + (nStepX*2) / 32, nFirstY + nStepY - nStepY/10, p);

	        p.setTypeface(Typeface.DEFAULT);
	        canvasTemp.drawText("Total", nStartX + nStepX + (nStepX*4) / 4, nFirstY + nStepY * 2 - nStepY/10 + nStepY * CustomerInfo.mMaxKind, p);
	        canvasTemp.drawText(Integer.toString(mCustomerInfo.GetTotalCount()), nStartX + nStepX*7 + (nStepX*2) / 5, nFirstY + nStepY * 2 - nStepY/10 + nStepY * CustomerInfo.mMaxKind, p);
	        canvasTemp.drawText(CustomerInfo.Cents2RM(mCustomerInfo.GetTotalPrice()), nStartX + nStepX*9 + (nStepX*2) / 32, nFirstY + nStepY * 2 - nStepY/10 + nStepY * CustomerInfo.mMaxKind, p);
	        */
	        
	        canvasTemp.drawText("NO.", nStartX + nStartX/10, nFirstY + nStepY - nStepY/10 , p);
	        canvasTemp.drawText("Description", nStartX + nStepX + nStartX/10, nFirstY + nStepY - nStepY/10, p);
	        canvasTemp.drawText("Price(RM)", nStartX + nStepX*5 + nStartX/10, nFirstY + nStepY - nStepY/10, p);
	        canvasTemp.drawText("Quantity", nStartX + nStepX*7 + nStartX/10, nFirstY + nStepY - nStepY/10, p);
	        canvasTemp.drawText("Amount(RM)", nStartX + nStepX*9 + nStartX/32, nFirstY + nStepY - nStepY/10, p);
	        canvasTemp.drawText("Total", nStartX + nStepX + nStartX/10, nFirstY + nStepY * 2 - nStepY/10 + nStepY * CustomerInfo.mMaxKind, p);
	        canvasTemp.drawText(Integer.toString(-mCustomerInfo.GetTotalCount()), nStartX + nStepX*7 + nStartX/10, nFirstY + nStepY * 2 - nStepY/10 + nStepY * CustomerInfo.mMaxKind, p);
	        canvasTemp.drawText(CustomerInfo.Cents2RM(-mCustomerInfo.GetTotalPrice()), nStartX + nStepX*9 + nStartX/10, nFirstY + nStepY * 2 - nStepY/10 + nStepY * CustomerInfo.mMaxKind, p);
	        
	        p.setTypeface(Typeface.DEFAULT);
	        // products
	        int nIdx = 0;
	        for (int i=0; i<mPriceInfoList.size(); ++i) {
	        	if (mPriceInfoList.get(i).GetProductCount() > 0){
	        		canvasTemp.drawText(Integer.toString(nIdx + 1), nStartX + nStartX/10, nFirstY + nStepY*(nIdx+2)- nStepY/10 , p);
			        canvasTemp.drawText(mPriceInfoList.get(i).GetProductName(), nStartX + nStepX + nStartX/10, nFirstY + nStepY*(nIdx+2) - nStepY/10, p);
			        canvasTemp.drawText(CustomerInfo.Cents2RM(mPriceInfoList.get(i).GetProductPrice()), nStartX + nStepX*5 + nStartX/10, nFirstY + nStepY*(nIdx+2) - nStepY/10, p);
			        canvasTemp.drawText(Integer.toString(-mPriceInfoList.get(i).GetProductCount()), nStartX + nStepX*7 + nStartX/10, nFirstY + nStepY*(nIdx+2) - nStepY/10, p);
			        canvasTemp.drawText(CustomerInfo.Cents2RM(-mPriceInfoList.get(i).GetProductPrice()*mPriceInfoList.get(i).GetProductCount()), nStartX + nStepX*9 + nStartX/10, nFirstY + nStepY*(nIdx+2) - nStepY/10, p);
			        ++nIdx;
	        	}
	        }
	        
	        p.setTextSize(19);
	        canvasTemp.drawText("file://" + fileName, nStartX, template.getHeight() - nStepY/2, p);
	        imgDisPlay.setImageBitmap(newb);
	    }
	    
	    private final class TouchListener implements OnTouchListener {
	          
	        /** 记录是拖拉照片模式还是放大缩小照片模式 */
	        private int mode = 0;// 初始状态 
	        /** 拖拉照片模式 */
	        private static final int MODE_DRAG = 1;
	        /** 放大缩小照片模式 */
	        private static final int MODE_ZOOM = 2;
	          
	        /** 用于记录开始时候的坐标位置 */
	        private PointF startPoint = new PointF();
	        /** 用于记录拖拉图片移动的坐标位置 */
	        private Matrix matrix = new Matrix();
	        /** 用于记录图片要进行拖拉时候的坐标位置 */
	        private Matrix currentMatrix = new Matrix();
	      
	        /** 两个手指的开始距离 */
	        private float startDis;
	        /** 两个手指的中间点 */
	        private PointF midPoint;
	  
	        @SuppressLint("ClickableViewAccessibility")
			@Override
	        public boolean onTouch(View v, MotionEvent event) {
	        	matrix.set(imgDisPlay.getImageMatrix());
	        	
	            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
	            switch (event.getAction() & MotionEvent.ACTION_MASK) {
	            // 手指压下屏幕
	            case MotionEvent.ACTION_DOWN:
	                mode = MODE_DRAG;
	                // 记录ImageView当前的移动位置
	                currentMatrix.set(imgDisPlay.getImageMatrix());
	                startPoint.set(event.getX(), event.getY());
	                break;
	            // 手指在屏幕上移动，改事件会被不断触发
	            case MotionEvent.ACTION_MOVE:
	                // 拖拉图片
	                if (mode == MODE_DRAG) {
	                    float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
	                    float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
	                    // 在没有移动之前的位置上进行移动
	                    matrix.set(currentMatrix);
	                    matrix.postTranslate(dx, dy);
	                }
	                // 放大缩小图片
	                else if (mode == MODE_ZOOM) {
	                    float endDis = distance(event);// 结束距离
	                    if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
	                        float scale = endDis / startDis;// 得到缩放倍数
	                        matrix.set(currentMatrix);
	                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
	                    }
	                }
	                break;
	            // 手指离开屏幕
	            case MotionEvent.ACTION_UP:
	                // 当触点离开屏幕，但是屏幕上还有触点(手指)
	            case MotionEvent.ACTION_POINTER_UP:
	                mode = 0;
	                break;
	            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
	            case MotionEvent.ACTION_POINTER_DOWN:
	                mode = MODE_ZOOM;
	                /** 计算两个手指间的距离 */
	                startDis = distance(event);
	                /** 计算两个手指间的中间点 */
	                if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
	                    midPoint = mid(event);
	                    //记录当前ImageView的缩放倍数
	                    currentMatrix.set(imgDisPlay.getImageMatrix());
	                }
	                break;
	            }
	            imgDisPlay.setImageMatrix(matrix);
	            
	            return gestureDetector.onTouchEvent(event);
	        }
	  
	        /** 计算两个手指间的距离 */
	        private float distance(MotionEvent event) {
	            float dx = event.getX(1) - event.getX(0);
	            float dy = event.getY(1) - event.getY(0);
	            /** 使用勾股定理返回两点之间的距离 */
	            return (float) Math.sqrt(dx * dx + dy * dy);
	        }
	  
	        /** 计算两个手指间的中间点 */
	        private PointF mid(MotionEvent event) {
	            float midX = (event.getX(1) + event.getX(0)) / 2;
	            float midY = (event.getY(1) + event.getY(0)) / 2;
	            return new PointF(midX, midY);
	        }
	  
	    }
	    
	    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

	        @Override
	        public boolean onDown(MotionEvent e) {
	            return true;
	        }
	        // event when double tap occurs
	        @Override
	        public boolean onDoubleTap(MotionEvent e) {
	        	imgDisPlay.setImageMatrix(fullMatrix);
	            return true;
	        }
	    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.product_preview_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_preview_save: {
	    		SaveOrder();
	    		return true;
	    	}
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void SaveOrder() {
		new AlertDialog.Builder(PreviewActivity.this)  
		.setTitle("This order will be save as")
		.setMessage(fileName)
		.setIcon(R.drawable.ic_launcher)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog,  
                    int whichButton) {
            	SaveImage();
            	SaveCsv();
            	TryShare();
            }
        })  
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog,  
                    int whichButton) {
            }
        })  
		.show();
	}
	
	void SaveImage() {
		File imageFile = new File(Environment.getExternalStorageDirectory() + "/GiantReturn" + "/image/" + fileName + ".jpg");  
    	try {
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
        	newb.compress(CompressFormat.JPEG, 50, fos);
        	fos.flush();
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void SaveCsv() {
		File imageFile = new File(Environment.getExternalStorageDirectory() + "/GiantReturn" + "/csv/" + fileName + ".csv");  
    	try {
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
			// cols
			String cols = "\"Customer\"" + ",\"Date\"" + ",\"NO.\"" + ",\"Description\"" + ",\"Price(RM)\"" + ",\"Quantity\"" + ",\"Amount(RM)\"\n";
			fos.write(cols.getBytes());
			
			// values
			int nIdx = 0;
	        for (int i=0; i<mPriceInfoList.size(); ++i) {
	        	if (mPriceInfoList.get(i).GetProductCount() > 0){
	        		String row = "";
	        		row += "\"" + mCustomerInfo.GetName() + "\"";
	        		row += ",\"" + date + "\"";
	        		row += ",\"" + Integer.toString(nIdx + 1) + "\"";
	        		row += ",\"" + mPriceInfoList.get(i).GetProductName() + "\"";
	        		row += ",\"" + CustomerInfo.Cents2RM(mPriceInfoList.get(i).GetProductPrice()) + "\"";
	        		row += ",\"" + Integer.toString(-mPriceInfoList.get(i).GetProductCount()) + "\"";
	        		row += ",\"" + CustomerInfo.Cents2RM(-mPriceInfoList.get(i).GetProductPrice()*mPriceInfoList.get(i).GetProductCount()) + "\"\n";			       
			        ++nIdx;
			        fos.write(row.getBytes());
	        	}
	        }
			
        	fos.flush();
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void TryShare() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PreviewActivity.this);
	    builder.setTitle("This order has be saved, you can share now!");
	    builder.setIcon(R.drawable.ic_launcher);
	    builder.setItems(new CharSequence[]
	            {"Share Image", "Share File", "Share Image and File", "Cancel"},
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    switch (which) {
	                        case 0: {
	                        	Intent sharingIntent = new Intent(Intent.ACTION_SEND);
	                        	Uri uir = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/GiantReturn" + "/image/" + fileName + ".jpg"));
	                            sharingIntent.setType("image/jpeg");
	                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uir);
	                            PreviewActivity.this.startActivityForResult(Intent.createChooser(sharingIntent, "Share Image using"), 1234);
	                            break;
	                        }
	                        case 1: {
	                        	Intent sharingIntent = new Intent(Intent.ACTION_SEND);
	                        	Uri uir = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/GiantReturn" + "/csv/" + fileName + ".csv"));
	                        	sharingIntent.setType("plain/text");
	                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uir);
	                            PreviewActivity.this.startActivityForResult(Intent.createChooser(sharingIntent, "Share File using"), 1234);
	                            break;
	                        }
	                        case 2: {
	                        	Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	                        	ArrayList<Uri> uris = new ArrayList<Uri>();
	                        	uris.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/GiantReturn" + "/image/" + fileName + ".jpg")));
	                        	uris.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/GiantReturn" + "/csv/" + fileName + ".csv")));
	                        	sharingIntent.setType("plain/text");
	                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uris);
	                            PreviewActivity.this.startActivityForResult(Intent.createChooser(sharingIntent, "Share Image and File using"), 1234); 
	                            break;
	                        }
	                        case 3: {
	                        	GotoMainActivity();
	                            break;
	                        }
	                    }
	                }
	            });
	    builder.create().show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		GotoMainActivity();
	}
	
	private void GotoMainActivity() {		
		mCustomerInfo.ClearOrder();
		Intent intent = new Intent(PreviewActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
