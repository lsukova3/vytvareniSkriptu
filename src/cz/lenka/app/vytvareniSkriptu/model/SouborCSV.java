package cz.lenka.app.vytvareniSkriptu.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SouborCSV extends Soubor{
    /**
     * Adresář s sql soubory
     */
    private final String SQL_DIRECTORY = "csv2sql\\sql\\";

    /**
     * oddelovac definovaý v xml
     */
    private final String ODDELOVAC;
    /**
     * Seznam tabulek
     */
    private Tabulka tabulka;

    /**
     * Záhlaví souboru, struktura věty, získává se z první řádky souboru
     */
    private String zahlavi;
    /**
     * Pole položek, získává se ze záhlaví
     */
    private String[] polozkyZahlavi;
    /**
     * Řádky souboru před zpracováním
     */
    private final List<Radek> RADKY = new ArrayList<>();

    /**
     * Konstruktor
     * @param pathname cesta k souboru

     * @param oddelovac oddělovač
     */
    public SouborCSV(String pathname, String oddelovac) {
        super(pathname);
        this.ODDELOVAC =oddelovac;
    }

    /**
     * Konstruktor
     *
     * @param pathname cesta k souboru, včetně jeho jména a přípony
     */
    public SouborCSV(String pathname, String nazev, String oddelovac) {
        this(pathname, oddelovac);
    }

    public void setTabulka(Tabulka tabulka) {
        this.tabulka = tabulka;
    }

    /**
     * Otevře soubor a načte záhlaví a řádky
     * @throws IOException výjimka při čtení souboru
     */
    private void dejZahlaviARadky() throws IOException {
        BufferedReader br = null;
        String s;
        try{
            br = new BufferedReader(new FileReader(this.getPath()));
            this.zahlavi = br.readLine();
            this.polozkyZahlavi = this.zahlavi.split(ODDELOVAC);
            while((s = br.readLine())!=null){
                RADKY.add(new Radek(s, this.ODDELOVAC, this.polozkyZahlavi));
            }
        }catch(FileNotFoundException e){
            System.err.println("CSV soubor " + this.getName() + " nebyl nalezen.");
        }
        finally {
            if(br != null){
                br.close();
            }
        }
    }

    /**
     * naformátuje řetězec varchar2
     * odstraní bílé znaky na začátku a na konci
     * zdvojí ' obsažené v řetězci
     * obalí řetězec ''
     * @param polozka Položka
     * @return Naformátovaný řetězec
     */
    private String formatVarchar2(String polozka){
        return "'" + polozka.replaceAll("'","''").trim() + "'";
    }
    private String formatPolozky(String polozka, Sloupec sloupec){
        String formatedPolozka = "";

        //pokud je položka null, dopln default nebo NULL
        if(polozka==null || polozka.isEmpty()){
            if(sloupec.getDefaultHodnota()==null || sloupec.getDefaultHodnota().isEmpty()) {
                formatedPolozka = "NULL";
            } else {
                formatedPolozka = sloupec.getDefaultHodnota();
            }
        }
        else{
            //pokud jsou na začátku a na konci uvozovky, odstraň je
            //neodstraňuj, pokud jsou uprostřed retezce v uvozovkach
            if(polozka.matches("^\".*\"$")) {
                polozka = polozka.replaceAll("^\"", "").replaceAll("\"$", "");
            }

            switch (sloupec.getDatatype()) {
                case VARCHAR2: {
                    formatedPolozka = formatVarchar2(polozka);
                    break;
                }
                case NUMBER: {
                    if (sloupec.getMaska().isEmpty()) {
                        formatedPolozka = polozka;
                    } else {
                        formatedPolozka = "TO_NUMBER('" + polozka + "', '" + sloupec.getMaska() + "')";
                    }
                    break;
                }
                case DATE: {
                    formatedPolozka = "TO_DATE('" + polozka + "', '" + sloupec.getMaska() + "')";
                    break;
                }
                case TIMESTAMP: {
                    formatedPolozka = "TO_TIMESTAMP('" + polozka + "', '" + sloupec.getMaska() + "')";
                    break;
                }
            }
        }
        return formatedPolozka;
    }
    /**
     * Vytvoří insert příkaz pro jeden řádek
     * @param radek řádek
     * @return insert statement pro řádek
     */
    private String getStatement(Radek radek){
        String statement = "INSERT INTO " + ((this.tabulka.getSCHEMA() != null) ? this.tabulka.getSCHEMA() + "." : "") + this.tabulka.getNAZEV() + "(";
        String statement2 = ") VALUES (";
        String carka = "";


        for(Sloupec sl : this.tabulka.getSLOUPCE()){
            statement += carka + sl.getNazev();
            statement2 += carka + formatPolozky(radek.getMAP().get(sl.getPolozkaSouboru()), sl);
            if(carka.isEmpty()){
                carka = ", ";
            }
        }


       // radek.getMap()

        //statement += ") VALUES ("+ radek.dejRadek()+ ");";
        return statement + statement2 + ");";
    }

    /**
     * Uloží create příkaz do souboru
     */
    private void saveCreateStatement(){
        PrintWriter bw = null;
        try{
            bw = new PrintWriter(new FileWriter(this.SQL_DIRECTORY + this.tabulka.getNAZEV()+"_create.sql"));
            bw.println(this.tabulka.getCreateScript());
            System.out.println("\nCreate skript pro tabulku " + this.tabulka.getNAZEV() + " ulozen.");
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(bw!=null){
                bw.close();
            }
        }
    }

    /**
     * Uloží insert příkazy do souboru
     * @throws IOException chyba při zápisu do souboru
     */
    public void saveStatements() throws IOException{
        this.dejZahlaviARadky();
        if(!Paths.get(this.SQL_DIRECTORY).toFile().exists()){
            System.out.println("Zakladam adresar sql ...");
            Files.createDirectory(Paths.get(this.SQL_DIRECTORY));
        }

        saveCreateStatement();
        PrintWriter bw = null;
        try{
            bw = new PrintWriter(new FileWriter(this.SQL_DIRECTORY + this.tabulka.getNAZEV()+"_insert.sql", true));
            bw.println(this.tabulka.getCreateScript());
            for(int i=0; i<this.RADKY.size();i++){
                bw.println(getStatement(this.RADKY.get(i)));
                if(((i+1)%1000)==0) bw.println("\nCOMMIT;\n");
            }
            bw.println("\nCOMMIT;\n");
            System.out.println("\nSkript pro tabulku " + this.tabulka.getNAZEV() + " ulozen.");
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(bw!=null){
                bw.close();
            }

        }

    }

}
