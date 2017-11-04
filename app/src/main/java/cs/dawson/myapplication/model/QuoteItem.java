package cs.dawson.myapplication.model;

/**
 * Encapsulates the properties and characteristics to represent a Quote
 * in the Quotes application. A QuoteItem will contains properties such as the
 * name of the attributed, the blurb of the attributed, the date the quote was added,
 * the date of birth of the attributed, the short and full quotes, and the reference
 * of the website the quote was taken from.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 */
public class QuoteItem {
    private String attributed;
    private String blurb;
    private String date_added;
    private String dob;
    private String quote_full;
    private String quote_short;
    private String reference;

    /**
     * Default constructor
     */
    public QuoteItem(){
        this("","","","","","","");
    }

    /**
     * Constructor that initializes the properties of the model class
     *
     * @param attributed String name of the attributed
     * @param blurb String blurb of the attributed
     * @param date_added String date the quote was added YYYY-MM-DD
     * @param dob String date of birth of the attributed
     * @param quote_full String the full quote
     * @param quote_short String the short quote
     * @param reference String the website reference
     */
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

    /**
     * Returns the name of the attributed
     *
     * @return String name
     */
    public String getAttributed() {
        return attributed;
    }

    /**
     * Sets the name of the attributed
     *
     * @param attributed String name
     */
    public void setAttributed(String attributed) {
        this.attributed = attributed;
    }

    /**
     * Returns the blurb information of the attributed
     *
     * @return String blurb
     */
    public String getBlurb() {
        return blurb;
    }

    /**
     * Sets the blurb information of the attributed
     *
     * @param blurb String blurb
     */
    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    /**
     * Returns the date that quote was added
     *
     * @return String date YYYY-MM-DD
     */
    public String getDate_added() {
        return date_added;
    }

    /**
     * Sets the date that quote was added
     *
     * @param date_added String date YYYY-MM-DD
     */
    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    /**
     * Returns the day of birth of the attributed
     *
     * @return String date
     */
    public String getDob() {
        return dob;
    }

    /**
     * Sets the day of birth of the attributed
     *
     * @param dob String date
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Returns the full quote
     *
     * @return String full quote
     */
    public String getQuote_full() {
        return quote_full;
    }

    /**
     * Sets the full quote
     *
     * @param quote_full String full quote
     */
    public void setQuote_full(String quote_full) {
        this.quote_full = quote_full;
    }

    /**
     * Returns the short quote
     *
     * @return String short quote
     */
    public String getQuote_short() {
        return quote_short;
    }

    /**
     * Sets the short quote
     *
     * @param quote_short String short quote
     */
    public void setQuote_short(String quote_short) {
        this.quote_short = quote_short;
    }

    /**
     * Returns the reference of the quote
     *
     * @return String reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference of the quote
     *
     * @param reference String reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }
}
