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
package dk.deck.remoteconsole.proxy;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Executes commands on the local machine and redirects output to standard out and standard error of current program
 * 
 * @author Jesper Terkelsen
 */
public class LocalExecutor {
    
    private static final String LOCAL_SCRIPT = "local-executor-temp.sh";
    
    public void executeAsScript(String command, String[] arguments, final PrintStream out, final PrintStream error) throws IOException, InterruptedException{
        File scriptFile = new File(LOCAL_SCRIPT);
        String script = command;
        for (String argument : arguments) {
            script += " " + argument;
        }
        scriptFile.createNewFile();
        FileUtils.writeStringToFile(scriptFile, script);
        
        Runtime.getRuntime().exec("chmod +x " + LOCAL_SCRIPT);
        execute("/bin/bash", new String[]{LOCAL_SCRIPT}, out, error);
        scriptFile.delete();
    }
    
    
    public void execute(String command, String[] arguments, final PrintStream out, final PrintStream error) throws IOException, InterruptedException{
        List<String> args = new ArrayList<String>();
        args.add(command);
        args.addAll(Arrays.asList(arguments));
        ProcessBuilder builder = new ProcessBuilder(args);
//        Map<String, String> env = builder.environment();
//        System.out.println("Env:");
//        for (Map.Entry<String, String> entry : env.entrySet()) {
//            System.out.println(entry.getKey() + "=" + entry.getValue());
//        }
        final Process p = builder.start();
        Runnable copyOutput = new Runnable() {

            @Override
            public void run() {
                try {
                    IOUtils.copyLarge(p.getInputStream(), out);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        Runnable copyError = new Runnable() {

            @Override
            public void run() {
                try {
                    IOUtils.copyLarge(p.getErrorStream(), error);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };        
        new Thread(copyOutput).start();
        new Thread(copyError).start();
        
        int exitValue = p.waitFor();
        if (exitValue != 0){
            throw new IllegalStateException("Exit value for dump was " + exitValue);
        }        
    }
}
