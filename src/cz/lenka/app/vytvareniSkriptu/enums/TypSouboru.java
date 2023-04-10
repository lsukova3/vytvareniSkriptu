package cz.lenka.app.vytvareniSkriptu.enums;

/**
 * Typy souborů, které aplikace zpracovává
 * CSV
 * XML
 * JSON (TODO)
 */

public enum TypSouboru {
    /**
     * Soubor CSV
     */
    CSV("csv"),
    /**
     * Soubor XML
     */
    XML("xml"),
    /**
     * Soubor JSON
     */
    JSON("json");

    /**
     * přípona souboru
     */
    private final String PRIPONA;

    /**
     * Konstruktor
     * @param pripona Přípona souboru (.xml, .csv, ..)
     */
    TypSouboru(String pripona) {
        this.PRIPONA = pripona;
    }

    public String getPRIPONA() {
        return PRIPONA;
    }

}
