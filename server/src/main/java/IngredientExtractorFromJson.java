import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class IngredientExtractorFromJson {
    public static void main(String[] args) {
        String inputJsonFile = "server/recipes.json";  
        String outputCsvFile = "ingredients.csv";

        Set<String> ingredientSet = new HashSet<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(new File(inputJsonFile));

            for (JsonNode recipe : root) {
                JsonNode ingredients = recipe.get("extendedIngredients");
                if (ingredients != null && ingredients.isArray()) {
                    for (JsonNode ingredient : ingredients) {
                        // Get the name field from the ingredient object
                        String ing = ingredient.asText().trim().toLowerCase();
                        // Remove numbers and units
                        ing = ing.replaceAll("\\d+\\s*(g|kg|ml|l|oz|lb|cup|cups|tbsp|tsp|tablespoon|teaspoon|gram|ounce|pound|quart|gallon|pinch|dash|piece|pieces|whole|halves|slice|slices|can|cans|bottle|bottles|package|packages|jar|jars|box|boxes|bag|bags|container|containers|stick|sticks|bunch|bunches|head|heads|clove|cloves|sprig|sprigs|leaf|leaves|stalk|stalks|rib|ribs|fillet|fillets|strip|strips|chunk|chunks|cube|cubes|wedge|wedges|segment|segments|section|sections|portion|portions|serving|servings|to taste|as needed|optional)\\b", "");
                        // Remove any common instruction words and everything after them
                        ing = ing.replaceAll("(?i)\\b(cut|chop|dice|slice|mince|grate|peel|wash|drain|stir|mix|blend|whisk|beat|fold|knead|roll|press|squeeze|pour|add|combine|place|put|set|arrange|layer|spread|sprinkle|top|garnish|decorate|serve|preheat|heat|cool|freeze|refrigerate|store|keep|save|use|prepare|make|create|cook|bake|roast|grill|fry|saute|boil|simmer|steam|poach|broil|toast|broil|toast|grill|fry|saute|boil|simmer|steam|poach|broil|toast|broil|toast)\\b.*$", "").trim();
                        // Remove any remaining numbers
                        ing = ing.replaceAll("\\d+", "").trim();
                        // Remove any extra whitespace
                        ing = ing.replaceAll("\\s+", " ").trim();
                        if (!ing.isEmpty()) {
                            ingredientSet.add(ing);
                        }
                    }
                }
            }

            // Sort the set alphabetically
            List<String> sortedIngredients = new ArrayList<>(ingredientSet);
            Collections.sort(sortedIngredients);

            // Write sorted ingredients to CSV
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputCsvFile))) {
                for (String ingredient : sortedIngredients) {
                    writer.println(ingredient);
                }
            }

            System.out.println("Ingredients written to " + outputCsvFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

