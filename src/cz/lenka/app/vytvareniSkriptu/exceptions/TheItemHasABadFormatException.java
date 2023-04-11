package cz.lenka.app.vytvareniSkriptu.exceptions;

public abstract class TheItemHasABadFormatException extends Exception{

    private String zprava;

    public String getZprava() {
        return zprava;
    }

    public void setZprava(String zprava) {
        this.zprava = zprava;
    }
}
