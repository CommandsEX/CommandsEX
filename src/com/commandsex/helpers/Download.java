package com.commandsex.helpers;

import java.io.*;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class Download implements Runnable {

    /**
     * A list of possible download statuses
     */
    public enum Status {
        DOWNLOADING, COMPLETE, FAILED
    }

    private URL url;
    private File saveLocation;
    private Status status = Status.DOWNLOADING;
    private AtomicInteger bytesRead = new AtomicInteger(0);
    private int totalSize = 0;

    /**
     * Constructs a new download and begins downloading it in a seperate thread
     * @param url The url of the file to download
     * @param saveLocation Where the file should be saved
     */
    public Download(URL url, File saveLocation){
        this.url = url;
        this.saveLocation = saveLocation;

        download();
    }

    private void download(){
        try {
            totalSize = url.openConnection().getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Gets the current status of the download
     * @return The status of the download
     */
    public Status getStatus(){
        return status;
    }

    /**
     * Gets the total size of the download
     * @return The size of the download
     */
    public int getTotalSize(){
        return totalSize;
    }

    /**
     * Gets the amount of data downloaded so far
     * @return The amount of data downloaded so far
     */
    public int getDownloadedSize(){
        return bytesRead.get();
    }

    /**
     * Gets the current progress
     * @return The current progress (0-100)
     */
    public int getProgress(){
        synchronized (bytesRead){
            return ((bytesRead.get() * 100) / totalSize);
        }
    }

    public void run() {
        try {
            InputStream inputStream = new BufferedInputStream(url.openStream());

            try {
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveLocation));

                try {
                    byte[] buffer = new byte[2048];

                        while (bytesRead.get() != -1){
                            int read = inputStream.read(buffer);

                            if (read > 0){
                                outputStream.write(buffer, 0, read);
                            }

                            synchronized (bytesRead){
                                bytesRead.set(bytesRead.get() + read);
                            }
                        }

                    status = Status.COMPLETE;
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException ex){
            ex.printStackTrace();
            status = Status.FAILED;
        }
    }

}
