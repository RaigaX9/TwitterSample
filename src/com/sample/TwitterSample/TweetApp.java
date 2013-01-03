package com.sample.TwitterSample;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import javax.swing.*;


public class TweetApp extends JFrame{
    // Variables declaration - do not modify  
	private javax.swing.JButton authButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JTextArea postArea;
    private javax.swing.JLabel postLabel;
    private javax.swing.JScrollPane postScrool;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JButton tweetButton;
    private javax.swing.JTextField tweetField;
    private javax.swing.JLabel tweetLabel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel note1Label;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton logoutButton;
    // End of variables declaration 
    
    private NavigationController navigation;
    private AuthController authenciate;
	
	public TweetApp(AuthController ac, NavigationController nc){
		titleLabel = new javax.swing.JLabel();
        tweetLabel = new javax.swing.JLabel();
        logoLabel = new javax.swing.JLabel();
        tweetField = new javax.swing.JTextField();
        postScrool = new javax.swing.JScrollPane();
        postArea = new javax.swing.JTextArea();
        postLabel = new javax.swing.JLabel();
        tweetButton = new javax.swing.JButton();
        authButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        note1Label = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        logoutButton = new javax.swing.JButton();
        
        this.authenciate = ac;        
        this.navigation = nc;
        
        
        initComponents();
		
	}
	
	public void initComponents(){

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tweet App");
        setBackground(new java.awt.Color(0, 0, 0));
        setBounds(new java.awt.Rectangle(0, 0, 527, 400));
        setPreferredSize(new java.awt.Dimension(560, 530));
        getContentPane().setLayout(null);

        titleLabel.setFont(new java.awt.Font("Trebuchet MS", 1, 24)); 
        titleLabel.setText("My Tweet App");
        getContentPane().add(titleLabel);
        titleLabel.setBounds(189, 11, 161, 29);

        tweetLabel.setFont(new java.awt.Font("Sylfaen", 1, 18)); 
        tweetLabel.setText("Tweet:");
        getContentPane().add(tweetLabel);
        tweetLabel.setBounds(82, 169, 59, 24);

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("twitterLogo.jpg")));
        getContentPane().add(logoLabel);
        logoLabel.setBounds(189, 46, 171, 117);
        getContentPane().add(tweetField);
        tweetField.setBounds(151, 171, 270, 20);

        postArea.setEditable(false);
        postArea.setColumns(20);
        postArea.setRows(5);
        postScrool.setViewportView(postArea);

        getContentPane().add(postScrool);
        postScrool.setBounds(151, 211, 270, 133);

        postLabel.setFont(new java.awt.Font("Sylfaen", 1, 18)); 
        postLabel.setText("Your Post(s):");
        getContentPane().add(postLabel);
        postLabel.setBounds(29, 211, 112, 24);

        tweetButton.setFont(new java.awt.Font("Tahoma", 1, 11)); 
        tweetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tweetButtonActionPerformed(evt);
            }
        });
        tweetButton.setText("Submit Tweet");
        getContentPane().add(tweetButton);
        tweetButton.setBounds(194, 355, 121, 23);
        
        authButton.setFont(new java.awt.Font("Tahoma", 1, 11)); 
        authButton.setText("Authentication");
        getContentPane().add(authButton);
        authButton.setBounds(373, 30, 120, 23);
        
        authButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authButtonActionPerformed(evt);
            }
        });
        
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        getContentPane().add(refreshButton);
        refreshButton.setBounds(50, 260, 81, 23);

        note1Label.setFont(new java.awt.Font("Times New Roman", 3, 14)); // NOI18N
        note1Label.setText("Note: Make sure you click \"Refresh\" to make sure you've already made your tweet.");
        getContentPane().add(note1Label);
        note1Label.setBounds(20, 380, 490, 40);
        
        resetButton.setText("Reset");
        resetButton.setEnabled(false);
        resetButton.setFocusable(false);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        getContentPane().add(resetButton);
        resetButton.setBounds(50, 290, 70, 23);
        
        logoutButton.setText("Logout");
        logoutButton.setFocusable(false);
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });
        getContentPane().add(logoutButton);
        logoutButton.setBounds(20, 30, 85, 23);

        pack();
	}
	
    private void authButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.authenciate.showAuthDialog(this);
        
    }
    
    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
		Twitter t = new TwitterFactory().getInstance();
		try{
		File fXmlFile = new File("C:\\data.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("Authenticate");
        
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				
		//Consumer and Access Token information
        t.setOAuthConsumer(getTagValue("Ck", eElement), getTagValue("Cs", eElement));
        t.setOAuthAccessToken(new AccessToken(getTagValue("At", eElement), getTagValue("Ats", eElement)));
			}
		}	

        
        try {
        	
        	//This will list 7 statuses from your Twitter profile. You can change how many past statuses here.
        	ResponseList<Status> a = t.getUserTimeline(new Paging(1,7));
        	
        	//Prints each of your status in a text area
        	for(Status b: a) {
        		postArea.append(b.getText()+"\n");
        	}

        }catch(Exception e ){
        	
        }
	 }catch(Exception e){
 		e.printStackTrace();
 	} 
		resetButton.setEnabled(true);
		refreshButton.setEnabled(false);
    }
    
    private void tweetButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	if((this.tweetField.getText().length() == 0))
        {
            this.noInfo();
            
                 
        }
    	else{
		try{
		//Locates and gets information from the xml file
			
		//Place your xml file path here
		File fXmlFile = new File("C:\\data.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("Authenticate");
        
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

		    	String consumerKey = getTagValue("Ck", eElement);
		        String consumerSecret = getTagValue("Cs", eElement);
		        String accessToken = getTagValue("At", eElement);
		        String accessSecret = getTagValue("Ats", eElement);

		        ConfigurationBuilder c = new ConfigurationBuilder();
		        c.setDebugEnabled(true)
		            .setOAuthConsumerKey(consumerKey)
		            .setOAuthConsumerSecret(consumerSecret)
		            .setOAuthAccessToken(accessToken)
		            .setOAuthAccessTokenSecret(accessSecret);
		        try 
		        {
		           TwitterFactory f = new TwitterFactory(c.build());
		           Twitter t = f.getInstance();
		           
		           //This will send your tweet and give you a message that your message has been posted on your twitter profile
		           String latestStatus = tweetField.getText();
		           Status s = t.updateStatus(latestStatus);
		           JOptionPane.showMessageDialog(this,
		        		   t.getScreenName() + "\n" + "Your tweet, [" + s.getText() + "] has been posted on your twitter profile.", 
		        		   "Tweet Tweet!", JOptionPane.PLAIN_MESSAGE);
		          
		            }catch (TwitterException te) {
		               te.printStackTrace();
		               System.exit(-1);
		            }
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
    	}
    	 tweetField.setText("");
    	 postArea.setText("");
        
    }
    
    /**
     * Retrieves the element from a specified tag from the xml file
     */
    private static String getTagValue(String sTag, Element eElement) {
    	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
     
            Node nValue = (Node) nlList.item(0);
     
    	return nValue.getNodeValue();
      }
    /**
     * Display an error if nothing is inputed in the tweet text field
     */
    private void noInfo() {
        JOptionPane.showMessageDialog(this,
                "You need to input your Tweet.", "Tweet Error", JOptionPane.ERROR_MESSAGE);
    } 
    
    /**
     * Resets the post text area
     */
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	postArea.setText("");
    	refreshButton.setEnabled(true);
    	resetButton.setEnabled(false);
        
    }
    
    /**
     * Logs out the user and back to the login page
     */
    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	this.navigation.logout();
        
    }

}
