package rz.thesis.core.options;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SoftwareOptionCollection extends ArrayList<SoftwareOption> {
	private static final long serialVersionUID = 4391582080867825483L;
	private Document doc;

	public SoftwareOptionCollection(Document dom) {
		this.doc=dom;
		if (!dom.hasChildNodes()) {
			initializeEmptyDocument(dom);
		}
		NodeList nl= dom.getFirstChild().getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			this.add(new SoftwareOption(nl.item(i)));
		}
	}
	private void initializeEmptyDocument(Document doc){
		Node basend= doc.createElement("Options");
		doc.appendChild(basend);
		try {
			this.add("ProjectsPath", "string", "undefined");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	public boolean contains(String optionName){
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getName().toLowerCase().equals(optionName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	public SoftwareOption get(String optionName){
		for (int i = 0; i < this.size(); i++) {
			if ((this.get(i).getName().toLowerCase().equals((optionName.toLowerCase())))) {
				return this.get(i);
			}
		}
		throw new RuntimeException("Option" +  optionName + " not found!");
	}
	public SoftwareOption add(String name, String type, String value){
		if (contains(name)) {
           return get(name).setType(type).setValue(value);  
		}else{
			SoftwareOption so= SoftwareOption.Create(doc.getFirstChild(), name , type, value);
			this.add(so);
			return so;
		}
	}

}
