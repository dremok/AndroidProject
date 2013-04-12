package com.dremok.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dremok.handlers.PersonHandler;
import com.dremok.model.Person;

public class XmlParser {

	private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		return xmlReader;
	}
	
	public ArrayList<Person> parsePeopleResponse(String xml) {
		try {
			XMLReader xmlReader = initializeReader();
			PersonHandler personHandler = new PersonHandler();
			xmlReader.setContentHandler(personHandler);
			xmlReader.parse(new InputSource(new StringReader(xml)));
			return personHandler.retrievePersonList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
