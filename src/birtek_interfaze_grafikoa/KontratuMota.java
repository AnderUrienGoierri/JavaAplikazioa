package birtek_interfaze_grafikoa;

import java.math.BigDecimal;

public class KontratuMota {
    private int idKontratua;
    private String izena;
    private String deskribapena;
    private BigDecimal soldata;

    public KontratuMota(int idKontratua, String izena, String deskribapena, BigDecimal soldata) {
        this.idKontratua = idKontratua;
        this.izena = izena;
        this.deskribapena = deskribapena;
        this.soldata = soldata;
    }

    public int getIdKontratua() { return idKontratua; }
    public void setIdKontratua(int idKontratua) { this.idKontratua = idKontratua; }

    public String getIzena() { return izena; }
    public void setIzena(String izena) { this.izena = izena; }

    public String getDeskribapena() { return deskribapena; }
    public void setDeskribapena(String deskribapena) { this.deskribapena = deskribapena; }

    public BigDecimal getSoldata() { return soldata; }
    public void setSoldata(BigDecimal soldata) { this.soldata = soldata; }
}
