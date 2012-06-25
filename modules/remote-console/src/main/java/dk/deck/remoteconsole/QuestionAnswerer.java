/*
 * Copyright 2011 Jesper Terkelsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package dk.deck.remoteconsole;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

/**
 * Class that reads from a shell, and allows the user to wait for certain strings, and then send data
 * @todo add a global timeout config and throw an exception when timeout occours
 * 
 * @author Jesper Terkelsen
 */
public class QuestionAnswerer {

    private long globalTimeout = 10000;
    private InputStream in;
    private OutputStream out;
    private OutputStreamWriter writer;
    private StringBuffer allInput = new StringBuffer();
    private StringBuffer input = new StringBuffer();
    private boolean running = false;
    private boolean done = false;
    private ChannelShell channel;
    private Runnable reader = new Runnable() {

        public void run() {
            Reader reader = new InputStreamReader(in);
            try {
                while (!done) {
                    if (reader.ready()) {
                        int data = reader.read();
                        if (data == -1) {
                            break;
                        }
                        char c = (char) data;
                        input.append(c);
                        allInput.append(c);
                        System.out.print(c);
                        System.out.flush();
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    };

    public QuestionAnswerer() {
    }

    public void init(ChannelShell channel) throws IOException{
        this.channel = channel;
        this.in = channel.getInputStream();
        this.out = channel.getOutputStream();
        writer = new OutputStreamWriter(out);
    }

    public void start() throws IOException {
        try {
            if (running){
                throw new IllegalStateException("Already running");
            }
            if (done){
                throw new IllegalStateException("This question answerer is used, please create a new one");
            }
            channel.connect();
            Thread readerThread = new Thread(reader);
            readerThread.start();
            running = true;
        } catch (JSchException ex) {
            IOException ioe =  new IOException();
            ioe.initCause(ex);
            throw ioe;
        }
    }

    public void stop() throws IOException {
        if (!done){
            done = true;
            running = false;
            in.close();
            out.close();
            channel.disconnect();
        }
        // Ignore, it is ok to call stop multible times
    }

    public void waitFor(String waitfor) {
        long start = System.currentTimeMillis();
        while (!input.toString().contains(waitfor)) {
            try {
                System.out.println("Input " + input.toString());
                timeout(start);
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        // Found, lets reset the waitForBuffer
        input = new StringBuffer();
    }

    /**
     * Wait until the prefered string is found or the alternate string is found in the output.
     *
     * Blocks until one of them is found
     *
     * @param prefered The prefered string
     * @param alternate The alternate string
     * @return true of the prefered string is found, false if the alternate string is found
     */
    public boolean waitFor(String prefered, String alternate){
        long start = System.currentTimeMillis();
        boolean result = true;
        while (true) {
            boolean preferedFound = input.toString().contains(prefered);
            boolean alternateFound = input.toString().contains(alternate);
            if (preferedFound){
                result = true;
                break;
            }
            if (alternateFound){
                result = false;
                break;
            }
            try {
                timeout(start);
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        // Found, lets reset the waitForBuffer
        input = new StringBuffer();
        return result;
    }

    public void write(String data) throws IOException {
        try {
            Thread.sleep(1000); // Make sure that the server is waiting for input
            writer.write(data);
            writer.flush();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void setGlobalTimeout(long globalTimeout) {
        this.globalTimeout = globalTimeout;
    }

    public long getGlobalTimeout() {
        return globalTimeout;
    }

    private void timeout(long startTime){
        long now = System.currentTimeMillis();
        if (now - startTime > globalTimeout){
            throw new IllegalStateException("Timeout occoured");
        }
    }

}
