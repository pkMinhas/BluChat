/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package bluetooth;

import java.util.Vector;


/**
 * Interface to be implemented by UI classes expecting interaction
 * with bluetooth client component
 * @author Preet Kamal Singh Minhas
 */
public interface ClientCaller extends Caller{
    /**
     * This method will signal that the search for devices running this app is
     * complete.
     * @param serverDetailsRecords Details of devices running this application
     */
    public void signalSearchComplete(Vector serverDetailsRecords);
}
