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
package co.edu.icesi.i2t.slrtools.mixstrings;

/**
 * clase combinatorio de palabras base de datos IEEEXplore
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 */
public class MixStringsIEEEXplore {

    /**
     * Funcion para realizar el combinatorio de palabras para la base de datos
     * IEEEXplore.
     *
     * @param domainStrings El parametro lWords1 define el primer listado de
     * palabras con el cual se realizara la combinacion para la creación de las
     * cadenas de busqueda, es un String separando las palabras por ;
     * @param focusedStrings El parametro lWords2 define el segundo listado de
     * palabras con el cual se realizara la combinacion para la creación de las
     * cadenas de busqueda, es un String separando las palabras por ;
     * @return String la funcion devuelve el listado en forma de cadena
     * separando las diferentes combinaciones por ;utiliza la opcion de busqueda
     * solo titulo
     */
    public static String mixStrings(String[] domainStrings, String[] focusedStrings) {
        String resultado = "";
        if (focusedStrings != null) {
            for (String domainString : domainStrings) {
                String tempWord = "(\"Document Title\":" + domainString + ")";
                for (String focusedString : focusedStrings) {
                    resultado = resultado + tempWord + " AND \"Document Title\":" + focusedString + ");";
                }
            }
        } else {
            for (String domainString : domainStrings) {
                resultado = resultado + "(\"Document Title\":" + domainString + ");";
            }
        }
        return resultado.substring(0, resultado.length() - 1);
    }

}
