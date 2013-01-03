package data;

import entities.DataModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLPersister implements Persistence {

    private Document xml;
    private XPath path;
    private HashMap<String, HashMap<String, Method>> getters = new HashMap<String, HashMap<String, Method>>(20);
    private HashMap<String, Node> nodes = new HashMap<String, Node>(20);
    private HashMap<String, HashMap<String, String>> filters = new HashMap<String, HashMap<String, String>>(20);
    
    @Override
    public boolean open(Class model) {
        FileWriter fileWriter = null;
        try {
            String className = model.getSimpleName();
            
            File dataFile;
            dataFile = new File( getFilePath() );
            
            //IF FILE DOES NOT EXIST, CREATE ONE
            if (! dataFile.exists()) {
                dataFile.createNewFile();
                
                //SETUP AND WRITE NEW XML FILE FOR THE SYSTEM
                fileWriter = new FileWriter( dataFile );
                XMLOutputFactory xmlOut = XMLOutputFactory.newInstance();
                XMLStreamWriter xmlWriter = xmlOut.createXMLStreamWriter( fileWriter );
                xmlWriter.writeStartDocument("utf-8", "1.0");
                xmlWriter.writeStartElement( "tweetapp" );
                xmlWriter.writeAttribute("count", "0");
                xmlWriter.writeEndDocument();
                xmlWriter.flush();
                xmlWriter.close();
                fileWriter.close();
                
            }
            //If first time opening.
            if ( this.xml == null ) {
                //CREATE XML DOM
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                this.xml = builder.parse(dataFile);
                this.xml.normalizeDocument();
                //SET UP XPATH
                XPathFactory xFactory = XPathFactory.newInstance();
                this.path = xFactory.newXPath();
            }
            
            //CREATE MODEL REPRESENTATION
            Element root = this.xml.getDocumentElement();
            XPathExpression lookupModel = this.path.compile("/tweetapp/"+className);
            Node result = (Node) lookupModel.evaluate( this.xml, XPathConstants.NODE );
            if ( result == null) {
                Element xmlModel = this.xml.createElement(className);
                xmlModel.setAttribute("highest_id", "0");
                root.appendChild(xmlModel);
                result = xmlModel;
            }
            
            //ADD NODE AS REFERENCE TO MODEL
            this.nodes.put(className, result);
            //WRITE CHANGES TO FILE
            writeDOMToFile( this.xml, dataFile );
            
            //STORE REFERENCES TO GETTER METHODS OF MODEL
            ArrayList<String> fieldLabels = DataModel.getFieldLabels( model );
            String fieldName;
            String methodName;
            this.getters.put(className, new HashMap<String, Method>(10));
            for (Method m : model.getMethods()) {
                methodName = m.getName();
                fieldName = m.getName().replaceFirst("get|set", "");
                fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                
                // if the method is a non-id getter of a private field that begins with "get" or "set".
                if ( fieldName.matches("id")) continue;
                if (! fieldLabels.contains(fieldName)) continue;
                if (methodName.matches( DataModel.CLASS_GETTER_PATTERN ))
                    this.getters.get(className).put( fieldName, m);
            }
            //SET DEFAULT BEHAVIOR IN REGARDS TO SOFT DELETES
            this.setFilter(model, "isSoftDeleted", "false");
            
            return true;
                    
        } 
        catch (XPathExpressionException ex) { 
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ParserConfigurationException ex) { 
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (XMLStreamException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) {

        }
        return false;
    }

    public boolean commit() {
        return writeDOMToFile( this.xml, new File(getFilePath()));
    }
    
    @Override
    public boolean close(Class model) {
        this.nodes.remove(model.getSimpleName());
        this.filters.remove(model.getSimpleName());
        this.getters.remove(model.getSimpleName());
                
        return true;
    }

    @Override
    public boolean closeAll() {
        this.nodes.clear();
        this.filters.clear();
        this.getters.clear();
        
        //hint to the JVM that the garbage collector should collect the xml dom.
        System.gc();
        
        return true;
    }

    @Override
    public void setFilter(Class model, String fieldName, String expression) {

        String className = model.getSimpleName();
        
        if ( this.filters.containsKey( className )) {
            this.filters.get( className ).put(fieldName, expression);
        }
        else {
            HashMap<String, String> fieldFilter = new HashMap<String, String>();
            fieldFilter.put(fieldName, expression);
            this.filters.put( className, fieldFilter);
        }
        
    }
    
    private String getXPath( Class model ) {
        
        String className = model.getSimpleName();
        String path = "/tweetapp/"+className+"/record";
        
        if (this.filters.get( className ) == null ) return path;
        else path += "[";
        for ( Iterator it = this.filters.get(className).entrySet().iterator(); it.hasNext(); ) {
            
            Entry pair = (Entry) it.next();
            path += pair.getKey()+"='"+pair.getValue()+"' ";
            if ( it.hasNext() ) path+="and ";
            
        }
        path += "]";
        
        return path;
    
    }
    
    public void removeFilters(DataModel model) {
        this.filters.get(model.getClass().getSimpleName()).clear();
    }
    
    public void removeFilter(DataModel model, String fieldName) {
        this.filters.get(model.getClass().getSimpleName()).remove(fieldName);
    }
    
    @Override
    public boolean read(DataModel model, int recordNumber) {
        return false;
    }
        
    public Collection<? extends DataModel> readAll(Class model) {
        try {
            //DECLARATIONS
            XPathExpression lookupModels;
            String className;
            
            //INITIALIZATIONS
            className = model.getClass().getSimpleName();
            lookupModels = this.path.compile(this.getXPath(model));
            NodeList nodes = (NodeList) lookupModels.evaluate(this.xml, XPathConstants.NODESET);
            
            Collection<DataModel> models = new ArrayList<DataModel>();
            
            for ( int i=0; i<nodes.getLength(); i++) {
                models.add(convertNodeToModelCopy( nodes.item(i), model));
            }
            
            return models;
            
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
        
    }
    
    private ArrayList<? extends DataModel> readAll( NodeList nodes, Class model ) {
        ArrayList<DataModel> collection = new ArrayList<DataModel>();
        
        for (int i=0; i<nodes.getLength(); i++) {
            if ( nodes.item(i).getNodeName().contains("#")) continue;
            collection.add( convertNodeToModelCopy( nodes.item(i), model));
        }
        
        return collection;
    }
    
    private DataModel convertNodeToModelCopy( Node node, Class model ) {
        try {
            DataModel newModel = (DataModel) model.newInstance();
            Node fieldNode;
            NodeList fieldNodes = node.getChildNodes();
            
            newModel.setId( Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()) );
            
            try {
                for ( int i = 0; i<fieldNodes.getLength(); i++ ) {
                    fieldNode = fieldNodes.item(i);
                    if ( nodeHasAttribute( fieldNode, "many")) {
                        //ParameterizedType listType = (ParameterizedType) model.getDeclaredField(fieldNode.getNodeName()).getGenericType();
                        //Class<?> type = (Class<?>) listType.getActualTypeArguments()[0];
                        ArrayList<DataModel> collection = (ArrayList<DataModel>) this.readAll( fieldNode.getChildNodes(), newModel.getCollectionClass());
                        newModel.setField(fieldNode.getNodeName(), collection);
                    }
                    else {
                        if (fieldNode.getNodeName().contains("#")) continue;
                        if (fieldNode.getNodeName().contains("Id")) {
                            String referenceId = fieldNode.getTextContent().replaceAll("^.*\\[@id=\\'([0-9]+)\\'\\]$", "$1");
                            newModel.setField(fieldNode.getNodeName(), referenceId);
                            XPathExpression lookupReference = this.path.compile(fieldNode.getTextContent());
                            NodeList fieldElements = (NodeList) ( (Node) lookupReference.evaluate(this.xml, XPathConstants.NODE)).getChildNodes();
                            for (int j=0; j<fieldElements.getLength(); j++) {
                                Node fieldElement = fieldElements.item(j);
                                if (fieldElement.getNodeName().contains("#")) continue;
                                else newModel.setField(fieldElement.getNodeName(), fieldElement.getTextContent());
                            }
                        }
                        else newModel.setField(fieldNode.getNodeName(), fieldNode.getTextContent());
                    }
                }

                return newModel;
            } 
            catch (XPathExpressionException ex) {
                Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SecurityException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        catch (InstantiationException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IllegalAccessException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
        
    }

    @Override
    public boolean create(DataModel model) {
        try {
            //DECLARATIONS
            String className;
            int id;
            Element entry;
            //INITIALIZATIONS
            className = model.getClass().getSimpleName();
            entry = this.xml.createElement( "record" );
            //XPathExpression recordCount = this.path.compile("count(/restaurant/"+className+"/record)");
            //id = (int) ((Double) recordCount.evaluate(this.xml, XPathConstants.NUMBER) + 1);
            id = Integer.parseInt(this.nodes.get(className).getAttributes().getNamedItem("highest_id").getNodeValue())+1;
            model.setId(id);
            this.nodes.get(className).getAttributes().getNamedItem("highest_id").setNodeValue(String.valueOf(id));
            
            //LOGIC
            //set the new entry's id to count + 1
            entry.setAttribute("id", String.valueOf(id) );
            //create entry fields by iterating through the data models getters.
            
            this.populateModelIntoNode(model, entry);
            
            this.nodes.get(className).appendChild(entry);
            
            return true;
                                    
        
        } catch (SecurityException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }  catch (IllegalArgumentException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    @Override
    public boolean update(DataModel model) {
        try {
            
            //DECLARATIONS
            String elementValue;
            String className;
            int id;
            Node field;
            XPathExpression lookupField;
            //INITIALIZATIONS
            className = model.getClass().getSimpleName();
            id = model.getId();
            
            //LOGIC
            for (Entry<String, Method> pair : this.getters.get(className).entrySet() ) {
                
                lookupField = this.path.compile("/tweetapp/"+className+"/record[@id='"+id+"']"+"/"+pair.getKey());
                field = (Node) lookupField.evaluate( this.xml, XPathConstants.NODE);
                if ( Collection.class.isAssignableFrom(pair.getValue().getReturnType()) ) {
                    this.appendCollectionToNode((Collection)pair.getValue().invoke(model, null), field);
                }
                else {
                    elementValue = pair.getValue().invoke( model, null ).toString();
                    field.setTextContent(elementValue);
                }
            }
            
        } 
        catch (XPathExpressionException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex) {        
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IllegalArgumentException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InvocationTargetException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        return false;
    }

    @Override
    public boolean remove(DataModel model) {
        try {
            //DECLARATIONS
            String className;
            int id;
            Node record;
            XPathExpression lookupRecord;
            //INITIALIZATIONS
            className = model.getClass().getSimpleName();
            id = model.getId();
            
            //LOGIC
            lookupRecord = this.path.compile("/tweetapp/"+className+"/record[@id='"+id+"']");
            record = (Node) lookupRecord.evaluate( this.xml, XPathConstants.NODE );
            this.nodes.get(className).removeChild(record);
            
        } 
        catch (XPathExpressionException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IllegalArgumentException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
        return false;
    }
    
    public boolean softRemove(DataModel model) {
        model.setIsSoftDeleted(true);
        return this.update(model);
    }
        
    private void populateModelIntoNode( DataModel model, Node node ) {
        Element fieldEntry;
        String xPath;
        String className;
        String superclassName;
        String elementValue;
        boolean hasSuperclass;
        
        className = model.getClass().getSimpleName();
        hasSuperclass = ( superclassName = model.getClass().getSuperclass().getSimpleName() ) != "DataModel";
        
        try {
            for (Entry<String, Method> pair : this.getters.get( className ).entrySet() ) {
                
                fieldEntry = xml.createElement(pair.getKey());
                
                if (hasSuperclass && (superclassName+"Id").toUpperCase().matches(pair.getKey().toUpperCase())) {
                    xPath = "/tweetapp/"+superclassName+"/record[@id='"+pair.getValue().invoke(model, null).toString()+"']";
                    fieldEntry.setTextContent(xPath);
                }
                else if ( Collection.class.isAssignableFrom(pair.getValue().getReturnType()) ) {
                    fieldEntry.setAttribute("many", "many");
                    this.appendCollectionToNode( (Collection)pair.getValue().invoke(model, null), fieldEntry);
                }
                else {
                    elementValue = pair.getValue().invoke( model, null ).toString();
                    fieldEntry.setTextContent(elementValue);
                }
                
                node.appendChild( fieldEntry );

            }
        }
        catch (IllegalAccessException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IllegalArgumentException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InvocationTargetException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }
 
    private void appendCollectionToNode( Collection<DataModel> cltn, Node field ) {
        Element entry;
        String className = null;
        int id = 0;
        
        NodeList nodes = field.getChildNodes();
        int len = field.getChildNodes().getLength();
        for (int i =0; i<len; i++) {
            if (nodes.item(i)==null) continue;
            if (nodes.item(i).getNodeName().contains("#")) continue;
            field.removeChild(nodes.item(i));
        }
                
        for ( DataModel item : cltn ) {
                
            if (className==null) className = item.getClass().getSimpleName();
            if (! this.getters.containsKey(item.getClass().getSimpleName())) this.open( item.getClass() );

            entry = this.xml.createElement(className);
            entry.setAttribute("id", String.valueOf(++id));
            
            this.populateModelIntoNode(item, entry);
            field.appendChild(entry);
                
        }
    }
    
    private static boolean writeDOMToFile( Document xml, File dataFile ) {
        FileWriter fw = null;
        boolean success = false;
        try {
            
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult( stringWriter );
            
            DOMSource source = new DOMSource( xml );
            transformer.transform(source, result);
            fw = new FileWriter( dataFile );
            fw.write( stringWriter.toString() );
            success = true;      
            
        } catch (IOException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(XMLPersister.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        return success;
    }
    
    private static boolean nodeHasAttribute( Node node, String attribute) {
        
        NamedNodeMap attributes = node.getAttributes();
        
        if (!node.hasAttributes()) return false;
        for (int i=0; i<attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            if (attr.getNodeName().equals(attribute)) return true;
        }
        
        return false;
    }
    
    private static String getFilePath() {
        return System.getProperty("user.dir")+File.separator+"data.xml";
    }
    
    
}
