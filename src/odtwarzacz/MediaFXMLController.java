/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz;


import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import odtwarzacz.Connection.Connection;
import odtwarzacz.Metadata.Metadata;
import odtwarzacz.Metadata.MetadataAudio;
import odtwarzacz.Metadata.MetadataVideo;
import odtwarzacz.Sliders.TimeSlider;
import odtwarzacz.Sliders.VolumeSlider;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * FXML Controller class
 *
 * @author Pregiel
 */
public class MediaFXMLController implements Initializable {


    @FXML
    private BorderPane pane;
    @FXML
    private HBox controllers;
    @FXML
    private Label timeLabel;
    @FXML
    private MediaView mediaView;
    @FXML
    private Button playButton;

    private MediaPlayer mediaPlayer;

    private boolean atEndOfMedia = false;
    private Duration duration;
    private boolean repeat;

    @FXML
    public Button volButton;
    @FXML
    private HBox volBox;
    @FXML
    private AnchorPane volBackTrack;

    private VolumeSlider volSlider;
    @FXML
    private Pane volTrack;
    @FXML
    private AnchorPane timeBackTrack;
    @FXML
    private Pane timeTrack;

    private TimeSlider timeSlider;

    private Connection connection;
    @FXML
    private Button backwardButton;
    @FXML
    private Button forwardButton;

    @FXML
    private ToggleButton repeatTooglebutton;

    private InfoLabel fileInfoLabel;

    private Timer previewTimer;

    private Metadata metadata;


    public void setConnection(Connection connection) {
        this.connection = connection;
        if (connection != null) {
            connection.setMediaController(this);
            if (volSlider != null) {
                volSlider.setConnection(connection);
            }
            if (metadata != null) {
                sendFileLabel(metadata);
            }
        }
//
//        volSlider.setVolume(mediaPlayer.getVolume());
    }

    public void setFileInfoLabel(InfoLabel fileInfoLabel) {
        this.fileInfoLabel = fileInfoLabel;
    }

    /**
     * Initializes the controller class.
     */
//    public File file;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        File file = new File("C:\\video.mp4");
//        File file = new File("C:\\Users\\Pregiel\\Desktop\\Example Videos\\SampleVideo_1280x720_1mb.mp4");
//        File file = new File("C:\\Users\\Pregiel\\Desktop\\video1.mp4");
//        Media media = new Media(file.toURI().toString());
//
//        changeMediaPlayer(media);

//        mediaView.setPreserveRatio(true);
//        mediaView.fitWidthProperty().bind(mediaPane.widthProperty());
//        mediaView.fitHeightProperty().bind(mediaPane.heightProperty());
//        
        pane.heightProperty().addListener((observable) -> {
            mediaView.setFitHeight(pane.getHeight());
        });

        pane.widthProperty().addListener((observable) -> {
            mediaView.setFitWidth(pane.getWidth());
        });

        repeat = Boolean.parseBoolean(String.valueOf(Odtwarzacz.getConfig().get("repeat")));

        if (repeat) {
            repeatTooglebutton.setSelected(true);
        }

//        previewTimer = new Timer();
//        previewTimer.schedule(new PreviewSend(), 0, 100);


//        mediaView.setPreserveRatio(true);
//        mediaView.fitWidthProperty().bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
//        mediaView.fitHeightProperty().bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

    }

    public void changeFile(File file) {

        mediaPlayer.dispose();
//        Media media = new Media(file.toURI().toString());
        changeMediaPlayer(file);
        mediaView.setMediaPlayer(mediaPlayer);
    }


    public void changeMediaPlayer(File file) {
        Media media = new Media(file.toURI().toString());
        MediaPlayer mp = new MediaPlayer(media);

        mp.setAutoPlay(true);

        mp.currentTimeProperty().addListener((Observable observable) -> {
            updateValues();
        });

        mp.setOnPlaying(() -> {
//            System.out.println(mediaPlayer.getStatus() + " : onPlaying");
            playButton.setText("||");

        });

        mp.setOnPaused(() -> {
//            System.out.println(mediaPlayer.getStatus() + " : onPaused");
            if (!timeSlider.isChanging()) {
                playButton.setText(">");
            }
        });

        mp.setOnReady(() -> {
            duration = mp.getMedia().getDuration();

            MAX_MOVETO_VALUE_SPEED = (int) (duration.toSeconds() * 0.05);
            START_MOVETO_VALUE = (int) (duration.toSeconds() * 0.01);

            updateValues();
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(() -> {
            updateValues();
//            System.out.println(mediaPlayer.getStatus() + " : onEndOfMedia");
            if (!repeat) {
                if (MainFXMLController.getPlaylist().getPlaylistIndex() == -1) {
                    playButton.setText(">");
                    atEndOfMedia = true;
                } else {
//                    nextPlaylistIndex();
//                    MainFXMLController.getPlaylist().nextPlaylistIndex();
                    MainFXMLController.getPlaylist().playNext();
//                    changeFile(new File(MainFXMLController.getPlaylist().getPlaylistList().get(playlistIndex - 1)));
                }
            }
        });

        mediaView.setMediaPlayer(mp);
        volSlider = new VolumeSlider(volBackTrack, volTrack, mp);
        Platform.runLater(() -> {
            volSlider.setVolume(Double.parseDouble(Odtwarzacz.getConfig().getProperty("volume")));
        });

        volSlider.getBackTrack().setScaleX(0);
        volButton.setOnMouseEntered((event) -> {
            if (volSlider.getCollapseOnRelease()) {
                volSlider.setCollapseOnRelease(false);
            }
            volSlider.animateVolumeSliderExpand(event);
        });

        volSlider.setConnection(connection);

        volBox.setOnMouseExited((event) -> {
            if (volSlider.isChanging()) {
                volSlider.setCollapseOnRelease(true);
            } else {
                volSlider.animateVolumeSliderCollapse(event);
            }
        });

        timeSlider = new TimeSlider(timeBackTrack, timeTrack, mp);

        mediaPlayer = mp;

        volSlider.setVolume(mediaPlayer.getVolume());

        metadata = generateMetadata(file);

        metadata.generate(file, () -> {
            sendFileLabel(metadata);
            fileInfoLabel.setInfoText(InfoLabel.FILE_OPEN, metadata.generateLabel());
        });

        sendFileLabel(metadata);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fileInfoLabel.setInfoText(InfoLabel.FILE_OPEN, metadata.generateLabel());
            }
        });

    }

    public Metadata generateMetadata(File file) {
        Metadata metadata;

        if (Arrays.asList(MainFXMLController.SUPPORTED_AUDIO).contains("*." + Utils.getExtension(file.getAbsolutePath()).toUpperCase())) {
            metadata = new MetadataAudio();
        } else if (Arrays.asList(MainFXMLController.SUPPORTED_VIDEO).contains("*." + Utils.getExtension(file.getAbsolutePath()).toUpperCase())) {
            metadata = new MetadataVideo();
        } else {
            metadata = new Metadata() {
                @Override
                public void generateMetadata(File file) {
                }
            };
        }

        return metadata;
    }

    private void sendFileLabel(Metadata metadata) {
        if (connection != null) {
            if (metadata instanceof MetadataAudio) {
                if (((MetadataAudio) metadata).getArtist() != null) {
                    connection.sendMessage(Connection.FILE_NAME, metadata.getTitle(), ((MetadataAudio) metadata).getArtist());
                } else {
                    connection.sendMessage(Connection.FILE_NAME, metadata.generateLabel());
                }
            } else {
                connection.sendMessage(Connection.FILE_NAME, metadata.generateLabel());
            }
        }
    }

    private boolean pilotTimeSliderMoving = false;

    public void setPilotTimeSliderMoving(boolean pilotTimeSliderMoving) {
        this.pilotTimeSliderMoving = pilotTimeSliderMoving;
    }

    private void updateValues() {
        Platform.runLater(() -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            String timeText = durationToTime(currentTime) + "/" + durationToTime(duration);
            timeLabel.setText(timeText);


            if (duration.greaterThan(Duration.ZERO) && !timeSlider.isChanging()) {
                timeSlider.setSliderPosition(currentTime.divide(
                        duration.toMillis()).toMillis() * timeSlider.getBackTrack().getWidth()
                        / timeSlider.getBackTrack().getWidth());
            }


            if (connection != null) {
                if (!pilotTimeSliderMoving) {
                    connection.sendMessage(Connection.TIME, currentTime.toMillis(), duration.toMillis());
                } else {
//                    connection.sendSnapshot();
                }
            }
        });
    }

    private String durationToTime(Duration duration) {
        int hours = 0, minutes = 0, seconds = 0;
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            hours = intDuration / (60 * 60);
            minutes = (intDuration - hours * 60 * 60) / 60;
            seconds = intDuration - hours * 60 * 60 - minutes * 60;
        }

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @FXML
    public void playPauseButton(ActionEvent event) {
        if (!pilotTimeSliderMoving) {
            Status status = mediaPlayer.getStatus();
            System.out.println(mediaPlayer.getStatus());

            switch (status) {
                case UNKNOWN:
                case HALTED:
                    System.out.println("Blad");
                    break;
                case PAUSED:
                case READY:
                case STOPPED:
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }
                    mediaPlayer.play();
                    break;
                case PLAYING:
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
//                    mediaPlayer.play();
//                    playButton.setText("||");
                    } else {
                        mediaPlayer.pause();
                    }
                    break;
                default:
                    mediaPlayer.pause();
                    break;
            }
        }

    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void play() {
        if (atEndOfMedia) {
            mediaPlayer.seek(mediaPlayer.getStartTime());
            atEndOfMedia = false;
        }
        mediaPlayer.play();
    }

    @FXML
    private void expand(ActionEvent event) {
        volSlider.mute();
    }

    @FXML
    public void backwardButton(ActionEvent event) {
        timeSlider.moveTo(BACKWARD_VALUE);
    }

    public void forwardButton(MouseEvent mouseEvent) {
//        timeSlider.moveTo(FORWARD_VALUE);
//        System.out.println("forwardButton");
    }

    private Timer moveToTimer;
    private int moveToValueJump;

    private int START_MOVETO_VALUE = 5;
    private int MAX_MOVETO_VALUE_SPEED = 30;
    private static final Duration BACKWARD_VALUE = Duration.seconds(-5);


    public void forwardButtonPressed(MouseEvent mouseEvent) {
        moveToValueJump = 0;
        moveToTimer = new Timer();
        moveToTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeSlider.moveTo(getMoveToValue(1));
            }
        }, 0, 500);
    }

    public void forwardButtonReleased(MouseEvent mouseEvent) {
        moveToTimer.cancel();
        moveToTimer.purge();
    }

    public void forwardButtonDrag(MouseEvent mouseEvent) {
        Bounds buttonBounds = forwardButton.localToScene(forwardButton.getBoundsInLocal());
        int buttonPosition = (int) (buttonBounds.getMaxX() - (buttonBounds.getWidth()) + (backwardButton.getWidth() / 2));
        int maxPosition = (int) (timeSlider.getWidth() - buttonPosition);

        double mouseX = mouseEvent.getX() - (forwardButton.getWidth() / 2);

        if (mouseX >= 0) {
            moveToValueJump = (int) ((mouseEvent.getX() / maxPosition) * MAX_MOVETO_VALUE_SPEED);
        } else {
            moveToValueJump = (int) ((mouseEvent.getX() / buttonPosition) * (START_MOVETO_VALUE - 1));
            if (moveToValueJump < 1 - START_MOVETO_VALUE) {
                moveToValueJump = 1 - START_MOVETO_VALUE;
            }
        }
    }


    private Duration getMoveToValue(int sign) {
//        System.out.println("tak");
//        System.out.println(START_MOVETO_VALUE);
//        System.out.println(moveToValueJump);
        return Duration.seconds(sign * (START_MOVETO_VALUE + moveToValueJump));
    }

    public void backwardButtonPressed(MouseEvent mouseEvent) {
        moveToValueJump = 0;
        moveToTimer = new Timer();
        moveToTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeSlider.moveTo(getMoveToValue(-1));
            }
        }, 0, 500);
    }

    public void backwardButtonReleased(MouseEvent mouseEvent) {
        moveToTimer.cancel();
        moveToTimer.purge();
    }

    public void backwardButtonDrag(MouseEvent mouseEvent) {
        Bounds buttonBounds = backwardButton.localToScene(backwardButton.getBoundsInLocal());
        int buttonPosition = (int) (buttonBounds.getMaxX() - (buttonBounds.getWidth()) + (backwardButton.getWidth() / 2));
        int maxPosition = (int) (timeSlider.getWidth() - buttonPosition);

        double mouseX = -(mouseEvent.getX() - (backwardButton.getWidth() / 2));


        if (mouseX >= 0) {
            moveToValueJump = (int) ((mouseX / buttonPosition) * MAX_MOVETO_VALUE_SPEED);
        } else {
            moveToValueJump = (int) ((mouseX / maxPosition) * (START_MOVETO_VALUE - 1));
            if (moveToValueJump < 1 - START_MOVETO_VALUE) {
                moveToValueJump = 1 - START_MOVETO_VALUE;
            }
        }
    }


    public VolumeSlider getVolSlider() {
        return volSlider;
    }

    public TimeSlider getTimeSlider() {
        return timeSlider;
    }

    @FXML
    private void showHidePlaylist(ActionEvent event) {
        MainFXMLController.getPlaylist().toogle();
    }

    @FXML
    private void repeatToggle(ActionEvent event) {
        repeat = repeatTooglebutton.isSelected();

        Odtwarzacz.getConfig().setProperty("repeat", String.valueOf(repeat));
        Odtwarzacz.getConfig().save();

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

        connection.sendMessage(repeat ? Connection.REPEAT_ON : Connection.REPEAT_OFF);
    }

    public void repeatToggle() {
        repeatTooglebutton.fire();
    }

    public MediaView getMediaView() {
        return mediaView;
    }
}
