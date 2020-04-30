package com.cqrs.commands;

import com.cqrs.commands.exceptions.CommandExecutionFailed;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Callable;

public class ConcurrentProofFunctionCaller<RETURN_TYPE> {

    private static final int DEFAULT_MAXIMUM_RETRIES = 5;

    public RETURN_TYPE executeFunction(Callable<RETURN_TYPE> pureFunction, Integer maximumSaveRetries)
        throws TooManyCommandExecutionRetries, CommandExecutionFailed
    {
        int retries = -1;
        if (maximumSaveRetries == null) {
            maximumSaveRetries = DEFAULT_MAXIMUM_RETRIES;
        }
        do {
            try {
                /* The real function call*/
                return pureFunction.call();
            } catch (ConcurrentModificationException e) {
                retries++;
                System.out.println("retry # " + retries);
                if (retries >= maximumSaveRetries) {
                    throw new TooManyCommandExecutionRetries(
                        String.format(
                            "TooManyCommandExecutionRetries: %d (%s)",
                            retries,
                            e.getMessage()
                        )
                    );
                }
            } catch (Exception e) {
               throw new CommandExecutionFailed(e);
            }
        } while (true);
    }
}
