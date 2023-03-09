/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vpkextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yo
 */
class Entry {

    static final int TERMINATOR = 0x7FFF;
    Archivo archivo;
    short archivoIndex;
    byte[] preloadData;
    String nombreFichero;
    String extension;

    int crc;
    int offset;
    int length;
    short terminator;

    protected Entry(Archivo archivo, short archivoIndex, byte[] preloadData, String nombreFichero, String extension, int crc, int offset, int length, short terminator) {
        this.archivo = archivo;
        this.archivoIndex = archivoIndex;
        this.preloadData = preloadData;
        this.nombreFichero = nombreFichero;
        this.extension = extension;
        this.crc = crc;
        this.offset = offset;
        this.length = length;
        this.terminator = terminator;
    }   

    byte[] readData() {
        //chequeo de precarga
        if (preloadData != null) {
            return preloadData;
        }
        //Obtengo fichero objetivo
        File target = null;

        if (archivo.isVariasPartes()) {
            target = archivo.getChildArchive(archivoIndex);
        } else {
            target = archivo.getFile();
        }
        //Preparo para leer los datos
        byte[] data = new byte[length];
        try {
            FileInputStream fis = new FileInputStream(target);
            if (archivoIndex == TERMINATOR) {
                fis.skip(archivo.getLongitud());
                fis.skip(archivo.getLongitudCabecera());
            }
            //Leo los datos
            fis.skip(offset);
            fis.read(data, 0, length);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    void extract(File file) {
        try {
            preloadData = readData();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(preloadData);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String getFullName() {
        return nombreFichero + "." + extension;
    }

    /*Getters*/
    public Archivo getArchivo() {
        return archivo;
    }

    public short getArchivoIndex() {
        return archivoIndex;
    }

    public byte[] getPreloadData() {
        return preloadData;
    }

    public String getNombreFichero() {
        return nombreFichero;
    }

    public String getExtension() {
        return extension;
    }

    public int getCrc() {
        return crc;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public short getTerminator() {
        return terminator;
    }
}
