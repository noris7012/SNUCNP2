package snu.cnp2.client;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.boxes.apple.AppleNameBox;
import com.googlecode.mp4parser.util.Path;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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
