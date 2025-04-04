/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udenardbms;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IA2 {

    private CountDownLatch latch;

    public String recibo(String comand, String rutasString) {
        String[] rutas = null;
        String[] parts = rutasString.split(";", 3);
        String rutUniques = parts[0].replaceAll(RegrexExp.deleteCorchetes, "").trim();
        // Primera parte: quitar corchetes
        String ruta = parts[1].replaceAll(RegrexExp.deleteCorchetes, "").trim();
        rutas = rutUniques.replaceAll("\\s+", "").split(",");

        this.latch = new CountDownLatch(1);
        initClient(rutas);
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(IA2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LinkedList<String> urls = new LinkedList<>(Arrays.asList(ruta.replaceAll("\\s+", "").split(",")));
        LinkedList<String> uniqueUrl = new LinkedList<>(Arrays.asList(rutas));

        Fuctionclass.ConsulAlter(urls, uniqueUrl);

        comand = comand.replaceAll(RegrexExp.afterIquals, " = IA");//cambiarf ese IA
        return comand;

    }

    public void initClient(String[] rut) {
        boolean ident = false;
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
