package birtek_interfaze_grafikoa;

import java.sql.Date;
import java.sql.Timestamp;

public class Langilea {
    private int idLangilea;
    private String izena;
    private String abizena;
    private String nan;
    private Date jaiotzaData;
    private int herriaId;
    private String helbidea;
    private String postaKodea;
    private String telefonoa;
    private String emaila;
    private String hizkuntza;
    private String pasahitza;
    private String saltoTxartelaUid;
    private Timestamp altaData;
    private Timestamp eguneratzeData;
    private boolean aktibo;
    private int sailaId;
    private int kontratuaId;
    private String iban;

    public Langilea(int idLangilea, String izena, String abizena, String nan, Date jaiotzaData, int herriaId,
                    String helbidea, String postaKodea, String telefonoa, String emaila, String hizkuntza,
                    String pasahitza, String saltoTxartelaUid, Timestamp altaData, Timestamp eguneratzeData,
                    boolean aktibo, int sailaId, int kontratuaId, String iban) {
        this.idLangilea = idLangilea;
        this.izena = izena;
        this.abizena = abizena;
        this.nan = nan;
        this.jaiotzaData = jaiotzaData;
        this.herriaId = herriaId;
        this.helbidea = helbidea;
        this.postaKodea = postaKodea;
        this.telefonoa = telefonoa;
        this.emaila = emaila;
        this.hizkuntza = hizkuntza;
        this.pasahitza = pasahitza;
        this.saltoTxartelaUid = saltoTxartelaUid;
        this.altaData = altaData;
        this.eguneratzeData = eguneratzeData;
        this.aktibo = aktibo;
        this.sailaId = sailaId;
        this.kontratuaId = kontratuaId;
        this.iban = iban;
    }

    public int getIdLangilea() { return idLangilea; }
    public void setIdLangilea(int idLangilea) { this.idLangilea = idLangilea; }

    public String getIzena() { return izena; }
    public void setIzena(String izena) { this.izena = izena; }

    public String getAbizena() { return abizena; }
    public void setAbizena(String abizena) { this.abizena = abizena; }

    public String getNan() { return nan; }
    public void setNan(String nan) { this.nan = nan; }

    public Date getJaiotzaData() { return jaiotzaData; }
    public void setJaiotzaData(Date jaiotzaData) { this.jaiotzaData = jaiotzaData; }

    public int getHerriaId() { return herriaId; }
    public void setHerriaId(int herriaId) { this.herriaId = herriaId; }

    public String getHelbidea() { return helbidea; }
    public void setHelbidea(String helbidea) { this.helbidea = helbidea; }

    public String getPostaKodea() { return postaKodea; }
    public void setPostaKodea(String postaKodea) { this.postaKodea = postaKodea; }

    public String getTelefonoa() { return telefonoa; }
    public void setTelefonoa(String telefonoa) { this.telefonoa = telefonoa; }

    public String getEmaila() { return emaila; }
    public void setEmaila(String emaila) { this.emaila = emaila; }

    public String getHizkuntza() { return hizkuntza; }
    public void setHizkuntza(String hizkuntza) { this.hizkuntza = hizkuntza; }

    public String getPasahitza() { return pasahitza; }
    public void setPasahitza(String pasahitza) { this.pasahitza = pasahitza; }

    public String getSaltoTxartelaUid() { return saltoTxartelaUid; }
    public void setSaltoTxartelaUid(String saltoTxartelaUid) { this.saltoTxartelaUid = saltoTxartelaUid; }

    public Timestamp getAltaData() { return altaData; }
    public void setAltaData(Timestamp altaData) { this.altaData = altaData; }

    public Timestamp getEguneratzeData() { return eguneratzeData; }
    public void setEguneratzeData(Timestamp eguneratzeData) { this.eguneratzeData = eguneratzeData; }

    public boolean isAktibo() { return aktibo; }
    public void setAktibo(boolean aktibo) { this.aktibo = aktibo; }

    public int getSailaId() { return sailaId; }
    public void setSailaId(int sailaId) { this.sailaId = sailaId; }

    public int getKontratuaId() { return kontratuaId; }
    public void setKontratuaId(int kontratuaId) { this.kontratuaId = kontratuaId; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
}
