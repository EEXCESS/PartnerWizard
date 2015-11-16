/*
Copyright (C) 2015
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.partnerrecommender;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLTools implements Serializable {

	private static final long serialVersionUID = 6627374527169179744L;


		public XMLTools() {
	    }

		public String getStringFromDocument(Document doc)
	    {
	        try
	        {
	           DOMSource domSource = new DOMSource(doc);
	           StringWriter writer = new StringWriter();
	           StreamResult result = new StreamResult(writer);
	           TransformerFactory tf = TransformerFactory.newInstance();
	           javax.xml.transform.Transformer transformer = tf.newTransformer();
	           transformer.transform(domSource, result);
	           return writer.toString();
	        }
	        catch(TransformerException ex)
	        {
	           ex.printStackTrace();
	           return null;
	        }
	    }

		public String format(String unformattedXml) {
	        try {
	            final Document document = parseXmlFile(unformattedXml);

	            OutputFormat format = new OutputFormat(document);
	            format.setLineWidth(65);
	            format.setIndenting(true);
	            format.setIndent(2);
	            Writer out = new StringWriter();
	            XMLSerializer serializer = new XMLSerializer(out, format);
	            serializer.serialize(document);

	            return out.toString();
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    private Document parseXmlFile(String in) {
	        try {
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            InputSource is = new InputSource(new StringReader(in));
	            return db.parse(is);
	        } catch (ParserConfigurationException e) {
	            throw new RuntimeException(e);
	        } catch (SAXException e) {
	            throw new RuntimeException(e);
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }
	    
	    
		public Document convertStringToDocument(String xmlStr) {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        //factory.setNamespaceAware(false);
	        DocumentBuilder builder;  
	        try 
	        {  
	            builder = factory.newDocumentBuilder();  
	            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
	            return doc;
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } 
	        return null;
	    }		
		
//		private String removeNamespace ="<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"> <xsl:template match=\"node()\"> <xsl:copy> <xsl:apply-templates select=\"node()|@*\" /> </xsl:copy> </xsl:template> <xsl:template match=\"*\"> <xsl:element name=\"{local-name()}\"> <xsl:apply-templates select=\"node()|@*\" /> </xsl:element> </xsl:template> <xsl:template match=\"@*\"> <xsl:attribute name=\"{local-name()}\"> <xsl:apply-templates select=\"node()|@*\" /> </xsl:attribute> </xsl:template> </xsl:stylesheet>";

		/*
		public Document convertStringToDocumentRemoveNamespaces(String input)
		{
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	        DocumentBuilder builder;  
	        DOMResult domResult = new DOMResult();
	        try 
	        {  
	            builder = factory.newDocumentBuilder();  
	            Document doc = builder.parse( new InputSource( new StringReader( input ) ) ); 
		        DOMSource source = new DOMSource(doc);
	    		TransformerFactory tFactory = TransformerFactory.newInstance();
	            javax.xml.transform.Transformer transformerRemoveNamespace = tFactory.newTransformer(new StreamSource(new StringReader(removeNamespace)));
	            transformerRemoveNamespace.transform(source, domResult);
	            return doc;
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } 
	        return null;
		}
		*/
		
		/*
		private static void wipeRootNamespaces(Document xml) {       
		    Node root = xml.getDocumentElement();
		    renameNamespaceRecursive(root, null);
		}

		
		private static void renameNamespaceRecursive(Node node, String namespace) {
		    Document document = node.getOwnerDocument();
		    if (node.getNodeType() == Node.ELEMENT_NODE) {
		        document.renameNode(node, namespace, node.getNodeName());
		    }
		    NodeList list = node.getChildNodes();
		    for (int i = 0; i < list.getLength(); ++i) {
		        renameNamespaceRecursive(list.item(i), namespace);
		    }
		}
		*/
}
