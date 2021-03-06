package tree;

import exception.MergeFailureException;
import exception.ParsingException;
import exception.RenameFailureException;
import exception.WritingException;
import interfaces.IPreprocessFileService;
import interfaces.IRandomNameGeneratorService;
import model.IsolateResult;
import nodes.functions.Function;
import nodes.functions.TypeDeclaration;
import nodes.j.FunctionsSection;
import nodes.j.GlobalsSection;
import services.RandomNameGeneratorService;
import nodes.AbstractFunction;
import nodes.j.Script;
import services.PreprocessFileService;
import interfaces.ISyntaxTree;
import nodes.j.Variable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a syntactically-correct and correctly-formatted
 * JASS script
 */
public final class SyntaxTree implements ISyntaxTree {

    private Script script;

    /**
     * Creates a new SyntaxTree from a pre-existing Script file.
     *
     * @param script    Script file to create from
     */
    public SyntaxTree(Script script) {
        this.script = script;
    }

    public static SyntaxTree from(IsolateResult isolateResult) {
        List<AbstractFunction> functions = new ArrayList<>(isolateResult.getIsolatedFunctions());
        List<Variable> variables = new ArrayList<>(isolateResult.getIsolatedVariables());
        return new SyntaxTree(
                new Script(
                        new GlobalsSection(variables,
                                new TreeContext()),
                        new FunctionsSection(functions,
                                new TreeContext()),
                        new ArrayList<>(),
                        new TreeContext()));
    }

    /**
     * Reads a SyntaxTree from a full, provides script String
     *
     * @param input Input Script
     * @return      Read syntax tree
     */
    public static ISyntaxTree readTree(String input) {
        TreeContext context = new TreeContext();
        IPreprocessFileService preprocessor = new PreprocessFileService();
        try {
            Script script = new Script(preprocessor.preprocessFile(new Scanner(input)), context);
            ISyntaxTree tree = new SyntaxTree(script);
            return tree;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ParsingException("Failed to parse tree: " + ex.getMessage() + ". Last line: " + context.getLastLine());
        }
    }

    /**
     * Reads a SyntaxTree from a full, provides script File
     *
     * @param inputFile Input Script (file)
     * @return          Read syntax tree
     */
    public static ISyntaxTree readTree(File inputFile) {
        try {
            return readTree(FileUtils.readFileToString(inputFile, Charset.defaultCharset()));
        } catch (Exception ex) {
            throw new ParsingException(ex);
        }
    }

    /**
     * Writes this SyntaxTree out to a file.
     *
     * @param file  File path to write to
     */
    @Override
    public final void write(File file) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(script.toString());
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            throw new WritingException(ex);
        }
    }

    /**
     * Combines this SyntaxTree with another and then checks
     * for errors. Gracefully handles function main.
     *
     * @param other Other syntax tree to combine
     */
    @Override
    public final void merge(ISyntaxTree other) {
        try {
            this.script.merge(other.getScript());
        } catch (Exception ex) {
            throw new MergeFailureException(ex);
        }
    }

    /**
     * Changes the name of a variable from old to new name
     *
     * @param oldVariableName   Existing variable name
     * @param newVariableName   New variable name
     */
    @Override
    public final void renameVariable(String oldVariableName, String newVariableName) {
        try {
            this.script.renameVariable(oldVariableName, newVariableName);
        } catch (Exception ex) {
            throw new RenameFailureException(ex);
        }
    }

    /**
     * Changes the name of a function from old to new name
     *
     * @param oldFunctionName Existing function name
     * @param newFunctionName New function name
     */
    @Override
    public final void renameFunction(String oldFunctionName, String newFunctionName) {
        try {
            this.script.renameFunction(oldFunctionName, newFunctionName);
        } catch (Exception ex) {
            throw new RenameFailureException(ex);
        }
    }

    /**
     * Cleans up the code slightly
     */
    @Override
    public ISyntaxTree postprocess() {
        String script = this.toString();
        while(script.contains("\n\n")) {
            script = script.replace("\n\n", "\n");
        }
        return SyntaxTree.readTree(script);
    }

    /**
     * Changes the name of a local variable from old to new name
     *
     * @param containingFunction   The function that has the local variable inside it
     * @param oldLocalVariableName Existing local variable name
     * @param newLocalVariableName New local variable name
     */
    @Override
    public final void renameLocalVariable(String containingFunction, String oldLocalVariableName, String newLocalVariableName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Generates a new Tree with randomized variable and function names
     */
    @Override
    public final void deduplicate(IRandomNameGeneratorService generator) {
        List<Variable> variables = script.getGlobalsSection().getGlobalVariables();
        List<AbstractFunction> functions = script.getFunctionsSection().getFunctions();
        for(Variable var : variables) {
            renameVariable(var.getName(), generator.next());
        }
        for(AbstractFunction function : functions) {
            if(!function.getName().equals("main")) {
                renameFunction(function.getName(), generator.next());
            }
        }
    }

    /**
     * Provides the Script object that this SyntaxTree contains.
     *
     * @return  Script object
     */
    @Override
    public final Script getScript() {
        return script;
    }

    /**
     * Returns the JASS Script file as a String
     *
     * @return  JASS Script as String
     */
    @Override
    public final String toString() {
        return script.toString();
    }

    /**
     * Returns the Tree as a formatted String
     *
     * @return Formatted tree
     */
    @Override
    public String getFormatted() {
        return script.toFormattedString(0);
    }

    /**
     * Returns the Tree as a non-formatted String
     *
     * @return Non-formatted tree
     */
    @Override
    public String getString() {
        return toString();
    }

    /**
     * Returns all "type" declarations in the script
     *
     * @return  List of type declarations
     */
    @Override
    public List<TypeDeclaration> getTypes() {
        return script.getTypes();
    }


    /**
     * Returns all global variables in the script
     *
     * @return List of all global variables
     */
    @Override
    public List<Variable> getGlobalVariables() {
        return script.getGlobalsSection().getGlobalVariables();
    }

    /**
     * Returns all functions in the script
     *
     * @return List of all functions
     */
    @Override
    public List<AbstractFunction> getFunctions() {
        return script.getFunctionsSection().getFunctions();
    }

    /**
     * Adds a blank function main to make the script compile
     */
    @Override
    public void addFunctionMain() {
        if(script != null) {
            script.addFunctionMain();
        }
    }
}
