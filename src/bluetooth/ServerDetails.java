/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package bluetooth;

/**
 * POJO to keep server details (name, connection url)
 * @author Preet Kamal Singh Minhas
 */
public class ServerDetails {
    private String name;
    private String connUrl;

    public String getConnUrl() {
        return connUrl;
    }

    public void setConnUrl(String connUrl) {
        this.connUrl = connUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }




}
