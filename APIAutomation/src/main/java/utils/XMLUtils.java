package utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLUtils {

	
	/**
	 *  This method goes to the xmlFile and reads the value of the respective xmlPath
	 *
	 *@author bghosh
	 *@param xmlFile
	 *@param xmlPath
	 *@return value of respective xmlPath
	 */
	public static String readXml(String xmlFile,  String xmlPath)
	{ 
		String value = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try 
		{
			builder = factory.newDocumentBuilder();
			Document doc;
			doc= builder.parse(new InputSource(new StringReader(xmlFile)));
			XPath xPath=XPathFactory.newInstance().newXPath();
			NodeList nodeList;
			nodeList =  (NodeList) xPath.compile(xmlPath).evaluate(doc, XPathConstants.NODESET);
			Node nNode = nodeList.item(0);
			value =  nNode.getTextContent();
		}  
		catch   (Exception  e)   
		{
			e.printStackTrace(); 
		}
		return value;
    }
	
	
	
	/**
	 *  This method goes to the response and replaces the value of the every 
	 *	xmlpath passed over the list
	 *@author bghosh
	 *@param res1_body
	 *@param pathList
	 *@return updated response body
	 */	
	public  static  String updateXmlResponsebody(String res1_body,  List<String> pathList)
	{ 
		for(String path:pathList) 
		{ 
			if(path.contains("("))
			{
				res1_body=APIUtils.replaceString(path,res1_body);
			} 
			else
			{
				res1_body=JSONUtils.deleteJsonValue(res1_body,path);
			}
		}
			return res1_body;
	}
	
	
	/**
	 *  This method goes to the response and deletes the value of the given 
	 *	jsonpath
	 *@author bghosh
	 *@param response
	 *@param jsonpath
	 *@return updated response body
	 */		
	public static String deleteXmlValue(String response,  String jsonpath)
	{ 
		String value = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try  {
			builder = factory.newDocumentBuilder();
			Document doc;
			doc= builder.parse(new InputSource(new StringReader(response)));
			XPath xPath=XPathFactory.newInstance().newXPath();
			String expression = jsonpath;
			NodeList nodeList;
			nodeList =  (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			Node nNode = nodeList.item(0);
			nNode.getParentNode().removeChild(nNode);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t;
			t = tf.newTransformer();
			Writer out = new StringWriter();
			t.transform(new DOMSource(doc), new StreamResult(out));
			value =  out.toString();
		}  
		catch(Exception e)   
		{
			e.printStackTrace(); 
		}
		return value;
}
}
