package services;

import interfaces.IPreprocessFileService;
import interfaces.IRandomNameGeneratorService;

public final class SyntaxTreeServices {

    public static IPreprocessFileService defaultPreprocessor() {
        return new PreprocessFileService();
    }

    public static IRandomNameGeneratorService defaultRandomNameGenerator() {
        return new RandomNameGeneratorService();
    }

    private SyntaxTreeServices() {}
}
