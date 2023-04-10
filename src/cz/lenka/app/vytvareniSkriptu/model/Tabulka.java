package cz.lenka.app.vytvareniSkriptu.model;

import java.util.ArrayList;
import java.util.List;


public class Tabulka {
    private final String NAZEV;
    private final String SCHEMA;
    private final String TABLESPACE;
    private final String PRIMARY_KEY;
    private final List<Sloupec> SLOUPCE = new ArrayList<>();


    public Tabulka(String nazev, String schema, String tablespace, String primaryKey) {
        this.NAZEV = nazev.toLowerCase();
        this.SCHEMA = schema.toLowerCase();
        this.TABLESPACE = tablespace.toLowerCase();
        this.PRIMARY_KEY = primaryKey.toLowerCase();
    }

    public void pridejSloupec(Sloupec sloupec){
        SLOUPCE.add(sloupec);
    }

    /**
     * Vytvoří create script podle xml dokumentu
     * @return příkaz CREATE, ALTER
     */
    public String getCreateScript(){
        String createScript = "";
        // CREATE TABLE schema.table (
        createScript += "--DROP TABLE " + ((this.SCHEMA != null) ? this.SCHEMA + "." : "") + this.NAZEV + ";\n\n";
        createScript += "CREATE TABLE " + ((this.SCHEMA != null) ? this.SCHEMA + "." : "") + this.NAZEV + "(";
        String carka = "";

        // sloupec typ nullable default
        for(Sloupec s: SLOUPCE){
            createScript += carka + "\n\t" + s.getNazev() + "\t" + s.getDatatype();
            createScript += (s.getDelka()!=0) ? "("+s.getDelka()+")" : "";
            createScript += (s.getDefaultHodnota().isEmpty()) ? "" : "\tDEFAULT " + s.getDefaultHodnota();
            createScript += (s.isNotNull()? "\tNOT NULL" : "");
            if(carka.equals("")){
                carka = ",";
            }
        }
        createScript += ")" + ((this.TABLESPACE != null) ? " TABLESPACE " + this.TABLESPACE + ";" : "");


        if(!this.PRIMARY_KEY.isEmpty()){
            createScript += "\n\n";
            createScript += "ALTER TABLE " + ((this.SCHEMA != null) ? this.SCHEMA + "." : "") + this.NAZEV + " ADD CONSTRAINT " + this.NAZEV + "_pk PRIMARY KEY (" + this.PRIMARY_KEY + ");\n";
        }

        return createScript;
    }

    public String getNAZEV() {
        return NAZEV;
    }

    public String getSCHEMA() {
        return SCHEMA;
    }

    public List<Sloupec> getSLOUPCE() {
        return SLOUPCE;
    }

    @Override
    public String toString() {
        return "Tabulka{" +
                ", nazev='" + NAZEV + '\'' +
                ", schema='" + SCHEMA + '\'' +
                ", tablespace='" + TABLESPACE + '\'' +
                ", primaryKey='" + PRIMARY_KEY + '\'' +
                ", sloupce=" + SLOUPCE +
                '}';
    }
}

