package cz.lenka.app.vytvareniSkriptu.model;

import cz.lenka.app.vytvareniSkriptu.enums.DatovyTyp;

public class Sloupec {
    private Tabulka tabulka;
    private String nazev;
    private DatovyTyp datatype;
    private int delka;
    private int delkaDesMist;
    private String maska;
    private boolean notNull;
    private String check;
    private String defaultHodnota;
    private String polozkaSouboru;

    public void setTabulka(Tabulka tabulka) {
        this.tabulka = tabulka;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev.toLowerCase();
    }

    public DatovyTyp getDatatype() {
        return datatype;
    }

    public void setDatatype(DatovyTyp datatype) {
        this.datatype = datatype;
    }

    public int getDelka() {
        return delka;
    }

    public int getDelkaDesMist() {
        return delkaDesMist;
    }

    public void setDelkaDesMist(int delkaDesMist) {
        this.delkaDesMist = delkaDesMist;
    }

    public void setDelka(int delka) {
        this.delka = delka;
    }

    public String getMaska() {
        return maska;
    }

    public void setMaska(String maska) {
        this.maska = maska.toUpperCase();
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getDefaultHodnota() {
        return defaultHodnota;
    }

    public void setDefaultHodnota(String defaultHodnota) {
        this.defaultHodnota = defaultHodnota;
    }

    public String getPolozkaSouboru() {
        return polozkaSouboru;
    }

    public void setPolozkaSouboru(String polozkaSouboru) {
        this.polozkaSouboru = polozkaSouboru;
    }

    @Override
    public String toString() {
        return "Sloupec{" +
                "tabulka=" + tabulka +
                ", nazev='" + nazev + '\'' +
                ", datatype=" + datatype +
                ", delka=" + delka +
                ", maska='" + maska + '\'' +
                ", notNull=" + notNull +
                ", check='" + check + '\'' +
                ", defaultHodnota='" + defaultHodnota + '\'' +
                ", polozkaSouboru='" + polozkaSouboru + '\'' +
                '}';
    }
}
