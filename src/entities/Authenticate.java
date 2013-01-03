package entities;

public class Authenticate extends DataModel {
    
    private String ck;
    private String cs;
    private String at;
    private String ats;
        
    
    /** Gets Consumer key */
    public String getCk() {
        return this.ck;
    }
    
    /** Gets Consumer secret */
    public String getCs() {
        return this.cs;
    }
    
    /** Gets Authentication token */
    public String getAt() {
        return this.at;
    }
    
    /** Gets Authentication token secret */
    public String getAts() {
        return this.ats;
    }
    
    /** Sets Consumer key */
    public void setCk( String ck ) {
        this.ck = ck;
    }
    
    /** Sets Consumer secret */
    public void setCs( String cs ) {
        this.cs = cs;
    }
    
    /** Sets Authentication token */
    public void setAt( String at ) {
        this.at = at;
    }
    
    /** Sets Authentication token secret */
    public void setAts( String ats ) {
        this.ats = ats;
    }
}