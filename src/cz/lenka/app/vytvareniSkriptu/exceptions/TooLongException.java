package cz.lenka.app.vytvareniSkriptu.exceptions;

public class TooLongException extends TheItemHasABadFormatException {

    public TooLongException(String nazevPolozky, int maxDelka) {
        this.setZprava("Polozka " + nazevPolozky + " nesmi byt delsi nez " + maxDelka + " znaků.");
    }

    public TooLongException(String nazevPolozky, String hodnotaPolozky, int maxDelka, int maxDelkaDesMist) {
        if (maxDelkaDesMist==1){
            this.setZprava("Číslo " + nazevPolozky + " (" + hodnotaPolozky + ") nesmi mít více než " + maxDelka + " míst a " + maxDelkaDesMist + " desetinné místo.");
        } else if(maxDelkaDesMist<=4){
            this.setZprava("Číslo " + nazevPolozky + " (" + hodnotaPolozky + ") nesmi mít více než " + maxDelka + " míst a " + maxDelkaDesMist + " desetinná mista.");
        } else {
            this.setZprava("Číslo " + nazevPolozky + " (" + hodnotaPolozky + ") nesmi mít více než " + maxDelka + " míst a " + maxDelkaDesMist + " desetinnych mist.");
        }
    }

}
