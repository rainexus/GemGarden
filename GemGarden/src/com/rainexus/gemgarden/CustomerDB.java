package com.rainexus.gemgarden;

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
	static ArrayList<CustomerInfo> mCustomerInfoList = null;
	static {
		mCustomerInfoList = new ArrayList<CustomerInfo>();
		
		{
			File folder = new File(Environment.getExternalStorageDirectory() + "/GemGarden/");
			if (!folder.exists()) {
			    boolean success = folder.mkdir();
			    if (!success) {
			    	throw new RuntimeException("folder.mkdir");
			    }
			}
		}
		
		{
			File folder = new File(Environment.getExternalStorageDirectory() + "/GemGarden/image/");
			if (!folder.exists()) {
			    boolean success = folder.mkdir();
			    if (!success) {
			    	throw new RuntimeException("folder.mkdir");
			    }
			}
		}
		
		{
			File folder = new File(Environment.getExternalStorageDirectory() + "/GemGarden/csv/");
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
	
	static void ReadFromXml() {
		InputStream inputstream = null;
		try {
			inputstream = new FileInputStream(Environment.getExternalStorageDirectory() + "/GemGarden/customer.xml");
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

            File file = new File(Environment.getExternalStorageDirectory() + "/GemGarden/customer.xml");
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
