/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vpkextractor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Yo
 */
public class VPKExtractor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner in = new Scanner(System.in);
        System.out.println("Ingrese la ruta donde se encuentra el fichero a leer");
        String entrada = "";//in.nextLine();
        entrada = "C:/steam/steamapps/common/dota 2 beta/game/dota/maps";
        boolean verb = false;
        File inputFile = new File(entrada);        
        FilenameFilter fil = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".vpk"))
                    return true;
                return false;
            }
        };
        File[] ficheros = inputFile.listFiles(fil);
        System.out.println("Se detectaron "+ficheros.length+" ficheros con extension VPK");
        for (File f : ficheros) {
            System.out.println("\tFichero: "+f.getAbsolutePath());
            File outputDir = new File("\\VPKExtractor", f.getName());
            //System.out.println("Desea ver todo el proceso?\nS o N");
            //entrada = in.nextLine();
            if (entrada.equalsIgnoreCase("S")) {
                verb = true;
            }
            try {
                if (verb) {
                    System.out.println("Creado directorio raiz en: ");
                    System.out.println("\t" + outputDir.getAbsolutePath());
                }
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                //cargar
                if (verb) {
                    System.out.println("Cargando VPK...");
                }
                Archivo fichero = new Archivo(f);//inputFile);
                fichero.cargar();
                if (verb) {
                    System.out.println("\tArchivo: " + f.getName());//inputFile.getName());
                    System.out.println("\tFirma: " + fichero.getFirma());;
                    System.out.println("\tVersion: " + fichero.getVersion());
                    System.out.println("\tDirectorios: " + fichero.getDirectorios().size());
                }

                //recorrer vpk
                if (verb) {
                    System.out.println("Extrayendo entradas...");
                }
                for (Directorio dirs : fichero.getDirectorios()) {
                    if (verb) {
                        System.out.println("\tRuta: " + dirs.getRuta());
                    }
                    for (Entry entry : dirs.getEntries()) {
                        if (verb == true) {
                            System.out.println("\t\tFichero: " + entry.getFullName());
                            System.out.println("\tCRC: " + entry.getCrc());
                            System.out.println("\tExtension: " + entry.getExtension());
                            System.out.println("\tLongitud: " + entry.getLength() + " bytes");
                        }
                        try {
                            File entryDir = new File(outputDir, dirs.getRuta());
                            File entryFil = new File(outputDir, dirs.getRutaPara(entry));

                            if (!entryDir.exists()) {
                                if (verb) {
                                    System.out.println("No existe el directorio: \t" + entryDir.getAbsolutePath());
                                    System.out.println("Creando directorio(s)...");
                                }
                                entryDir.mkdirs();
                            }
                            if (verb) {
                                System.out.println("Extrayendo " + entryFil.getName() + " en la ruta " + entryDir.getParent());
                            }
                            entry.extract(entryFil);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        System.out.println("\t\n");
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
