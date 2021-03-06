package nodes.functions;

import nodes.AbstractNode;
import exception.ParsingException;
import tree.TreeContext;

import java.util.Scanner;

/**
 * Represents the Output block of a function/native
 */
public final class Output extends AbstractNode {

    private String type;

    /**
     * Sets up this node with a scanner to receive words.
     *
     * @param inputScanner Scanner containing JASS code
     */
    public Output(Scanner inputScanner, TreeContext context) {
        super(inputScanner, context);
    }

    /**
     * Converts this node back to its original form.
     * Indentation is not added.
     *
     * @return Original form of this node (code or string)
     */
    @Override
    public final String toString() {
        return "returns " + getType();
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
     * Parse the JASS code contained in the Scanner into a model object
     */
    @Override
    protected final void readNode() {
        String line = readLine();
        if(!line.startsWith("returns ")) {
            throw new ParsingException("Not an output: " + line);
        }
        line = line.substring("returns ".length());
        type = line;
    }

    public final String getType() {
        return type;
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
        Output other = (Output) obj;
        return this.toString().equals(other.toString());
    }
}
