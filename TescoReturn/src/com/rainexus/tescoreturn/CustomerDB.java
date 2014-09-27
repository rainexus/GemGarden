package com.rainexus.tescoreturn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.os.Environment;

public class CustomerDB {
	static boolean globalProductEdit = true;
	static boolean globalPriceUnique = true;
	static int globalMainActivityTextSize = 20;
	static int globalProductListActivityTextSize = 20;
	static int globalProductOperateActivityTextSize = 20;
	static int globalCustomerOperateActivityTextSize = 20;
	static ArrayList<CustomerInfo> mCustomerInfoList = null;
	static {
		mCustomerInfoList = new ArrayList<CustomerInfo>();
		
		{
			File folder = new File(Environment.getExternalStorageDirectory() + "/TescoReturn/");
			if (!folder.exists()) {
			    boolean success = folder.mkdir();
			    if (!success) {
			    	throw new RuntimeException("folder.mkdir");
			    }
			}
		}
		
		{
			File folder = new File(Environment.getExternalStorageDirectory() + "/TescoReturn/image/");
			if (!folder.exists()) {
			    boolean success = folder.mkdir();
			    if (!success) {
			    	throw new RuntimeException("folder.mkdir");
			    }
			}
		}
		
		{
			File folder = new File(Environment.getExternalStorageDirectory() + "/TescoReturn/csv/");
			if (!folder.exists()) {
			    boolean success = folder.mkdir();
			    if (!success) {
			    	throw new RuntimeException("folder.mkdir");
			    }
			}
		}
		
		ReadFromXml();
	}
	
	static void AddCustomerInfo(CustomerInfo customerInfo) {
		for (int i=0; i<mCustomerInfoList.size(); ++i) {
			if (mCustomerInfoList.get(i).GetName().equals(customerInfo.GetName())) {
				throw new RuntimeException("AddCustomerInfo");
			}
		}
		if (globalProductEdit && mCustomerInfoList.size() > 0) {
			customerInfo.SetProductPriceInfoList(mCustomerInfoList.get(0).GetProductPriceInfoList());
		}
		mCustomerInfoList.add(customerInfo);
	}
	
	static void RemoveCustomerInfo(String name) {
		for (int i=0; i<mCustomerInfoList.size(); ++i) {
			if (mCustomerInfoList.get(i).GetName().equals(name)) {
				mCustomerInfoList.remove(i);
				return;
			}
		}
		
		throw new RuntimeException("RemoveCustomerInfo");
	}
	
	static ArrayList<CustomerInfo> GetCustomerInfoList() {
		return mCustomerInfoList;
	}
	static ArrayList<String> GetCustomerStrList() {
		ArrayList<String> customerStrList = new ArrayList<String>();
		for (int i=0; i<mCustomerInfoList.size(); ++i) {
			customerStrList.add(mCustomerInfoList.get(i).GetName());
		}
		return customerStrList;
	}
	
	static void BroadCastPriceInfoList(ArrayList<ProductInfo> priceInfoList) {
		if (!globalProductEdit)
			return;
		
		for (int i=0; i<mCustomerInfoList.size(); ++i) {
			mCustomerInfoList.get(i).SetProductPriceInfoList(priceInfoList);
		}
	}
	
	
	static void ReadFromXml() {
		InputStream inputstream = null;
		try {
			inputstream = new FileInputStream(Environment.getExternalStorageDirectory() + "/TescoReturn/customer.xml");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
		    DocumentBuilder builder=factory.newDocumentBuilder();
		    Document document=builder.parse(inputstream);
		    Element root=document.getDocumentElement();
		    NodeList items=root.getElementsByTagName("customer");
		    String globalProductEdit = root.getAttribute("GlobalProductEdit");
		    if (globalProductEdit.equalsIgnoreCase("false"))
		    	CustomerDB.globalProductEdit = false;
		    else
		    	CustomerDB.globalProductEdit = true;
		    
		    String globalPriceUnique = root.getAttribute("GlobalPriceUnique");
		    if (globalPriceUnique.equalsIgnoreCase("false"))
		    	CustomerDB.globalPriceUnique = false;
		    else
		    	CustomerDB.globalPriceUnique = true;
		    
		    String globalMainActivityTextSize = root.getAttribute("GlobalMainActivityTextSize");
		    if (globalMainActivityTextSize.length() > 0) {
		    	CustomerDB.globalMainActivityTextSize = Integer.parseInt(globalMainActivityTextSize);
		    }
		    
		    String globalProductListActivityTextSize = root.getAttribute("GlobalProductListActivityTextSize");
		    if (globalProductListActivityTextSize.length() > 0) {
		    	CustomerDB.globalProductListActivityTextSize = Integer.parseInt(globalProductListActivityTextSize);
		    }
		    
		    String globalProductOperateActivityTextSize = root.getAttribute("GlobalProductOperateActivityTextSize");
		    if (globalProductOperateActivityTextSize.length() > 0) {
		    	CustomerDB.globalProductOperateActivityTextSize = Integer.parseInt(globalProductOperateActivityTextSize);
		    }
		    
		    String globalCustomerOperateActivityTextSize = root.getAttribute("GlobalCustomerOperateActivityTextSize");
		    if (globalCustomerOperateActivityTextSize.length() > 0) {
		    	CustomerDB.globalCustomerOperateActivityTextSize = Integer.parseInt(globalCustomerOperateActivityTextSize);
		    }
		    
		    for(int i=0; i<items.getLength(); i++)
		    {
		    	Element item=(Element)items.item(i);
		    	String customerName = item.getAttribute("name");
		    	
		    	ArrayList<ProductInfo> priceInfoList = new ArrayList<ProductInfo>();
		    	NodeList productItems = item.getElementsByTagName("product");
		    	for (int j=0; j<productItems.getLength(); ++j) {
		    		Element productItem=(Element)productItems.item(j);
		    		String productPrice = productItem.getAttribute("price");
		    		String productName = productItem.getAttribute("name");
		    		priceInfoList.add(new ProductInfo(productName, Integer.parseInt(productPrice)));
		    	}
		    	
		    	CustomerInfo customerInfo = new CustomerInfo(customerName, priceInfoList);
		    	mCustomerInfoList.add(customerInfo);
		    }
		    
		    inputstream.close();
		    
		} catch (ParserConfigurationException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (SAXException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		
		WriteToXml();
	}
	
	static void WriteToXml() {
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
		    DocumentBuilder builder=factory.newDocumentBuilder();
		    Document document = builder.newDocument();
		    Element root=document.createElement("customers");
		    
		    if (CustomerDB.globalProductEdit)
		    	root.setAttribute("GlobalProductEdit", "true");
		    else
		    	root.setAttribute("GlobalProductEdit", "false");
		    
		    if (CustomerDB.globalPriceUnique)
		    	root.setAttribute("GlobalPriceUnique", "true");
		    else
		    	root.setAttribute("GlobalPriceUnique", "false");
		    
		    root.setAttribute("GlobalMainActivityTextSize", Integer.toString(CustomerDB.globalMainActivityTextSize));
		    root.setAttribute("GlobalProductListActivityTextSize", Integer.toString(CustomerDB.globalProductListActivityTextSize));
		    root.setAttribute("GlobalProductOperateActivityTextSize", Integer.toString(CustomerDB.globalProductOperateActivityTextSize));
		    root.setAttribute("GlobalCustomerOperateActivityTextSize", Integer.toString(CustomerDB.globalCustomerOperateActivityTextSize));
		    
		    document.appendChild(root);
		    
		    for(int i=0; i<mCustomerInfoList.size(); i++)
		    {
		    	Element customerItem = document.createElement("customer");
		    	customerItem.setAttribute("name", mCustomerInfoList.get(i).GetName());
		    	root.appendChild(customerItem);
		    	
		    	ArrayList<ProductInfo> priceInfoList = mCustomerInfoList.get(i).GetProductPriceInfoList();
		    	for (int j=0; j<priceInfoList.size(); ++j) {
		    		Element productItem = document.createElement("product");
		    		productItem.setAttribute("name", priceInfoList.get(j).GetProductName());
		    		productItem.setAttribute("price", Integer.toString(priceInfoList.get(j).GetProductPrice()));
		    		customerItem.appendChild(productItem);
		    	}
		    }
		    
		    TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);

            File file = new File(Environment.getExternalStorageDirectory() + "/TescoReturn/customer.xml");
            OutputStream outputStream = new FileOutputStream(file);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
            outputStream.close();
		    
		} catch (ParserConfigurationException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
