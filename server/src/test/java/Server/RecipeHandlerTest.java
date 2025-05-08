package Server;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import spark.Request;
import spark.Response;

public class RecipeHandlerTest {
    private static RecipeHandler handler;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static FindIterable<Document> findIterable;
    private static MongoCursor<Document> cursor;

    @BeforeAll
    public static void setup() {
        // Mock MongoDB components
        mongoClient = mock(MongoClient.class);
        database = mock(MongoDatabase.class);
        collection = mock(MongoCollection.class);
        findIterable = mock(FindIterable.class);
        cursor = mock(MongoCursor.class);
        
        // Setup mock behavior
        when(mongoClient.getDatabase(anyString())).thenReturn(database);
        when(database.getCollection(anyString())).thenReturn(collection);
        when(collection.find(any(org.bson.conversions.Bson.class))).thenReturn(findIterable);
        
        // Create test recipe document
        Document testRecipe = new Document()
            .append("title", "Test Buffalo Strips")
            .append("extendedIngredients", "chicken breast, buttermilk, all-purpose flour")
            .append("instructions", "Test instructions")
            .append("readyInMinutes", 30)
            .append("servings", 4);
            
        List<Document> testRecipes = new ArrayList<>();
        testRecipes.add(testRecipe);
        
        // Setup mock to return our test recipe
        when(findIterable.first()).thenReturn(testRecipe);
        when(findIterable.into(any())).thenReturn(testRecipes);
        when(findIterable.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, false); // Return true first time, false after
        when(cursor.next()).thenReturn(testRecipe);
        
        // Initialize handler with mocked client
        handler = new RecipeHandler(mongoClient, "test_db", "test_collection");
    }

    @Test
    public void testSearchRecipesByIngredients() {
        // Mock the Spark Request and Response objects
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        // Test searching for ingredients that should match the test recipe
        when(request.queryParams("ingredients")).thenReturn("chicken,buttermilk,flour");

        try {
            // Call the handler
            String result = (String) handler.handle(request, response);
            
            // Parse the response
            Moshi moshi = new Moshi.Builder().build();
            Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(type);
            Map<String, Object> jsonResponse = adapter.fromJson(result);

            // Verify the response structure
            assertNotNull(jsonResponse, "Response should not be null");
            assertEquals("success", jsonResponse.get("response"), "Response should indicate success");
            assertNotNull(jsonResponse.get("recipes"), "Recipes list should not be null");
            
            // Get the recipes list
            List<Map<String, Object>> recipes = (List<Map<String, Object>>) jsonResponse.get("recipes");
            assertFalse(recipes.isEmpty(), "Should find at least one recipe");
            
            // Verify each recipe has the required ingredients
            for (Map<String, Object> recipe : recipes) {
                String ingredients = (String) recipe.get("extendedIngredients");
                assertNotNull(ingredients, "Recipe should have ingredients");
                assertTrue(ingredients.toLowerCase().contains("chicken"), "Recipe should contain chicken");
                assertTrue(ingredients.toLowerCase().contains("buttermilk"), "Recipe should contain buttermilk");
                assertTrue(ingredients.toLowerCase().contains("flour"), "Recipe should contain flour");
            }
            
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    public void testSearchWithNoIngredients() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        // Test with no ingredients parameter
        when(request.queryParams("ingredients")).thenReturn(null);

        String result = (String) handler.handle(request, response);

        try {
            Moshi moshi = new Moshi.Builder().build();
            RecipeHandler.RecipeFailureResponse failureResponse = moshi.adapter(RecipeHandler.RecipeFailureResponse.class)
                .fromJson(result);

            assertNotNull(failureResponse);
            assertEquals("error_bad_request", failureResponse.response());
            assertEquals("Missing required parameter: ingredients", failureResponse.message());
        } catch (Exception e) {
            fail("Failed to parse response: " + e.getMessage());
        }
    }

    @Test
    public void testSearchWithEmptyIngredients() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        // Test with empty ingredients string
        when(request.queryParams("ingredients")).thenReturn("");

        String result = (String) handler.handle(request, response);

        try {
            Moshi moshi = new Moshi.Builder().build();
            RecipeHandler.RecipeFailureResponse failureResponse = moshi.adapter(RecipeHandler.RecipeFailureResponse.class)
                .fromJson(result);

            assertNotNull(failureResponse);
            assertEquals("error_bad_request", failureResponse.response());
            assertEquals("Missing required parameter: ingredients", failureResponse.message());
        } catch (Exception e) {
            fail("Failed to parse response: " + e.getMessage());
        }
    }
} 