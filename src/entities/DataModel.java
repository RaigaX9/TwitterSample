package entities;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;


public class DataModel implements Cloneable {
    
    private int id = 1;
    private boolean isSoftDeleted = false;
        
    //PARAMETER TYPE HASH
    HashMap<String, Class> setterTypeMap = new HashMap<String, Class>();
    //CONSTANTS
    private final static String RECORD_START_PATTERN = "^RECORD (\\d+)";
    private final static String RECORD_FIELD_PATTERN = "^([a-zA-Z]+):(.+)$";
    public final static String CLASS_SETTER_PATTERN = "^set[A-Z][A-Za-z]*$";
    public final static String CLASS_GETTER_PATTERN = "^get[A-Z][A-Za-z]+$";

    
    public DataModel() {
        
        for ( Method m : this.getClass().getMethods() ) {
            if ( m.getName().matches( CLASS_SETTER_PATTERN )) {
                this.setterTypeMap.put( m.getName(), m.getParameterTypes()[0]);
            }
        }
        
    }
    
    //GETTER
    /**
     * returns the id of the current record of this entity.
     * @return integer representing the id.
     */
    public int getId() {
        return this.id;
    }
    
    public boolean getIsSoftDeleted() {
        return this.isSoftDeleted;
    }
    //SETTER    
    /**
     * Sets the id of the current record of this entity.
     * @param id 
     */
    public void setId( int id ) {
        this.id = id;
    }
    
    public void setIsSoftDeleted(boolean isSoftDeleted ) {
        this.isSoftDeleted = isSoftDeleted;
    }
    
    /**
     * Returns a deep copy of this entity.
     * @return a copy of this object.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Sets the field 
     * @param fieldName name of the field in camelCase
     * @param fieldValue 
     * @return 
     */
    public boolean setField( String fieldName, Object fieldValue ) {
        
        try {
            fieldName = "set"+fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
            Method m = this.getClass().getMethod(fieldName, this.setterTypeMap.get(fieldName));
            
            Object value = null;

            Class type = this.setterTypeMap.get(fieldName);

            if ( type == int.class ) {
                value = new Integer( Integer.parseInt((String)fieldValue));
            }
            else if (type == double.class ) {
                value = new Double( Double.parseDouble((String)fieldValue));
            }
            else if (type == float.class) {
                value = new Float( Float.parseFloat((String)fieldValue));
            }
            else if (type == byte.class) {
                value = new Byte( Byte.parseByte((String)fieldValue));
            }
            else if (type == long.class) {
                value = new Long( Long.parseLong((String)fieldValue));
            }
            else if (type == char.class) {
                value = new Character( ((String)fieldValue).charAt(0));
            }
            else if (type == short.class) {
                value = new Short( Short.parseShort((String)fieldValue));
            }
            else if (type == boolean.class) {
                value = new Boolean(Boolean.parseBoolean((String)fieldValue));
            }
            else if (type == String.class) {
                value = fieldValue;
            }        
            else if (type == BigDecimal.class) {
                value = new BigDecimal(Double.parseDouble((String)fieldValue)).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            else if (type == ArrayList.class) {
                value = (ArrayList<DataModel>)fieldValue;
            }

            m.invoke(this, value );
            
            return true;
        } 
        catch (NoSuchMethodException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (SecurityException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IllegalArgumentException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InvocationTargetException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        return false;
    }
    
    public boolean setCollection( String fieldName, ArrayList<? extends DataModel> collection) {
        
        try {
            fieldName = "set"+fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
            Method m = this.getClass().getMethod(fieldName, this.setterTypeMap.get(fieldName));
            
            Object value = null;
            Class type = this.setterTypeMap.get(fieldName);
            
            value = collection;
            
            m.invoke(this, value );
            
            return true;
            
        } 
        catch (IllegalAccessException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IllegalArgumentException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InvocationTargetException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NoSuchMethodException ex) {        
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (SecurityException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        }        
            
        return false;
    }
    
    public Class getCollectionClass() {
        return null;
    }
        
    public static ArrayList<String> getFieldLabels( Class model ) {
        Field[] fields = model.getDeclaredFields();
        ArrayList<String> fieldsAsStrings = new ArrayList<String>(fields.length);
        for (Field f : fields) {
            fieldsAsStrings.add(f.getName());
        }
        fieldsAsStrings.add("isSoftDeleted");
        return fieldsAsStrings;
    }
}

