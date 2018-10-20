/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SincronizadorCarpetas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javier
 */
public class ComprobarEscribirArchivo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            obtenerCarpetas();
        } catch (IOException ex) {
            Logger.getLogger(ComprobarEscribirArchivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * obtenmos carpetas a comprobar y realizamos operaciones necesarias
     *
     * @throws IOException
     */
    public static void obtenerCarpetas() throws IOException {
        String ruta = "/home/javier/Escritorio/DAM2";
        Process p = Runtime.getRuntime().exec("ls -a " + ruta);
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String tmpLine = "";

        ArrayList<String> carpetas = new ArrayList();

        while ((tmpLine = bri.readLine()) != null) {
            if (!tmpLine.equals(".") && !tmpLine.equals("..")) {
                carpetas.add(tmpLine);
            }
        }
        
        
        operarInformacion(comprobarActMasReciente(carpetas, ruta), verUltimaMod(), ruta, carpetas);
    }

    
    /**
     * realizamos las operaciones nesesarias dependiendo de si la carpeta ha
     * sido mofificada o no
     * @param datosMasRecientes array de array que contiene los datos mas
     * recientes de las carpetas
     * @param datosAlmacenados array de array que contiene la informacion de la
     * utlima actualizacion conocida
     */
    private static void operarInformacion(ArrayList<ArrayList<String>> datosMasRecientes, ArrayList<ArrayList<String>> datosAlmacenados, 
            String ruta, ArrayList<String> carpetas) throws IOException {
       
        
        for (int i = 0; i < datosMasRecientes.size(); i++) {
            boolean nuevo = true;
            
            String datoRutaR = datosMasRecientes.get(i).get(0);
            String datoModR = datosMasRecientes.get(i).get(1);

            for (int j = 0; j < datosAlmacenados.size(); j++) {
                String datoRutaA = datosAlmacenados.get(j).get(0);
                String datoModA = datosAlmacenados.get(j).get(1);

                if (datoRutaA.equals(datoRutaR)) {
                    nuevo = false;
                    if (!datoModA.equals(datoModR)) {
                        sincronizarDatos(datoRutaR);
                    }
                } 
            }
            if (nuevo) {
                sincronizarDatos(datoRutaR);
            }
        }
        almacenarCambios(carpetas, ruta);

    }

    /**
     * copiamos los nuevos datos en la ruta de almacenamiento
     * @param ruta ruta de los datos que hay que guardar
     * @throws IOException 
     */
    private static void sincronizarDatos(String ruta) throws IOException {
        String comand = "cp -r " + ruta + " /home/javier/Escritorio/repositorioCopias/"+obtenerNombre(ruta);
        Process p = Runtime.getRuntime().exec(comand);
    }
    
    /**
     * obtenemos el nombre de la carpeta
     * @param ruta ruta que contiene el nombre de la carpeta al final
     * @return devolvemos el nombre de la carpeta
     */
    private static String obtenerNombre(String ruta){
        String rutas[] = ruta.split("/");
        String nombre = rutas[rutas.length-1];
        return nombre;
    }

    /**
     * almacenamos cuando se modificarion por ultima vez los cambios
     *
     * @param carpetas carpetas a comprobar
     * @param ruta ruta de las carpetas
     * @throws IOException
     */
    private static void almacenarCambios(ArrayList<String> carpetas, String ruta) throws IOException {
        String datos = "";
        for (int i = 0; i < carpetas.size(); i++) {
            Process p = Runtime.getRuntime().exec("stat " + ruta + "/" + carpetas.get(i));
            BufferedReader BF = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String dato = "";
            while ((dato = BF.readLine()) != null) {
                String j[] = dato.split(" ");
                for (int k = 0; k < j.length; k++) {
                    if (j[k].equals("Fichero:")) {
                        datos += quitarComillas(dato);
                        datos += "\n";
                    } else{
                        if (j[k].equals("Cambio:")) {
                            datos += dato;
                            datos += "\n";
                        }
                    }

                }
            }
        }
        System.out.println(datos);
    }

    /**
     * extraemos las fechas de actualizacion y la ruta de su carpeta
     *
     * @return devolvemos un arrray de array que contiene ruta de carpeta y su
     * ultima modificacion.
     */
    private static ArrayList<ArrayList<String>> verUltimaMod() {
        Scanner input = new Scanner(System.in);

        ArrayList<ArrayList<String>> datosMod = new ArrayList();
        
        String nombre = "";
        try {
            nombre = input.nextLine().trim();
        } catch (Exception ex) {
            nombre = "";
        }
        
        //act es la fecha de la ultima actualizacion
        String act = "";

        while (!nombre.equals("")) {
            String nom[] = nombre.split(" ");

            //la funcion trim() elimina espacios en blanco al inicio y al final
            act = input.nextLine().trim();

            //F_act es la fecha de actualizacion
            String F_act[] = act.split(" ");

            ArrayList<String> almacenamiento = new ArrayList();
            almacenamiento.add(nom[1]);
            almacenamiento.add(F_act[1] + F_act[2]);

            datosMod.add(almacenamiento);
            nombre = input.nextLine().trim();
        }
        
        return datosMod;
    }

    /**
     * obtenemos la actualizacion mas reciente de las carpetas y su ruta
     *
     * @param carpetas array de carpetas para extraer la informacion
     * @param ruta ruta de las carpetas par extraer la informacion
     * @return devuelve los datos mas recientes de las carpetas
     * @throws IOException
     */
    private static ArrayList<ArrayList<String>> comprobarActMasReciente(ArrayList<String> carpetas, String ruta) throws IOException {
        ArrayList<ArrayList<String>> datosActualizacion = new ArrayList();
        for (int i = 0; i < carpetas.size(); i++) {
            ArrayList<String> datos = new ArrayList();
            Process p = Runtime.getRuntime().exec("stat " + ruta + "/" + carpetas.get(i));
            BufferedReader BF = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String dato = "";
            while ((dato = BF.readLine()) != null) {
                String j[] = dato.split(" ");
                for (int k = 0; k < j.length; k++) {
                    if (j[k].equals("Fichero:") || j[k].equals("Cambio:")) {
                        if (j[k].equals("Cambio:")) {
                            datos.add(j[k + 1] + j[k + 2]);
                        } else {
                            datos.add(quitarComillas(j[k + 1]));
                        }
                    }

                }
            }
            datosActualizacion.add(datos);
        }

        return datosActualizacion;
    }
    
    /**
     * funcion que quita las comillas de la ruta de las carpetas recibidas por el comando stat
     * @param ruta ruta de la carpeta con comillas
     * @return devolvemos la ruta de la carpeta sin comillas
     */
    public static String quitarComillas(String ruta){
        String resultado = "";
        for (int i = 1; i < ruta.length()-1; i++) {
            resultado += ruta.charAt(i);
        }
        return resultado;
    }

}
