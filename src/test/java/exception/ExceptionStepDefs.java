package exception;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class ExceptionStepDefs {

    private Exception exception;

    @Then("Exception should have message {string}")
    public void exception_should_have_message(String message) {
        Assert.assertEquals(message, exception.getMessage());
    }

    @Given("MergeFailureException created with an exception with message {string}")
    public void mergefailureexception_created_with_an_exception_with_message(String message) {
        exception = new MergeFailureException(new RuntimeException(message));
    }


    @Given("MergeFailureException created with message {string}")
    public void mergefailureexception_created_with_message(String message) {
        exception = new MergeFailureException(message);
    }

    @Given("MergeFailureException created with no message")
    public void mergefailureexception_created_with_no_message() {
        exception = new MergeFailureException();
    }


    @Given("ParsingException created with an exception with message {string}")
    public void parsingexception_created_with_an_exception_with_message(String message) {
        exception = new ParsingException(new RuntimeException(message));
    }

    @Given("ParsingException created with message {string}")
    public void parsingexception_created_with_message(String message) {
        exception = new ParsingException(message);
    }

    @Given("ParsingException created with no message")
    public void parsingexception_created_with_no_message() {
        exception = new ParsingException();
    }

    @Given("RenameFailureException created with an exception with message {string}")
    public void renamefailureexception_created_with_an_exception_with_message(String message) {
        exception = new RenameFailureException(new RuntimeException(message));
    }

    @Given("RenameFailureException created with message {string}")
    public void renamefailureexception_created_with_message(String message) {
        exception = new RenameFailureException(message);
    }

    @Given("RenameFailureException created with no message")
    public void renamefailureexception_created_with_no_message() {
        exception = new RenameFailureException();
    }

    @Given("WritingException created with an exception with message {string}")
    public void writingexception_created_with_an_exception_with_message(String message) {
        exception = new WritingException(new RuntimeException(message));
    }

    @Given("WritingException created with message {string}")
    public void writingexception_created_with_message(String message) {
        exception = new WritingException(message);
    }

    @Given("WritingException created with no message")
    public void writingexception_created_with_no_message() {
        exception = new WritingException();
    }

}
