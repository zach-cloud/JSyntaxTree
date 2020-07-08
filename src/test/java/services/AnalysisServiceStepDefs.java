package services;

import generic.TestContext;
import interfaces.IAnalysisService;
import interfaces.ISyntaxTree;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.IsolateResult;
import nodes.j.Script;
import org.junit.Assert;
import tree.SyntaxTree;
import tree.TreeContext;

public class AnalysisServiceStepDefs {

    private IsolateResult result;
    private IAnalysisService service;

    @Given("aggressive expansion style")
    public void aggressive_expansion_style() {
        service = new AnalysisService();
        service.setExpansionStyle(IAnalysisService.ExpansionStyle.AGGRESSIVE);
    }

    @Given("passive expansion style")
    public void passive_expansion_style() {
        service = new AnalysisService();
        service.setExpansionStyle(IAnalysisService.ExpansionStyle.PASSIVE);
    }

    @When("input tree is isolated for variable usage with depth {int} and name {string}")
    public void input_tree_is_isolated_for_variable_usage_with_depth_and_name(Integer depth, String name) {
        Script script = new Script(TestContext.inputScanner, new TreeContext());
        ISyntaxTree tree = new SyntaxTree(script);
        result = service.isolateVariable(tree, name, depth);
    }

    @When("input tree is isolated for function usage with depth {int} and name {string}")
    public void input_tree_is_isolated_for_function_usage_with_depth_and_name(Integer depth, String name) {
        Script script = new Script(TestContext.inputScanner, new TreeContext());
        ISyntaxTree tree = new SyntaxTree(script);
        result = service.isolateFunction(tree, name, depth);
    }

    @Then("result should have {int} function usages and {int} variable usages")
    public void result_should_have_function_usages_and_variable_usages(int functions, int variables) {
        Assert.assertEquals(functions, result.getIsolatedFunctions().size());
        Assert.assertEquals(variables, result.getIsolatedVariables().size());
    }
}
