package org.nds.dbdroid.config;

import org.xml.sax.Attributes;

public class Element {
    String uri;
    String localName;
    String qName;
    String value = null;
    Attributes attributes;

    public Element(String uri, String localName, String qName, Attributes attributes) {
        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
        this.attributes = new AttributesImpl(attributes);
    }

    public String getUri() {
        return uri;
    }

    public String getLocalName() {
        return localName;
    }

    public String getQname() {
        return qName;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttributeValue(String attributeName) {
        String value = this.getAttributes().getValue(attributeName); // QName
        if (value == null || value.trim().equals("")) {
            value = this.getAttributes().getValue("", attributeName); // LocalName
        }

        return value;
    }

}
