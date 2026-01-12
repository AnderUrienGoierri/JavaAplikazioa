package birtek_interfaze_grafikoa;

import java.sql.Timestamp;

public class Hornitzailea {
    private int idHornitzailea;
    private String izenaSoziala;
    private String ifzNan;
    private String kontaktuPertsona;
    private String helbidea;
    private int herriaId;
    private String postaKodea;
    private String telefonoa;
    private String emaila;
    private String hizkuntza;
    private String pasahitza;
    private boolean aktibo;
    private Timestamp eguneratzeData;

    public Hornitzailea(int idHornitzailea, String izenaSoziala, String ifzNan, String kontaktuPertsona,
                        String helbidea, int herriaId, String postaKodea, String telefonoa, String emaila,
                        String hizkuntza, String pasahitza, boolean aktibo, Timestamp eguneratzeData) {
        this.idHornitzailea = idHornitzailea;
        this.izenaSoziala = izenaSoziala;
        this.ifzNan = ifzNan;
        this.kontaktuPertsona = kontaktuPertsona;
        this.helbidea = helbidea;
        this.herriaId = herriaId;
        this.postaKodea = postaKodea;
        this.telefonoa = telefonoa;
        this.emaila = emaila;
        this.hizkuntza = hizkuntza;
        this.pasahitza = pasahitza;
        this.aktibo = aktibo;
        this.eguneratzeData = eguneratzeData;
    }

    public int getIdHornitzailea() { return idHornitzailea; }
    public void setIdHornitzailea(int idHornitzailea) { this.idHornitzailea = idHornitzailea; }

    public String getIzenaSoziala() { return izenaSoziala; }
    public void setIzenaSoziala(String izenaSoziala) { this.izenaSoziala = izenaSoziala; }

    public String getIfzNan() { return ifzNan; }
    public void setIfzNan(String ifzNan) { this.ifzNan = ifzNan; }

    public String getKontaktuPertsona() { return kontaktuPertsona; }
    public void setKontaktuPertsona(String kontaktuPertsona) { this.kontaktuPertsona = kontaktuPertsona; }

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

    public boolean isAktibo() { return aktibo; }
    public void setAktibo(boolean aktibo) { this.aktibo = aktibo; }

    public Timestamp getEguneratzeData() { return eguneratzeData; }
    public void setEguneratzeData(Timestamp eguneratzeData) { this.eguneratzeData = eguneratzeData; }
}
