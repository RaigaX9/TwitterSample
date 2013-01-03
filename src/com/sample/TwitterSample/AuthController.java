package com.sample.TwitterSample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import entities.Authenticate;

public class AuthController extends Controller {
    private LinkedList<Authenticate> items;
    private AuthFrame authDialog;
    private Authenticate selectedItem;
    
    /**Constructor method */
    public AuthController(){
        this.data.open(Authenticate.class);
        this.items = new LinkedList((ArrayList<Authenticate>) this.data.readAll(Authenticate.class));
    }
    
   
    /** This will give you a dialog box that will allow the user to make to input his or her authenciation info */
    public void showAuthDialog(JFrame parent)
    {
    	this.authDialog = new AuthFrame(parent, this, true, true);
    	this.authDialog.populateFields(this.selectedItem);
    	this.authDialog.setVisible(true);
    }
     
    
    /** This will update the authentication information */

    public void updateAuth(String ck, String cs, String at, String ats) {
        this.selectedItem.setCk(ck);
        this.selectedItem.setCs(cs);
        this.selectedItem.setAt(at);
        this.selectedItem.setAts(ats);
        
      //Update the authentication linked list by replacing the old info with the updated info.
        int indexOfUpdatedItem = this.items.indexOf(this.selectedItem);
        this.items.set(indexOfUpdatedItem, this.selectedItem);
        
        this.data.update(this.selectedItem);
        this.data.commit();
                
        
    }
    

}