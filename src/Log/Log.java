/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class Log {
     private String  ruta=System.getProperty("user.dir");
     //private String  rutaXML="/var/www/html/docs/soap/";   //251
     private String rutaXML="/home/sat/soap/envio"; //KIO
    //private String  ruta="/var/lib/tomcat6/webapps/SIFEI/";


     /**
      * Hebert Ricardo Peña Serna
      * DESCRIPCION:
      * @param MensajeEr  Mensaje que se escribira en el LOG
      *
      */
     public Log()
    {
    
     }
    public void Write(String MensajeEr) {
     try
        {
            Date fecha = new Date();
            SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd");

            String nombre=formato.format(fecha)+ ".txt";
            File estarchivobit= new File(ruta+"/" +nombre);
            if(!(estarchivobit.exists())){
                PrintWriter creacionfic = new PrintWriter(estarchivobit);
                creacionfic.close();
           }
                BufferedWriter out = new BufferedWriter(new FileWriter(ruta + "/" + nombre,true));
                out.newLine();
                out.write("\n "+ new Date() + " - " + MensajeEr);
                out.close();
                System.out.println(new Date() + " - " +  MensajeEr);
           } catch (IOException e){
             System.out.println(""+e.getMessage());
         }
    }

  

}

