package data;

import entities.DataModel;


public interface Persistence {
    
    public boolean open( Class model );
    public boolean close( Class model );
    public boolean closeAll();
    public void setFilter( Class model, String fieldName, String expression );
    public boolean read( DataModel model, int recordNumber );
    public boolean create( DataModel model );
    public boolean update( DataModel model );
    public boolean remove( DataModel model );
    
}
