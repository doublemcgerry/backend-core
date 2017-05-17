package rz.thesis.core.options;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
@SuppressWarnings("unused")
public class SoftwareOption {
	private Node nd;
	private String name;
	private String type;
	private String value;
	public SoftwareOption setName(String Name){
		this.name=Name;
		nd.getAttributes().getNamedItem("name").setNodeValue(Name);
		return this;
	}
	public SoftwareOption setType(String Type){
		this.type=Type;
		nd.getAttributes().getNamedItem("type").setNodeValue(Type);
		return this;
	}
	public String getName(){
		return nd.getAttributes().getNamedItem("name").getNodeValue();
	}
	public String getType(){
		return nd.getAttributes().getNamedItem("type").getNodeValue();
	}
	public String getValue(){
		return nd.getTextContent();
	}
	public Node getValueChild(String Name){
		NodeList nl= nd.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName()==Name) {
				return nl.item(i);
			}
		}
		return null;
	}
	public SoftwareOption setValue(String value){
		nd.setTextContent(value);
		return this;
	}
	public SoftwareOption(Node nd){
		this.nd=nd;
	}
	
	public static SoftwareOption Create(Node parentNode,String name,String type,String value){
		Document doc=parentNode.getOwnerDocument();
		Node nd =doc.createElement("Option");
		
			nd.setTextContent(value);
		
		
		Attr nameatt= doc.createAttribute("name");
		nameatt.setValue(name);
		nd.getAttributes().setNamedItem(nameatt);
		Attr typeAttr= doc.createAttribute("type");
		typeAttr.setValue(type);
		nd.getAttributes().setNamedItem(typeAttr);
		SoftwareOption so= new SoftwareOption(nd);
		
		parentNode.appendChild(nd);
		return so;
	}
	
}	
