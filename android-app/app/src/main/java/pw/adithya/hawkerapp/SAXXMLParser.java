package pw.adithya.hawkerapp;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SAXXMLParser {
    public static ArrayList<Detail> parse(InputStream is) {
        ArrayList<Detail> detailList = null;
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SAXXMLHandler handler = new SAXXMLHandler();
            saxParser.parse(new InputSource(is), handler);
            // Get Employees list
            detailList = handler.getDetails();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }

        return detailList;
    }
}