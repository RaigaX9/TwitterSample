
package com.sample.TwitterSample;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;
import java.util.*;

public class Login extends JFrame {
	
	// Variables declaration - do not modify
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration
    
    private LoginController controller;
    private NavigationController navigation;

	/**
	 * This constructor initialize GUI components
	 */
	public Login(LoginController lc, NavigationController nc) {
        loginLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        
        this.controller = lc;
        this.navigation = nc;
        initComponents();
      
	}

	/**
	 *Implements all of the GUI features within the JFrame
	 */
	public void initComponents() {	

		setSize(400, 320);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        setPreferredSize(new java.awt.Dimension(400, 350));
        getContentPane().setLayout(null);

        loginLabel.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
        loginLabel.setText("Login");
        getContentPane().add(loginLabel);
        loginLabel.setBounds(164, 11, 59, 28);

        usernameLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        usernameLabel.setText("Username:");
        getContentPane().add(usernameLabel);
        usernameLabel.setBounds(56, 75, 82, 22);

        passwordLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        passwordLabel.setText("Password:");
        getContentPane().add(passwordLabel);
        passwordLabel.setBounds(56, 116, 78, 22);
        
        getContentPane().add(usernameField);
        usernameField.setBounds(142, 78, 127, 20);
        
        getContentPane().add(passwordField);
        passwordField.setBounds(144, 119, 125, 20);

        loginButton.setText("Login");
        getContentPane().add(loginButton);
        loginButton.setBounds(164, 193, 67, 23);
        
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        setVisible(true);

        pack();
        
	}
	
	
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	
        if (this.controller.login(this.usernameField.getText(), new String(this.passwordField.getPassword()))) {
            this.navigation.launchTweetApp(this.controller.getLoggedUser());
         
        }
        else {
            this.notifyPasswordIsIncorrect();
        }
    }

    /* Displays a denial prompt after a failed login attempt.
     */
    private void notifyPasswordIsIncorrect() {
        JOptionPane.showMessageDialog(this,
                "Incorrect username or password. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
    }
	

}// End of Login class
