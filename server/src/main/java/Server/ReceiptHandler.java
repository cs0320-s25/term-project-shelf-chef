package Server;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ReceiptHandler implements Route {
    private Set<String> ingredientSet = new HashSet<>();
    
    public ReceiptHandler() {
        this.ingredientSet = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader("server/ingredients.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Trim and skip empty lines
                line = line.trim();
                if (!line.isEmpty()) {
                    this.ingredientSet.add(line);
                }
            }
        }
        catch(Exception e) {
          System.out.println(e.getStackTrace());
        }

    }
    
    @Override
    public Object handle(Request request, Response response) {
        Map<String, Object> jsonResponse = new HashMap<>();
        try {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
            Part filePart = request.raw().getPart("file");
                if (filePart == null || filePart.getSize() == 0) {
                    jsonResponse.put("error", "no file uploaded");
                    return jsonResponse;
                }
                // Save to a temporary file
                File tempFile = File.createTempFile("receipt_", ".tmp");
                try (InputStream input = filePart.getInputStream();
                    FileOutputStream output = new FileOutputStream(tempFile)) {
                    input.transferTo(output);
                }
                catch (Exception e) {
                    return jsonResponse.put("error", e.getMessage());
                }

                // Convert PDF to image if needed
                File imageFile = tempFile;
                if (filePart.getSubmittedFileName().toLowerCase().endsWith(".pdf")) {
                    try {
                        PDDocument document = PDDocument.load(tempFile);
                        PDFRenderer pdfRenderer = new PDFRenderer(document);
                        BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // render first page at 300 DPI
                    
                        // Save the image to a temporary file
                        imageFile = File.createTempFile("receipt_image_", ".png");
                        ImageIO.write(image, "png", imageFile);
                    
                        document.close();
                    } catch (Exception e) {
                        return jsonResponse.put("error", "Failed to convert PDF: " + e.getMessage());
                    }
                }

                Tesseract tesseract = new Tesseract();
                // Set the path to the Tesseract executable
                System.setProperty("jna.library.path", "/opt/homebrew/lib");
                // Set the path to the tessdata directory
                tesseract.setDatapath("/opt/homebrew/share/tessdata");
                String text = "";
                try {
                    text = tesseract.doOCR(imageFile);
                    jsonResponse.put("success", this.filterByIngredients(text, ingredientSet));
                }
                catch (TesseractException e) {
                    jsonResponse.put("error", e.getMessage());
                } 
                finally {
                    filePart.delete();
                    tempFile.delete();
                    if (imageFile != tempFile) {
                        imageFile.delete();
                }
            }
        }
        catch (Exception e) {
            return jsonResponse.put("error", e.getMessage());
        }
        return jsonResponse;
        }

    private List<String> filterByIngredients(String ocrText, Set<String> ingredientSet) {
        List<String> matches = new ArrayList<>();
        String[] tokens = ocrText.toLowerCase().split("[^a-zA-Z]+");
        for (String token : tokens) {
            if (ingredientSet.contains(token)) {
                System.out.println("true");
                matches.add(token);
            }
        }
        return matches;
    }
}
