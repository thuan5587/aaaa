package com.example.demo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XMLMessageWriter {
    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder documentBuilder;
    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static Transformer transformer;
    private static Document doc;

    static {
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            File xmlFile = new File("chat_history.xml");
            if (xmlFile.exists()) {
                doc = documentBuilder.parse(xmlFile);
            } else {
                doc = documentBuilder.newDocument();
                Element rootElement = doc.createElement("messages");
                doc.appendChild(rootElement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeMessage(String sender, String message) {
        try {
            Element messageElement = doc.createElement("message");
            messageElement.setAttribute("sender", sender);
            messageElement.setTextContent(message);
            doc.getDocumentElement().appendChild(messageElement);

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("chat_history.xml"));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
