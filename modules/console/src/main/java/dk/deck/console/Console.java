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
package dk.deck.console;

import java.io.IOException;

/**
 *
 * @author jester
 */
public interface Console {
    /**
     * Executes a command on the client side and returns the output from standard out
     *
     * @param command unix command
     * @return Standard out result
     * @throws java.lang.IllegalStateException if the exit status is != 0
     * @throws java.io.IOException
     */
    String executeCommand(String command) throws IOException;

    /**
     * Executes a command on the client side and returns the output from standard out.
     *
     * @param command unix command
     * @param failOnExitNotZero indicator to fail on exit status != 0
     * @return Standard out result
     * @throws java.lang.IllegalStateException if the exit status is != 0 if failOnExitNotZero = true
     * @throws java.io.IOException
     */
    String executeCommand(String command, boolean failOnExitNotZero) throws IOException;

    /**
     * Executes a command on the client side and returns the output from standard out
     *
     * @param command unix command
     * @return A holder for the standard out and the exit value
     * @throws java.lang.IllegalStateException if the exit status is != 0
     * @throws java.io.IOException
     */
    CommandResult executeCommandResult(String command) throws IOException;

    /**
     * Executes a command on the client side and returns the output from standard out.
     *
     * @param command unix command
     * @param failOnExitNotZero indicator to fail on exit status != 0
     * @return A holder for the standard out and the exit value
     * @throws java.lang.IllegalStateException if the exit status is != 0 if failOnExitNotZero = true
     * @throws java.io.IOException
     */
    CommandResult executeCommandResult(String command, boolean failOnExitNotZero) throws IOException;

    
}
