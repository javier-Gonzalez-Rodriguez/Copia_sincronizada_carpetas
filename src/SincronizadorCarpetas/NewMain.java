/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SincronizadorCarpetas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javier
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        try {
            File archivo = new File("datos.txt");
            if (!archivo.exists()) {
                FileWriter flujo = new FileWriter(archivo); 
                BufferedWriter crearArchivo = new BufferedWriter(flujo);
                
                crearArchivo.write("");
                
                crearArchivo.close();
                flujo.close();
            }
            
            ejecutarProceso(ComprobarEscribirArchivo.class);
            
        } catch (IOException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int ejecutarProceso(Class clase) throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");

        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

        String javaPath = System.getProperty("java.class.path");

        String javaName = clase.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", javaPath, javaName);

        builder.redirectOutput(new File("datos.txt"));
        
        builder.redirectInput(new File("datos.txt"));

        Process proceso = builder.start();

        proceso.waitFor();

        OutputStream stream = proceso.getOutputStream();
        return proceso.exitValue();
    }

    

}
