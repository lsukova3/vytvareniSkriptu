package cz.lenka.app.vytvareniSkriptu.enums;

/**
 * Datové typy, které se mohou v tabulkách vyskytovat
 * NUMBER
 * VARCHAR2
 * DATE
 * TIMESTAMP
 */
public enum DatovyTyp {
    /**
     * Datový typ NUMBER
     */
    NUMBER("number"),
    /**
     * Datový typ VARCHAR2
     */
    VARCHAR2("varchar2"),
    /**
     * Datový typ DATE
     */
    DATE("date"),
    /**
     * Datový typ DATETIME
     */
    TIMESTAMP("timestamp");
    /**
     * Název typu
     */
    private final String NAZEV_TYPU;

    /**
     * Konstruktor
     * @param nazevTypu název typu
     */
    DatovyTyp(String nazevTypu) {
        this.NAZEV_TYPU = nazevTypu;
    }

    /**
     * Na základě vstupního řetězce vyhledá odpovídající datový typ
     * @param nazevTypu "number", "varchar2", "date", "timestamp"
     * @return DatovyTyp.NUMBER, VARCHAR2, DATE, TIMESTAMP
     */
    public static DatovyTyp getNazevTypu(String nazevTypu) {
        for(DatovyTyp t : DatovyTyp.values()){
            if(t.NAZEV_TYPU.equalsIgnoreCase(nazevTypu)){
                return t;
            }
        }
        return null;
    }
}
