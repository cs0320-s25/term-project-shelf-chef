package Scanning;

import java.io.File;

public interface OCR {
    String extractText(File file) throws Exception;
}
