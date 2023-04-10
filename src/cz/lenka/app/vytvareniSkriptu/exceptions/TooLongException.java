package cz.lenka.app.vytvareniSkriptu.exceptions;

public class TooLongException extends Exception{
    private int maxDelkaDesMist = 0;

    private String zprava;

    public TooLongException(String nazevPolozky, int maxDelka) {
        this.zprava = "Polozka " + nazevPolozky + " nesmi byt delsi nez " + maxDelka;
    }

    public TooLongException(String nazevPolozky, int maxDelka, int maxDelkaDesMist) {
        this.zprava = "Polozka " + nazevPolozky + " nesmi byt delsi nez " + maxDelka + "," + maxDelkaDesMist + ".";
    }

    public String getZprava(){
        return this.zprava;
    }
}
