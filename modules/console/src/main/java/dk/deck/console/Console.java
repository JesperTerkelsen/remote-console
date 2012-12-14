/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
