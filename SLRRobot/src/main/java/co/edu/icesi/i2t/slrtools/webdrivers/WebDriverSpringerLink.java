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
 * proceso de busqueda en la base de datos SpringerLink
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class WebDriverSpringerLink {

    /**
     * Funcion que se encarga de realizar automaticamente la busqueda en la base
     * de datos SpringerLink conforme a una cadena de busqueda introducida, la
     * automatizacion nos permite descargar los CSV que SpringerLink dispone con
     * el resultaod de las busquedas, la ruta donde se guardan losr archivos
     * dependen de la selección del usuario, si por alguna razon el resultado de
     * la busqueda es mayor a 1000 datos, el buscador solo deja descargar los
     * primeros 1000 ordenados por importancia de acuerdo a las politicas de
     * SpringerLink, los archivos descargados posteriormente son procesadas para
     * la construccion del BIB consolodidado con los resultados obtenidos.
     *
     * @param searchStrings este parametro es la cadena de busqueda que retorna
     * la funcion mixScienceDirect#mixWords, cada cadena de busqueda esta
     * separada por ;
     * @param url este paremetro es el URL de la busqueda experta de
     * ScienceDirect, el url es incompleto y se complementa con la informacion
     * del primer parametro
     * @see mixWords.mixSpringer#mixWords(java.lang.String, java.lang.String)
     */
    public static void searchWeb(String searchStrings, String url) {
        /* a esta funcion se debe mejorar
         * 1: automatizar las preferencias para la descarga automatica
         * 2: la ruta de descarga
         * 3: el cierre del navegador al finalizar la ultima descarga*/
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Searching Springer Link...");
        System.out.println("Search strings: " + searchStrings);
        System.out.println("");

        FirefoxProfile profile = new FirefoxProfile();

        String workingDir = System.getProperty("user.dir");
        
        File targetDirectory = new File("files" + File.separator + Database.SPRINGER_LINK.getName());
        targetDirectory.mkdir();

        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", workingDir + File.separator + "files" + File.separator + Database.SPRINGER_LINK.getName());
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperapps.neverAsk.saveToDisk", "application/x-latex;text/csv");
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);

        WebDriver webDriver = new FirefoxDriver(profile);

        String[] strings = searchStrings.split(";");
        for (int i = 0; i < strings.length; i++) {
            try {
                webDriver.get(url + strings[i]);
		//WebElement searchField = url.findElement(By.name("title-is"));
                //WebElement buttonSearch = url.findElement(By.id("submit-advanced-search"));
                //searchField.click();
                //searchField.sendKeys(lcadenasBusqueda[i]);
                //buttonSearch.click();
                try {
                    WebElement exportField = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#tool-download > span.icon")));
                    WebElement stringResult = webDriver.findElement(By.xpath("//h1[contains(@class, 'number-of-search-results-and-search-terms')]/strong"));
                    System.out.println("[INFO] Search string " + (i + 1) + " «" + strings[i] + "» has " + stringResult.getText() + " result(s) returned");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                    exportField.click();
                } catch (Exception e) {
                    System.out.println("[WARNING] Search string " + (i + 1) + " «" + strings[i] + "» retrieves no results");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Search string " + (i + 1) + " «" + strings[i] + "» failed. " + e.getMessage());
            }
        }
//	webDriver.quit();
        System.out.println("[INFO] Finished search in Springer Link");
        System.out.println("-----------------------------");
    }

}
