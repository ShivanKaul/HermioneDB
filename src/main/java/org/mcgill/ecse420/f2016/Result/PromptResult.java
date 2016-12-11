package org.mcgill.ecse420.f2016.Result;


/**
 * Used internally by Client as a wrapper on the communication results between Client and Master
 * and Client and a Worker for a single operation.
 * Does not need to be Serializable
 */
public class PromptResult {
    private WorkerResult workerResult = null;
    private MasterResult masterResult = null;
    private Method method;
    private boolean continueLoop = true;

    public enum Method {
        GET, SET
    }

    public PromptResult(WorkerResult workerResult, MasterResult masterResult,
                        Method method) {
        this.workerResult = workerResult;
        this.masterResult = masterResult;
        this.method = method;
    }

    public PromptResult(boolean continueLoop) {
        this.continueLoop = continueLoop;
    }

    public boolean continueLoop() {
        return this.continueLoop;
    }

    public WorkerResult workerResult() {
        return this.workerResult;
    }

    public MasterResult masterResult() {
        return this.masterResult;
    }

    public Method method() {
        return this.method;
    }

}