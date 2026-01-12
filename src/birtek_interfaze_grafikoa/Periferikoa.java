package birtek_interfaze_grafikoa;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Periferikoa extends Produktua {
    private String periferikoMota;
    private String konexioa;
    private String ezaugarriak;
    private boolean argiztapena;

    public Periferikoa(int idProduktua, int hornitzaileId, int kategoriaId, String izena, String marka, String mota,
                       String deskribapena, String irudiaUrl, Integer biltegiId, String produktuEgoera,
                       String produktuEgoeraOharra, boolean salgai, BigDecimal salmentaPrezioa, int stock,
                       BigDecimal eskaintza, Timestamp sortzeData, Timestamp eguneratzeData,
                       String periferikoMota, String konexioa, String ezaugarriak, boolean argiztapena) {
        super(idProduktua, hornitzaileId, kategoriaId, izena, marka, mota, deskribapena, irudiaUrl, biltegiId,
              produktuEgoera, produktuEgoeraOharra, salgai, salmentaPrezioa, stock, eskaintza, sortzeData, eguneratzeData);
        this.periferikoMota = periferikoMota;
        this.konexioa = konexioa;
        this.ezaugarriak = ezaugarriak;
        this.argiztapena = argiztapena;
    }

    public String getPeriferikoMota() { return periferikoMota; }
    public void setPeriferikoMota(String periferikoMota) { this.periferikoMota = periferikoMota; }

    public String getKonexioa() { return konexioa; }
    public void setKonexioa(String konexioa) { this.konexioa = konexioa; }

    public String getEzaugarriak() { return ezaugarriak; }
    public void setEzaugarriak(String ezaugarriak) { this.ezaugarriak = ezaugarriak; }

    public boolean isArgiztapena() { return argiztapena; }
    public void setArgiztapena(boolean argiztapena) { this.argiztapena = argiztapena; }
}
