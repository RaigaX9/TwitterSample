

package com.sample.TwitterSample;

import java.util.ArrayList;
import entities.User;

public class LoginController extends Controller {
    
    private User model;
    private boolean isLoggedIn = false;
    
    /**
     * Creates a new instance of LoginController.
     */
    public LoginController() {
    }
    
    /**
     * Returns a boolean indicating whether the provided user credentials allowed 
     * the user to login to the system.
     */
    public boolean login(String username, String password) {
        
        ArrayList<User> usersFound;
                
        this.data.open(User.class);
        
        this.data.setFilter(User.class, "username", username);
        this.data.setFilter(User.class, "password", password);
        
        //This will find the username and password
        usersFound = (ArrayList<User>) this.data.readAll( User.class );
        
        if ( usersFound.size() == 1 ) {
            this.model = usersFound.get(0);
            this.isLoggedIn = true;
        }
        else usersFound.clear();
                
        this.data.close(User.class);
                
        return this.isLoggedIn;
        
    }
    
    /**
     * Returns the current logged user of the system.
     */
    public User getLoggedUser() {
        if ( this.isLoggedIn ) {
            this.model.setPassword(null);
            return this.model;
        }
        else return null;
    }
    
    /**
     * Logs the current user out of the system.
     */
    public void logout() {
        this.model = null;
        this.isLoggedIn = false;
    }
        
}