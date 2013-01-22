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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import dk.deck.console.CommandResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import dk.deck.remoteconsole.userinfo.LoggingUserInfo;
import dk.deck.remoteconsole.util.StreamUtil;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jesper Terkelsen
 */
public class SshRemoteConsole extends AbstractRemoteConsole implements UserInfoProvider {

    private static Log log = LogFactory.getLog(SshRemoteConsole.class);
    private final JSch jsch;
    private String user;
    private String host;
    private int port;
    private File identityFile;
    private Session session = null;
    private UserInfoProvider userInfoProvider = this;
    private boolean enablePty;

    public SshRemoteConsole(JSch jSch) {
        this.jsch = jSch;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setIdentityFile(File identityFile) {
        this.identityFile = identityFile;
    }

    @Override
    public File getIdentityFile() {
        return identityFile;
    }

    public void setEnablePty(boolean enablePty) {
        this.enablePty = enablePty;
    }

    public boolean isEnablePty() {
        return enablePty;
    }

    public void setUserInfoProvider(UserInfoProvider userInfoProvider) {
        if (userInfoProvider == null) {
            throw new IllegalArgumentException("UserInfoProvicer cannot be null");
        }
        this.userInfoProvider = userInfoProvider;
    }
    private static int MAX_CONTENT_LENGTH = 1024 * 1024; // 1m buffer should be enugh for most commands

    @Override
    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis, String disconnectAafterContent) throws IOException {
        return executeCommandResult(command, failOnExitNotZero, disconnectAfterMillis, disconnectAafterContent, null);
    }

    /**
     * Executes a command on the remote server, via a ssh channel.
     *
     * Captures stdout in the result, while stderr is only logged
     *
     * @param command The unix command to execute
     * @param failOnExitNotZero throw an exception if the unix command does not
     * return zero (0)
     * @param disconnectAfterMillis disconnect after a periods (this is usefull
     * when starting deamons)
     * @param disconnectAfterContent disconnect after this string has appeard
     * in output (this is usefull when starting deamons), can be used in
     * combination
     * @return a CommandResult entity with the output and errorcode.
     * @throws IOException on communication errors
     * @throws IllegalStateException if the exit code check is on
     * @todo Cleanup and split up into several methods
     */
    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis, String disconnectAfterContent, Writer liveOutput) throws IOException {
        try {
            CommandResult result = new CommandResult();
            SshRemoteConsole.log.debug("Executing > " + command);
            boolean connect = session == null || !session.isConnected();
            if (connect) {
                connect();
            }
            try {
                Channel channel = session.openChannel("exec"); // shell
                ((ChannelExec) channel).setCommand(command);
                if (enablePty) {
                    ((ChannelExec) channel).setPty(true);
                }
                InputStream error = ((ChannelExec) channel).getErrStream();
                // channel.setOutputStream(System.err);
                InputStream in = channel.getInputStream();
                channel.connect();
                long start = System.currentTimeMillis();
                boolean contentReached = true;
                if (disconnectAfterContent != null && !disconnectAfterContent.equals("")) {
                    contentReached = false;
                }
                StringBuilder output = new StringBuilder();
                StringBuilder errorOutput = new StringBuilder();
                try {
                    byte[] inTmp = new byte[1024];
                    byte[] errorTmp = new byte[1024];
                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(inTmp, 0, 1024);
                            if (i < 0) {
                                break;
                            }
                            output(new String(inTmp, 0, i), liveOutput, output);
                            log.trace(new String(inTmp, 0, i));
                        }
                        while (error.available() > 0) {
                            int i = error.read(errorTmp, 0, 1024);
                            if (i < 0) {
                                break;
                            }
                            if (output.toString().length() < MAX_CONTENT_LENGTH) {
                                errorOutput.append(new String(errorTmp, 0, i));
                            }
                            log.debug("ERROR: " + new String(errorTmp, 0, i));
                        }
                        if (channel.isClosed()) {
                            result.setExitCode(channel.getExitStatus());
                            if (failOnExitNotZero && channel.getExitStatus() != 0) {
                                log.debug("exit-status: " + channel.getExitStatus());
                                throw new IllegalStateException("Exitstatus was: " + channel.getExitStatus() + " output: " + output.toString() + " error-output: " + errorOutput.toString());
                            }
                            break;
                        }
                        if (disconnectAfterMillis > 0 && contentReached) {
                            long now = System.currentTimeMillis();
                            if (now - start > disconnectAfterMillis) {
                                log.trace("exiting before command is finished after: " + ((now - start) / 1000) + " seconds.");
                                break;
                            }
                        }
                        // TODO fix possible flaw that clashes with MAX_CONTENT_LENGTH
                        if (!contentReached && (output.toString().contains(disconnectAfterContent) || errorOutput.toString().contains(disconnectAfterContent))) {
                            contentReached = true;
                            start = System.currentTimeMillis();
                            if (disconnectAfterMillis == 0) {
                                break;
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            log.warn("Interrupted in sleep", ex);
                        }
                    }
                } finally {
                    channel.disconnect();

                }
                result.setOutput(output.toString());
                result.setErrorOutput(errorOutput.toString());
            } finally {
                if (connect) {
                    disconnect();
                }
            }
            return result;
        } catch (JSchException ex) {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
    }

    private void output(String data, Writer liveOutput, StringBuilder output) throws IOException {
        if (liveOutput != null) {
            liveOutput.append(data);
            liveOutput.flush();
        }
        if (output.toString().length() < MAX_CONTENT_LENGTH) {
            output.append(data);
        }
    }

    /**
     * Opens a shell channel to the server
     *
     * @return The ChannelShell object
     * @throws IOException
     */
    @Override
    public ChannelShell openShell() throws IOException {
        try {
            Channel channel = session.openChannel("shell"); // shell
            ChannelShell shell = (ChannelShell) channel;
            return shell;
        } catch (JSchException ex) {
            IOException ioe = new IOException();
            ioe.initCause(ex);
            throw ioe;
        }
    }

    /**
     * Uploads a file from the URL to the location specified on the server
     *
     * @param lfile URL with the file to upload
     * @param rfile Text location relative to home directory
     * @throws IOException
     */
    @Override
    public void uploadFile(URL lfile, String rfile) throws IOException {
        uploadFile(lfile, rfile, "scp -p -t " + rfile);
    }

    /**
     * Uploads a file to the server using the protocol from scp -t
     *
     * @param lfile URL with the file to upload
     * @param rfile Text location relative to home directory
     * @param command the scp -t command (with or without sudo)
     * @throws IOException
     */
    @Override
    public void uploadFile(URL lfile, String rfile, String command) throws IOException {
        try {
            log.debug("Upload file " + lfile.getFile() + " to " + rfile);
            boolean connect = (session == null || !session.isConnected());
            if (connect) {
                connect();
            }
            // exec 'scp -t rfile' remotely

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            channel.connect();
            int ack = checkAck(in);
            if (ack != 0) {
                throw new IllegalStateException("checkAck failed value " + ack);
            }
            //out.flush();
            InputStream fis = lfile.openStream();
            // Messure length
            long filesize = StreamUtil.messureContentLenth(fis);
            fis = lfile.openStream();
            command = "C0644 " + filesize + " ";
            if (lfile.getFile().lastIndexOf('/') > 0) {
                command += lfile.getFile().substring(lfile.getFile().lastIndexOf('/') + 1);
            } else {
                command += lfile.getFile();
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            ack = checkAck(in);
            if (ack != 0) {
                throw new IllegalStateException("checkAck failed value " + ack);
            }
            // send a content of lfile
            long written = 0;
            long percent = 0;
            long lastpercent = -1;
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                written += len;
                percent = ((written * 100 / filesize));
                if (lastpercent != percent) {
                    lastpercent = percent;
                    log.trace("written " + written + "/" + filesize + " bytes " + percent + "%");
                }
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            ack = checkAck(in);
            if (ack != 0) {
                throw new IllegalStateException("checkAck failed value " + ack);
            }
            out.close();
            channel.disconnect();
            if (connect) {
                disconnect();
            }
        } catch (JSchException ex) {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
    }

    @Override
    public void downloadFile(String rfile, OutputStream finalOutput) throws IOException {
        try {
            log.debug("Download file " + rfile);

            String command = "scp -f " + rfile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') {
                        break;
                    }
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }

                String file = null;
                for (int i = 0;; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
                long readed = 0;
                long percent = 0;
                long lastpercent = -1;
                long totalFileSize = filesize;

                // read a content of lfile
                int len;
                while (true) {
                    if (buf.length < filesize) {
                        len = buf.length;
                    } else {
                        len = (int) filesize;
                    }
                    len = in.read(buf, 0, len);
                    if (len < 0) {
                        // error 
                        break;
                    }
                    readed += len;
                    percent = ((readed * 100 / filesize));
                    if (lastpercent != percent) {
                        lastpercent = percent;
                        log.trace("reading " + readed + "/" + totalFileSize + " bytes " + percent + "%");
                    }

                    finalOutput.write(buf, 0, len);
                    filesize -= len;
                    if (filesize == 0L) {
                        break;
                    }
                }
                finalOutput.close();

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }

        } catch (JSchException ex) {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
    }

    /**
     * Check for acknogede
     *
     * @param in The inputstream, at a point where acknogede is expected.
     * @return 0 for success, 1 for error, 2 for fatal error, -1 end of stream
     * @throws IOException
     */
    private static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                log.warn(sb.toString());
                throw new IllegalStateException("error: " + sb.toString());
            }
            if (b == 2) { // fatal error
                log.warn(sb.toString());
                throw new IllegalStateException("fatal error: " + sb.toString());
            }
        }
        return b;
    }

    /**
     * Creates a tcp connection with a ssh session. This is where the
     * authentication occours. From here you can execute commands or open a
     * shell, via channels.
     *
     * This method is using the credentials added on the setter methods.
     *
     * @throws IOException
     */
    @Override
    public void connect() throws IOException {
        try {
            if (isConnected()) {
                throw new IllegalStateException("Already connected");
            }
            session = createSession();
            log.debug("Connecting to " + getUser() + "@" + getHost());
            session.connect();
        } catch (JSchException ex) {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
    }

    /**
     * Disconnects the tcp connection, and logout any channel open.
     */
    @Override
    public void disconnect() {
        if (isConnected()) {
            log.debug("Disconnecting");
            session.disconnect();
            session = null;
        } else {
            throw new IllegalStateException("Not connected");
        }
    }

    @Override
    public boolean isConnected() {
        if (session != null && session.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private Session createSession() throws JSchException {
        jsch.removeAllIdentity();
        if (identityFile != null) {
            if (identityFile.exists()) {
                jsch.addIdentity(identityFile.getAbsolutePath(), "passphrase");
            } else {
                log.warn("Identity file " + identityFile + " does not exists");
            }
        }
        Session mysession = jsch.getSession(user, host, port);
        UserInfo info = userInfoProvider.getUserInfo();
        mysession.setUserInfo(info);
        return mysession;
    }

    @Override
    public UserInfo getUserInfo() {
        return new LoggingUserInfo();
    }
}
