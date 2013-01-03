package com.sample.TwitterSample;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;


import javax.swing.*;
import entities.Authenticate;

public class AuthFrame extends JDialog{
	
	// Variables declaration - do not modify
	
    private javax.swing.JButton applyButton;
	private javax.swing.JButton cancelButton;
    private javax.swing.JTextField atField;
    private javax.swing.JLabel atLabel;
    private javax.swing.JTextField atsField;
    private javax.swing.JLabel atsLabel;
    private javax.swing.JLabel authLabel;
    private javax.swing.JTextField ckField;
    private javax.swing.JLabel ckLabel;
    private javax.swing.JTextField csField;
    private javax.swing.JLabel csLabel;
    
    
    // End of variables declaration
    
    private AuthController controller;
    private boolean isUpdating;
	
    public AuthFrame(java.awt.Frame parent, AuthController ac, boolean modal, boolean isUpdating) {
    	super(parent, modal);
    	
        initComponents();
        
        this.isUpdating = isUpdating;

    }
    
	 /**
	 * Populates the text fields with the model's corresponding properties to update.
	 * @param items 
	 */
    public void populateFields( Authenticate items ) {

    	
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
	
	    				this.ckField.setText(getTagValue("Ck", eElement));
	    				this.csField.setText(getTagValue("Cs", eElement));
	    				this.atField.setText(getTagValue("At", eElement));
	    				this.atsField.setText(getTagValue("Ats", eElement));
	    			}
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    
}
    
    //Implements all of the GUI features within the JDialog
    private void initComponents() {

        authLabel = new javax.swing.JLabel();
        ckLabel = new javax.swing.JLabel();
        csLabel = new javax.swing.JLabel();
        atLabel = new javax.swing.JLabel();
        atsLabel = new javax.swing.JLabel();
        atField = new javax.swing.JTextField();
        ckField = new javax.swing.JTextField();
        atsField = new javax.swing.JTextField();
        csField = new javax.swing.JTextField();
        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        
        setTitle("Authentication Information");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 400, 300));
        setPreferredSize(new java.awt.Dimension(590, 370));
        getContentPane().setLayout(null);

        authLabel.setFont(new java.awt.Font("Times New Roman", 1, 24)); 
        authLabel.setText("Auethentication");
        getContentPane().add(authLabel);
        authLabel.setBounds(160, 10, 166, 28);

        ckLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); 
        ckLabel.setText("Consumer key:");
        getContentPane().add(ckLabel);
        ckLabel.setBounds(34, 57, 96, 17);

        csLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); 
        csLabel.setText("Consumer secret:");
        getContentPane().add(csLabel);
        csLabel.setBounds(34, 92, 110, 17);

        atLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); 
        atLabel.setText("Access token:");
        getContentPane().add(atLabel);
        atLabel.setBounds(34, 142, 137, 17);

        atsLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); 
        atsLabel.setText("Access token secret:");
        getContentPane().add(atsLabel);
        atsLabel.setBounds(34, 170, 197, 17);

        getContentPane().add(atField);
        atField.setBounds(130, 140, 370, 20);
        getContentPane().add(ckField);
        ckField.setBounds(140, 60, 230, 20);
        getContentPane().add(atsField);
        atsField.setBounds(165, 170, 310, 20);
        getContentPane().add(csField);
        csField.setBounds(150, 90, 340, 20);

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        getContentPane().add(applyButton);
        applyButton.setBounds(180, 230, 73, 23);
        
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelButton);
        cancelButton.setBounds(20, 20, 73, 23);

        pack();
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }
    
    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	
        if((this.ckField.getText().length() == 0) || (this.csField.getText().length() == 0) 
                || (this.atField.getText().length() == 0) || (this.atsField.getText().length() == 0))
        {
            this.noInfo();
            
                 
        }
        
        else{
     	   try {
     		   //Locates and retrieves the information for authentication from the xml file
     			String filepath = "C:\\data.xml";
     			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
     			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
     			Document doc = docBuilder.parse(filepath);
     	 
    	 
     			Node auth = doc.getElementsByTagName("Authenticate").item(0);
     	 
    			//The auth child node will loop at each element
     			NodeList list = auth.getChildNodes();
     	 
     			for (int i = 0; i < list.getLength(); i++) {
     	 
     	           Node node = list.item(i);
     	 
     			   //Finds the Consumer key element, and update the value
     			   if ("Ck".equals(node.getNodeName())) {
     				node.setTextContent(ckField.getText());
     			   }
     			   
     			   //Finds the Consumer secret element, and update the value
     			   if ("Cs".equals(node.getNodeName())) {
     				node.setTextContent(csField.getText());
     			   }
     			   
     			   //Finds the Access token element, and update the value
     			   if ("At".equals(node.getNodeName())) {
     				node.setTextContent(atField.getText());
     			   }
     			   
     			   //Finds the Access token secret element, and update the value
     			   if ("Ats".equals(node.getNodeName())) {
     				node.setTextContent(atsField.getText());
     			   }
     	 
     	 
     			}
     	 
     			//Writes the content into the xml file
     			TransformerFactory transformerFactory = TransformerFactory.newInstance();
     			Transformer transformer = transformerFactory.newTransformer();
     			DOMSource source = new DOMSource(doc);
     			StreamResult result = new StreamResult(new File(filepath));
     			transformer.transform(source, result);
     	 
     			this.approveInfo();
     	 
     		   } catch (ParserConfigurationException pce) {
     			pce.printStackTrace();
     		   } catch (TransformerException tfe) {
     			tfe.printStackTrace();
     		   } catch (IOException ioe) {
     			ioe.printStackTrace();
     		   } catch (SAXException sae) {
     			sae.printStackTrace();
     		   }
     
          }
    }
    
    /**
     * Displays an error message if the user did not put in all the information for accessing your twitter account.
     */   
    private void noInfo() {
       JOptionPane.showMessageDialog(this,
               "You need to put in all the information.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
   }  
    
    /**
     * Displays a message if the user inputed all the information for the authentication.
     */   
    private void approveInfo() {
       JOptionPane.showMessageDialog(this,
               "All information is inputted. You may click 'Cancel' or 'x' to get out.", "Authentication Inputted", JOptionPane.PLAIN_MESSAGE);
   }  
    /**
     * Retrieves the element from a specified tag from the xml file
     */
    private static String getTagValue(String sTag, Element eElement) {
    	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
     
            Node nValue = (Node) nlList.item(0);
     
    	return nValue.getNodeValue();
      }
    
    

}
