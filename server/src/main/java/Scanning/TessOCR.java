package Scanning;

import java.io.File;

import net.sourceforge.tess4j.Tesseract;

public class TessOCR implements OCR {
    @Override
    public String extractText(File file) throws Exception {
        Tesseract tesseract = new Tesseract();
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
        tesseract.setDatapath("/opt/homebrew/share/tessdata");
        return tesseract.doOCR(file);
    }
}
