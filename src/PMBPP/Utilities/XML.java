package PMBPP.Utilities;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class XML {
	Document document;
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public Document CreateDocument() throws ParserConfigurationException {
		 DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	     DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	     Document doc = docBuilder.newDocument();
	     setDocument(doc);
	     return doc;
	}
	public Element CreateElement(Document doc, String ElementName, String Value) {
		
		 Element element = doc.createElement(ElementName);
		 element.setTextContent(String.valueOf(Value));
		 return element;
	}
	
	public void SetElementValue(Element element, String Value) {
		
		element.setTextContent(String.valueOf(Value));
		
	}
	public void  WriteDocument(Document doc, String DocName) throws TransformerException {
		
		if(new File(DocName).exists()) {
			
		}
		DOMSource source = new DOMSource(doc);
	     StreamResult result = new StreamResult(new File(DocName));
	     
	     TransformerFactory transformerFactory = TransformerFactory.newInstance();
	     Transformer transformer = transformerFactory.newTransformer();
	    
	     transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	     transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	     transformer.transform(source, result);
	}
}
