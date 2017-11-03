package cs.dawson.myapplication.model;


public class QuoteItem {
    private String attributed;
    private String blurb;
    private String date_added;
    private String dob;
    private String quote_full;
    private String quote_short;
    private String reference;

    public QuoteItem(){
        this("","","","","","","");
    }

    public QuoteItem(String attributed, String blurb, String date_added,
                      String dob, String quote_full, String quote_short,
                        String reference){
        this.attributed = attributed;
        this.blurb = blurb;
        this.date_added = date_added;
        this.dob = dob;
        this.quote_full = quote_full;
        this.quote_short = quote_short;
        this.reference = reference;
    }

    public String getAttributed() {
        return attributed;
    }

    public void setAttributed(String attributed) {
        this.attributed = attributed;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getQuote_full() {
        return quote_full;
    }

    public void setQuote_full(String quote_full) {
        this.quote_full = quote_full;
    }

    public String getQuote_short() {
        return quote_short;
    }

    public void setQuote_short(String quote_short) {
        this.quote_short = quote_short;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
