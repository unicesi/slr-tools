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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 *
 * @author Nasser Abdala
 * 
 * @deprecated clase que no es usada, la busqueda en IEEEXplore incluye esta base de datos
 */
public class WebDriverIEEEComputerScience 
{
    
	/**
	 * 
	 * @param cadenasBusqueda
	 * @param direccionBusqueda
	 */
    public static void searchWeb(String cadenasBusqueda,String direccionBusqueda)
    {
	/* a esta funcion se debe mejorar
	 * 1: automatizar las preferencias para la descarga automatica
	 * 2: la ruta de descarga
	 * 3: el cierre del navegador al finalizar la ultima descarga*/
        System.out.println("----------inicia busqueda web IEEE Computer Society----------");
        System.out.println("Cadena de palabras a buscar: "+cadenasBusqueda);
        System.out.println("------------------------------------------------------");
	FirefoxProfile profile = new FirefoxProfile();
	WebDriver url = new FirefoxDriver(profile);
	String[] lcadenasBusqueda = cadenasBusqueda.split(";");	
	for (int i=0;i<lcadenasBusqueda.length;i++)
	{
            try
            {		
		url.get(direccionBusqueda);
		WebElement searchField = url.findElement(By.name("queryText1"));                
		WebElement buttonSearch = url.findElement(By.id("searchButton"));
		searchField.click();
		searchField.sendKeys(lcadenasBusqueda[i]);
		buttonSearch.click();
                WebElement selectField = url.findElement(By.id("select1"));
		WebElement buttonField = url.findElement(By.id("form1Button"));
		selectField.sendKeys("100");
		buttonField.click();
		try
		{                 
                    WebElement stringResult = url.findElement(By.xpath("//div[contains(@class, 'searchwhitebox')]"));   
                    System.out.println("Busqueda: "+i+" cadena de busqueda:" + lcadenasBusqueda[i] + " - " + stringResult.getText());	
                    //String sourceCode=url.getPageSource();
                    //PrintWriter archivo = new PrintWriter("busquedasIEEECS/"+lcadenasBusqueda[i]+".html", "UTF-8");
                    //archivo.print(sourceCode);
                    //archivo.close();              
		}
		catch (Exception e)
		{
                    System.out.println("[OJO]"+" Busqueda: " +i+ " la cadena de busqueda: "+lcadenasBusqueda[i]+" no trae resultados");
		}	
            }
            catch(Exception e)
            {
		System.out.println("excepcion busqueda cadena: "+lcadenasBusqueda[i]+" , motivo excepcion: "+e);
            }	
	}
	//url.quit();
	System.out.println("----------fin busqueda web IEEE Computer Society----------");
    }
    
}
