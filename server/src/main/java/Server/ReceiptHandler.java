package Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class ReceiptHandler implements Route {
    private final Set<String> ingredientSet;
    private final Moshi moshi;

    public ReceiptHandler() {
        this.ingredientSet = new HashSet<>();
        this.moshi = new Moshi.Builder().build();
        try (BufferedReader br = new BufferedReader(new FileReader("server/ingredients.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    this.ingredientSet.add(line);
                }
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
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
                return toJson(jsonResponse);
            }

            File tempFile = File.createTempFile("receipt_", ".tmp");
            try (InputStream input = filePart.getInputStream();
                 FileOutputStream output = new FileOutputStream(tempFile)) {
                input.transferTo(output);
            } catch (Exception e) {
                jsonResponse.put("error", e.getMessage());
                return toJson(jsonResponse);
            }

            File imageFile = tempFile;
            if (filePart.getSubmittedFileName().toLowerCase().endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(tempFile)) {
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300);
                    imageFile = File.createTempFile("receipt_image_", ".png");
                    ImageIO.write(image, "png", imageFile);
                } catch (Exception e) {
                    jsonResponse.put("error", "Failed to convert PDF: " + e.getMessage());
                    return toJson(jsonResponse);
                }
            }

            Tesseract tesseract = new Tesseract();
            System.setProperty("jna.library.path", "/opt/homebrew/lib");
            tesseract.setDatapath("/opt/homebrew/share/tessdata");

            try {
                String text = tesseract.doOCR(imageFile);
                jsonResponse.put("success", filterByIngredients(text, ingredientSet));
            } catch (TesseractException e) {
                jsonResponse.put("error", e.getMessage());
            } finally {
                filePart.delete();
                tempFile.delete();
                if (imageFile != tempFile) {
                    imageFile.delete();
                }
            }
        } catch (Exception e) {
            jsonResponse.put("error", e.getMessage());
        }
        return toJson(jsonResponse);
    }

    private List<String> filterByIngredients(String ocrText, Set<String> ingredientSet) {
        List<String> matches = new ArrayList<>();
        String[] lines = ocrText.toLowerCase().split("\\r?\\n");
        List<String> sortedIngredients = new ArrayList<>(ingredientSet);
        sortedIngredients.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String line : lines) {
            for (String ingredient : sortedIngredients) {
                if (line.contains(ingredient.toLowerCase())) {
                    matches.add(ingredient);
                    break;
            }
        }
    }

    return matches;
    }

    private String toJson(Map<String, Object> map) {
        Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(type);
        return adapter.toJson(map);
    }
}
