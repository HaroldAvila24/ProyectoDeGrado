/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udenardbms;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectItem;

public class FileManager {

    public static final String pathschema = "tblschema.dat";
    RandomAccessFile file;
    public static HashMap<String, ArrayList<Object>> mapIA = new HashMap<>();

    public FileManager() {
        try {
            file = new RandomAccessFile(pathschema, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTable(String tbl) {
        try {
            RandomAccessFile filetable = new RandomAccessFile(tbl + ".tbl", "rw");
            filetable.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private StringBuilder table_constrain(ArrayList<Schema> schemas, List<Expression> expressions) {
        for (int i = 0; i < schemas.size(); i++) {
            Schema schema = schemas.get(i);
            String spects = schema.spects.equals("null") ? "" : schema.spects.replace("[", "").replace("]", "");
            List<String> arrScpects = new ArrayList<String>(Arrays.asList(spects.split(",")));
            int index_pk = -1;
            for (int j = 0; j < arrScpects.size(); j++) {
                //System.out.println("vv->" + arrScpects.get(j));
                if (arrScpects.get(j).toLowerCase().trim().equals("primary")) {
                    index_pk = j;
                    break;
                }
            }

            // VALIDA LLAVES PRIMARIAS
            if (index_pk != -1) {
                try {
                    String columnname = schema.columnname;
                    Expression filter = CCJSqlParserUtil.parseCondExpression(columnname + "=" + expressions.get(i).toString());
                    HashMap<String, ArrayList<Object>> data = readFile(schema.tablename, schemas, filter);
                    if (!data.isEmpty()) {
                        if (data.get(columnname).size() > 0) {
                            System.err.println("Llave primaria <" + columnname + "> con valor " + expressions.get(i).toString() + " ya existe.");
                            return new StringBuilder("Llave primaria <" + columnname + "> con valor " + expressions.get(i).toString() + " ya existe.");
                        }
                    }
                } catch (JSQLParserException ex1) {
                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        return null;
    }

    /*
    private StringBuilder table_checkclassexit(ArrayList<Schema> schemas) {

        for (int i = 0; i < schemas.size(); i++) {
            Schema schema = schemas.get(i);
            String spects = schema.spects.toLowerCase().equals("class") ? "" : schema.spects.replace("[", "").replace("]", "");
            List<String> arrScpects = new ArrayList<String>(Arrays.asList(spects.split(",")));
            int index_pk = -1;
            for (int j = 0; j < arrScpects.size(); j++) {
                if (arrScpects.get(j).toLowerCase().trim().equals("class")) {
                    index_pk = j;
                    break;
                }
            }
            // VALIDA LLAVES PRIMARIAS
            if (index_pk != -1) {
                String columnname = schema.columnname;
                return new StringBuilder(columnname);

            }
        }
        return null;
    }

     */
    public String table_checkclass(String tbl) {
        try {
            ArrayList<Schema> schemas = readTableSchema(tbl);
            for (int i = 0; i < schemas.size(); i++) {
                Schema schema = schemas.get(i);
                String spects = schema.spects.toLowerCase().equals("class") ? "" : schema.spects.replace("[", "").replace("]", "");
                List<String> arrScpects = new ArrayList<String>(Arrays.asList(spects.split(",")));
                int index_pk = -1;
                for (int j = 0; j < arrScpects.size(); j++) {
                    if (arrScpects.get(j).toLowerCase().trim().equals("class")) {
                        index_pk = j;
                        break;
                    }
                }

                // VALIDA LLAVES PRIMARIAS
                if (index_pk != -1) {
                    String columnname = schema.columnname;
                    return schema.columnname;
                }

            }

            // return "-1, has un if y que si retorno yo entonces enviale al cliente de una que no existe la tabla..";
            return "";
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }

    }

    private StringBuilder table_check(List<Schema> schemas, List<Expression> expressions) {
        for (int i = 0; i < schemas.size(); i++) {
            Schema schema = schemas.get(i);
            String spects = schema.spects.equals("null") ? "" : schema.spects.replace("[", "").replace("]", "");
            List<String> arrScpects = new ArrayList<String>(Arrays.asList(spects.split(",")));
            int index_check = -1;
            for (int j = 0; j < arrScpects.size(); j++) {
                if (arrScpects.get(j).toLowerCase().trim().equals("check")) {
                    index_check = j;
                    break;
                }
            }
            if (index_check != -1) {
                String check = arrScpects.get(index_check + 1);
                String columnname = schema.columnname;
                String parser = check.replace(columnname, expressions.get(i).toString());
                try {
                    if (SimpleEvaluateExpr.evaluate(parser) == 0.0) {
                        System.err.println(columnname + " no cumple con la restricción: " + check);
                        return new StringBuilder(columnname + " no cumple con la restricción: " + check);
                    }
                } catch (JSQLParserException ex) {
                    return new StringBuilder("Error al validar restricciones:: " + ex.getMessage());
                }
            }
        }
        return null;
    }

    public StringBuilder insert(String tbl, List<Expression> expressions) {
        try {
            ArrayList<Schema> schemas = readTableSchema(tbl);
            StringBuilder tc = table_constrain(schemas, expressions);
            StringBuilder tchk = table_check(schemas, expressions);

            if (tc != null) {
                return tc;
            }
            if (tchk != null) {
                return tchk;
            }

            if (isRighSchema(expressions, schemas)) {
                try (RandomAccessFile filetable = new RandomAccessFile(tbl + ".tbl", "rw")) {
                    filetable.seek(filetable.length());
                    for (int i = 0; i < schemas.size(); i++) {
                        Schema schema = schemas.get(i);
                        Expression expression = expressions.get(i);
                        String datatype = schema.datatype.toLowerCase();
                        String spects = schema.spects;

                        if (datatype.equals("int")) {
                            filetable.writeInt(Integer.parseInt(expression + ""));
                        } else if (datatype.equals("float")) {
                            filetable.writeFloat(Float.parseFloat(expression + ""));
                        } else if (datatype.substring(0, 7).equals("varchar")
                                && expression.toString().charAt(0) == '\''
                                && expression.toString().charAt(expression.toString().length() - 1) == '\'') {
                            String exp = expression.toString();
                            int t = exp.length();
                            filetable.writeUTF(exp.substring(1, t - 1));
                        }

                    }
                    filetable.close();
                }

                System.err.println("INSERT: " + expressions);
                return new StringBuilder("INSERT: " + expressions);

            }
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.err.println("ERROR: " + expressions);
        return new StringBuilder("ERROR: " + expressions);

    }

    public StringBuilder update(String table, List<Column> columns, List<Expression> expressions, Expression condition) {
        try {
            int affected_rows = 0;
            ArrayList<Schema> schemas = readTableSchema(table);

            if (schemas.isEmpty()) {
                System.err.println("Esquema no existe");
                return new StringBuilder("Esquema no existe");
            }

            // Mapa para búsqueda rápida de columnas
            Map<String, Schema> schemaMap = schemas.stream()
                    .collect(Collectors.toMap(Schema::getColumnname, Function.identity()));
            ArrayList<Schema> schemas_to_check = new ArrayList<>();

            for (Column column : columns) {
                Schema schema = schemaMap.get(column.getColumnName());
                if (schema != null) {
                    schemas_to_check.add(schema);
                } else {
                    System.err.println("El campo " + column + " no hace parte del esquema " + schemas.get(0).tablename);
                    return new StringBuilder("El campo " + column + " no hace parte del esquema " + schemas.get(0).tablename);
                }
            }

            if (!isRighSchema(expressions, schemas_to_check)) {
                System.err.println("Los tipos de datos no coinciden");
                return new StringBuilder("Los tipos de datos no coinciden");
            }

            StringBuilder tc = table_check(schemas_to_check, expressions);
            if (tc != null) {
                return new StringBuilder(tc);
            }

            // Precomputar la condición WHERE si es posible
            String precomputedWhere = condition == null ? "" : condition.toString();

            try (RandomAccessFile filetable = new RandomAccessFile(table + ".tbl", "rw"); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                while (filetable.getFilePointer() < filetable.length()) {
                    HashMap<String, Object> map = new HashMap<>();
                    long pointer = filetable.getFilePointer();

                    for (Schema schema : schemas) {
                        String datatype = schema.datatype.toLowerCase();
                        String columnname = schema.columnname;
                        Object datavalue = null;

                        if (datatype.equals("int")) {
                            datavalue = filetable.readInt();
                        } else if (datatype.equals("float")) {
                            datavalue = filetable.readFloat();
                        } else if (datatype.startsWith("varchar")) {
                            datavalue = "'" + filetable.readUTF() + "'";
                        }

                        map.put(columnname, datavalue);
                    }

                    try {
                        String where = precomputedWhere;
                        if (!where.equals("")) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                where = where.replaceAll("(?<=^\\s*)" + entry.getKey() + "(?=\\s*=)", entry.getValue().toString().replace("\\", "\\\\") + "");
                            }
                            if (SimpleEvaluateExpr.evaluate(where) == 0) {
                                continue;
                            }
                        }
                    } catch (JSQLParserException | EmptyStackException ex) {
                        System.err.println("No ha sido posible evaluar la condición: " + condition);
                        continue;
                    }

                    // EFECTUAR UPDATE
                    byte[] aux = new byte[(int) filetable.length() - (int) filetable.getFilePointer()];
                    filetable.read(aux);
                    filetable.setLength(pointer);

                    for (Schema schema : schemas) {
                        Object data = map.get(schema.columnname);

                        for (int j = 0; j < columns.size(); j++) {
                            if (schema.columnname.equals(columns.get(j).getColumnName())) {
                                data = expressions.get(j);
                                break;
                            }
                        }

                        String datatype = schema.datatype.toLowerCase();
                        if (datatype.equals("int")) {
                            int dataInt = Integer.parseInt(data.toString());
                            filetable.writeInt(dataInt);
                            pointer = filetable.getFilePointer();
                        } else if (datatype.equals("float")) {
                            float dataFloat = Float.parseFloat(data.toString());
                            filetable.writeFloat(dataFloat);
                            pointer = filetable.getFilePointer();
                        } else if (datatype.startsWith("varchar")
                                && data.toString().charAt(0) == '\''
                                && data.toString().charAt(data.toString().length() - 1) == '\'') {
                            String exp = data.toString();
                            filetable.writeUTF(exp.substring(1, exp.length() - 1));
                            pointer = filetable.getFilePointer();
                        }
                    }

                    // Escribir el resto de los datos después de la actualización
                    filetable.write(aux);
                    filetable.seek(pointer);
                    affected_rows++;
                }
                return new StringBuilder("Filas afectadas: " + affected_rows);
            }
        } catch (IOException ex) {
            System.err.println("Error al ejecutar update: " + ex.getMessage());
            return new StringBuilder("Error al ejecutar update: " + ex.getMessage());
        }
    }

    public StringBuilder delete(String tbl, Expression condition) {
        try {
            int affected_rows = 0;
            ArrayList<Schema> schemas = readTableSchema(tbl);

            if (schemas.isEmpty()) {
                System.err.println("Esquema no existe");
                return new StringBuilder("Esquema no existe");
            }

            try (RandomAccessFile filetable = new RandomAccessFile(tbl + ".tbl", "rw")) {
                while (filetable.getFilePointer() < filetable.length()) {
                    long pointer = filetable.getFilePointer();
                    boolean delete = true;
                    HashMap<String, Object> map = new HashMap<>();

                    for (Schema schema : schemas) {
                        String datatype = schema.datatype.toLowerCase();
                        String columnname = schema.columnname;
                        Object datavalue = null;

                        if (datatype.equals("int")) {
                            datavalue = filetable.readInt();
                        } else if (datatype.equals("float")) {
                            datavalue = filetable.readFloat();
                        } else if (datatype.startsWith("varchar")) {
                            datavalue = filetable.readUTF();
                        }
                        map.put(columnname, datavalue);
                    }

                    if (condition != null) {
                        String where = condition.toString();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            where = where.replace(entry.getKey(), entry.getValue().toString());
                        }
                        if (SimpleEvaluateExpr.evaluate(where) == 0) {
                            delete = false;
                        }
                    }

                    if (delete) {
                        byte[] aux = new byte[(int) filetable.length() - (int) filetable.getFilePointer()];
                        int a = filetable.read(aux);
                        filetable.setLength(pointer);
                        filetable.write(aux);
                        filetable.seek(pointer);
                        affected_rows += 1;
                    }
                }
                filetable.close();
                return new StringBuilder("Filas afectadas: " + affected_rows);
            }
        } catch (IOException | JSQLParserException | EmptyStackException ex) {
            System.err.println("Error al ejecutar delete: " + ex.getMessage());
            return new StringBuilder("Error al ejecutar delete: " + ex.getMessage());
        }
    }

    public boolean isRighSchema(List<Expression> expressions, ArrayList<Schema> schemas) {
        for (int i = 0; i < schemas.size(); i++) {
            Schema schema = schemas.get(i);
            Expression expression = expressions.get(i);
            String datatype = schema.datatype.toLowerCase();
            if (datatype.equals("int")) {
                try {
                    Integer.parseInt(expression + "");
                } catch (NumberFormatException e) {
                    return false;
                }
            } else if (datatype.equals("float")) {
                try {
                    Float.parseFloat(expression + "");
                } catch (NumberFormatException e) {
                    return false;
                }
            } else if (datatype.substring(0, 7).equals("varchar")
                    && (expression.toString().charAt(0) != '\''
                    || expression.toString().charAt(expression.toString().length() - 1) != '\'')) {
                return false;
            }

        }
        return true;
    }

    public ArrayList<Schema> readTableSchema(String tbl) throws IOException {
        ArrayList<Schema> tableschema = new ArrayList<>();
        try {
            file = new RandomAccessFile(pathschema, "rw");
            while (file.getFilePointer() < file.length()) {
                Schema sq = new Schema(file.readUTF(), file.readUTF(), file.readUTF(), file.readUTF());
                if (sq.tablename.equals(tbl)) {
                    tableschema.add(sq);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            file.close();
        }
        return tableschema;

    }

    public void writeTableSchema(String tablename, String columnname, String datatype, String spects) {
        try {
            file.seek(file.length());
            file.writeUTF(tablename);
            file.writeUTF(columnname);
            file.writeUTF(datatype);
            file.writeUTF(spects);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StringBuilder select(String tbl, List<SelectItem> projection, Expression filter) {
        StringBuilder sb = new StringBuilder();
        try {
            ArrayList<Schema> schemas = readTableSchema(tbl);
            if (isRightProjection(schemas, projection)) {
                ArrayList<String> strprojection = getStrProjections(projection, schemas);

                HashMap<String, ArrayList<Object>> data = null;
                if (filter == null) {
                    data = readFile(tbl, schemas);

                } else if (filter.toString().contains("IA")) {
                    data = mapIA;

                } else {
                    data = readFile(tbl, schemas, filter);
                }

                String column = strprojection.get(0);
                sb.append(strprojection.toString() + "\n");

                for (int i = 0; i < data.get(column).size(); i++) {
                    for (String selectItem : strprojection) {

                        sb.append(data.get(selectItem).get(i) + ",");
                    }
                    sb.append("\n");
                }
                return sb;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);

        }
        return sb;
    }

    private HashMap<String, ArrayList<Object>> readFile(String tbl, ArrayList<Schema> schemas) {
        HashMap<String, ArrayList<Object>> map = new HashMap<>();
        try {
            RandomAccessFile filetable = new RandomAccessFile(tbl + ".tbl", "rw");
            if (filetable.length() > 0) {
                while (filetable.getFilePointer() < filetable.length()) {
                    for (int i = 0; i < schemas.size(); i++) {
                        Schema schema = schemas.get(i);
                        String datatype = schema.datatype.toLowerCase();
                        String columnname = schema.columnname;
                        Object datavalue = null;

                        if (datatype.equals("int")) {
                            datavalue = filetable.readInt();
                        } else if (datatype.equals("float")) {
                            datavalue = filetable.readFloat();
                        } else if (datatype.substring(0, 7).equals("varchar")) {
                            datavalue = filetable.readUTF();
                        }
                        if (map.get(columnname) == null) {
                            ArrayList<Object> lst = new ArrayList<>();
                            lst.add(datavalue);
                            map.put(columnname, lst);
                        } else {
                            ArrayList<Object> lst = map.get(columnname);
                            lst.add(datavalue);
                        }
                    }
                }
            } else {
                for (int i = 0; i < schemas.size(); i++) {
                    Schema schema = schemas.get(i);
                    String columnname = schema.columnname;
                    if (map.get(columnname) == null) {
                        map.put(columnname, new ArrayList<>());
                    }
                }
            }

            filetable.close();

        } catch (FileNotFoundException ex) {
            System.err.println("Archivo no encontrado: " + ex.getMessage());
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            System.err.println("Error de E/S: " + ex.getMessage());
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);

        }

        return map;
    }

    public Object[] Arrayrut(String tbl) {
        try {

            ArrayList<Schema> schemas = readTableSchema(tbl);
            HashMap<String, ArrayList<Object>> map = new HashMap<>();
            String Ruta = null;

            String columtype = null;

            LinkedList<Object> unicos = new LinkedList<>();
            ArrayList<Object> Listrut = new ArrayList<>();
            try {
                RandomAccessFile filetable = new RandomAccessFile(tbl + ".tbl", "rw");
                while (filetable.getFilePointer() < filetable.length()) {
                    for (int i = 0; i < schemas.size(); i++) {
                        Schema schema = schemas.get(i);
                        String datatype = schema.datatype.toLowerCase();
                        String columnname = schema.columnname;

                        Object datavalue = null;

                        if (datatype.equals("int")) {
                            datavalue = filetable.readInt();
                        } else if (datatype.equals("float")) {
                            datavalue = filetable.readFloat();
                        } else if (datatype.substring(0, 7).equals("varchar")) {
                            datavalue = filetable.readUTF();

                            if (identURL((String) datavalue)) {
                                String arrayrut = datavalue.toString();
                                columtype = datatype;
                                Ruta = columnname;
                                Listrut.add(arrayrut);
                                if (unicos.isEmpty()) {
                                    unicos.add(arrayrut);
                                } else if (!unicos.contains(arrayrut)) {
                                    unicos.add(arrayrut);
                                }

                            }
                        }
                        if (map.get(columnname) == null) {
                            ArrayList<Object> lst = new ArrayList<>();
                            lst.add(datavalue);
                            map.put(columnname, lst);
                        } else {
                            ArrayList<Object> lst = map.get(columnname);
                            lst.add(datavalue);
                        }

                    }

                }
                filetable.close();

                Object[] priceColumnValues = Listrut.toArray();
                return new Object[]{unicos, priceColumnValues};

            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean identURL(String Cadena) {
        //String regex = "^[a-zA-Z]:/(?:[^/:?\"<>|\\r\\n]+/)(?:[^/:*?\"<>|\\r\\n]+)?$";
        String regex = "^[a-zA-Z]:[\\\\/](?:[^\\\\/:?\"<>|\\r\\n]+[\\\\/])?(?:[^\\\\/:*?\"<>|\\r\\n]+)?$";

        return Cadena.matches(regex);
    }

    private HashMap<String, ArrayList<Object>> readFile(String tbl, ArrayList<Schema> schemas, Expression filter) {
        HashMap<String, ArrayList<Object>> map = new HashMap<>();
        String sfilter = filter.toString();
        boolean exitcolum = false;
        boolean exitcolumdef = true;

        try {
            RandomAccessFile filetable = new RandomAccessFile(tbl + ".tbl", "rw");
            if (filetable.length() > 0) {
                while (filetable.getFilePointer() < filetable.length()) {
                    String sexpresion = sfilter;
                    for (int i = 0; i < schemas.size(); i++) {
                        Schema schema = schemas.get(i);
                        String datatype = schema.datatype.toLowerCase();
                        String columnname = schema.columnname;
                        Object datavalue = null;
                        if (datatype.equals("int")) {
                            datavalue = filetable.readInt();
                        } else if (datatype.equals("float")) {
                            datavalue = filetable.readFloat();
                        } else if (datatype.substring(0, 7).equals("varchar")) {
                            datavalue = filetable.readUTF();
                        }

                        if (map.get(columnname) == null) {
                            ArrayList<Object> lst = new ArrayList<>();
                            lst.add(datavalue);
                            map.put(columnname, lst);
                        } else {
                            ArrayList<Object> lst = map.get(columnname);
                            lst.add(datavalue);
                        }
                        if (datatype.contains("varchar")) {
                            datavalue = "'" + datavalue + "'";
                        }
                        if (sexpresion.contains(columnname)) {
                            exitcolum = true;

                        }
                        sexpresion = sexpresion.replaceAll("(?<=^\\s*)" + columnname + "(?=\\s*=)", String.valueOf(datavalue).replace("\\", "\\\\") + "");
                    }

                    if (exitcolum == true) {
                        if (SimpleEvaluateExpr.evaluate(sexpresion) == 0) {
                            for (Map.Entry<String, ArrayList<Object>> entry : map.entrySet()) {
                                entry.getValue().remove(entry.getValue().size() - 1);
                            }
                        }
                    } else {
                        exitcolumdef = false;
                    }
                }
            } else {
                for (int i = 0; i < schemas.size(); i++) {
                    Schema schema = schemas.get(i);
                    String columnname = schema.columnname;

                    if (map.get(columnname) == null) {
                        map.put(columnname, new ArrayList<>());
                    }
                }
            }
            if (!exitcolumdef) {

                map.clear();
                for (int i = 0; i < schemas.size(); i++) {
                    Schema schema = schemas.get(i);
                    String columnname = schema.columnname;

                    if (map.get(columnname) == null) {
                        map.put(columnname, new ArrayList<>());
                    }
                }
            }

            filetable.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSQLParserException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    }

    public boolean isRightProjection(ArrayList<Schema> schemas, List<SelectItem> projection) {
        for (SelectItem selectItem : projection) {
            boolean isInlist = false;
            for (Schema schema : schemas) {
                if (selectItem.toString().equals(schema.columnname) || selectItem.toString().equals("*")) {
                    isInlist = true;
                    break;
                }
            }
            if (!isInlist) {
                return false;
            }
        }
        return true;
    }

    public void closeTableSchema() {
        try {
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openTableSchema() {
        try {
            file = new RandomAccessFile(pathschema, "rw");
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<String> getStrProjections(List<SelectItem> projection, ArrayList<Schema> schemas) {
        ArrayList<String> strprojections = new ArrayList<>();
        for (SelectItem selectItem : projection) {
            if (selectItem.toString().equals("*")) {
                for (Schema schema : schemas) {
                    strprojections.add(schema.columnname);
                }
            } else {
                strprojections.add(selectItem.toString());
            }
        }
        return strprojections;
    }

    public void addColumn(String tbl, String column, String type, Object[] defauls) {
        RandomAccessFile filetable = null;
        RandomAccessFile tempFile = null;

        try {
            ArrayList<Schema> schemas = readTableSchema(tbl);
            filetable = new RandomAccessFile(tbl + ".tbl", "rw");
            tempFile = new RandomAccessFile("temp_" + tbl + ".tbl", "rw");

            while (filetable.getFilePointer() < filetable.length()) {
                for (Schema schema : schemas) {
                    String datatype = schema.datatype.toLowerCase();
                    String columnname = schema.columnname;

                    Object datavalue = null;

                    if (column.equalsIgnoreCase(columnname)) {

                        for (Object defaul : defauls) {

                            // Añadir valor por defecto para la nueva columna
                            if ("varchar".equalsIgnoreCase(type)) {
                                tempFile.writeUTF((String) defaul);
                            } else if ("int".equalsIgnoreCase(type)) {
                                tempFile.writeInt((int) defaul);
                            } else if ("float".equalsIgnoreCase(type)) {
                                tempFile.writeFloat((float) defaul);
                            }
                        }
                        continue;
                    }
                    switch (datatype) {
                        case "int":

                            if (filetable.getFilePointer() + Integer.BYTES <= filetable.length()) {
                                datavalue = filetable.readInt();

                                tempFile.writeInt((int) datavalue);

                            } else {
                                Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "Datos insuficientes para leer int.");
                                tempFile.writeInt(0);

                            }
                            break;
                        case "float":
                            if (filetable.getFilePointer() + Float.BYTES <= filetable.length()) {
                                datavalue = filetable.readFloat();

                                tempFile.writeFloat((float) datavalue);

                            } else {
                                Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "Datos insuficientes para leer float.");
                                tempFile.writeFloat((float) 0.0);
                            }
                            break;
                        case "varchar":
                            try {
                                datavalue = filetable.readUTF();

                                tempFile.writeUTF((String) datavalue);
                            } catch (EOFException e) {
                                Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "Datos insuficientes para leer varchar.");
                                tempFile.writeUTF("X"); // Escribe un valor vacío por defecto
                            }
                            break;
                        default:
                            Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "Tipo de dato desconocido: " + datatype);
                            break;
                    }
                }

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (filetable != null) {

                    filetable.close();
                }
                if (tempFile != null) {

                    tempFile.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }// Intentar eliminar el archivo original y renombrar el archivo temporal
        File oldFile = new File(tbl + ".tbl");
        File newFile = new File("temp_" + tbl + ".tbl");

        // Registrar el estado del archivo antes de eliminarlo
        if (oldFile.exists()) {
            Logger.getLogger(FileManager.class.getName()).log(Level.INFO, "El archivo original existe y será eliminado.");

        } else {
            Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "El archivo original no existe.");
        }

        boolean eliminado = false;
        for (int i = 0; i < 5; i++) {
            if (oldFile.delete()) {
                eliminado = true;
                break;
            } else {
                try {
                    Thread.sleep(100); // Esperar 100 ms antes de reintentar
                } catch (InterruptedException e) {
                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }

        // boolean eliminado= oldFile.delete();
        if (!eliminado) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, "No se pudo eliminar el archivo original." + eliminado);
            oldFile.deleteOnExit();
        } else {
            Logger.getLogger(FileManager.class.getName()).log(Level.INFO, "Archivo original eliminado con éxito." + eliminado);
            boolean renombrar = newFile.renameTo(oldFile);
            if (!renombrar) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, "No se pudo renombrar el archivo temporal." + renombrar);
            } else {
                Logger.getLogger(FileManager.class.getName()).log(Level.INFO, "Archivo temporal renombrado con éxito." + oldFile);
            }
        }

        // Registrar el estado final del archivo
        if (oldFile.exists()) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, "El archivo original aún existe después del intento de eliminación.");
        } else {
            Logger.getLogger(FileManager.class.getName()).log(Level.INFO, "El archivo original no existe después del intento de eliminación.");
        }

    }

    public boolean doesColumnExist(String tbl, String columnName) throws IOException {
        ArrayList<Schema> schemas = readTableSchema(tbl);

        // Recorremos la lista de esquemas
        for (Schema schema : schemas) {
            // Verificamos si el nombre de la columna en el esquema coincide con el nombre de la columna buscada
            if (schema.columnname.equalsIgnoreCase(columnName)) {
                return true; // La columna existe
            }
        }

        return false; // La columna no existe
    }

    public String Alter(LinkedList<String> urls, LinkedList<String> unicos, LinkedList<Integer> clases, String name_tab, Integer Predicion) {
        try {
            ArrayList<Schema> schemas = readTableSchema(name_tab);
            try (RandomAccessFile dos = new RandomAccessFile("classes.tbl", "rw")) {
                for (String url : urls) {
                    int i = unicos.indexOf(url);
                    dos.writeInt(clases.get(i));
                }
            } catch (IOException e) {
                System.out.println("Error al escribir en el archivo binario: " + e.getMessage());
            }
            HashMap<String, ArrayList<Object>> sb = readAndWriteFile(name_tab, schemas, Predicion);
            getIA(sb);

            return "";

        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static HashMap<String, ArrayList<Object>> getIA(HashMap<String, ArrayList<Object>> map) {
        return mapIA = map;
    }

    private HashMap<String, ArrayList<Object>> readAndWriteFile(String tbl, ArrayList<Schema> schemas, Integer predicion) {
        HashMap<String, ArrayList<Object>> map = new HashMap<>();

        boolean exitcolumdef = true;
        int classe = -1;
        openTableSchema();
        writeTableSchema(tbl, "Clase", "int", "[class]");
        try (RandomAccessFile dis = new RandomAccessFile("classes.tbl", "rw"); RandomAccessFile filetable = new RandomAccessFile(tbl + ".tbl", "rw"); RandomAccessFile output = new RandomAccessFile("temp_" + tbl + ".tbl", "rw")) {

            if (filetable.length() > 0) {
                while (filetable.getFilePointer() < filetable.length()) {
                    for (int i = 0; i < schemas.size(); i++) {
                        Schema schema = schemas.get(i);
                        String datatype = schema.datatype.toLowerCase();
                        String columnname = schema.columnname;
                        Object datavalue = null;

                        // Leer los datos según el tipo de dato
                        if (datatype.equals("int")) {
                            datavalue = filetable.readInt();
                            output.writeInt((int) datavalue);  // Escribir al nuevo archivo
                        } else if (datatype.equals("float")) {
                            datavalue = filetable.readFloat();
                            output.writeFloat((float) datavalue);  // Escribir al nuevo archivo
                        } else if (datatype.substring(0, 7).equals("varchar")) {
                            datavalue = filetable.readUTF();
                            output.writeUTF((String) datavalue);  // Escribir al nuevo archivo
                        }

                        // Actualizar el HashMap
                        if (map.get(columnname) == null) {
                            ArrayList<Object> lst = new ArrayList<>();
                            lst.add(datavalue);
                            map.put(columnname, lst);
                        } else {
                            ArrayList<Object> lst = map.get(columnname);
                            lst.add(datavalue);
                        }
                    }

                    // Leer y escribir la clase
                    if (dis.getFilePointer() + 4 <= dis.length()) {
                        classe = dis.readInt();
                        output.writeInt(classe);  // Escribir el valor de "Clase" al nuevo archivo

                        if (map.get("Clase") == null) {
                            ArrayList<Object> ls = new ArrayList<>();
                            ls.add(classe);
                            map.put("Clase", ls);
                        } else {
                            ArrayList<Object> lst = map.get("Clase");
                            lst.add(classe);
                        }
                    } else {
                        throw new EOFException("No hay suficientes datos para leer un entero en classes.tbl.");
                    }

                    // Verificación de la clase y eliminación si no coincide con la predicción
                    if (classe != predicion) {
                        for (Map.Entry<String, ArrayList<Object>> entry : map.entrySet()) {
                            entry.getValue().remove(entry.getValue().size() - 1);
                        }
                    } else {
                        exitcolumdef = false;
                    }
                }
            } else {
                // Manejo para archivo vacío
                for (int i = 0; i < schemas.size(); i++) {
                    Schema schema = schemas.get(i);
                    String columnname = schema.columnname;
                    if (map.get(columnname) == null) {
                        map.put(columnname, new ArrayList<>());
                    }
                }
            }

            try {
                if (filetable != null) {

                    filetable.close();
                }
                if (output != null) {

                    output.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            File oldFile = new File(tbl + ".tbl");
            File newFile = new File("temp_" + tbl + ".tbl");

            // Registrar el estado del archivo antes de eliminarlo
            if (oldFile.exists()) {
                Logger.getLogger(FileManager.class.getName()).log(Level.INFO, "El archivo original existe y será eliminado.");

            } else {
                Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "El archivo original no existe.");
            }
            boolean eliminado = false;
            for (int i = 0; i < 5; i++) {
                if (oldFile.delete()) {
                    eliminado = true;
                    break;
                } else {
                    try {
                        Thread.sleep(100); // Esperar 100 ms antes de reintentar.
                    } catch (InterruptedException e) {
                        Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
            if (!eliminado) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, "No se pudo eliminar el archivo original." + eliminado);
                oldFile.deleteOnExit();
            } else {
                Logger.getLogger(FileManager.class.getName()).log(Level.INFO, "Archivo original eliminado con éxito." + eliminado);
                boolean renombrar = newFile.renameTo(oldFile);
                if (!renombrar) {
                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, "No se pudo renombrar el archivo temporal." + renombrar);
                } else {
                    Logger.getLogger(FileManager.class.getName()).log(Level.INFO, "Archivo temporal renombrado con éxito." + oldFile);
                }
            }
            return map;

        } catch (IOException e) {
            System.out.println("Error al leer o escribir los archivos: " + e.getMessage());
            return null;
        }
    }
}
