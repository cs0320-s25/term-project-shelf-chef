package Server;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.util.HashMap;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;
import java.io.File;


public class ReceiptHandler implements Route {
    
   @Override
  public Object handle(Request request, Response response) {
    String filePath = request.queryParams("file");
    Map<String, Object> jsonResponse = new HashMap<>();

    if(filePath == null) {
        jsonResponse.put("error", "no receipt loaded");
        return jsonResponse;
    }

    File receipt = new File(filePath);
    if(!receipt.exists()) {
        jsonResponse.put("error", "no receipt found");
        return jsonResponse;
    }

    Tesseract tesseract = new Tesseract();
    tesseract.setDatapath("../../data/tessdata");

    String text = "";
    try {
        text = tesseract.doOCR(receipt);
    }
    catch (TesseractException e) {
        jsonResponse.put("error", "ocr failure");
    } 
    finally {
        receipt.delete();
        jsonResponse.put("success", text);
    }
    return jsonResponse;
  }
}
