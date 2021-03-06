package nodes.functions;

import interfaces.IFunctionRenameable;
import interfaces.IVariableRenameable;
import nodes.AbstractNode;
import nodes.arguments.Argument;
import tree.TreeContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a Function Call that gets split into argument pieces
 */
public final class FunctionCall extends AbstractNode implements IFunctionRenameable, IVariableRenameable {

    private String functionName;
    private List<Argument> argumentsList;

    /**
     * Sets up this node with a scanner to receive words.
     *
     * @param inputScanner Scanner containing JASS code
     */
    public FunctionCall(Scanner inputScanner, TreeContext context) {
        super(inputScanner, context);
    }

    /**
     * No-args constructor used for creating from an existing
     */
    public FunctionCall(String functionName, List<Argument> argumentsList, TreeContext context) {
        super(context);
        this.functionName = functionName;
        this.argumentsList = argumentsList;
    }

    /**
     * Sets up any class-level variables before
     * performing the node reading.
     */
    @Override
    protected final void setupVariables() {
        this.argumentsList = new ArrayList<>();
    }

    public FunctionCall inline(String functionName, String newText) {
        if(functionName.equals(this.functionName)) {
            return new FunctionCall(new Scanner(newText), new TreeContext());
        }
        List<Argument> newArgumentsList = new ArrayList<>();
        for(Argument argument: argumentsList) {
            newArgumentsList.add(argument.inline(functionName, newText));
        }
        return new FunctionCall(this.functionName, newArgumentsList, new TreeContext());
    }

    /**
     * Parse the JASS code contained in the Scanner into a model object
     */
    @Override
    protected final void readNode() {
        String line = readLine();
        String functionName = line.substring(0, line.indexOf("("));
        String argumentsPart = line.substring(line.indexOf("("), line.lastIndexOf(")")+1).trim();
        this.functionName = functionName;

        List<String> functionCalls = new ArrayList<>();

        int parenthesisLevel = 0;
        StringBuilder currentPart = new StringBuilder();
        boolean quoted = false;
        char lastChar1 = ' ';
        char lastChar2 = ' ';
        for(char c : argumentsPart.toCharArray()) {
            if(c == '\"') {
                // Handle escape characters on quotes
                if (lastChar1 != '\\') {
                    quoted = !quoted;
                } else {
                    if (lastChar2 == '\\') {
                        quoted = !quoted;
                    }
                }
                currentPart.append(c);
            } else if(c == '(' && !quoted) {
                parenthesisLevel++;
                if(parenthesisLevel > 1) {
                    currentPart.append(c);
                }
            } else if(c == ')' && !quoted) {
                if(parenthesisLevel > 1) {
                    currentPart.append(c);
                }
                parenthesisLevel--;
            } else if(c == ',' && parenthesisLevel == 1 && !quoted) {
                functionCalls.add(currentPart.toString().trim());
                currentPart.setLength(0);
            } else {
                currentPart.append(c);
            }
            lastChar2 = lastChar1;
            lastChar1 = c;
        }
        if(currentPart.length() > 0) {
            functionCalls.add(currentPart.toString().trim());
        }
        for(String arg : functionCalls) {
            Argument argument = new Argument(new Scanner(arg), context);
            argumentsList.add(argument);
        }
    }

    /**
     * Converts this node back to its original form.
     * Indentation is not added.
     *
     * @return Original form of this node (code or string)
     */
    @Override
    public final String toString() {
        StringBuilder built = new StringBuilder();
        built.append("(");
        for(Argument argument: argumentsList) {
            built.append(argument.toString()).append(",");
        }
        if(built.length()>1) {
            built.setLength(built.length()-1);
        }
        built.append(")");
        return functionName + built.toString();
    }

    /**
     * Converts this node back to its original form.
     *
     * @param indentationLevel Current indentation level
     * @return Original form of this node (code or string) with indentation
     */
    @Override
    public String toFormattedString(int indentationLevel) {
        return this.toString();
    }

    /**
     * Renames the variable and all uses of this variable.
     *
     * @param oldVariableName   Existing variable name
     * @param newVariableName   Desired variable name
     */
    public final void renameVariable(String oldVariableName, String newVariableName) {
        for(Argument arg : argumentsList) {
            arg.renameVariable(oldVariableName, newVariableName);
        }
    }

    public boolean usesAsFunction(String functionName) {
        for(Argument argument : argumentsList) {
            if(argument.usesAsFunction(functionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean calls(String functionName) {
        return usesAsFunction(functionName) || this.functionName.equals(functionName);
    }

    /**
     * Renames a function and uses to a new name
     *
     * @param oldFunctionName   Existing function name
     * @param newFunctionName   Desired function name
     */
    @Override
    public final void renameFunction(String oldFunctionName, String newFunctionName) {
        for(Argument arg : argumentsList) {
            arg.renameFunction(oldFunctionName, newFunctionName);
        }
        this.functionName = rename(functionName, oldFunctionName, newFunctionName);
    }

    public String getFunctionName() {
        return functionName;
    }

    public final List<Argument> getArguments() {
        List<Argument> arguments = new ArrayList<>();
        for(Argument argument : argumentsList) {
            arguments.addAll(argument.getArguments());
        }
        return arguments;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FunctionCall other = (FunctionCall) obj;
        return this.toString().equals(other.toString());
    }
}
