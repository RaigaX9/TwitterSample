package entities;

public class User extends DataModel {
    
    private String username;
    private String password;
    private boolean isAdmin;
        
    
    /** Gets username */
    public String getUsername() {
        return this.username;
    }
    
    /** Gets password */
    public String getPassword() {
        return this.password;
    }

    /** Sets username */
    public void setUsername( String username ) {
        this.username = username;
    }
    
    /** Sets password */
    public void setPassword( String password ) {
        this.password = password;
    }
    
    /** Sets the program if the user logged in is an administrator */
    public void setIsAdmin( boolean isAdmin ) {
        this.isAdmin = isAdmin;
    }
}