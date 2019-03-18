package pw.adithya.hawkerapp;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SAXXMLHandler extends DefaultHandler {

    private List<ParsingStructure> parsingStructure;
    private StringBuilder tempVal;
    private ParsingStructure tempStr;

    public SAXXMLHandler() {
        parsingStructure = new ArrayList<>();
    }

    public List<ParsingStructure> getParsingvalues() {
        return parsingStructure;
    }
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {

        tempVal = new StringBuilder();
        if (qName.equalsIgnoreCase("Placemark")) {
            tempStr = new ParsingStructure();
        }
    }

    public void characters(char[] ch, int start, int length) {
        if (tempVal != null) {
            for (int i=start; i<start+length; i++) {
                tempVal.append(ch[i]);
            }
        }
    }

    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("Placemark")) {
            // add it to the list
            parsingStructure.add(tempStr);
        } else if (qName.equalsIgnoreCase("name")) {
            if(tempStr != null)
                tempStr.setName(tempVal.toString());
        } else if (qName.equalsIgnoreCase("description")) {
            if(tempStr != null)
                tempStr.setDescription(tempVal.toString());
        } else if (qName.equalsIgnoreCase("coordinates")) {
            if(tempStr != null) {
                String[] latlong =  tempVal.toString().split(",");
                float longitude = Float.parseFloat(latlong[0]);
                float latitude = Float.parseFloat(latlong[1]);
                tempStr.setLat(latitude);
                tempStr.setLon(longitude);
            }
        } else if(qName.equalsIgnoreCase("kml")) {
            Log.e("kml", tempVal.toString());
        }
    }

    public void warning(SAXParseException e) {

    }

    public void error(SAXParseException e) {

    }

    public void fatalError(SAXParseException e) {

    }
}
