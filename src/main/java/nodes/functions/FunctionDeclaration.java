package nodes.functions;

import interfaces.IFunctionRenameable;
import interfaces.IVariableRenameable;
import nodes.AbstractNode;
import exception.ParsingException;
import tree.TreeContext;

import java.util.Scanner;

/**
 * Function Declaration line (i.e. function x takes y returns z)
 */
public final class FunctionDeclaration extends GenericDeclaration implements IFunctionRenameable, IVariableRenameable {

    /**
     * Sets up this node with a scanner to receive words.
     *
     * @param inputScanner Scanner containing JASS code
     */
    public FunctionDeclaration(Scanner inputScanner, TreeContext context) {
        super(inputScanner, context);
        setStartText("function");
    }

    /**
     * No-args constructor used for creating from an existing
     *
     * @param context
     */
    public FunctionDeclaration(TreeContext context, String name, Inputs inputs, Output output, boolean constant, String accessModifier) {
        super(context, name, inputs, output, constant, accessModifier);
        setStartText("function");
    }

    @Override
    protected void setupVariables() {
        super.setupVariables();
        setStartText("function");
    }

    /**
     * Renames the variable and all uses of this variable.
     *
     * @param oldVariableName   Existing variable name
     * @param newVariableName   Desired variable name
     */
    @Override
    public final void renameVariable(String oldVariableName, String newVariableName) {
        // do nothing
    }

    /**
     * Renames a function and uses to a new name
     *
     * @param oldFunctionName   Existing function name
     * @param newFunctionName   Desired function name
     */
    @Override
    public final void renameFunction(String oldFunctionName, String newFunctionName) {
        this.setName(rename(this.getName(), oldFunctionName, newFunctionName));
    }
}
