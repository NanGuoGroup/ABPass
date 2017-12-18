package com.hyyas.www.abpassclient;

import ABPassNew.Client.Client;

/**
 * Created by 41861 on 2017/6/28.
 */

public class ClientSingleton {
    private static  Client client;

    public static Client getClient() {
        return client;
    }

    public static void setClient(Client client) {
        ClientSingleton.client = client;
    }


}
