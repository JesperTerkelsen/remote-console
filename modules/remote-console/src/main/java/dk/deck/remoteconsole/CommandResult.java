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

/**
 * Entity that holds a result of a command
 * @author Jesper Terkelsen
 */
public class CommandResult {

    public static final int OK = 0;

    private String output;
    private String errorOutput;
    private int exitCode;

    public CommandResult() {
    }

    public CommandResult(String output, int exitCode) {
        this.output = output;
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setErrorOutput(String errorOutput) {
        this.errorOutput = errorOutput;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public boolean isOk(){
        return exitCode == OK;
    }
    
}
