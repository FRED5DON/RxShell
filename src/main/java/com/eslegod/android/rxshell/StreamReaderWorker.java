package com.eslegod.android.rxshell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamReaderWorker implements StreamType, Runnable {

    private Out out;
    private int type;
    private InputStream is;

    public StreamReaderWorker(InputStream is, int type) {
        this.is = is;
        this.type = type;
    }

    public StreamReaderWorker(InputStream is, int type, Out out) {
        this.is = is;
        this.type = type;
        this.out = out;
    }


    @Override
    public void run() {
        if (is == null) return;
        BufferedReader br = null;
        InputStreamReader isr = null;
        boolean isReadSth=false;
        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                isReadSth=true;
                if (out != null) {
                    if (this.type == NORMAL) {
                        out.normal(line);
                    } else {
                        out.error(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!isReadSth){
                if (out != null) {
                    out.error("");
                }
            }
            try {
                if (br != null) br.close();
                if (isr != null) isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}