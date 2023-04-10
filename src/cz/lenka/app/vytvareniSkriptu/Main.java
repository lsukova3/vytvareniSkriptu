package cz.lenka.app.vytvareniSkriptu;
import cz.lenka.app.vytvareniSkriptu.enums.TypSouboru;
import cz.lenka.app.vytvareniSkriptu.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main{
    /**
     * Zpracuje csv soubory v adresáři csv a podle konfigurace v xml souboru
     *   vytvoří CREATE/INSERT skripty, které uloží do adresáře sql. <br/>
     * - adresáře xml načítá xml soubory
     * - validuje je pomocí xsd
     * - parsuje xml, vytváří objekty SouborCSV, Tabulka, Sloupec
     * - zpracuje csv, vytvoří objekty Radek
     * - vysledny skript uloží <br/>
     * Exceptions
     * xml není validní - neodpovídá struktuře definového v souboru xsd
     * csv soubor není nalezen - v adresáři csv chybí soubor, který je konfigurovaný v xml
     */
    public static void main(String[] args){
        try{
            List<SouborXML> souboryList = getSouboryFromDirectory("csv2sql\\xml", TypSouboru.XML);
            for (SouborXML souborXML : souboryList) {
                for (SouborCSV souborCSV : souborXML.getSOUBORY_CSV()) {
                    souborCSV.saveStatements();
                }
            }
        } catch(NullPointerException ex){
            System.err.println("Nebyl nalezen adresár csv2sql\\xml");
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }


    }

    /**
     * Vrátí seznam xml souborů
     *
     * @param jmeno jméno souboru
     * @param typSouboru typ souboru
     *
     * @return Seznam xml souborů
     */
    public static List<SouborXML> getSouboryFromDirectory(String jmeno, TypSouboru typSouboru) {
        List<SouborXML> souboryList = new ArrayList<>();
        File adresar = new File(jmeno);

        File[] soubory = adresar.listFiles();
        for (File s : soubory) {
            if (s.isFile() && s.getName().matches(".*\\." + typSouboru.getPRIPONA())) {
                System.out.println("\n\n\n" + s.getName());
                SouborXML souborXML = new SouborXML(s.getPath());
                if(souborXML.isValid()) {
                    souboryList.add(souborXML);
                }
            }
        }
        return souboryList;
    }
}

