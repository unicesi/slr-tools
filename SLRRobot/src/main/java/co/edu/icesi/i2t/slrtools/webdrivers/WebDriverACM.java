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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase que se encarga, con la ayuda de selenium WebDriver, automatizar el
 * proceso de busqueda en la base de datos ACM Digital Library
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class WebDriverACM {

    /**
     * Funcion que se encarga de realizar automaticamente la busqueda en la base
     * de datos ACM Digital Library conforme a una cadena de busqueda
     * introducida, ACM digital library no permite la descarga de ningun archivo
     * del resultado de la busqueda, por tal razón, se descarga el codigo fuente
     * de la pagina en el source del proyecto el cual posteriormente es usado
     * para la extracción de la información y construccion del BIB con los
     * resultados obtenidos
     *
     * @param searchStrings este parametro es la cadena de busqueda que retorna
     * la funcion mixACM#mixWords2, cada cadena de busqueda esta separada por ;
     * @param url este paremetro es el URL de la busqueda avanzada de ACM
     * Digital Library
     * @see mixWords.mixACM#mixWords2(java.lang.String, java.lang.String)
     */
    public static void searchWeb(String searchStrings, String url) {
        /* a esta funcion se debe mejorar
         * 1: validar el boton siguiente sin try catch, mejorar el manejo de las expeciones
         */
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Searching ACM Digital Library...");
        System.out.println("Search strings: «" + searchStrings + "»");
        System.out.println("");

        FirefoxProfile profile = new FirefoxProfile();
        WebDriver webDriver = new FirefoxDriver(profile);
        String[] strings = searchStrings.split(";");
        for (int i = 0; i < strings.length; i++) {
            try {
                webDriver.get(url);

                WebElement searchField = webDriver.findElement(By.name("within"));
                searchField.click();
                searchField.sendKeys(strings[i]);

                WebElement buttonSearch = webDriver.findElement(By.name("Go"));
                buttonSearch.click();

                List<WebElement> stringResult = webDriver.findElements(By.xpath("//span[contains(@style, 'background-color:yellow')]"));
                if (!stringResult.isEmpty()) {
                    System.out.println("[WARNING] Search string " + (i + 1) + ": «" + strings[i] + "» retrieves no results");
                } else {
                    int counter = 1;
                    try {
                        WebElement nextField = null;
                        do {
                            String sourceCode = webDriver.getPageSource();
                            File targetDirectory = new File("files" + File.separator + Database.ACM.getName());
                            targetDirectory.mkdir();
                            try (PrintWriter file = new PrintWriter("files" + File.separator + Database.ACM.getName() + File.separator + "searchResults_" + i + "_" + counter + ".html", "UTF-8")) {
                                file.print(sourceCode);
                            }
                            nextField = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.linkText("next")));
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {

                            }
                            nextField.click();
                            counter++;
                        } while (true);
                    } catch (NoSuchElementException | TimeoutException | NullPointerException e) {
                        System.out.println("[INFO] Search string " + (i + 1) + ": «" + strings[i] + "» has " + counter + (counter == 1 ? " result" : " results") + "returned");
                    }
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                System.out.println("[ERROR] Search string " + (i + 1) + ": «" + strings[i] + "» failed. " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("[ERROR] The application has been blocked by ACM Digital Library. Try again later.");
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {

            }
        }
        webDriver.quit();
        System.out.println("[INFO] Finished search in ACM Digital Library");
        System.out.println("-----------------------------");
    }

}
