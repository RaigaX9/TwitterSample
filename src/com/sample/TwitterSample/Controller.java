package com.sample.TwitterSample;

import data.XMLPersister;

public abstract class Controller {
    public XMLPersister data;
    
    public Controller() {
        this.data = new XMLPersister();

    }

}
