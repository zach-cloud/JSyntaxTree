package tree;

import interfaces.IRandomNameGeneratorService;
import interfaces.ISyntaxTree;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class SyntaxTreeDeduplicateStepDefs {

    private ISyntaxTree tree;
    private IRandomNameGeneratorService generator;

    @Given("Non-deduplicated script:")
    public void non_deduplicated_script(String body) {
        this.tree = SyntaxTree.readTree(body);
    }

    @Given("mock random name generator set up for {string}")
    public void mock_random_name_generator_set_up_for(String name) {
        generator = new MockRandomNameGeneratorService(name);
    }

    @When("Tree is deduplicated")
    public void tree_is_deduplicated() {
         tree.deduplicate(generator);
    }

    @Then("Deduplicated script should be:")
    public void deduplicated_script_should_be(String body) {
        Assert.assertEquals(body.trim(), tree.toString().trim());
    }
}
