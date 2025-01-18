/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.udenardbms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import net.sf.jsqlparser.JSQLParserException;


public class UDENARDBMS implements Runnable {

    Thread hilo;

    static boolean isstart = true;
    private CountDownLatch latch;
    static String ruta = null;

    public UDENARDBMS() {

        hilo = new Thread(this);
        hilo.start();
    }

    public static void main(String[] args) {
        UDENARDBMS u = new UDENARDBMS();
        while (true) {
            if (!isstart) {
                u = new UDENARDBMS();
                isstart = true;
            }
        }
    }

    @Override
    public void run() {
        try {

            ServerSocket server = new ServerSocket(5050);
            DataInputStream in;
            DataOutputStream out;
            System.out.println("UDENARDBMS START");

            while (true) {
                Socket client = server.accept();
                System.out.println("Client connect");
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                String comand = in.readUTF();

                Fuctionclass.resetClasepredicha();
                Fuctionclass.status = false;
                
                if (comand.trim().toUpperCase().contains("IA")) {
                    comand= comand.replaceAll(RegrexExp.deleteparentesis, "");
                    if (Fuctionclass.Ruta(comand)) {
                        try {
                            String[] Rut = {ruta};
                            this.latch = new CountDownLatch(1);
                            initClient(Rut); //comentamos para provar el array

                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                System.err.println("El hilo principal fue interrumpido: " + e.getMessage());
                                e.printStackTrace();
                            }
                            if (Fuctionclass.status) {
                                out.writeUTF("error el Servidor IA no esta conectado intente nuevamente");
                                continue;
                            }
                            comand = Fuctionclass.splitcadenaa(comand, "where", "=", ",");
                            Fuctionclass.clasesList.clear();

                        } catch (Exception e) {
                            System.err.println("asignacion ia-> " + e);
                        }
                    }
                }

                StringBuilder r = null;
                try {
                    r = CRUDParser.parser(comand);

                } catch (JSQLParserException ex) {

                    r = new StringBuilder();
                    r.append("Failed to parse SQL query: ").append(ex.getMessage()).append("\n");
                    for (StackTraceElement element : ex.getStackTrace()) {
                        r.append(element.toString()).append("\n");
                    }
                }

                String cadena = r.toString();
                byte[] pathsBytes = cadena.getBytes("UTF-8");
                out.writeInt(pathsBytes.length);
                out.write(pathsBytes);
                Fuctionclass.clasesList.clear();
                client.close();
            }
        } catch (Exception ex) {
            isstart = false;
        }
    }

    public void initClient(String[] rut) {
        boolean ident = true;
        try {
            IA iaInstance = new IA(latch, rut, ident);
            Thread hilo = new Thread(iaInstance);
            hilo.start();
        } catch (Exception e) {
            System.err.println("Error al iniciar el hilo: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
