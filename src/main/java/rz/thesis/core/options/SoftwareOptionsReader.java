package rz.thesis.core.options;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class SoftwareOptionsReader {
	SoftwareOptionCollection soc;
	File OptionsFile;
	Document doc;

	public SoftwareOptionsReader(File OptionsFile) {
		this.OptionsFile = OptionsFile;
		if (OptionsFile.exists() && OptionsFile.isFile()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			doc = null;
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(OptionsFile.getCanonicalPath());

			} catch (ParserConfigurationException | SAXException | IOException e) {

				e.printStackTrace();
			}
			if (doc != null) {

				soc = new SoftwareOptionCollection(doc);

			}

		} else {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			doc = null;
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.newDocument();
				soc = new SoftwareOptionCollection(doc);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(OptionsFile);
				transformer.transform(source, result);
			} catch (ParserConfigurationException e) {

				e.printStackTrace();
			} catch (TransformerException e) {

				e.printStackTrace();
			}
		}
	}

	public void SaveOptions() {
		try {
			synchronized (doc) {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(OptionsFile);
				transformer.transform(source, result);
			}

		} catch (TransformerException e) {

			e.printStackTrace();
		}
	}

	public String getValue(String name) {
		try {
			return soc.get(name).getValue();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public String getValue(String name, String defaultValue) {
		try {
			return soc.get(name).getValue();
		} catch (Exception e) {

			soc.add(name, "string", defaultValue);
			SaveOptions();
			try {
				return soc.get(name).getValue();
			} catch (Exception ex) {
				return null;
			}
		}

	}

	public void setValue(String name, String value) {
		try {
			soc.get(name).setValue(value);
			SaveOptions();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
