package interfaces;

import model.IsolateResult;
import services.AnalysisService;

public interface IAnalysisService {

    enum ExpansionStyle {
        AGGRESSIVE,
        PASSIVE
    }

    /**
     * Retrieves all functions that use this variable.
     * Variables are stored in an IsolateResult.
     *
     * @param tree           Syntax tree to work from
     * @param variableName   Variable name to isolate
     * @param iterationCount Remaining iterations
     * @return               Isolate result containing only variables
     */
    IsolateResult isolateVariable(ISyntaxTree tree, String variableName, int iterationCount);

    /**
     * Retrieves all functions that are called by this
     * function, and all variables that are used by this
     * function.
     *
     * @param tree           Syntax tree to work from
     * @param variableName   Function name to isolate
     * @param iterationCount Remaining iterations
     * @return               Isolate result containing only variables
     */
    IsolateResult isolateFunction(ISyntaxTree tree, String variableName, int iterationCount);

    void setExpansionStyle(AnalysisService.ExpansionStyle style);
}
