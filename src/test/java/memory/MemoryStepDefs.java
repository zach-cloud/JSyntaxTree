package memory;

import generic.TestContext;
import interfaces.ISyntaxTree;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import tree.SyntaxTree;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemoryStepDefs {

    private List<ISyntaxTree> processedTrees = new ArrayList<>();
    private boolean crashed = false;
    private int processedAmount = 0;

    @When("The file war3map2 is loaded {int} times")
    public void the_file_war3map2_is_loaded_times(int times) {
        try {
            for(int i = 0; i < times; i++) {
                URL url = Thread.currentThread().getContextClassLoader().getResource("war3map2");
                String inputString = FileUtils.readFileToString(new File(url.getPath()), Charset.defaultCharset());
                TestContext.inputScanner = new Scanner(new File(url.getPath()));
                processedTrees.add(SyntaxTree.readTree(inputString));
                processedAmount++;
            }
        } catch (Throwable t) { // Intentional - we want to catch OutOfMemory errors too :)
            t.printStackTrace();
            crashed = true;
        }
        System.out.println("All done.");
    }

    @Then("the syntax tree should not have crashed")
    public void the_syntax_tree_should_not_have_crashed() {
        Assert.assertFalse("Syntax tree should not have crashed but it did. Failed after " + processedAmount + " times. "
                , crashed);
    }
}
