package birtek_interfaze_grafikoa;

import java.sql.Date;
import java.sql.Timestamp;

public class Langilea extends Pertsona {
    private String iban;
    private byte[] kurrikuluma; // PDF formatuan
    private int sailaId;
    private String saltoTxartelaUid;

    public Langilea(int idLangilea, String izena, String abizena, String nan, Date jaiotzaData, int herriaId,
            String helbidea, String postaKodea, String telefonoa, String emaila, String hizkuntza,
            String pasahitza, String saltoTxartelaUid, Timestamp altaData, Timestamp eguneratzeData,
            boolean aktibo, int sailaId, String iban, byte[] kurrikuluma) {
        super(idLangilea, izena, abizena, nan, jaiotzaData, helbidea, herriaId, postaKodea, telefonoa, emaila,
                hizkuntza, pasahitza, aktibo, altaData, eguneratzeData);
        this.sailaId = sailaId;
        this.iban = iban;
        this.kurrikuluma = kurrikuluma;
    }

    public int getIdLangilea() {
        return this.id;
    }

    public void setIdLangilea(int idLangilea) {
        this.id = idLangilea;
    }

    public String getNan() {
        return this.nanIfz;
    }

    public void setNan(String nan) {
        this.nanIfz = nan;
    }

    public String getSaltoTxartelaUid() {
        return saltoTxartelaUid;
    }

    public void setSaltoTxartelaUid(String saltoTxartelaUid) {
        this.saltoTxartelaUid = saltoTxartelaUid;
    }

    public int getSailaId() {
        return sailaId;
    }

    public void setSailaId(int sailaId) {
        this.sailaId = sailaId;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public byte[] getKurrikuluma() {
        return kurrikuluma;
    }

    public void setKurrikuluma(byte[] kurrikuluma) {
        this.kurrikuluma = kurrikuluma;
    }
}
