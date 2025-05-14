package Scanning;

import java.io.File;

public class MockOCR implements OCR {
    private final String mockText;

    public MockOCR(String mockText) {
        this.mockText = mockText;
    }

    @Override
    public String extractText(File file) {
        return mockText;
    }
}
