package nodes.arguments;

import exception.ParsingException;
import interfaces.IFunctionRenameable;
import interfaces.IVariableRenameable;
import nodes.AbstractNode;
import nodes.functions.FunctionCall;
import tree.TreeContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Stores an argument for a function call or statement.
 * This decomposes the argument into the most basic form possible.
 *
 * This class logically splits an argument into basic forms:
 *
 * - Basic argument: cannot be broken down any further and calls no method
 * - Function call: a single, standalone function call
 * - Aggregation: other arguments joined by an operator (for example +)
 * - Array call: An access to an array. Like myArray[5]. Two parts to it.
 * - "Not" Argument: An argument with a Not part on it.
 */
public final class Argument extends AbstractNode implements IFunctionRenameable, IVariableRenameable {

    private ArgumentType whichArgument;

    /**
     * If we removed parenthesis to begin with,
     * we want to add them back on at the end.
     */
    private boolean hasParenthesis;

    private boolean isAggregate;

    /**
     * Operators that cen separate an aggregation.
     * Note the spaces in the operator is important
     * Or else we can't differentiate between < and <= for example
     */
    private static String[] OPERATORS = {"+", "-", "/", "*", " and ", " or ", "> ", "< ", ">=", "<=", "==", "!=", " not "};
    /**
     * Some additional characters that aren't really
     * separators for an aggregation, but also can't be
     * in a function name.
     */
    private static String[] INVALID_FUNCTION_CHARACTERS = {"\"", "\\", ")", "(", "[", "]", "\n", " "};

    /**
     * Sets up this node with a scanner to receive words.
     *
     * @param inputScanner Scanner containing JASS code
     */
    public Argument(Scanner inputScanner, TreeContext context) {
        super(inputScanner, context);
    }

    /**
     * No-args constructor used for creating from an existing
     */
    public Argument(ArgumentType whichArgument, boolean hasParenthesis, TreeContext context) {
        super(context);
        this.whichArgument = whichArgument;
        this.hasParenthesis = hasParenthesis;
    }

    /**
     * Determines whether or not this line is an
     * atomic function call, for example:
     *
     * myFunction(x) : is a function call.
     * myFunction(x) + 5 : is not a function call (it's an aggregation)
     *
     * @param line  Argument line
     * @return      True if function call; false if not.
     */
    private boolean isFunctionCall(String line) {
        if (line.contains("(") && line.contains(")")) {
            // Extract everything up to the first ( as the functon name
            String functionName = line.substring(0, line.indexOf("("));
            // Check function name for validity
            // If it fails these checks, it wasn't a function call
            for (String operator : OPERATORS) {
                if (functionName.contains(operator)) {
                    return false;
                }
            }
            for (String operator : INVALID_FUNCTION_CHARACTERS) {
                if (functionName.contains(operator)) {
                    return false;
                }
            }
            // Everything other than the function name should be the arguments
            String argumentsPart = line.substring(functionName.length());
            boolean quoted = false;
            boolean firstParenthesisFound = false;
            int parenthesisLevel = 0; // Keeps track of how many parenthesis deep we are in a line
            // Used to handle escape chars
            char lastChar1 = ' ';
            char lastChar2 = ' ';
            boolean shouldBeFinished = false;

            for (char c : argumentsPart.toCharArray()) {
                if (shouldBeFinished) {
                    // We should have reached the end of line but didn't
                    // Therefore this isn't an atomic function call
                    return false;
                }
                if (c == '\"') {
                    // Handle quotes appropriately, taking escape characters into account
                    if (lastChar1 != '\\') {
                        quoted = !quoted;
                    } else {
                        if (lastChar2 == '\\') {
                            quoted = !quoted;
                        }
                    }
                } else if (c == '(' && !quoted) {
                    // Non-quoted parenthesis means we started a function call
                    // and went one-parenthesis deeper than before
                    parenthesisLevel++;
                    firstParenthesisFound = true;
                } else if (c == ')' && !quoted) {
                    // Non-quoted end-parenthesis means we went up one
                    // parenthesis level and maybe finished the function call
                    parenthesisLevel--;
                    if (parenthesisLevel == 0 && firstParenthesisFound) {
                        // If we ended our function call, this needs to be the
                        // last char of the line to be valid.
                        // If not the last char of the line, this boolean will
                        // make it return false
                        shouldBeFinished = true;
                    }
                }
                // Update the last 2 characters to handle escape characters correctly
                lastChar2 = lastChar1;
                lastChar1 = c;
            }
            // If we reach the end here without returning false, that means it IS atomic
            return true;
        } else {
            // It doesn't have parenthesis at all so it's not a function call
            return false;
        }
    }

    /**
     * Parse the JASS code contained in the Scanner into a model object
     */
    @Override
    protected final void readNode() {
        String line = readLine();
        line = line.replace("<", "< ");
        line = line.replaceAll("< =", "<=");
        line = line.replace(">", "> ");
        line = line.replaceAll("> =", ">=");
        readIntoArgument(line);
    }

    private final void readIntoArgument(String line) {
        line = line.trim();
        line = formatSpacing(line);
        // Handle the annoying "is it < or <=" case by using spacing
        line = line.replaceAll("<^[\\=]", "< ");
        line = line.replaceAll(">^[\\=]", "> ");
        String basicArgument = null;
        if (shouldTrim(line)) {
            // Maintain original parenthesis
            hasParenthesis = true;
            line = trimParenthesis(line).trim();
        } else {
            hasParenthesis = false;
        }
        if(line.startsWith("not ")) {
            line = " " + line;
        }
        List<String> splitParts = new ArrayList<>();
        StringBuilder splitPart = new StringBuilder();
        String operator;
        List<Argument> aggregation = new ArrayList<>();
        if (isFunctionCall(line)) {
            this.whichArgument = new FunctionCallArgument(line, context);
        } else {
            // If it's not a function call, try to make it an
            // aggregation first. If it's not, then make it a basic arg.
            boolean foundOperator = false;
            boolean quoted = false;
            String whichOperator = "";
            int parenthesisLevel = 0;
            char lastChar1 = ' ';
            char lastChar2 = ' ';
            for (char c : line.toCharArray()) {
                splitPart.append(c);
                // Handle quotes and escapes quotes
                if (c == '\"') {
                    if (lastChar1 != '\\') {
                        quoted = !quoted;
                    } else {
                        if (lastChar2 == '\\') {
                            quoted = !quoted;
                        }
                    }
                } else if (c == '(') {
                    parenthesisLevel++;
                } else if (c == ')') {
                    parenthesisLevel--;
                } else if (parenthesisLevel == 0) {
                    for (String op : OPERATORS) {
                        // Try to split by every operator
                        // If we can split by this operator, then split
                        // the remainder of the entry by it as well.
                        if (splitPart.toString().endsWith(op) && !quoted && !foundOperator) {
                            foundOperator = true;
                            whichOperator = op + "";
                            removeFinalCharacter(op.length(), splitPart);
                            splitParts.add(splitPart.toString());
                            splitPart.setLength(0);
                        } else if (foundOperator) {
                            // Split by already known operator
                            if ((c + "").equals(whichOperator) && !quoted && splitPart.length() > 0) {
                                removeFinalCharacter(op.length(), splitPart);
                                splitParts.add(splitPart.toString());
                                splitPart.setLength(0);
                            }
                        }
                    }
                }
                // Manage last characters for escaped quotes handling
                lastChar2 = lastChar1;
                lastChar1 = c;
            }
            if(splitPart.length()>0) {
                splitParts.add(splitPart.toString());
            }
            if (foundOperator) {
                // If we found an operator, then it's an aggregation
                operator = whichOperator.trim();
                int size = splitParts.size();
                for (String part : splitParts) {
                    // Handle empty part, for example "-1" splits into "", "1"
                    // But this is not an aggregation!
                    if (part == null || part.isEmpty()) {
                        size--;
                    }
                }
                // This is for empty part handling again.
                if (size >= 2) {
                    for (String part : splitParts) {
                        // Parse each sub-argument into arguments.
                        Argument argumentPart = new Argument(new Scanner(part), context);
                        aggregation.add(argumentPart);
                    }
                    whichArgument = new AggregationArgument(aggregation, operator, context);
                    this.isAggregate = true;
                } else if(size == 1 && operator.equals("not")) {
                    whichArgument = new NotArgument(splitParts.get(1), context);
                } else {
                    basicArgument = line;
                }
            } else {
                basicArgument = line;
            }
        }
        if(basicArgument != null && basicArgument.matches(".*\\[.*\\]") && basicArgument.endsWith("]")) {
            String firstPart = basicArgument.substring(0, basicArgument.indexOf("["));
            String secondPart = basicArgument.substring(1+basicArgument.indexOf("["), basicArgument.length()-1);
            // Clear out basicArgument and set array parts
            whichArgument = new ArrayArgument(firstPart, secondPart, context);
        } else if(basicArgument != null && !basicArgument.isEmpty()) {
            whichArgument = new BasicArgument(basicArgument, context);
        }
    }

    /**
     * Renames the variable and all uses of this variable.
     *
     * @param oldVariableName   Existing variable name
     * @param newVariableName   Desired variable name
     */
    @Override
    public final void renameVariable(String oldVariableName, String newVariableName) {
        if(whichArgument == null) {
            return;
        }
        whichArgument.renameVariable(oldVariableName, newVariableName);
    }

    /**
     * Renames a function and uses to a new name
     *
     * @param oldFunctionName   Existing function name
     * @param newFunctionName   Desired function name
     */
    @Override
    public final void renameFunction(String oldFunctionName, String newFunctionName) {
        if(whichArgument == null) {
            return;
        }
        whichArgument.renameFunction(oldFunctionName, newFunctionName);
    }

    public Argument inline(String functionName, String newText) {
        if(whichArgument == null) {
            return this;
        }
        return new Argument(whichArgument.inline(functionName, newText), hasParenthesis, context);
    }

    public boolean calls(String functionName) {
        if(whichArgument == null) {
            return false;
        }
        return whichArgument.calls(functionName);
    }

    public boolean usesAsFunction(String functionName) {
        if(whichArgument == null) {
            return false;
        }
        return whichArgument.usesAsFunction(functionName);
    }

    /**
     * Sets up any class-level variables before
     * performing the node reading.
     */
    @Override
    protected final void setupVariables() {

    }

    /**
     * Converts this node back to its original form.
     * Indentation is not added.
     *
     * @return Original form of this node (code or string)
     */
    @Override
    public final String toString() {
        if(whichArgument == null) {
            return "";
        }

        StringBuilder built = new StringBuilder();
        if (hasParenthesis) {
            // Add back on the trimmed parenthesis, if required.
            built.append("(");
        }
        built.append(whichArgument.toString());
        if (hasParenthesis) {
            // Add back on the trimmed parenthesis, if required.
            built.append(")");
        }
        // Trim spaces from and/or because we added an extra one on.
        return built.toString().replace("  and  ", " and ").replace("  or  ", " or ");
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

    public boolean isNot() {
        return whichArgument instanceof NotArgument;
    }

    public Argument getNotPart() {
        return ((NotArgument)whichArgument).getNotPart();
    }

    public final List<Argument> getArguments() {
        List<Argument> baseArguments = new ArrayList<>();

        if(whichArgument instanceof BasicArgument || whichArgument instanceof  FunctionCallArgument) {
            baseArguments.add(this);
        }

        baseArguments.addAll(whichArgument.getArguments());

        return baseArguments;
    }

    public void setArgument(String line) {
        readIntoArgument(line);
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
        Argument other = (Argument) obj;
        return this.toString().equals(other.toString());
    }
}
