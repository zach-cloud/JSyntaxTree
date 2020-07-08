package services;

import exception.ParsingException;
import interfaces.IAnalysisService;
import interfaces.ISyntaxTree;
import model.IsolateResult;
import nodes.AbstractFunction;
import nodes.functions.Argument;
import nodes.j.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalysisService implements IAnalysisService {

    private ExpansionStyle expansionStyle;

    public ExpansionStyle getExpansionStyle() {
        return expansionStyle;
    }

    public void setExpansionStyle(ExpansionStyle expansionStyle) {
        this.expansionStyle = expansionStyle;
    }

    private IsolateResult isolateVariable(ISyntaxTree tree, String variableName,
                                          int iterationCount, IsolateResult recursiveResult) {
        if (iterationCount == 0) {
            // Base case - no more iterations to perform
            return recursiveResult;
        } else {
            // Add the requested variable to be isolated to the isolation result
            Variable selectedVariable = null;
            for (Variable variable : tree.getScript().getGlobalsSection().getGlobalVariables()) {
                if (variable.getName().equals(variableName)) {
                    selectedVariable = variable;
                }
            }

            if (selectedVariable == null) {
                throw new ParsingException("Expected to be able to isolate on variable "
                        + variableName + " but was not found.");

            }

            recursiveResult.addVariable(selectedVariable);
            // Find all functions that use this variable
            Set<AbstractFunction> newAddedFunctions = new HashSet<>();
            for (AbstractFunction function : tree.getScript()
                    .getFunctionsSection().getFunctions()) {
                for (Argument argument : function.getArguments()) {
                    if (argument.toString().equals(variableName)) {
                        if (recursiveResult.addFunction(function)) {
                            newAddedFunctions.add(function);
                        }
                        break; // exit inner loop
                    }
                }
            }

            // Decrease iteration count because we just finished one
            iterationCount--;

            // Perform recursive calls
            for (AbstractFunction function : newAddedFunctions) {
                isolateFunction(tree, function.getName(), iterationCount, recursiveResult);
            }
            return recursiveResult;
        }
    }

    private IsolateResult isolateFunction(ISyntaxTree tree, String functionName,
                                          int iterationCount, IsolateResult recursiveResult) {
        if (iterationCount == 0) {
            // Base case - no more iterations to perform
            return recursiveResult;
        } else {
            // Find all functions/variables used by this function
            // First, let's grab this function's code.
            AbstractFunction selectedFunction = null;
            for (AbstractFunction function : tree.getScript().getFunctionsSection().getFunctions()) {
                if (function.getName().equals(functionName)) {
                    selectedFunction = function;
                    break;
                }
            }
            if (selectedFunction == null) {
                throw new ParsingException("Expected to be able to isolate on function "
                        + functionName + " but was not found.");
            }

            recursiveResult.addFunction(selectedFunction);
            // Grab all used arguments from this function
            List<String> arguments = new ArrayList<>();
            for (Argument arg : selectedFunction.getArguments()) {
                arguments.add(arg.toString());
            }

            // Set up values for storing new found usages
            Set<AbstractFunction> newAddedFunctions = new HashSet<>();
            Set<Variable> newAddedVariables = new HashSet<>();

            // Now scan all variables for usages in this function
            for (Variable variable : tree.getScript().getGlobalsSection().getGlobalVariables()) {
                if (arguments.contains(variable.getName())) {
                    if (recursiveResult.getIsolatedVariables().add(variable)) {
                        newAddedVariables.add(variable);
                    }
                }
            }

            // We do the same for functions (See if this function calls that function)
            for (AbstractFunction function : tree.getScript().getFunctionsSection().getFunctions()) {
                for(Argument arg : selectedFunction.getArguments()) {
                    if(arg.usesAsFunction(function.getName())) {
                        if (recursiveResult.getIsolatedFunctions().add(function)) {
                            newAddedFunctions.add(function);
                        }
                    }
                }
            }

            // Now do the inverse of the above - check all functions and see if that function calls this function
            // This will expans VERY FAST so we have a feature flag for it
            if(expansionStyle == ExpansionStyle.AGGRESSIVE) {
                for (AbstractFunction function : tree.getScript().getFunctionsSection().getFunctions()) {
                    for (Argument arg : function.getArguments()) {
                        if (arg.usesAsFunction(functionName)) {
                            if (recursiveResult.getIsolatedFunctions().add(function)) {
                                newAddedFunctions.add(function);
                            }
                        }
                    }
                }
            }


            // Decrease iteration count because we just finished one
            iterationCount--;

            // Perform recursive calls
            for (Variable variable : newAddedVariables) {
                isolateVariable(tree, variable.getName(), iterationCount, recursiveResult);
            }
            for (AbstractFunction function : newAddedFunctions) {
                isolateFunction(tree, function.getName(), iterationCount, recursiveResult);
            }
            return recursiveResult;
        }
    }

    /**
     * Retrieves all functions that use this variable.
     * Variables are stored in an IsolateResult.
     *
     * @param tree           Syntax tree to work from
     * @param variableName   Variable name to isolate
     * @param iterationCount Remaining iterations
     * @return Isolate result containing only variables
     */
    @Override
    public IsolateResult isolateVariable(ISyntaxTree tree, String variableName, int iterationCount) {
        return isolateVariable(tree, variableName, iterationCount, new IsolateResult());
    }

    /**
     * Retrieves all functions that are called by this
     * function, and all variables that are used by this
     * function.
     *
     * @param tree           Syntax tree to work from
     * @param variableName   Function name to isolate
     * @param iterationCount Remaining iterations
     * @return Isolate result containing variables/functions
     */
    @Override
    public IsolateResult isolateFunction(ISyntaxTree tree, String variableName, int iterationCount) {
        return isolateFunction(tree, variableName, iterationCount, new IsolateResult());
    }

}
