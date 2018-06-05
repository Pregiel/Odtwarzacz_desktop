/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import odtwarzacz.InfoLabel;
import odtwarzacz.MainFXMLController;

/**
 *
 * @author Pregiel
 */
public class WifiConnection extends Connection {
    
    private ServerSocket socket;
    private Socket clientSocket;


    @Override
    public void connect() {
        System.out.println("Connecting");
        getConnectionInfo().setInfoText(InfoLabel.CONNECTION_WAITFORCLIENT_WIFI);

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new ServerSocket(1755);
                    clientSocket = socket.accept();
                    clientSocket = socket.accept();      //This is blocking. It will wait.

                } catch (IOException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                setConnected(true);
                try {
                    setStreams(clientSocket.getInputStream(), clientSocket.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(WifiConnection.class.getName()).log(Level.SEVERE, null, ex);
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
            socket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
