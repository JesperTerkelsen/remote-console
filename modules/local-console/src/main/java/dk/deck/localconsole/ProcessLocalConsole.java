/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.deck.localconsole;

import dk.deck.console.CommandResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author jester
 */
public class ProcessLocalConsole implements LocalConsole {

    private File directory;

    public ProcessLocalConsole(File directory) {
        this.directory = directory;
    }

    public ProcessLocalConsole() {
        this.directory = new File(System.getProperty("user.dir"));
    }
    
    @Override
    public String executeCommand(String command) throws IOException {
        return executeCommand(command, true);
    }

    @Override
    public String executeCommand(String command, boolean failOnExitNotZero) throws IOException {
        CommandResult result = executeCommandResult(command, failOnExitNotZero);
        return result.getOutput();
    }

    @Override
    public CommandResult executeCommandResult(String command) throws IOException {
        return executeCommandResult(command, true);
    }

    @Override
    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero) throws IOException {
        System.out.println("> " + command);
        CommandResult result = new CommandResult();
        // This can cause problems if we escape spaces, we need a better splitter here.
        String[] commandAndArguments = command.split(" ");
        // We do not consider path currently
        ProcessBuilder builder = new ProcessBuilder(commandAndArguments);
        builder.directory(directory);
        builder.environment().put("LOCAL_CONSOLE", "true");
        Process process = builder.start();
        final InputStream errorOutput = process.getErrorStream();
        final InputStream output = process.getInputStream();
        CountDownLatch readLatch = new CountDownLatch(2);
        OutputReader outputReader = new OutputReader(output, readLatch);
        new Thread(outputReader).start();
        OutputReader errprReader = new OutputReader(errorOutput, readLatch);
        new Thread(errprReader).start();
        try {
            int exitCode = process.waitFor(); // Wait for process to finish
            readLatch.await(); // Wait for output to be consumed
            result.setExitCode(exitCode);
            result.setOutput(outputReader.getOutputBuffer().toString());
            result.setErrorOutput(errprReader.getOutputBuffer().toString());
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }

        return result;
    }

    /**
     * Read the output from a command.
     */
    private static class OutputReader implements Runnable {

        private final InputStream output;
        private final StringBuilder outputBuffer = new StringBuilder();
        private final Charset charset = Charset.forName("UTF-8");
        private final CountDownLatch readLatch;
        
        public OutputReader(InputStream output, CountDownLatch readLatch) {
            this.output = output;
            this.readLatch = readLatch;
        }

        @Override
        public void run() {
            try {
                InputStreamReader reader = new InputStreamReader(output, charset);
                char[] buffer = new char[1024];
                int length = reader.read(buffer);
                while (length != -1){
                    outputBuffer.append(buffer, 0, length);
                    length = reader.read(buffer);
                }
                readLatch.countDown();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }

        }

        public StringBuilder getOutputBuffer() {
            return outputBuffer;
        }
        
        
    }
}
