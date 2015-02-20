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
package co.edu.icesi.i2t.slrtools.bib;

import co.edu.icesi.i2t.slrtools.bib.filter.FilterBib;
import static co.edu.icesi.i2t.slrtools.bib.filter.FilterBib.saveReferencesToBibFile;
import co.edu.icesi.i2t.slrtools.mixstrings.MixStrings;
import co.edu.icesi.i2t.slrtools.config.Database;
import co.edu.icesi.i2t.slrtools.config.SLRConfig;
import co.edu.icesi.i2t.slrtools.config.SLRConfigReader;
import co.edu.icesi.i2t.slrtools.bib.transformations.TransformBibACM;
import co.edu.icesi.i2t.slrtools.bib.transformations.TransformBibIEEEXplore;
import co.edu.icesi.i2t.slrtools.bib.transformations.TransformBibScienceDirect;
import co.edu.icesi.i2t.slrtools.bib.transformations.TransformBibSpringerLink;
import co.edu.icesi.i2t.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class SLRBib {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SLRConfig slrConfig = SLRConfigReader.loadSLRConfiguration();
            if (slrConfig != null) {
                if (!slrConfig.isDomainSearchStringsEmpty()) {
                    String mixedStrings = MixStrings.mixStrings(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings());
                    if (slrConfig.allDatabases()) {
                        for (Database database : Database.values()) {
                            transformAndFilter(slrConfig, database, mixedStrings);
                        }
                    } else {
                        for (Database database : slrConfig.getDatabases()) {
                            transformAndFilter(slrConfig, database, mixedStrings);
                        }
                    }
                } else {
                    System.out.println("[WARNING] No domain search strings. Application will exit.");
                }
            } else {
                System.out.println("[WARNING] The configuration file is not correctly formated. Application will exit.");
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SLRBib.class.getName()).log(Level.SEVERE, "[ERROR] There was a problem loading the configuration file", ex);
        }
    }

    /**
     * 
     * @param slrConfig
     * @param database
     * @param mixedStrings
     */
    public static void transformAndFilter(SLRConfig slrConfig, Database database, String mixedStrings) {
        boolean bibFileCreated = false;
        String exportedFilesPath = "files" + File.separator + database.getName() + File.separator;
        String sourceBibFilePath = "bib" + File.separator;
        String bibFileName = database.getName() + ".bib";
        String targetBibDirectory = "bib" + File.separator + "filtered" + File.separator;
        if (database == Database.ACM) {
            bibFileCreated = TransformBibACM.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        if (database == Database.IEEE_XPLORE) {
            bibFileCreated = TransformBibIEEEXplore.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        if (database == Database.SCIENCE_DIRECT) {
            bibFileCreated = TransformBibScienceDirect.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        if (database == Database.SPRINGER_LINK) {
            bibFileCreated = TransformBibSpringerLink.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        if (bibFileCreated) {
            String[] references = FilterBib.loadBibContent(sourceBibFilePath + bibFileName);
            String[] referencesTitle = FilterBib.includeReferencesByTitle(mixedStrings, references);
            String[] referencesAbstract = FilterBib.includeReferencesByAbstract(mixedStrings, references);
            references = new String[referencesTitle.length + referencesAbstract.length];
            for (int i = 0; i < referencesTitle.length; i++) {
                references[i] = referencesTitle[i];
            }
            for (int i = 0, j = referencesTitle.length; i < referencesAbstract.length; i++, j++) {
                if (!Utils.arrayContainsElement(references, referencesAbstract[i])) {
                    references[j] = referencesAbstract[i];
                }
            }
            if (!slrConfig.isExclusionCriteriaEmpty()) {
                references = FilterBib.removeReferencesByAbstract(slrConfig.getExclusionCriteria(), references);
            }
            try {
                saveReferencesToBibFile(references, targetBibDirectory, bibFileName);
            } catch (Exception e) {
                System.out.println("[ERROR] Failed to create filtered BibTeX file. " + e.getMessage());
            }
        }
    }

}
