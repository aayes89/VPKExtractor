/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vpkextractor;

import java.util.ArrayList;

/**
 *
 * @author Yo
 */
class Directorio {

    final String SEPARATOR = "/";
    String ruta;
    ArrayList<Entry> entries;

    protected Directorio(String ruta) {
        this.ruta = ruta;
        entries = new ArrayList<>();
    }

    String getRuta() {
        return this.ruta;
    }

    String getRutaPara(Entry entry) {
        return (this.ruta + SEPARATOR + entry.getFullName());
    }

    void addEntry(Entry entry) {
        entries.add(entry);
    }

    void removeEntry(Entry entry) {
        entries.remove(entry);
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

}
