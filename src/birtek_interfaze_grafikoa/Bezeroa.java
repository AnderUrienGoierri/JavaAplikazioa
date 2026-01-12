package birtek_interfaze_grafikoa;

import java.sql.Date;
import java.sql.Timestamp;

public class Bezeroa {
    private int idBezeroa;
    private String izenaEdoSoziala;
    private String abizena;
    private String ifzNan;
    private Date jaiotzaData;
    private String sexua;
    private String bezeroOrdainketaTxartela;
    private String helbidea;
    private int herriaId;
    private String postaKodea;
    private String telefonoa;
    private String emaila;
    private String hizkuntza;
    private String pasahitza;
    private Timestamp altaData;
    private Timestamp eguneratzeData;
    private boolean aktibo;

    public Bezeroa(int idBezeroa, String izenaEdoSoziala, String abizena, String ifzNan, Date jaiotzaData,
                   String sexua, String bezeroOrdainketaTxartela, String helbidea, int herriaId, String postaKodea,
                   String telefonoa, String emaila, String hizkuntza, String pasahitza, Timestamp altaData,
                   Timestamp eguneratzeData, boolean aktibo) {
        this.idBezeroa = idBezeroa;
        this.izenaEdoSoziala = izenaEdoSoziala;
        this.abizena = abizena;
        this.ifzNan = ifzNan;
        this.jaiotzaData = jaiotzaData;
        this.sexua = sexua;
        this.bezeroOrdainketaTxartela = bezeroOrdainketaTxartela;
        this.helbidea = helbidea;
        this.herriaId = herriaId;
        this.postaKodea = postaKodea;
        this.telefonoa = telefonoa;
        this.emaila = emaila;
        this.hizkuntza = hizkuntza;
        this.pasahitza = pasahitza;
        this.altaData = altaData;
        this.eguneratzeData = eguneratzeData;
        this.aktibo = aktibo;
    }

    public int getIdBezeroa() { return idBezeroa; }
    public void setIdBezeroa(int idBezeroa) { this.idBezeroa = idBezeroa; }

    public String getIzenaEdoSoziala() { return izenaEdoSoziala; }
    public void setIzenaEdoSoziala(String izenaEdoSoziala) { this.izenaEdoSoziala = izenaEdoSoziala; }

    public String getAbizena() { return abizena; }
    public void setAbizena(String abizena) { this.abizena = abizena; }

    public String getIfzNan() { return ifzNan; }
    public void setIfzNan(String ifzNan) { this.ifzNan = ifzNan; }

    public Date getJaiotzaData() { return jaiotzaData; }
    public void setJaiotzaData(Date jaiotzaData) { this.jaiotzaData = jaiotzaData; }

    public String getSexua() { return sexua; }
    public void setSexua(String sexua) { this.sexua = sexua; }

    public String getBezeroOrdainketaTxartela() { return bezeroOrdainketaTxartela; }
    public void setBezeroOrdainketaTxartela(String bezeroOrdainketaTxartela) { this.bezeroOrdainketaTxartela = bezeroOrdainketaTxartela; }

    public String getHelbidea() { return helbidea; }
    public void setHelbidea(String helbidea) { this.helbidea = helbidea; }

    public int getHerriaId() { return herriaId; }
    public void setHerriaId(int herriaId) { this.herriaId = herriaId; }

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

    public Timestamp getAltaData() { return altaData; }
    public void setAltaData(Timestamp altaData) { this.altaData = altaData; }

    public Timestamp getEguneratzeData() { return eguneratzeData; }
    public void setEguneratzeData(Timestamp eguneratzeData) { this.eguneratzeData = eguneratzeData; }

    public boolean isAktibo() { return aktibo; }
    public void setAktibo(boolean aktibo) { this.aktibo = aktibo; }
}
