/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vpkextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 *
 * @author Yo
 */
public class Archivo {

    File f;
    ArrayList<Directorio> directorios;
    boolean variasPartes;
    int firma;
    int version;
    int longitud;
    int longitudCabecera;
    final int FIRMA = 0x55AA1234;
    final char NULL_TERMINATOR = 0x0;
    final int MIN_VERSION = 1;
    final int MAX_VERSION = 2;
    final int VER_ONE = 1;
    final int VER_TWO = 2;
    final int VER_ONE_HEAD = 12;
    final int VER_TWO_HEAD = 28;

    public Archivo(File f) {
        this.f = f;
        this.variasPartes = false;
        this.firma = 0;
        this.version = 0;
        this.longitud = 0;
        this.longitudCabecera = 0;
        this.directorios = new ArrayList<Directorio>();
    }

    int readUnsignedInt(FileInputStream fis) {
        return readBytes(fis, 4).getInt();
    }

    short readUnsignedShort(FileInputStream fis) {
        return readBytes(fis, 2).getShort();
    }

    String readString(FileInputStream fis) {
        StringBuilder sb = new StringBuilder();
        try {
            int c = 0;
            while ((c = fis.read()) != NULL_TERMINATOR) {
                sb.append((char) c);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }
    
    String extractPath(String ruta){
        return ruta.substring(0, ruta.length()-4);                
    }

    ByteBuffer readBytes(FileInputStream fis, int size) {
        ByteBuffer bf = null;
        try {
            byte[] buff = new byte[size];
            fis.read(buff);
            bf = ByteBuffer.wrap(buff);
            bf.order(ByteOrder.LITTLE_ENDIAN);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bf;
    }

    File getChildArchive(int index) {
        //chequeo
        if (!variasPartes) {
            System.out.println("Fichero no tiene varias partes");
        }
        //Obtengo el padre
        File parent = f.getParentFile();
        if (parent == null) {
            System.out.println("El archivo no tiene padre");
        }
        //Nombre del hijo
        String nombreArchivo = f.getName();
        String raiz = nombreArchivo.substring(0, nombreArchivo.length() - 8);
        String nombreHijo = String.format("%s_%03d.vpk", raiz, index);
        return new File(parent, nombreHijo);
    }

    void cargar() {
        try {
            FileInputStream fis = new FileInputStream(f);
            //tiene mas hijos?
            variasPartes = f.getName().contains("_dir");
            //leer cabecera
            firma = this.readUnsignedInt(fis);
            version = this.readUnsignedInt(fis);
            longitud = this.readUnsignedInt(fis);
            //chequear firma y version
            if (firma != FIRMA) {
                System.out.println("Fima no válida");//throw new ArchivoExcepcion("Firma no válida");
            }
            if (version < MIN_VERSION || version > MAX_VERSION)//throw new Exception
            {
                System.out.println("Versión no soportada");
            }
            //Manejo de versiones
            switch (version) {
                case VER_ONE:
                    longitudCabecera = VER_ONE_HEAD;
                    break;
                case VER_TWO:
                    longitudCabecera = VER_TWO_HEAD;
                    /*Leo otros datos en caso de futuras versiones y chequeos*/
                    break;
            }
            while (fis.available() != 0) {
                //obtengo la extension
                String ext = readString(fis);
                if (ext.isEmpty()) {
                    break;
                }
                while (true) {
                    //Obtengo ruta
                    String ruta = (readString(fis));
                    if (ruta.isEmpty()) {
                        break;
                    }
                    //directorio
                    Directorio dir = new Directorio(ruta);
                    directorios.add(dir);
                    
                    while (true) {
                        //Obtengo nombre de fichero
                        String nombreFichero = readString(fis);
                        if (nombreFichero.isEmpty()) {
                            break;
                        }
                        //Leer datos
                        int crc = readUnsignedInt(fis);
                        short preloadSize = readUnsignedShort(fis);
                        short index = readUnsignedShort(fis);
                        int entryOffset = readUnsignedInt(fis);
                        int entryLength = readUnsignedInt(fis);
                        short terminator = readUnsignedShort(fis);
                        byte[] preloadData = null;
                        if (preloadSize > 0) {
                            //leo los datos
                            preloadData = new byte[preloadSize];
                            fis.read(preloadData);
                        }
                        //Creo una entrada
                        Entry entry = new Entry(this, index, preloadData, nombreFichero, ext, crc, entryOffset, entryLength, terminator);
                        dir.addEntry(entry);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*Getters and Setters*/
    public File getFile() {
        return f;
    }

    public ArrayList<Directorio> getDirectorios() {
        return directorios;
    }

    public boolean isVariasPartes() {
        return variasPartes;
    }

    public int getFirma() {
        return firma;
    }

    public int getVersion() {
        return version;
    }

    public int getLongitud() {
        return longitud;
    }

    public int getLongitudCabecera() {
        return longitudCabecera;
    }

}
