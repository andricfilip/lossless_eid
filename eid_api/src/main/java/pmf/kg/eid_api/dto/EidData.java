package pmf.kg.eid_api.dto;

public class EidData {
    private String ime;
    private String prezime;
    private String jmbg;
    private String brojLicneKarte;

    // konstruktori
    public EidData() {}
    public EidData(String ime, String prezime, String jmbg, String brojLicneKarte) {
        this.ime = ime;
        this.prezime = prezime;
        this.jmbg = jmbg;
        this.brojLicneKarte = brojLicneKarte;
    }

    // get/set
    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }

    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public String getJmbg() { return jmbg; }
    public void setJmbg(String jmbg) { this.jmbg = jmbg; }

    public String getBrojLicneKarte() { return brojLicneKarte; }
    public void setBrojLicneKarte(String brojLicneKarte) { this.brojLicneKarte = brojLicneKarte; }
}