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
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import odtwarzacz.InfoLabel;
import odtwarzacz.MainFXMLController;

/**
 * @author Pregiel
 */
public class BtConnection extends Connection {

    private StreamConnection connection;
    private StreamConnectionNotifier service;

    @Override
    public void connect() {
        System.out.println("Connecting");
        getConnectionInfo().setInfoText(InfoLabel.CONNECTION_WAITFORCLIENT_BT);

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UUID uuid = new UUID("b6324e17d6024765822ae68be2112efd", false);

                    String connectionString = "btspp://localhost:" + uuid + ";name=Server";
                    service = (StreamConnectionNotifier) Connector.open(connectionString);

                    connection = service.acceptAndOpen();

                    RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
                    System.out.println("Remote device address: " + dev.getBluetoothAddress());

                    DataInputStream DIS = new DataInputStream(connection.openDataInputStream());
                    DataOutputStream DOS = new DataOutputStream(connection.openDataOutputStream());

                    setConnected(true);
                    setStreams(DIS, DOS);
                } catch (BluetoothStateException ex) {
                    ex.printStackTrace();
                    getConnectionInfo().setInfoText(InfoLabel.NO_BLUETOOTH_SUPPORT);
                } catch (IOException | NullPointerException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
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
            if (connection != null)
                connection.close();
            if (service != null)
                service.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

    }
}
