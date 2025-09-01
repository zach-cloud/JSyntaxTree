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
    private IAnalysisService.VariableScope scope;
    private IAnalysisService service;

    @Given("aggressive expansion style")
    public void aggressive_expansion_style() {
        service = new AnalysisService(IAnalysisService.ExpansionStyle.AGGRESSIVE);
        Assert.assertEquals(IAnalysisService.ExpansionStyle.AGGRESSIVE, service.getExpansionStyle());
    }

    @Given("passive expansion style")
    public void passive_expansion_style() {
        service = new AnalysisService();
        service.setExpansionStyle(IAnalysisService.ExpansionStyle.PASSIVE);
        Assert.assertEquals(IAnalysisService.ExpansionStyle.PASSIVE, service.getExpansionStyle());
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

    @When("variable scope is found for {string}")
    public void variable_scope_is_found_for(String variableName) {
        service = new AnalysisService();
        Script script = new Script(TestContext.inputScanner, new TreeContext());
        ISyntaxTree tree = new SyntaxTree(script);
        scope = service.findVariableScope(tree, variableName);
    }

    @Then("scope result should be global")
    public void scope_result_should_be_global() {
        Assert.assertEquals(IAnalysisService.VariableScope.GLOBAL, scope);
    }

    @Then("scope result should be unused")
    public void scope_result_should_be_unused() {
        Assert.assertEquals(IAnalysisService.VariableScope.UNUSED, scope);
    }

    @Then("scope result should be single use")
    public void scope_result_should_be_single_use() {
        Assert.assertEquals(IAnalysisService.VariableScope.SINGLE_USE, scope);
    }

    @Then("isolate variable should throw exception for {string}")
    public void isolate_variable_should_throw_exception_for(String variableName) {
        try {
            Script script = new Script(TestContext.inputScanner, new TreeContext());
            ISyntaxTree tree = new SyntaxTree(script);
            service.isolateVariable(tree, variableName, -1);
            Assert.fail("Expected exception, encountered none.");
        } catch (Exception ex) {
            // expected..
        }
    }

    @Then("isolate function should throw exception for {string}")
    public void isolate_function_should_throw_exception_for(String functionName) {
        try {
            Script script = new Script(TestContext.inputScanner, new TreeContext());
            ISyntaxTree tree = new SyntaxTree(script);
            service.isolateFunction(tree, functionName, -1);
            Assert.fail("Expected exception, encountered none.");
        } catch (Exception ex) {
            // expected..
        }
    }
}
