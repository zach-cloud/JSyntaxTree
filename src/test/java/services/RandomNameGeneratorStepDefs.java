package services;

import interfaces.IRandomNameGeneratorService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;

import java.util.HashSet;
import java.util.Set;

public class RandomNameGeneratorStepDefs {
    private Set<String> names = new HashSet<>();
    private boolean duplicated = false;

    @Given("{int} randomly generated names")
    public void randomly_generated_names(Integer number) {
        IRandomNameGeneratorService generator = SyntaxTreeServices.defaultRandomNameGenerator();
        for(int i = 0 ; i < number; i++) {
            if(!names.add(generator.next())) {
                duplicated = true;
            }
        }
    }

    @Then("there should be no duplicates")
    public void there_should_be_no_duplicates() {
        Assert.assertFalse(duplicated);
    }

}
