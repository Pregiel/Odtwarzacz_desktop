/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import odtwarzacz.InfoLabel;
import odtwarzacz.MainFXMLController;
import odtwarzacz.MediaFXMLController;

/**
 *
 * @author Pregiel
 */
public class BtConnection extends Connection {
    
    private StreamConnection connection;
    private StreamConnectionNotifier streamConnNotifier;

    @Override
    public void connect() {
        System.out.println("Connecting");
        getConnectionInfo().setInfoText(InfoLabel.CONNECTION_WAITFORCLIENT_BT);

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UUID uuid = new UUID("1101", true);
                    //Create the servicve url
                    String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";
                    streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);//open server url

                    connection = streamConnNotifier.acceptAndOpen();
                    RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
                    System.out.println("Remote device address: " + dev.getBluetoothAddress());

                } catch (IOException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                setConnected(true);
                try {
                    setStreams(connection.openDataInputStream(), connection.openDataOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(BtConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        connect.setDaemon(true);
        connect.start();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        try {
            connection.close();
            streamConnNotifier.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
