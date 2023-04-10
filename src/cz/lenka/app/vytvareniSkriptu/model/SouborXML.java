package cz.lenka.app.vytvareniSkriptu.model;

import cz.lenka.app.vytvareniSkriptu.enums.DatovyTyp;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Definuje strukturu tabulky, mapuje csv soubor do tabulky
 */
public class SouborXML extends Soubor{
    /**
     * xsd soubor definuje strukturu xml
     */
    private final String XSD_STRUKTURA_FILE = "csv2sql\\xml\\struktura.xsd";
    /**
     * XML uzly
     */
    private final String SOUBOR = "soubor";
    private final String NAZEV = "nazev";
    private final String ODDELOVAC = "oddelovac";
    private final String TABULKA = "tabulka";
    private final String SCHEMA = "schema";
    private final String NAZEV_TABULKY = "nazev";
    private final String TABLESPACE = "tablespace";
    private final String PRIMARY_KEY = "primary_key";
    private final String SLOUPCE = "sloupce";
    private final String SLOUPEC = "sloupec";
    private final String NAZEV_SLOUPCE = "nazev";
    private final String DATOVY_TYP = "datovy_typ";
    private final String DELKA = "delka";
    private final String MASKA = "maska";
    private final String NOT_NULL = "not_null";
    private final String CHECK = "check";
    private final String DEFAULT = "default";
    private final String POLOZKA_SOUBORU = "polozka_souboru";

    /**
     * csv soubory, které jsou konfigurovány v xml
     */
    private final List<SouborCSV> SOUBORY_CSV = new ArrayList<>();

    private boolean isValid = false;

    /**
     * Konstruktor
     * @param pathname cesta k souboru
     */
    public SouborXML(String pathname) {
        super(pathname);
        if(validateXMLSchema()) {
            this.isValid = true;
            parseXML();
        } else {
            System.err.println("XML soubor " + this.getName() + " není validní.");
        }
    }

    public List<SouborCSV> getSOUBORY_CSV() {
        return SOUBORY_CSV;
    }

    public boolean isValid() {
        return isValid;
    }

    /**
     * Provede validaci vstupního xml podle xsd souboru
     * @return true, je-li dokument validní, false není validní
     */
    private boolean validateXMLSchema(){
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(XSD_STRUKTURA_FILE));
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.validate(new StreamSource(this));
            return true;
        }
        catch(SAXException|IOException ex){
            return false;
         }
    }
    /**
     * Parsuje XML, vytvari objekty SouborCSV, Tabulka, Sloupec
     * Vloží sloupce do tabulky
     * a tabulku do csv souboru
     */
    SouborCSV souborCSV = null;
    Tabulka tab = null;
    Sloupec sl = null;
    private void parseXML(){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(this);
            doc.getDocumentElement().normalize();

            //definuj koren
            Element root = doc.getDocumentElement();
            System.out.println("root: " + root.getNodeName());

            //nacti postupne jeden soubor po druhem
            NodeList souborNodes = doc.getElementsByTagName(SOUBOR);

            for (int i = 0; i < souborNodes.getLength(); i++) {

                Node souborNode = souborNodes.item(i);
                String nazev_csv = souborNode.getAttributes().getNamedItem(NAZEV).getNodeValue();
                String oddelovac_csv = souborNode.getAttributes().getNamedItem(ODDELOVAC).getNodeValue();
                souborCSV = new SouborCSV("csv2sql\\csv\\"+nazev_csv, oddelovac_csv);
                System.out.println(SOUBOR + ": " + nazev_csv);
                traverseXML(souborNode);

                SOUBORY_CSV.add(souborCSV);

            }
        }catch(ParserConfigurationException | SAXException | IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Prochází uzly XML
     * Rekurzívní metoda, volá se pro každou úroveň xml
     * Vytváří objekty Tabulka a Sloupec
     *
     * @param element uzel xml
     */
    private void traverseXML(@NotNull Node element){


        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {

            //vynech uzaviraci tagy
            if (child.getNodeType() != 1) continue;

            //podle názvu xml tagu vytvor příslušnou taxonomickou kategorii
            switch (child.getNodeName()){
                case TABULKA:{
                    String schema = child.getAttributes().getNamedItem(SCHEMA).getNodeValue();
                    String tableName = child.getAttributes().getNamedItem(NAZEV_TABULKY).getNodeValue();
                    String tablespace = child.getAttributes().getNamedItem(TABLESPACE).getNodeValue();
                    String primaryKey = child.getAttributes().getNamedItem(PRIMARY_KEY).getNodeValue();
                    tab = new Tabulka(tableName, schema,tablespace,primaryKey);
                    System.out.println("\t" + TABULKA + ": " + tableName);
                    traverseXML(child);
                    souborCSV.setTabulka(tab);
                    break;
                }
                case SLOUPCE:{
                    traverseXML(child);

                    break;
                }
                case SLOUPEC:{
                    sl = new Sloupec();
                    sl.setTabulka(tab);
                    traverseXML(child);
                    tab.pridejSloupec(sl);
                    System.out.println("\t\t" + SLOUPEC + ": " + sl.getNazev());
                    break;
                }
                case NAZEV_SLOUPCE:{
                    sl.setNazev(child.getTextContent());
                    break;
                }
                case DATOVY_TYP:{
                    sl.setDatatype(DatovyTyp.getNazevTypu(child.getTextContent()));
                    break;
                }
                case DELKA:{
                    String s = child.getTextContent();
                    if(!s.isEmpty()) {
                        sl.setDelka(Integer.parseInt(s));
                    }
                    break;
                }
                case MASKA:{
                    sl.setMaska(child.getTextContent());
                    break;
                }
                case NOT_NULL:{
                    sl.setNotNull(Boolean.parseBoolean(child.getTextContent()));
                    break;
                }
                case CHECK:{
                    sl.setCheck(child.getTextContent());
                    break;
                }
                case DEFAULT:{
                    sl.setDefaultHodnota(child.getTextContent());
                    break;
                }
                case POLOZKA_SOUBORU:{
                    sl.setPolozkaSouboru(child.getTextContent());
                    break;
                }
            }
        }
    }
}
