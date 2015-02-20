/*
 * Copyright © 2014 Universidad Icesi
 * 
 * This file is part of the SLR Tools.
 * 
 * The SLR Tools are free software: you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General 
 * Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * The SLR Tools are distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU Lesser General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU Lesser General 
 * Public License along with The SLR Support Tools. If not, 
 * see <http://www.gnu.org/licenses/>.
 */
package co.edu.icesi.i2t.slrtools.config;

import co.edu.icesi.i2t.utils.Utils;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * @version 1.0, December 2014
 */
public class SLRConfigReader {
    
	/**
	 * 
	 */
    private static final String CONFIG_FILE = "SLRConfig.xml";
    
    /**
     * 
     * @return
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     */
    public static SLRConfig loadSLRConfiguration() throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document slrConfigDocument = documentBuilder.parse(CONFIG_FILE);
        if (slrConfigDocument == null) {
            throw new FileNotFoundException("[ERROR] SLR configuration file not found.");
        }

        SLRConfig slrConfig = null;

        Database[] databases = null;
        Database[] trimmedDatabases = null;
        String[] exclusionCriteria = null;
        String[] trimmedExclusionCriteria = null;
        String[] domainSearchStrings = null;
        String[] trimmedDomainSearchStrings = null;
        String[] focusedSearchStrings = null;
        String[] trimmedFocusedSearchStrings = null;

        NodeList slrConfigRoot = slrConfigDocument.getChildNodes();

        if (slrConfigRoot.getLength() == 1) {

            Node root = slrConfigRoot.item(0);

            if (root.getNodeName().equalsIgnoreCase("slr") && root.hasChildNodes()) {

                NodeList slrConfigChildren = root.getChildNodes();

                for (int i = 0; i < slrConfigChildren.getLength(); i++) {
                    Node node = slrConfigChildren.item(i);

                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("databases") && node.hasChildNodes()) {
                        NodeList databasesList = node.getChildNodes();
                        databases = new Database[databasesList.getLength()];

                        System.out.println("[INFO] Selected databases:");
                        for (int j = 0; j < databasesList.getLength(); j++) {
                            Node databaseNode = databasesList.item(j);
                            
                            if (databaseNode.getNodeType() == Node.ELEMENT_NODE && databaseNode.getNodeName().equalsIgnoreCase("database")) {
                                if (!databaseNode.getTextContent().equals("")) {
                                    databases[j] = Database.getByName(databaseNode.getTextContent());
                                    System.out.println(databases[j]);
                                }
                            }
                        }
                        
                        trimmedDatabases = Utils.trimArray(databases);
                    }

                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("inclusioncriteria") && node.hasChildNodes()) {
                        System.out.println("");
                        System.out.println("[INFO] Inclusion criteria:");
                    }

                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("exclusioncriteria") && node.hasChildNodes()) {
                        NodeList exclusionCriteriaList = node.getChildNodes();
                        exclusionCriteria = new String[exclusionCriteriaList.getLength()];

                        System.out.println("");
                        System.out.println("[INFO] Exclusion criteria:");
                        for (int j = 0; j < exclusionCriteriaList.getLength(); j++) {
                            Node stringNode = exclusionCriteriaList.item(j);

                            if (stringNode.getNodeType() == Node.ELEMENT_NODE && stringNode.getNodeName().equalsIgnoreCase("string")) {
                                if (!stringNode.getTextContent().equals("")) {
                                    exclusionCriteria[j] = stringNode.getTextContent();
                                    System.out.println(exclusionCriteria[j]);
                                }
                            }
                        }
                        
                        trimmedExclusionCriteria = Utils.trimArray(exclusionCriteria);
                    }

                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("searchstrings") && node.hasChildNodes()) {
                        NodeList searchStringsList = node.getChildNodes();

                        for (int j = 0; j < searchStringsList.getLength(); j++) {
                            Node searchSetNode = searchStringsList.item(j);

                            if (searchSetNode.getNodeType() == Node.ELEMENT_NODE && searchSetNode.getNodeName().equalsIgnoreCase("domainstrings") && searchSetNode.hasChildNodes()) {
                                NodeList domainStringsList = searchSetNode.getChildNodes();
                                domainSearchStrings = new String[domainStringsList.getLength()];

                                System.out.println("");
                                System.out.println("[INFO] Domain strings:");
                                for (int k = 0; k < domainStringsList.getLength(); k++) {
                                    Node stringNode = domainStringsList.item(k);

                                    if (stringNode.getNodeType() == Node.ELEMENT_NODE && stringNode.getNodeName().equalsIgnoreCase("string")) {
                                        if (!stringNode.getTextContent().equals("")) {
                                            domainSearchStrings[k] = stringNode.getTextContent();
                                            System.out.println(domainSearchStrings[k]);
                                        }
                                    }
                                }
                                
                                trimmedDomainSearchStrings = Utils.trimArray(domainSearchStrings);
                            }
                            
                            if (searchSetNode.getNodeType() == Node.ELEMENT_NODE && searchSetNode.getNodeName().equalsIgnoreCase("focusedstrings") && searchSetNode.hasChildNodes()) {
                                NodeList focusedStringsList = searchSetNode.getChildNodes();
                                focusedSearchStrings = new String[focusedStringsList.getLength()];

                                System.out.println("");
                                System.out.println("[INFO] Focused strings:");
                                for (int k = 0; k < focusedStringsList.getLength(); k++) {
                                    Node stringNode = focusedStringsList.item(k);

                                    if (stringNode.getNodeType() == Node.ELEMENT_NODE && stringNode.getNodeName().equalsIgnoreCase("string")) {
                                        if (!stringNode.getTextContent().equals("")) {
                                            focusedSearchStrings[k] = stringNode.getTextContent();
                                            System.out.println(focusedSearchStrings[k]);
                                        }
                                    }
                                }
                                
                                trimmedFocusedSearchStrings = Utils.trimArray(focusedSearchStrings);
                            }
                        }
                    }

                    slrConfig = new SLRConfig(trimmedDatabases, trimmedExclusionCriteria, trimmedDomainSearchStrings, trimmedFocusedSearchStrings);
                }
            }
        }
        return slrConfig;
    }

}
