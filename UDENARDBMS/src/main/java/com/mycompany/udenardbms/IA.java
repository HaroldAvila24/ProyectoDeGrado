/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udenardbms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;


public class IA implements Runnable {

    
    private String[] ruta;
    private CountDownLatch latch;
    private boolean ident;
     
    public IA(CountDownLatch latch, String[] rut,boolean ident) {
        this.latch = latch;
        this.ruta = rut;
        this.ident = ident;
        
    }

    @Override
    public void run() {
        final String HOST = "127.0.0.1";
        final int PUERTO = 5000;

        DataInputStream in;
        DataOutputStream out;

        try {
            Socket sc = new Socket(HOST, PUERTO);

            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            if (ruta.length>0) {
                int index = 1;

                String paths;
                paths= String.join(",", Arrays.asList(ruta));   

                byte[] pathsBytes = paths.getBytes("UTF-8");
               
                out.writeInt(pathsBytes.length);
                out.write(pathsBytes);
              
              
                while (index <= ruta.length) {          
                    int clasePredicha = in.readInt();
                 
                    if (ident) {
                        Fuctionclass.setClasepredicha(clasePredicha);
                        
                    }else{
                         Fuctionclass.clasesList.add(clasePredicha);
                    }
                     index++;
                }

            } else {
                System.out.println("El archivo de imagen no existe.");
            }
            sc.close();
        }  catch (UnknownHostException e) {
            System.err.println("Host desconocido: " );
            e.printStackTrace();
        } catch (java.net.ConnectException e) {
            Fuctionclass.status = true;
            System.err.println("Conexión rechazada: " + e.getMessage());
        } catch (IOException e) {
                     
            System.err.println("Error de E/S: " + e.getMessage());
            e.printStackTrace();
        }
        latch.countDown();
    }
    

}
