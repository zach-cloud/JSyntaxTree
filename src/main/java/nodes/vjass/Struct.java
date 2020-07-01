package nodes.vjass;

import tree.TreeContext;

import java.util.Scanner;

public final class Struct extends ScriptContainer {

    private String lineFlags;

    /**
     * Sets up this abstract node with a scanner to receive words.
     *
     * @param inputScanner Scanner containing JASS code
     * @param context Tree context
     */
    public Struct(Scanner inputScanner, TreeContext context) {
        super(inputScanner, context);
        this.startText = "struct";
        this.endText = "endstruct";
    }

    /**
     * No-args constructor used for creating from an existing
     *
     * @param context Tree context
     */
    public Struct(TreeContext context) {
        super(context);
        this.startText = "struct";
        this.endText = "endstruct";
    }

    /**
     * Parses the name line, capturing all additional arguments
     * to the Struct
     *
     * @param line  Name line
     */
    @Override
    protected void parseNameLine(String line) {
        if(line.contains(" ")) {
            lineFlags = line.substring(line.indexOf(" "));
        }
    }

    /**
     * Converts the name line back into
     * a String
     *
     * @param builder   StringBuilder to add to
     */
    @Override
    protected void buildFlags(StringBuilder builder) {
        if(lineFlags != null && !lineFlags.isEmpty()) {
            builder.append(lineFlags);
        }
    }

    /**
     * Sets up initial variables
     */
    @Override
    protected void setupVariables() {
        super.setupVariables();
        this.startText = "struct";
        this.endText = "endstruct";
    }
}
