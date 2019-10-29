package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javafx.scene.paint.Color;

public class xml {

	public static final int TARGET_OBJ = 1;
	public static final int TARGET_BACK = 2;
	public static final int TARGET_BOTH = 0;
	public static final int TYPE_BEHIND = 0;
	public static final int TYPE_FRONT = 1;
	
	Element root;
	public xml(String name){
		root = new Element(name);
	}


	public void addNode(String nodename){
		Element elem = new Element(nodename);
		root.addContent(elem);
	}

	public void addNode(xml node){
		root.addContent(node.getElement());
	}

	public void addNode(Element nodeelem){
		root.addContent(nodeelem);
	}

	public <T,Z> void addData(Z container, T data){
		Element cont = null;
		if(container instanceof xml)
			cont = ((xml)container).getElement();
		else if(container instanceof Element)
			cont = ((Element)container);
		else if(container instanceof String)
			cont = new Element((String)container);
		else {
			System.out.println("Falsche Eingabe bei container!, Datentyp nicht bekannt!");
			return;
		}
		
		if(data instanceof String) {
			cont.addContent(""+data);
		}
		else if( data instanceof Character) {
			cont.addContent(""+data);
		}
		else if( data instanceof Integer) {
			cont.addContent(""+data);
		}
		else if( data instanceof Double) {
			cont.addContent(""+data);
		}
		else if( data instanceof Float) {
			cont.addContent(""+data);
		}
		else if( data instanceof Color) {
			cont.addContent(""+data.toString());
		}
		else if( data instanceof Boolean) {
			boolean b = (boolean)data;
			if(b==true) {
				cont.addContent(""+data);
			}
			else {
				cont.addContent(""+data);
			}
		}
			root.addContent(cont);
	}

	public <T,Z> void addData(Z container, T[] data){
		Element cont = null;
		if(container instanceof xml)
			cont = ((xml)container).getElement();
		else if(container instanceof Element)
			cont = ((Element)container);
		else if(container instanceof String)
			cont = new Element((String)container);
		else {
			System.out.println("Falsche Eingabe bei container!, Datentyp nicht bekannt!");
			return;
		}
			
		for(int i=0;i<data.length;i++) {
//			Element cont = new Element(container);
			if(data[i] instanceof String) {
				cont = new Element(""+data[i]);
				cont.setAttribute("type", "string");
			}
			else if( data[i] instanceof Character) {
				cont = new Element(""+data[i]);
				cont.setAttribute("type", "char");
			}
			else if( data[i] instanceof Integer) {
				cont = new Element(""+data[i]);
				cont.setAttribute("type", "integer");
			}
			else if( data[i] instanceof Double) {
				cont = new Element(""+data[i]);
				cont.setAttribute("type", "double");
			}
			else if( data[i] instanceof Float) {
				cont = new Element(""+data[i]);
				cont.setAttribute("type", "float");
			}
			else if( data[i] instanceof Color) {
				cont.addContent(""+data.toString());
				cont.setAttribute("type", "color");
			}
			else if( data[i] instanceof Boolean) {
				boolean b = (boolean)data[i];
				if(b==true) {
					cont = new Element("true");
					cont.setAttribute("type", "boolean");
				}
				else {
					cont = new Element("false");
					cont.setAttribute("type", "boolean");
				}
			}
			
				root.addContent(cont);
		}
	}
	

	public Element getElement() {
		return root;
	}


	//Statisch Klassen zum lesen
		public static String convert_to_XML(Element rootNode){
			try{
				Document doc = new Document(rootNode);
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				String str = xmlOutput.outputString(doc);
				return str;
			} catch (Exception io) {
				System.out.println(io.getMessage());
				return "";
			}
		}
	

	//Statisch Klassen zum lesen
	public static boolean saveXML(Element rootNode, File file){
		try{
			Document doc = new Document(rootNode);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("iso-8859-15"));
//			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(file));
			return true;
		} catch (IOException io) {
			System.out.println(io.getMessage());
			return false;
		}
	}

	public static Element readXML(File file) throws FileNotFoundException, JDOMException, IOException{
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(new FileReader(file));
		return doc.getRootElement();
	}


}
