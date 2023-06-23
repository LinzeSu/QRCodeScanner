package com.linzesu.qrautoscan;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class QRCodeScanner {

    public static void main(String[] args) throws AWTException, InterruptedException {
        QRCodeScanner scanner = new QRCodeScanner();
        scanner.monitorScreenForQRCode();
    }

    private Set<String> openedLinks = new HashSet<>();

    public void monitorScreenForQRCode() throws AWTException, InterruptedException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        Robot robot = new Robot();

        while (true) {
            for (GraphicsDevice screen : screens) {
                Rectangle screenRect = screen.getDefaultConfiguration().getBounds();
                BufferedImage screenCapture = robot.createScreenCapture(screenRect);
                Result qrCodeResult = decodeQRCode(screenCapture);

                if (qrCodeResult != null && !openedLinks.contains(qrCodeResult.getText())) {
                    String link = qrCodeResult.getText();
                    System.out.println("QR Code detected: " + link);
                    openedLinks.add(link);
                    if (isValidURL(link)) {
                        playSound();
                        openLink(link);
                    }
                }
            }

            Thread.sleep(4000); // Adjust the delay between screen captures as needed
        }
    }

    private Result decodeQRCode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            return new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) {
            return null;
        }
    }

    private boolean isValidURL(String urlString) {
        try {

            new URL(urlString).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private void playSound() {
        try {
//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/alert.wav"));
//            Clip clip = AudioSystem.getClip();
//            clip.open(audioInputStream);
//            clip.start();
            System.out.println("Beep");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openLink(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}