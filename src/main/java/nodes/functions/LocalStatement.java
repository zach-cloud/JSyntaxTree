package nodes.functions;

import interfaces.IFunctionRenameable;
import interfaces.IVariableRenameable;
import nodes.AbstractStatement;
import nodes.arguments.Argument;
import nodes.j.Variable;
import exception.ParsingException;
import tree.TreeContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a local variable statement
 * This is basically a variable statement beginning with local
 */
public final class LocalStatement extends AbstractStatement implements IFunctionRenameable, IVariableRenameable {

    private Variable localVariable;

    /**
     * Sets up this node with a scanner to receive words.
     *
     * @param inputScanner Scanner containing JASS code
     */
    public LocalStatement(Scanner inputScanner, TreeContext context) {
        super(inputScanner, context);
    }

    /**
     * No-args constructor used for creating from an existing
     */
    public LocalStatement(Variable localVariable, TreeContext context) {
        super(context);
        this.localVariable = localVariable;
    }

    /**
     * Converts this node back to its original form.
     * Indentation is not added.
     *
     * @return Original form of this node (code or string)
     */
    @Override
    public final String toString() {
        return "local " + localVariable.toString();
    }

    /**
     * Converts this node back to its original form.
     *
     * @param indentationLevel Current indentation level
     * @return Original form of this node (code or string) with indentation
     */
    @Override
    public String toFormattedString(int indentationLevel) {
        StringBuilder built = new StringBuilder();
        addTabs(built, indentationLevel);
        built.append(this.toString());
        return built.toString();
    }

    /**
     * Renames the variable and all uses of this variable.
     *
     * @param oldVariableName   Existing variable name
     * @param newVariableName   Desired variable name
     */
    @Override
    public final void renameVariable(String oldVariableName, String newVariableName) {
        localVariable.renameVariable(oldVariableName, newVariableName);
    }

    /**
     * Renames a function and uses to a new name
     *
     * @param oldFunctionName   Existing function name
     * @param newFunctionName   Desired function name
     */
    @Override
    public final void renameFunction(String oldFunctionName, String newFunctionName) {
        localVariable.renameFunction(oldFunctionName, newFunctionName);
    }

    /**
     * Converts the given function name into an inline function.
     * Replaces usages of function
     *
     * @param functionName Function name to replace
     * @param newText      Function text to replace with
     * @return Replaced statements
     */
    @Override
    public AbstractStatement inline(String functionName, String newText) {
        return new LocalStatement((Variable)localVariable.inline(functionName, newText), context);
    }

    public boolean usesAsFunction(String functionName) {
        return localVariable.usesAsFunction(functionName);
    }

    /**
     * Parse the JASS code contained in the Scanner into a model object
     */
    @Override
    protected final void readNode() {
        String line = readLine();
        if(!line.startsWith("local ")) {
            throw new ParsingException("Not a local statement: " + line);
        }
        line = line.substring(6);
        localVariable = new Variable(new Scanner(line), context);
    }

    public final String getType() {
        return localVariable.getType();
    }

    public final Argument getInitialValue() {
        return localVariable.getInitialValue();
    }

    public final String getName() {
        return localVariable.getName();
    }

    public final boolean isArray() {
        return localVariable.isArray();
    }

    public final List<Argument> getArguments() {
        List<Argument> arguments = new ArrayList<>();
        if(localVariable.getInitialValue() != null) {
            arguments.addAll(localVariable.getInitialValue().getArguments());
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
        LocalStatement other = (LocalStatement) obj;
        return this.toString().equals(other.toString());
    }
}
