/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SincronizadorCarpetas;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JOptionPane;

/**
 * hilo encargado de lanzar el proceso NewMain.java
 *
 * @author javier
 */
public class RunProceso implements Runnable {

    public RunProceso() {
    }

    @Override
    public void run() {
        try {
            ejecutarProceso(NewMain.class);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ("error al lanzar proceso: \n" + ex.toString() + " error: 015"), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, ("error al lanzar proceso: \n" + ex.toString() + " error: 016"), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static int ejecutarProceso(Class clase) throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");

        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

        String javaPath = System.getProperty("java.class.path");

        String javaName = clase.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", javaPath, javaName);

        
        Process proceso = builder.start();

        proceso.waitFor();

        OutputStream stream = proceso.getOutputStream();

        return proceso.exitValue();
    }

}
