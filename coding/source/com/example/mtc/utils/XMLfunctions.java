package com.example.mtc.utils;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XMLfunctions {

	public final static Document XMLfromString(String xml) {

		Document doc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setEncoding("utf-8");
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}

		return doc;

	}

	/**
	 * Returns element value
	 * 
	 * @param elem
	 *            element (it is XML tag)
	 * @return Element value otherwise empty String
	 */
	public final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid
						.getNextSibling()) {
					// Log.d("======= ", "========");
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					} else if (kid.getNodeType() == Node.CDATA_SECTION_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public static String getXML(String url) {
		String line = null;
		BufferedReader in = null;
		try {

			// Log.d("url   ", url);

			// DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new HttpPost(url);
			//
			// HttpResponse httpResponse = httpClient.execute(httpPost);
//			 HttpEntity httpEntity = httpResponse.getEntity();
//			 line = EntityUtils.toString(httpEntity, "utf-8");

			// HttpGet request = new HttpGet(url);
			// HttpResponse response = httpClient.execute(request);
			// HttpEntity httpEntity = response.getEntity();
			// line = EntityUtils.toString(httpEntity, "utf-8");
			// line = in.readLine();
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));			
			HttpEntity httpEntity = response.getEntity();
			line = EntityUtils.toString(httpEntity, "utf-8");
			
		//	Log.d("line output : ", "" + line);
		} catch (UnsupportedEncodingException e) {
			line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			Log.e("Error 01 ", e.getMessage());
		} catch (MalformedURLException e) {
			line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			Log.e("Error 02 ", e.getMessage());
		} catch (Exception e) {
			line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			Log.e("Error 03 ", e.getMessage());
		}
		Log.d("line", "" + line);
		return line;

	}

	public static int numResults(Document doc) {
		Node results = doc.getDocumentElement();
		int res = -1;

		try {
			// res =
			// Integer.valueOf(results.getAttributes().getNamedItem("database").getNodeValue());
			NodeList nodes = doc.getElementsByTagName("Database");
			res = nodes.getLength();
		} catch (Exception e) {
			try {
				// res =
				// Integer.valueOf(results.getAttributes().getNamedItem("node").getNodeValue());
				NodeList nodes = doc.getElementsByTagName("node");
				res = nodes.getLength();
			} catch (Exception e1) {

				try {
					NodeList nodes = doc.getElementsByTagName("row");
					res = nodes.getLength();
				} catch (Exception e2) {
					res = -1;
				}

			}
		}

		return res;
	}

	public static String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		// Log.d(" node list========================<>", ""+ n.getLength());

		for (int i = 0; i < n.getLength(); i++) {
			// Log.d("  =============>  ", "  "+
			// (XMLfunctions.getElementValue(n.item(i))));
		}

		return XMLfunctions.getElementValue(n.item(0));
	}
}
