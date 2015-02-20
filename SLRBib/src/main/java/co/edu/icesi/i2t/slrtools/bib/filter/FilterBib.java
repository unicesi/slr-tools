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
package co.edu.icesi.i2t.slrtools.bib.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Paquete con las funciones encargadas de realizar revision de los abstract y
 * realizar la inclusion o no dependiendo de las palabras claves que el usuario
 * defina
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class FilterBib {

	/**
	 * 
	 * @param bibFilePath
	 * @return
	 */
    public static String[] loadBibContent(String bibFilePath) {
        String[] references = null;
        File bibFile = new File(bibFilePath);
        String bibContent = "";
        String line = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(bibFile))) {
            while ((line = bufferedReader.readLine()) != null) {
                bibContent += line;
            }
            references = bibContent.split("@");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to read BibTeX «" + bibFilePath + "». " + e.getMessage());
        }
        return references;
    }

    /**
     * Funcion que se encarga de extraer el abstract de una cadena con toda la
     * informacion de un bibtex
     *
     * @param reference String con toda la informacion de una referencia
     * bibliografica
     * @return String con la informacion del abstract
     */
    private static String getAbstract(String reference) {
        String referenceAbstract = "";
        if (reference.contains("abstract = {")) {
            referenceAbstract = reference.substring(reference.indexOf("abstract = {") + 12);
            referenceAbstract = referenceAbstract.substring(0, referenceAbstract.indexOf("}"));
            referenceAbstract = referenceAbstract.replaceAll("\\p{Punct}", " ");
        } else if (reference.contains("abstract = \"")) {
            referenceAbstract = reference.substring(reference.indexOf("abstract = \"") + 12);
            referenceAbstract = referenceAbstract.substring(0, referenceAbstract.indexOf("\""));
            referenceAbstract = referenceAbstract.replaceAll("\\p{Punct}", " ");
        }
        return referenceAbstract.toLowerCase();
    }

    /**
     * Funcion que se encarga de extraer el titulo de una cadena con toda la
     * informacion de un bibtex
     *
     * @param reference String con toda la informacion de una referencia
     * bibliografica
     * @return String con la informacion del titulo
     */
    private static String getTitle(String reference) {
        String referenceTitle = "";
        if (reference.contains("title = {")) {
            referenceTitle = reference.substring(reference.indexOf("title = {") + 9);
            referenceTitle = referenceTitle.substring(0, referenceTitle.indexOf("}"));
            referenceTitle = referenceTitle.replaceAll("\\p{Punct}", " ");
        } else if (reference.contains("title = \"")) {
            referenceTitle = reference.substring(reference.indexOf("title = \"") + 9);
            referenceTitle = referenceTitle.substring(0, referenceTitle.indexOf("\""));
            referenceTitle = referenceTitle.replaceAll("\\p{Punct}", " ");
        }
        return referenceTitle.toLowerCase();
    }

    /**
     * Funcion que se encarga de revisar si los abstract contienen una serie de
     * palabras claves definidas por el usuario, si contiene al menos una el
     * articulo es incluido, la funcion permite busqueda independiente de
     * palabras o una combinacion de maximo dos palabras
     *
     * @param searchStrings String con la composicion de palabras a revisar
     * separadas por ; si se desea hacer una comparacion doble la palabras deben
     * estar separadas por el simbolo &
     *
     * @param references
     * @return
     */
    public static String[] includeReferencesByAbstract(String searchStrings, String[] references) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Adding references by abstract...");
        System.out.println("");

        searchStrings = searchStrings.replaceAll("\\Q*\\E", "");
        String[] strings = searchStrings.split(";");
        for (String searchString : strings) {
            int referenceCounter = 0;
            for (int i = 0; i < references.length; i++) {
                if (references[i] != null) {
                    String referenceAbstract = getAbstract(references[i]);
                    if (searchString.contains("&")) {
                        String[] temp = searchString.split("&");
                        if (!(referenceAbstract.toLowerCase().contains(temp[0].toLowerCase()) && referenceAbstract.toLowerCase().contains(temp[1].toLowerCase()))) {
                            references[i] = null;
                        } else {
                            referenceCounter++;
                        }
                    } else if (!referenceAbstract.toLowerCase().contains(searchString.toLowerCase())) {
                        references[i] = null;
                    } else {
                        referenceCounter++;
                    }
                }

            }
            System.out.println("[INFO] The search string «" + searchString + "» has " + referenceCounter + (referenceCounter == 1 ? " match" : " matches"));
        }
        System.out.println("-----------------------------");
        return references;
    }

    /**
     * Funcion que se encarga de revisar si los abstract contienen una serie de
     * palabras claves definidas por el usuario, si contiene al menos una el
     * articulo es excluido, la funcion permite busqueda independiente de
     * palabras o una combinacion de maximo dos palabras
     *
     * @param exclusionCriteria String con la composicion de palabras a revisar
     * separadas por ; si se desea hacer una comparacion doble la palabras deben
     * estar separadas por el simbolo &
     *
     * @param references
     * @return
     */
    public static String[] removeReferencesByAbstract(String[] exclusionCriteria, String[] references) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Removing references by abstract...");
        System.out.println("");

        for (String exclusionCriterion : exclusionCriteria) {
            int referenceCounter = 0;
            for (int i = 0; i < references.length; i++) {
                if (references[i] != null) {
                    String referenceAbstract = getAbstract(references[i]);
                    if (referenceAbstract.toLowerCase().contains(exclusionCriterion.toLowerCase())) {
                        referenceCounter++;
                        references[i] = null;
                    }
                }
            }
            System.out.println("[INFO] The exclusion criterion «" + exclusionCriterion + "» has " + referenceCounter + (referenceCounter == 1 ? " match" : " matches"));
        }
        System.out.println("-----------------------------");
        return references;
    }

    /**
     * Funcion que se encarga de revisar si los titulos contienen una serie de
     * palabras claves definidas por el usuario, si contiene al menos una el
     * articulo es incluido, la funcion permite busqueda independiente de
     * palabras o una combinacion de maximo dos palabras
     *
     * @param searchStrings String con la composicion de palabras a revisar
     * separadas por ; si se desea hacer una comparacion doble la palabras deben
     * estar separadas por el simbolo &
     *
     * @param references
     * @return
     */
    public static String[] includeReferencesByTitle(String searchStrings, String[] references) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Adding references by title...");
        System.out.println("");

        searchStrings = searchStrings.replaceAll("\\Q*\\E", "");
        String[] strings = searchStrings.split(";");
        for (String searchString : strings) {
            int referenceCounter = 0;
            for (int i = 0; i < references.length; i++) {
                if (references[i] != null) {
                    String referenceTitle = getTitle(references[i]);
                    if (searchString.contains("&")) {
                        String[] temp = searchString.split("&");
                        if (!(referenceTitle.toLowerCase().contains(temp[0].toLowerCase()) && referenceTitle.toLowerCase().contains(temp[1].toLowerCase()))) {
                            references[i] = null;
                        } else {
                            referenceCounter++;
                        }
                    } else if (!referenceTitle.toLowerCase().contains(searchString.toLowerCase())) {
                        references[i] = null;
                    } else {
                        referenceCounter++;
                    }
                }
            }
            System.out.println("[INFO] The search string «" + searchString + "» has " + referenceCounter + (referenceCounter == 1 ? " match" : " matches"));
        }
        System.out.println("-----------------------------");
        return references;
    }

    /**
     * Funcion que se encarga de guardar el archivo BIB consolodidado con los
     * datos recolectados en la ruta de entrada deseada
     *
     * @param references String con todos los BIBTex encontrados en los HTML
     * descargados de la base de datos ACM
     * @param path String con la ruta fisica del directorio donde se guardara el
     * archivo consolodidao
     * @param fileName
     * @throws IOException Excepcion por si el sistema es incapaz de guardar el
     * archivo en el directorio destinado.
     */
    public static void saveReferencesToBibFile(String[] references, String path, String fileName) throws IOException {
        System.out.println("[INFO] Saving filtered BibTeX...");
        File targetDirectory = new File(path);
        targetDirectory.mkdir();
        File bibFile = new File(targetDirectory, fileName);
        bibFile.createNewFile();
        try (FileWriter fileWriter = new FileWriter(bibFile)) {
            String bibContent = "";
            for (int i = 0; i < references.length; i++) {
                if (references[i] != null) {
                    bibContent += "@" + references[i] + System.lineSeparator();
                }
            }
            fileWriter.write(bibContent);
            fileWriter.flush();
        }
        System.out.println("[INFO] Filtered BibTeX saved");
    }

}
