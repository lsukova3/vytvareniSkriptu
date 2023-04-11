package cz.lenka.app.vytvareniSkriptu.model;

import cz.lenka.app.vytvareniSkriptu.exceptions.*;

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

    public void setTabulka(Tabulka tabulka) {
        this.tabulka = tabulka;
    }

    /**
     * Otevře soubor a načte záhlaví a řádky
     * @throws IOException výjimka při čtení souboru
     */
    private void dejZahlaviARadky() throws IOException {
        BufferedReader br = null;
        String[] polozkyZahlavi;
        String s;
        try{
            br = new BufferedReader(new FileReader(this.getPath()));
            this.zahlavi = br.readLine();
            polozkyZahlavi = this.zahlavi.split(ODDELOVAC);
            while((s = br.readLine())!=null){
                RADKY.add(new Radek(s, this.ODDELOVAC, polozkyZahlavi));
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
    private String formatPolozky(String polozka, Sloupec sloupec) throws TheItemHasABadFormatException {
        String formatedPolozka = "";

        //pokud je položka null, dopln default nebo NULL
        //pokud má mít constraint NOT NULL, zapis do chybového souboru
        if(polozka==null || polozka.isEmpty()){
            if(sloupec.getDefaultHodnota()!=null && !sloupec.getDefaultHodnota().isEmpty()) {
                formatedPolozka = sloupec.getDefaultHodnota();
            } else if (!sloupec.isNotNull()) {
                formatedPolozka = "NULL";
            }else {
                throw new CannotBeNullException(sloupec.getNazev());
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
                    //pokud je řetězec delší než stanovená délka, vyhoď výjimku
                    if(sloupec.getDelka()>0 && formatedPolozka.length()>sloupec.getDelka()){
                        throw new TooLongException(sloupec.getPolozkaSouboru(),sloupec.getDelka());
                    }
                    break;
                }
                case NUMBER: {
                    // číslo bez požadované masky
                    if (sloupec.getMaska().isEmpty()) {
                        //Zkontroluj, zda se jedná o číslo (např. 12345, 12 345, 12.345, 12,345, 123.45, 123,4555555)
                        if(!polozka.matches("^[\\+\\-]?([0-9]{1,3})?([\\,|\\.|\\ ]?[0-9]{3})*([\\.|\\,][0-9]+)?$")){
                            throw new TheItemIsNotANumberException(sloupec.getPolozkaSouboru());
                        }
                        //Zkontroluj delku, odstraň oddelovače tisíců
                        //nejsou tam desetinná místa
                        if(sloupec.getDelkaDesMist()==0){
                            formatedPolozka = polozka.replaceAll("[\\,\\.\\ ]", "");
                            if(sloupec.getDelka()>0 && formatedPolozka.length() > sloupec.getDelka()){
                                throw new TooLongException(sloupec.getPolozkaSouboru(),sloupec.getDelka());
                            }
                        }

                        else
                        //jsou tam desetinná místa, odstraň oddelovače tisíců a desetinnou čárku nahraď tečkou
                        {
                            char[] ret = polozka.toCharArray();
                            for(int i = ret.length-1;i>=0;i--){
                                if(ret[i]=='.') {
                                    formatedPolozka = polozka.replaceAll("\\,", "");
                                    break;
                                } else if(ret[i]==','){
                                    formatedPolozka = polozka.replaceAll("\\.", "").replace(",",".");
                                    break;
                                }
                            }
                            String[] polozkaSplit = formatedPolozka.split("\\.");
                            if(((sloupec.getDelka()>0 || sloupec.getDelkaDesMist() > 0) && polozkaSplit[0].length() > sloupec.getDelka()) ||
                                    (polozkaSplit.length>1 &&   polozkaSplit[1].length() > sloupec.getDelkaDesMist())){
                                throw new TooLongException(sloupec.getPolozkaSouboru(), polozka, sloupec.getDelka(),sloupec.getDelkaDesMist());
                            }
                        }
                    } // číslo s maskou
                    else {
                        String regFromMaska = sloupec.getMaska().toUpperCase();
                        regFromMaska = regFromMaska.replace("9", "[0-9]?");
                        regFromMaska = regFromMaska.replace(".", "\\.");
                        regFromMaska = regFromMaska.replace(",", "\\,");
                        regFromMaska = regFromMaska.replace("D", "[\\.\\,]");
                        regFromMaska = regFromMaska.replace("G", "[\\.\\,]");
                        regFromMaska = regFromMaska.replace("S", "[\\+\\-]?");
                        regFromMaska = regFromMaska.replace("MI", "(\\-)?");

                        if(polozka.matches(regFromMaska)){
                            formatedPolozka = "TO_NUMBER('" + polozka + "', '" + sloupec.getMaska() + "')";
                        } else {
                            throw new WrongNumberFormatException(sloupec.getPolozkaSouboru(), polozka, sloupec.getNazev(), sloupec.getMaska(), regFromMaska);
                        }

                    }
                    break;
                }
                case DATE: {
                    String regFromMaska = sloupec.getMaska().toUpperCase();
                    regFromMaska = regFromMaska.replace(".","\\.");
                    regFromMaska = regFromMaska.replace("/","\\/");
                    regFromMaska = regFromMaska.replace("-","\\-");
                    regFromMaska = regFromMaska.replace(" ","\\ ");
                    regFromMaska = regFromMaska.replace(":","\\:");
                    regFromMaska = regFromMaska.replace("DD", "(0?[1-9]|[12][0-9]|3[01])");
                    regFromMaska = regFromMaska.replace("MMM", "(\\p{L}){3}");
                    regFromMaska = regFromMaska.replace("MM", "(0?[1-9]|1[012])");
                    regFromMaska = regFromMaska.replace("YYYY", "[0-9]{4}");
                    regFromMaska = regFromMaska.replace("YY", "[0-9]{2}");
                    regFromMaska = regFromMaska.replace("HH24", "0?[0-9]|1[0-9]|2[0-3]");
                    regFromMaska = regFromMaska.replace("HH", "0?[0-9]|1[0-2]");
                    regFromMaska = regFromMaska.replace("MI", "[0-5][0-9]");
                    regFromMaska = regFromMaska.replace("SS", "[0-5][0-9]");
                    regFromMaska = regFromMaska.replace("F", "[0-9]");

                    if(polozka.matches(regFromMaska)) {
                        formatedPolozka = "TO_DATE('" + polozka + "', '" + sloupec.getMaska() + "')";
                    } else {
                        throw new WrongDateFormatException(sloupec.getPolozkaSouboru(), polozka, sloupec.getNazev(), sloupec.getMaska(), regFromMaska);
                    }

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
    private String getStatement(Radek radek) throws TheItemHasABadFormatException {
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
        PrintWriter bwBad = null;
        try{
            bw = new PrintWriter(new FileWriter(this.SQL_DIRECTORY + this.tabulka.getNAZEV()+"_insert.sql", true));
            bwBad = new PrintWriter(new FileWriter(this.SQL_DIRECTORY + this.tabulka.getNAZEV()+"_bad.sql", true));
            //bw.println(this.tabulka.getCreateScript());
            int pocetRadku = 0;
            for(Radek radek : this.RADKY){
                try {
                    bw.println(getStatement(radek));
                    if (((++pocetRadku) % 1000) == 0) bw.println("\nCOMMIT;\n");
                } catch (TheItemHasABadFormatException e){
                    bwBad.println(e.getZprava());
                    System.out.println(e.getZprava());
                    bwBad.println(this.zahlavi);
                    bwBad.println(radek + "\n\n\n");
                }
            }
            bw.println("\nCOMMIT;\n");
            System.out.println("\nSkript pro tabulku " + this.tabulka.getNAZEV() + " ulozen.");
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(bw!=null){
                bw.close();
            }
            if(bwBad!=null){
                bwBad.close();
            }
        }

    }

}
