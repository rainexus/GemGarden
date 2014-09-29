package com.rainexus.pacificreturn;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

public class CustomerInfo {
	private String mName = null;
	private ArrayList<ProductInfo> mPriceInfoList = null;
	public final static int mMaxKind = 17;
	
	public CustomerInfo(String name, ArrayList<ProductInfo> priceList) {
		mName = name;
		mPriceInfoList = priceList;
	}
	
	public String GetName() {
		return mName;
	}
	
	public void SetName(String name) {
		mName = name;
	}
	
	public ArrayList<ProductInfo> GetProductPriceInfoList() {
		return mPriceInfoList;
	}
	
	public void SetProductPriceInfoList(ArrayList<ProductInfo> priceInfoList) {
		if (mPriceInfoList == priceInfoList)
			return;

		mPriceInfoList.clear();
		for (int i=0; i<priceInfoList.size(); ++i) {
			mPriceInfoList.add(priceInfoList.get(i));
		}
	}
	
	public void AddPriceInfo(ProductInfo priceInfo) {
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			if (priceInfo.GetProductName().equals(mPriceInfoList.get(i).GetProductName())) {
				throw new RuntimeException("AddPriceInfo");
			}
		}
		
		mPriceInfoList.add(priceInfo);
	}
	
	public void RemovePriceInfo(String priceName) {
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			if (priceName.equals(mPriceInfoList.get(i).GetProductName())) {
				mPriceInfoList.remove(i);
				return;
			}
		}
		
		throw new RuntimeException("RemovePriceInfo");
	}
	
	public ArrayList<String> GetProductPriceStrList() {
		ArrayList<String> customerStrList = new ArrayList<String>();
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			customerStrList.add("RM "+ Cents2RM(mPriceInfoList.get(i).GetProductPrice()) + "\n" + mPriceInfoList.get(i).GetProductName());
		}
		return customerStrList;
	}
	
	public ArrayList<String> GetProductPriceCountStrList() {
		ArrayList<String> customerStrList = new ArrayList<String>();
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			if (mPriceInfoList.get(i).GetProductCount() > 0)
				customerStrList.add("RM " + Cents2RM(mPriceInfoList.get(i).GetProductPrice()) + "\n" + mPriceInfoList.get(i).GetProductName() + " ¡Á " + mPriceInfoList.get(i).GetProductCount());
			else
				customerStrList.add("RM " + Cents2RM(mPriceInfoList.get(i).GetProductPrice()) + "\n" + mPriceInfoList.get(i).GetProductName());
		}
		return customerStrList;
	}
	
	public String GetGeneralStr() {
		int totalPrice = 0;
		int totalCount = 0;
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			totalPrice += mPriceInfoList.get(i).GetProductPrice() * mPriceInfoList.get(i).GetProductCount();
			totalCount += mPriceInfoList.get(i).GetProductCount();
		}
		
		return "To: " + mName + "    Total: RM " + Cents2RM(-totalPrice) + ", Quantity -" + totalCount;
	}
	
	public String GetGeneralStrWithDate(String date) {
		int totalPrice = 0;
		int totalCount = 0;
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			totalPrice += mPriceInfoList.get(i).GetProductPrice() * mPriceInfoList.get(i).GetProductCount();
			totalCount += mPriceInfoList.get(i).GetProductCount();
		}
		
		return "To: " + mName + "    Date: " + date + "    Total: RM " + Cents2RM(-totalPrice) + ", Quantity -" + totalCount;
	}
	
	public boolean CheckMaxKind(Context c) {
		int nKind = 0;
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			if (mPriceInfoList.get(i).GetProductCount() > 0)
				++nKind;
		}
		
		if (nKind > mMaxKind)
		{
			Toast.makeText(c, "Can only select " + mMaxKind + "kinds of product in one order!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	public int GetTotalCount() {
		int totalCount = 0;
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			totalCount += mPriceInfoList.get(i).GetProductCount();
		}
		
		return totalCount;
	}
	
	public int GetTotalPrice() {
		int totalPrice = 0;
		for (int i=0; i<mPriceInfoList.size(); ++i) {
			totalPrice += mPriceInfoList.get(i).GetProductPrice() * mPriceInfoList.get(i).GetProductCount();
		}
		
		return totalPrice;
	}
	
	void ClearOrder() {			
		for (int j=0; j<mPriceInfoList.size(); ++j) {
			mPriceInfoList.get(j).SetProductCount(0);
		}	
	}
	
	static String Cents2RM(int cents) {
		return Integer.toString(cents/100) + "." + Integer.toString(Math.abs(cents%100));
	}
}
