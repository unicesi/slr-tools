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
package co.edu.icesi.i2t.slrtools.robot;

import co.edu.icesi.i2t.slrtools.config.Database;
import co.edu.icesi.i2t.slrtools.config.SLRConfig;
import co.edu.icesi.i2t.slrtools.config.SLRConfigReader;
import co.edu.icesi.i2t.slrtools.webdrivers.WebDriver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * @version 1.0, December 2014
 */
public class SLRRobot {

	/**
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
        System.out.println("");
        try {
            SLRConfig slrConfig = SLRConfigReader.loadSLRConfiguration();
            if (slrConfig != null) {
                if (!slrConfig.isDomainSearchStringsEmpty()) {
                    if (slrConfig.allDatabases()) {
                        for (Database database : Database.values()) {
                            WebDriver.webSearch(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings(), database);
                        }
                    } else {
                        for (Database database : slrConfig.getDatabases()) {
                            WebDriver.webSearch(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings(), database);
                        }
                    }
                    System.out.println("");
                    System.out.println("[INFO] Finished searching. Save all exported files.");
                    System.out.println("");
                } else {
                    System.out.println("[WARNING] No domain search strings. Application will exit.");
                }
            } else {
                System.out.println("[WARNING] The configuration file is not correctly formated. Application will exit.");
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SLRRobot.class.getName()).log(Level.SEVERE, "[ERROR] There was a problem loading the configuration file", ex);
        }
    }

}
