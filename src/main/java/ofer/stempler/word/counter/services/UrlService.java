package ofer.stempler.word.counter.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.stereotype.Service;

import ofer.stempler.word.counter.models.PayloadType;

@Service
public class UrlService extends WordCounterService {
    @Override
    public void handleText(String text) throws IOException, InterruptedException {
        String filePath = downloadFile(text);
        LineIterator it = FileUtils.lineIterator(new File(filePath), "UTF-8");
        processText(it);
    }

    @Override
    public PayloadType getServiceType() {
        return PayloadType.URL;
    }

    private String downloadFile(String requestUrl) throws IOException {

        URL url = new URL(requestUrl);
        URLConnection connection = url.openConnection();
        InputStream initialStream = connection.getInputStream();

        Path path = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), UUID.randomUUID().toString());
        String tmpdir = Files.createDirectories(path).toFile().getAbsolutePath() + "/temp.txt";
        FileUtils.copyInputStreamToFile(initialStream, new File(tmpdir));

        return tmpdir;

    }
}
