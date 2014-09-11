package com.rainexus.gemreturn;

public class ProductInfo {
	private String mProductName;
	private int mProductPrice = ~0;
	private int mProductCount = 0;

	public ProductInfo(String producteName, int productPrice) {
		mProductName = producteName;
		mProductPrice = productPrice;
	}
	
	public String GetProductName() {
		return mProductName;
	}
	
	public void SetProductName(String productName) {
		mProductName = productName;
	}
	
	public int GetProductPrice() {
		return mProductPrice;
	}
	
	void SetProductPrice(int productPrice) {
		mProductPrice = productPrice;
	}
	
	void SetProductCount(int prodictCount) {
		mProductCount = prodictCount;
	}
	
	int GetProductCount() {
		return mProductCount;
	}
}
