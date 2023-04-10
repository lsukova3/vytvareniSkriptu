package cz.lenka.app.vytvareniSkriptu.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Řádek csv souboru
 */
public class Radek {

    /**
     * Celý řádek
     */
    private final String RADEK_STRING;

    /**
     * Mapování položek souboru na sloupce tabulky
     */
    private final Map<String,String> MAP = new HashMap<>();

    /**
     *
     * @param radekString Řetězec s celým řádkem
     * @param oddelovac Oddělovač položek v souboru
     * @param polozkyZahlavi Pole s položkami záhlaví
     */
    public Radek(String radekString, String oddelovac, String[] polozkyZahlavi) {
        this.RADEK_STRING = radekString;
        String[] polozky = this.RADEK_STRING.split(oddelovac + "(?=([^\"]|\"[^\"]*\")*$)");

        for(int i=0; i<polozkyZahlavi.length;i++){
            if(i<polozky.length) {
                this.MAP.put(polozkyZahlavi[i], polozky[i]);
            } else{
                this.MAP.put(polozkyZahlavi[i], "");
            }
        }
    }


    public Map<String, String> getMAP() {
        return MAP;
    }

    @Override
    public String toString() {
        return "Radek{" +
                "radekString='" + RADEK_STRING + '\'' +
                '}';
    }
}
