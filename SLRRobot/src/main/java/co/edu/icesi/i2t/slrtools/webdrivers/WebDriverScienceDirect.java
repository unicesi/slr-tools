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
package co.edu.icesi.i2t.slrtools.webdrivers;

import co.edu.icesi.i2t.slrtools.config.Database;
import java.io.File;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase que se encarga, con la ayuda de selenium WebDriver, automatizar el
 * proceso de busqueda en la base de datos ScienceDirect
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class WebDriverScienceDirect {

    /**
     * Funcion que se encarga de realizar automaticamente la busqueda en la base
     * de datos ScienceDirect conforme a una cadena de busqueda introducida, la
     * automatizacion nos permite descargar los BIB que ScienceDirect dispone
     * con el resultaod de las busquedas, la ruta donde se guardan losr archivos
     * dependen de la selección del usuario, si por alguna razon el resultado de
     * la busqueda es mayor a 1000 datos, el buscador solo deja descargar los
     * primeros 1000 ordenados por importancia de acuerdo a las politicas de
     * ScienceDirect, los archivos descargados posteriormente son procesadas
     * para la construccion del BIB consolodidado con los resultados obtenidos.
     *
     * @param searchStrings este parametro es la cadena de busqueda que retorna
     * la funcion mixScienceDirect#mixWords, cada cadena de busqueda esta
     * separada por ;
     * @param url este paremetro es el URL de la busqueda experta de
     * ScienceDirect
     * @see mixWords.mixScienceDirect#mixWords(java.lang.String,
     * java.lang.String)
     */
    public static void searchWeb(String searchStrings, String url) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Searching Science Direct...");
        System.out.println("Search strings: «" + searchStrings + "»");
        System.out.println("");

        String workingDir = System.getProperty("user.dir");
        
        File targetDirectory = new File("files" + File.separator + Database.SCIENCE_DIRECT.getName());
        targetDirectory.mkdir();

        FirefoxProfile profile = new FirefoxProfile();

        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", workingDir + File.separator + "files" + File.separator + Database.SCIENCE_DIRECT.getName());
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperapps.neverAsk.saveToDisk", "application/x-latex;text/csv");
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("browser.download.panel.shown", true);

        WebDriver webDriver = new FirefoxDriver(profile);

        String[] strings = searchStrings.split(";");
        for (int i = 0; i < strings.length; i++) {
            try {
                webDriver.get(url);
                WebElement searchField = webDriver.findElement(By.name("SearchText"));
//              WebElement searchField = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.name("SearchText")));
                WebElement submitSearch = webDriver.findElement(By.name("RegularSearch"));
//              WebElement submitSearch = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.name("RegularSearch")));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {

                }
                searchField.click();
                searchField.sendKeys(strings[i]);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {

                }
                submitSearch.click();
                try {
                    WebElement exportButton = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.down_sci_dir.exportArrow")));
                    WebElement stringResult = webDriver.findElement(By.xpath("//h1[contains(@class, 'queryText')]/strong"));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {

                    }
                    exportButton.click();
                    System.out.println("[INFO] Search string " + (i + 1) + " «" + strings[i] + "» " + stringResult.getText());
                    WebElement bibTex = webDriver.findElement(By.id("BIBTEX"));
                    bibTex.click();
                    WebElement export = webDriver.findElement(By.id("export_button"));
                    export.click();
                } catch (Exception e) {
                    System.out.println("[WARNING] Search string " + (i + 1) + " «" + strings[i] + "» retrieves no results");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Search string " + (i + 1) + " «" + strings[i] + "» failed. " + e.getMessage());
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {

            }
        }
//	webDriver.quit();
        System.out.println("[INFO] Finished search in Science Direct");
        System.out.println("-----------------------------");
    }

}
