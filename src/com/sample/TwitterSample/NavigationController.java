package com.sample.TwitterSample;


import javax.swing.JFrame;
import java.util.Stack;
import entities.User;

public class NavigationController {
    
    //Navigation Logic for Forward/Back
    private Stack<JFrame> navigationStack = new Stack<JFrame>();
    
    //Home Page Instances
    private TweetApp tweetFrame;
    
    //Login Page Instances
    private Login loginFrame;
    
    //User Instances
    private User loggedUser;
    
    /**Launches the login frame when the program is executed */
    public void launchApp() {
        
        
        this.loginFrame = new Login( new LoginController(), this );
        this.loginFrame.setVisible(true);
        
        this.navigationStack.push(loginFrame);
        
        
    }
   
    /**Launches the tweet app frame after logging in */
    public void launchTweetApp( User user ) {
        
        this.loggedUser = user;
        this.navigationStack.pop().dispose();
        this.tweetFrame = new TweetApp(new AuthController(), this);
        this.tweetFrame.setVisible(true);
        this.navigationStack.push(tweetFrame);
        
    }
    
    /**Navigates back to tweet app */
    public void back() {
        this.navigationStack.peek().dispose();
        this.navigationStack.pop().setVisible(false);
        this.navigationStack.peek().pack();
        this.navigationStack.peek().setVisible(true);
    }
    


        
    /**Logs out from the current session and goes back to the login frame */
    public void logout() {
        
        this.navigationStack.peek().dispose();
        this.navigationStack.removeAllElements();
        this.loggedUser = null;
        this.launchApp();
        
    }
    
}
