package pw.adithya.hawkerapp.utils;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import pw.adithya.hawkerapp.Detail;

public class SAXXMLHandler extends DefaultHandler {

    private StringBuilder str;
    private ArrayList<Detail> details;
    private Detail detail;
    private boolean isPhotoURL = false, isName = false, isShortAddr = false, isLongAddr = false, isDesc = false, isPlaceID = false, isLat = false, isLon = false, isNoOfStalls = false;

    public SAXXMLHandler() {
        details = new ArrayList<>();
    }

    public ArrayList<Detail> getDetails() {
        return details;
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        str = new StringBuilder();
        isName = false;
        isShortAddr = false;
        isLongAddr = false;
        isDesc = false;
        isPlaceID = false;
        isLat = false;
        isLon = false;
        isNoOfStalls = false;
        isPhotoURL = false;

        if (qName.equalsIgnoreCase("Placemark")) {
            detail = new Detail();
        } else if (qName.equalsIgnoreCase("SimpleData")) {
            if (attributes.getValue("name").equalsIgnoreCase("NO_OF_FOOD_STALLS"))
                isNoOfStalls = true;
            else if (attributes.getValue("name").equalsIgnoreCase("NAME"))
                isName = true;
            else if (attributes.getValue("name").equalsIgnoreCase("ADDRESSSTREETNAME"))
                isShortAddr = true;
            else if (attributes.getValue("name").equalsIgnoreCase("ADDRESS_MYENV"))
                isLongAddr = true;
            else if (attributes.getValue("name").equalsIgnoreCase("DESCRIPTION_MYENV"))
                isDesc = true;
            else if (attributes.getValue("name").equalsIgnoreCase("INC_CRC"))
                isPlaceID = true;
            else if (attributes.getValue("name").equalsIgnoreCase("LATITUDE"))
                isLat = true;
            else if (attributes.getValue("name").equalsIgnoreCase("LONGITUDE"))
                isLon = true;
            else if (attributes.getValue("name").equalsIgnoreCase("PHOTOURL"))
                isPhotoURL = true;
        }
    }

    public void characters(char[] ch, int start, int length) {
        if (str != null) {
            for (int i=start; i<start+length; i++) {
                str.append(ch[i]);
            }
        }
    }

    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("Placemark"))
            details.add(detail);
        else if (qName.equalsIgnoreCase("SimpleData"))
        {
            try {
                if (isNoOfStalls)
                    detail.setNoOfStalls(Integer.parseInt(str.toString()));

                else if (isName) {
                    int index1 = str.indexOf("(");
                    int index2 = str.indexOf(")");

                    if (index1 == -1)
                        detail.setName(str.toString());
                    else
                    {
                        detail.setName(str.substring(index1+1, index2));
                    }
                }

                else if (isShortAddr)
                    detail.setShortAddr(str.toString());

                else if (isLongAddr)
                    detail.setLongAddr(str.toString());

                else if (isDesc)
                    detail.setDescription(str.toString());

                else if (isPlaceID)
                    detail.setPlaceID(str.toString());

                else if (isLat)
                    detail.setLat(Float.parseFloat(str.toString()));

                else if (isLon)
                    detail.setLon(Float.parseFloat(str.toString()));

                else if (isPhotoURL)
                    detail.setPhotoURL(str.toString());
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
    }
}
