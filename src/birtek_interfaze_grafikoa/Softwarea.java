package birtek_interfaze_grafikoa;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Softwarea extends Produktua {
    private String softwareMota;
    private String lizentziaMota;
    private String bertsioa;
    private String garatzailea;

    public Softwarea(int idProduktua, int hornitzaileId, int kategoriaId, String izena, String marka, String mota,
                     String deskribapena, String irudiaUrl, Integer biltegiId, String produktuEgoera,
                     String produktuEgoeraOharra, boolean salgai, BigDecimal salmentaPrezioa, int stock,
                     BigDecimal eskaintza, Timestamp sortzeData, Timestamp eguneratzeData,
                     String softwareMota, String lizentziaMota, String bertsioa, String garatzailea) {
        super(idProduktua, hornitzaileId, kategoriaId, izena, marka, mota, deskribapena, irudiaUrl, biltegiId,
              produktuEgoera, produktuEgoeraOharra, salgai, salmentaPrezioa, stock, eskaintza, sortzeData, eguneratzeData);
        this.softwareMota = softwareMota;
        this.lizentziaMota = lizentziaMota;
        this.bertsioa = bertsioa;
        this.garatzailea = garatzailea;
    }

    public String getSoftwareMota() { return softwareMota; }
    public void setSoftwareMota(String softwareMota) { this.softwareMota = softwareMota; }

    public String getLizentziaMota() { return lizentziaMota; }
    public void setLizentziaMota(String lizentziaMota) { this.lizentziaMota = lizentziaMota; }

    public String getBertsioa() { return bertsioa; }
    public void setBertsioa(String bertsioa) { this.bertsioa = bertsioa; }

    public String getGaratzailea() { return garatzailea; }
    public void setGaratzailea(String garatzailea) { this.garatzailea = garatzailea; }
}
