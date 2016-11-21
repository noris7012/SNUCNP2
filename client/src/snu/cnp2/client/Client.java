package snu.cnp2.client;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.boxes.apple.AppleNameBox;
import com.googlecode.mp4parser.util.Path;

import java.io.*;

/**
 * Created by noris on 2016-11-18.
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Client cmd = new Client();
//        String xml = cmd.read("resource\\BigBuckBunny_4s3.m4s");
//        System.err.println(xml);
//        cmd.downloadFile();

        // start downloader
        Downloader.get().clearFrameRate();

        Downloader.get().addFrameRate(new Constants.FrameRate("1473801"))
                        .addFrameRate(new Constants.FrameRate("2409742"))
                        .addFrameRate(new Constants.FrameRate("3340509"))
                        .addFrameRate(new Constants.FrameRate("3936261"));

        Downloader.get().run();
    }

    public String read(String videoFilePath) throws IOException {
        IsoFile isoFile = new IsoFile(videoFilePath);

        AppleNameBox nam = Path.getPath(isoFile, "/moov[0]/udta[0]/meta[0]/ilst/©nam");
        String xml = nam.getValue();
        isoFile.close();
        return xml;
    }
}
