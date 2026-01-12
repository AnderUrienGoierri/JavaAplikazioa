package birtek_interfaze_grafikoa;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Kablea extends Produktua {
    private String kableMota;
    private BigDecimal luzeraM;
    private String konektoreA;
    private String konektoreB;
    private String bertsioa;

    public Kablea(int idProduktua, int hornitzaileId, int kategoriaId, String izena, String marka, String mota,
                  String deskribapena, String irudiaUrl, Integer biltegiId, String produktuEgoera,
                  String produktuEgoeraOharra, boolean salgai, BigDecimal salmentaPrezioa, int stock,
                  BigDecimal eskaintza, Timestamp sortzeData, Timestamp eguneratzeData,
                  String kableMota, BigDecimal luzeraM, String konektoreA, String konektoreB, String bertsioa) {
        super(idProduktua, hornitzaileId, kategoriaId, izena, marka, mota, deskribapena, irudiaUrl, biltegiId,
              produktuEgoera, produktuEgoeraOharra, salgai, salmentaPrezioa, stock, eskaintza, sortzeData, eguneratzeData);
        this.kableMota = kableMota;
        this.luzeraM = luzeraM;
        this.konektoreA = konektoreA;
        this.konektoreB = konektoreB;
        this.bertsioa = bertsioa;
    }

    public String getKableMota() { return kableMota; }
    public void setKableMota(String kableMota) { this.kableMota = kableMota; }

    public BigDecimal getLuzeraM() { return luzeraM; }
    public void setLuzeraM(BigDecimal luzeraM) { this.luzeraM = luzeraM; }

    public String getKonektoreA() { return konektoreA; }
    public void setKonektoreA(String konektoreA) { this.konektoreA = konektoreA; }

    public String getKonektoreB() { return konektoreB; }
    public void setKonektoreB(String konektoreB) { this.konektoreB = konektoreB; }

    public String getBertsioa() { return bertsioa; }
    public void setBertsioa(String bertsioa) { this.bertsioa = bertsioa; }
}
