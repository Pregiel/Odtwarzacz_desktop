package odtwarzacz;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import odtwarzacz.Connection.Connection;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Utils {
    public static String getDirectoryTree(File directory) {
        StringBuilder tree = new StringBuilder();
        File[] list = directory.listFiles();
        if (list != null) {
            for (File file : list) {
                tree.append(file.getAbsolutePath()).append(Connection.SEPARATOR);
            }
        }
        return tree.toString();
    }

    public static String getDriveList() {
        StringBuilder list = new StringBuilder();
        File[] rootDrive = File.listRoots();

        for(File sysDrive : rootDrive){
//            list.append(sysDrive).append(FileSystemView.getFileSystemView().getSystemDisplayName(sysDrive)).append(Connection.SEPARATOR);

            list.append(sysDrive).append(Connection.SEPARATOR);
        }

        return list.toString();
    }

    public static Image scale(Image source, int targetWidth, int targetHeight, boolean preserveRatio) {
        ImageView imageView = new ImageView(source);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setFitWidth(targetWidth);
        imageView.setFitHeight(targetHeight);
        return imageView.snapshot(null, null);
    }

    public static String getExtension(String file) {
        return file.substring(file.lastIndexOf(".") + 1);
    }
}
