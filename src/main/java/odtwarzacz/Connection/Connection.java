/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Connection;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import odtwarzacz.*;
import odtwarzacz.Utils.MyLocale;
import odtwarzacz.Utils.Utils;

import javax.imageio.ImageIO;

import static odtwarzacz.MainFXMLController.getPlaylist;

/**
 * @author Pregiel
 */
public abstract class Connection {

    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String FORWARD = "FORWARD";
    public static final String BACKWARD = "BACKWARD";
    public static final String TIME = "TIME";
    public static final String DURATION = "DURATION";
    public static final String VOLUME = "VOLUME";
    public static final String MUTE = "MUTE";
    public static final String MUTE_ON = "MUTE_ON";
    public static final String MUTE_OFF = "MUTE_OFF";
    public static final String UNMUTE = "UNMUTE";
    public static final String RANDOM = "RANDOM";
    public static final String RANDOM_ON = "RANDOM_ON";
    public static final String RANDOM_OFF = "RANDOM_OFF";
    public static final String REPEAT = "REPEAT";
    public static final String REPEAT_ON = "REPEAT_ON";
    public static final String REPEAT_OFF = "REPEAT_OFF";
    public static final String REROLL = "REROLL";

    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String FILE_NAME = "FILE_NAME";
    public static final String NEXT_FILE = "NEXT_FILE";

    public static final String TIMESLIDER_START = "TIMESLIDER_START";
    public static final String TIMESLIDER_STOP = "TIMESLIDER_STOP";

    public static final String PLAYLIST_SEND = "PLAYLIST_SEND";
    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    public static final String PLAYLIST_PLAY = "PLAYLIST_PLAY";
    public static final String PLAYLIST_PLAYING_INDEX = "PLAYLIST_PLAYING_INDEX";
    public static final String PLAYLIST_TITLES = "PLAYLIST_TITLES";

    public static final String FORWARD_PRESSED = "FORWARD_PRESSED";
    public static final String FORWARD_RELEASED = "FORWARD_RELEASED";
    public static final String FORWARD_CLICKED = "FORWARD_CLICKED";
    public static final String BACKWARD_PRESSED = "BACKWARD_PRESSED";
    public static final String BACKWARD_RELEASED = "BACKWARD_RELEASED";
    public static final String BACKWARD_CLICKED = "BACKWARD_CLICKED";

    public static final String VOLUME_UP_PRESSED = "VOLUME_UP_PRESSED";
    public static final String VOLUME_UP_RELEASED = "VOLUME_UP_RELEASED";
    public static final String VOLUME_UP_CLICKED = "VOLUME_UP_CLICKED";
    public static final String VOLUME_DOWN_PRESSED = "VOLUME_DOWN_PRESSED";
    public static final String VOLUME_DOWN_RELEASED = "VOLUME_DOWN_RELEASED";
    public static final String VOLUME_DOWN_CLICKED = "VOLUME_DOWN_CLICKED";

    public static final String FILECHOOSER_DIRECTORY_TREE = "FILECHOOSER_DIRECTORY_TREE";
    public static final String FILECHOOSER_SHOW = "FILECHOOSER_SHOW";
    public static final String FILECHOOSER_DRIVE_LIST = "FILECHOOSER_DRIVE_LIST";
    public static final String FILECHOOSER_PLAY = "FILECHOOSER_PLAY";
    public static final String FILECHOOSER_PLAYLIST_ADD = "FILECHOOSER_PLAYLIST_ADD";
    public static final String FILECHOOSER_PLAYLIST_ADD_ALREADYEXIST = "FILECHOOSER_PLAYLIST_ADD_ALREADYEXIST";

    public static final String SNAPSHOT = "SNAPSHOT";
    public static final String SNAPSHOT_REQUEST = "SNAPSHOT_REQUEST";

    private static final int SNAPSHOT_WIDTH = 200;
    private static final int SNAPSHOT_HEIGHT = 200;

    public static final String SEPARATOR = "::";

    private static Connection instance;

    private DataInputStream DIS;
    private DataOutputStream DOS;

    private boolean connected;

    private InfoLabel connectionInfo;

    private String connectedDeviceName;

    private MediaFXMLController mediaController;

    private MainFXMLController mainFXMLController;

    public Connection() {
        this.connected = false;
        instance = this;


        Thread closeSocketOnShutdown = new Thread() {
            public void run() {
                disconnect();
            }
        };
        Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);
    }

    public static Connection getInstance() {
        return instance;
    }

    public MainFXMLController getMainFXMLController() {
        return mainFXMLController;
    }

    public void setMainFXMLController(MainFXMLController mainFXMLController) {
        this.mainFXMLController = mainFXMLController;
    }

    public MediaFXMLController getMediaController() {
        return mediaController;
    }

    public void setMediaController(MediaFXMLController mediaController) {
        this.mediaController = mediaController;
    }

    public String getConnectedDeviceName() {
        return connectedDeviceName;
    }

    public void setConnectedDeviceName(String connectedDeviceName) {
        this.connectedDeviceName = connectedDeviceName;
    }

    public void setConnectionInfo(InfoLabel connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public InfoLabel getConnectionInfo() {
        return connectionInfo;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setStreams(InputStream inputStream, OutputStream outputStream) {
        this.DIS = new DataInputStream(inputStream);
        this.DOS = new DataOutputStream(outputStream);
        executorService = Executors.newFixedThreadPool(10);
//        connectionInfo = mediaController.getConnectionInfo();
        getMessage();
        if (mediaController != null) {
            mediaController.setConnection(this);
        }

        try {
            sendMessage(DEVICE_NAME, InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public abstract void connect();

    public Connection getConnection() {
        return this;
    }

    public void getMessage() {
        if (isConnected()) {
            Thread connect = new Thread(new Runnable() {
                String msg_received = "";

                @Override
                public void run() {

                    try {
                        msg_received = DIS.readUTF();
                        setUsbConnection();
                        System.out.println("Message: " + msg_received);
                        pilotController(msg_received);
                        getMessage();
                    } catch (IOException ex) {
                        disconnect();
//                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            connect.setDaemon(true);
            connect.start();
        }
    }


    public void pilotController(String msg) {
        String[] message = msg.split(SEPARATOR);
        System.out.println(msg);
        switch (message[0]) {
            case TIME:
                final double currentTimeMilis = Double.parseDouble(message[1]);
                if (mediaController != null) {
                    mediaController.getTimeSlider().moveTo(currentTimeMilis);
                }
                break;

            case VOLUME:
                if (mediaController != null) {
                    final double volumeValue = Double.parseDouble(message[1]);

                    mediaController.getVolSlider().setVolume(volumeValue);
                }
                break;

            case PLAY:
                if (mediaController != null) {
                    mediaController.playPauseButton(null);
                }
                break;

            case TIMESLIDER_START:
                if (mediaController != null) {
                    mediaController.pause();
                    mediaController.setPilotTimeSliderMoving(true);
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    sendSnapshot();

                                }
                            });

                        }
                    });
                }
                break;

            case TIMESLIDER_STOP:
                if (mediaController != null) {
                    mediaController.setPilotTimeSliderMoving(false);
                    mediaController.getTimeSlider().moveTo(Double.parseDouble(message[1]));
                    mediaController.play();

                    sendMessage(PLAY, mediaController.getMediaView().getMediaPlayer().getCurrentTime().toMillis());
                }
                break;

            case FORWARD:
                if (mediaController != null) {
                    mediaController.forwardButton(null);
                }
                break;

            case BACKWARD:
                if (mediaController != null) {
                    mediaController.backwardButton(null);
                }
                break;

            case MUTE:
                if (mediaController != null) {
                    mediaController.getVolSlider().mute();
                }
                break;

            case UNMUTE:
                if (mediaController != null) {
                    mediaController.getVolSlider().mute();
                }
                break;

            case REPEAT:
                mediaController.repeatToggle();
                break;

            case RANDOM:
                mediaController.randomToggle();
                break;

            case REROLL:
                Platform.runLater(() ->
                        getPlaylist().getPlaylistFXMLController().nextReroll.fire());
                break;

            case DEVICE_NAME:
                setConnectedDeviceName(message[1]);
                ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
                System.out.println(resourceBundle.getString("connection.connectedwith") + message[1]);
                connectionInfo.setInfoText(InfoLabel.CONNECTION_CONNECTED, message[1]);


                sendMessage(PLAYLIST_SEND, getPlaylist().toMessage());
                sendMessage(PLAYLIST_TITLES, getPlaylist().getPlaylistTitleIndex(), getPlaylist().titlesToMessage());
                break;

            case PLAYLIST_SEND:
                sendMessage(PLAYLIST_SEND, getPlaylist().toMessage());
                sendMessage(PLAYLIST_PLAYING_INDEX, getPlaylist().getPlaylistIndex());
                break;

            case PLAYLIST_TITLES:
                sendMessage(PLAYLIST_TITLES, getPlaylist().getPlaylistTitleIndex(), getPlaylist().titlesToMessage());
                break;

            case FORWARD_CLICKED:
                getPlaylist().playNext();
                break;

            case FORWARD_PRESSED:
                mediaController.forwardButtonPressed(null);
                break;

            case FORWARD_RELEASED:
                mediaController.forwardButtonReleased(null);
                break;

            case BACKWARD_CLICKED:
                getPlaylist().playPrev();
                break;

            case BACKWARD_PRESSED:
                mediaController.backwardButtonPressed(null);
                break;

            case BACKWARD_RELEASED:
                mediaController.backwardButtonReleased(null);
                break;

            case VOLUME_UP_CLICKED:
                if (mediaController != null) {
                    mediaController.getVolSlider().addVolume(MediaFXMLController.VOLUME_CLICK_VALUE);
                }
                break;

            case VOLUME_DOWN_CLICKED:
                if (mediaController != null) {
                    mediaController.getVolSlider().addVolume(-MediaFXMLController.VOLUME_CLICK_VALUE);
                }
                break;

            case PLAYLIST_PLAY:
                getPlaylist().play(Integer.parseInt(message[1]) + 1);
                break;

            case FILECHOOSER_DIRECTORY_TREE:
                sendMessage(FILECHOOSER_DIRECTORY_TREE, Utils.getDirectoryTree(new File(message[1])));
                break;

            case FILECHOOSER_SHOW:
                sendMessage(FILECHOOSER_SHOW, Utils.getDriveList());
                break;

            case FILECHOOSER_DRIVE_LIST:
                sendMessage(FILECHOOSER_DRIVE_LIST, Utils.getDriveList());
                break;

            case FILECHOOSER_PLAY:
                mainFXMLController.loadFile(new File(message[1]));
                break;

            case FILECHOOSER_PLAYLIST_ADD:
                getPlaylist().addCheckIfExist(new File(message[1]), 1);
                break;

            case FILECHOOSER_PLAYLIST_ADD_ALREADYEXIST:
                getPlaylist().add(new File(message[1]));
                break;

            case SNAPSHOT_REQUEST:
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sendSnapshot();

                            }
                        });

                    }
                });
        }
    }

    private ExecutorService executorService;

    public void sendMessage(Object... messages) {
        if (isConnected()) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                for (Object o : messages) {
                    stringBuilder.append(o).append(SEPARATOR);
                }
                System.out.println(stringBuilder.toString());
                if (DOS != null)
                    DOS.writeUTF(stringBuilder.toString());
            } catch (IOException ex) {
                disconnect();
//                    Logger.getLogger(WifiConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int snapshotId = 0;

    public void sendSnapshot() {
        if (isConnected() && mediaController.getMediaView() != null) {
            Image image = Utils.scale(mediaController.getMediaView().snapshot(new SnapshotParameters(), null),
                    SNAPSHOT_WIDTH,
                    SNAPSHOT_HEIGHT,
                    true);

//            Image image = mediaController.getMediaView().snapshot(new SnapshotParameters(), null);

            ByteArrayOutputStream s = new ByteArrayOutputStream();
            byte[] res;
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", s);
                res = s.toByteArray();
                sendMessage(Connection.SNAPSHOT, snapshotId++, Calendar.getInstance().getTimeInMillis(), res.length);
                DOS.write(res);

                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            setConnected(false);
            DIS = null;
            DOS = null;
            if (this instanceof UsbConnection) {
                if (((UsbConnection) this).isRetryConnect()) {
                    return;
                }
            }
            connectionInfo.setInfoText(InfoLabel.CONNECTION_DISCONNECTED, getConnectedDeviceName());
            executorService.shutdown();
        }

    }


    private void setUsbConnection() {
        if (this instanceof UsbConnection) {
            ((UsbConnection) this).setRetryConnect(false);
        }
    }


}
