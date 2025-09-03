package tree;

import services.RandomNameGeneratorService;

public class MockRandomNameGeneratorService extends RandomNameGeneratorService {

    private final String mockedValue;

    public MockRandomNameGeneratorService(String mockedValue) {
        this.mockedValue = mockedValue;
    }

    public String next() {
        return mockedValue;
    }
}
