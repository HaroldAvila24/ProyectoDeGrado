/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udenardbms;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;


public class Fuctionclass {

    static String recibo;
    static  LinkedList<Integer> clasesList = new LinkedList<>();
    static boolean status = false;
    static  String name_table;
    static IA2 IA2 = new IA2();
    static FileManager fm = new FileManager();
    //Separa la cadena y reemplaza la columna y clase predicha 
    public static String splitcadenaa(String where, String parametro1, String parametro2, String parametro3) {
       
        name_table = expressionnametable(where);
        String[] partes;
        //confirmaclass(where)
        // Usamos una expresión regular para dividir la cadena en tres partes
        if (where.contains(",")) {
            partes = where.split("(?<=" + parametro1 + ")|(?<=" + parametro2 + ")|(?<=" + parametro3 + ")", 4);
            partes[3] = partes[3].replace("'", "");
            partes[1] = " " + partes[3] + "=" + recibo;

            return partes[0] + partes[1];

        } else {
            partes = where.split("(?<=" + parametro1 + ")|(?<=" + parametro2 + ")", 3);
            String contclass = confirmaclass(name_table);
            if (!(contclass.contains("Error") || contclass.isEmpty())) {
                String comand = partes[0] + partes[1] + recibo;
                comand = comand.replaceAll(RegrexExp.replaceWhere, contclass);
                return comand;

            } else if (contclass.isEmpty()) {
                String comand = IA2.recibo(where, ArrayResult(name_table));
                return comand;
            } else if (contclass.contains("Error")) {
                return "error";
            }
            return null;
        }

    }

    public static String removelastCharacter(String where, String caracter) {
        // Dividir la cadena por el parámetro y conservar el delimitador

        String[] partes = where.split("(?<=" + caracter + ")", 2);

        if (partes.length > 1) {
            return partes[0].substring(0, partes[0].length() - 1);
        } else {
            return null; // Retorna cadena
        }
    }

    public static boolean Ruta(String where) {
        String regex = "=\\s*['\"]?([a-zA-Z]:[\\\\/][^'\"]*)['\"]?\\s*";

        // Expresión regular para encontrar una ruta de archivo (ajustada para Windows)
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(where);
        boolean verda = false;

        if (matcher.find()) {
            UDENARDBMS.ruta = matcher.group(1);
            if (UDENARDBMS.ruta.contains(",")) {
                UDENARDBMS.ruta = removelastCharacter(UDENARDBMS.ruta, ",");
            }
            verda = true;
        }
        return verda;
    }

    public static void setClasepredicha(int clase) {

        recibo = String.valueOf(clase);

    }

    // Método para resetear la variable a null
    public static void resetClasepredicha() {
        recibo = null;  // Resetea la variable a null
    }


    public static Object ValueDefault(String sql) {
        String[] parts = sql.split("DEFAULT", 2);

        // Comprobar que se hizo la división correctamente
        if (parts.length == 2) {
            // Obtener las partes antes y después de "DEFAULT"
            String beforeDefault = parts[0].trim();
            String afterDefault = parts[1].trim();
            Statement stmt;
            try {
                stmt = CCJSqlParserUtil.parse(beforeDefault);
                return new Object[]{stmt, afterDefault};
            } catch (JSQLParserException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("la casena no continee Default");
            try {
                Statement stmt = CCJSqlParserUtil.parse(sql);
                return new Object[]{stmt};
            } catch (JSQLParserException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static String confirmaclass(String tabla) {
        
        return fm.table_checkclass(tabla);

    }

    public static String expressionnametable(String comand) {
        String tableName = null;
        Pattern pattern = Pattern.compile(RegrexExp.nameTable);
        Matcher matcher = pattern.matcher(comand);
        if (matcher.find()) {
            tableName = matcher.group(1);

        } else {
            System.out.println("No se encontró el nombre de la tabla.");
        }
        return tableName;
    }

    public static String ArrayResult(String tbl) {
        Object[] array = fm.Arrayrut(tbl);
        return convertObjStr(array);
    }

    public static String convertObjStr(Object[] objet) {
        int numElements = objet.length;
        String objStr = null;
        StringBuilder concatenatedResult = new StringBuilder();

        for (int i = 0; i < numElements; i++) {
            String currentString;
            if (objet[i] instanceof Object[]) {
                Object[] obj = (Object[]) objet[i];
                currentString = Arrays.toString(obj);
            } else if (objet[i] instanceof String) {
                currentString = (String) objet[i];
            } else if (objet[i] instanceof List) {
                currentString = objet[i].toString(); // Maneja listas
            } else {
                currentString = "Tipo no soportado";
            }

            // Concatenar la cadena actual con un punto y coma
            if (concatenatedResult.length() > 0) {
                concatenatedResult.append("; ");
            }
            concatenatedResult.append(currentString);
        }
        return concatenatedResult.toString();
    }

    public static void ConsulAlter(LinkedList<String> urls,LinkedList<String> unicos){
        fm.Alter(urls, unicos, clasesList, name_table, Integer.parseInt(recibo));   
           
    }

}

