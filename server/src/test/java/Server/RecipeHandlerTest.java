package Server;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import com.mongodb.client.MongoClients;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.ConnectionString;

public class RecipeHandlerTest {
    private static RecipeHandler handler;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static FindIterable<Document> findIterable;
    private static MongoCursor<Document> cursor;
    private static Document testRecipe;
    private static List<Document> testRecipes;

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
        
        // Initialize handler with mocked client
        handler = new RecipeHandler(mongoClient, "test_db", "test_collection");
    }

    @BeforeEach
    public void setupEach() {
        // Create test recipe document
        testRecipe = new Document()
            .append("title", "Test Buffalo Strips")
            .append("extendedIngredients", "chicken breast, buttermilk, all-purpose flour")
            .append("instructions", "Test instructions")
            .append("readyInMinutes", 30)
            .append("servings", 4)
            .append("vegan", false)
            .append("vegetarian", false)
            .append("glutenFree", false)
            .append("dairyFree", false)
            .append("lowFodmap", false);
            
        testRecipes = new ArrayList<>();
        testRecipes.add(testRecipe);
        
        // Setup mock to return our test recipe
        when(findIterable.into(any())).thenReturn(testRecipes);
        when(findIterable.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(testRecipe);
    }

    @Test
    public void testSearchRecipesByIngredients() {
        // Mock the Spark Request and Response objects
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        // Test searching for ingredients that should match the test recipe
        when(request.queryParams("ingredients")).thenReturn("chicken,buttermilk,flour");
        when(request.queryParams("dietaryRestrictions")).thenReturn(null);

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
    public void testSearchWithDietaryRestrictions() {
        // Mock the Spark Request and Response objects
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        // Create a test recipe that matches dietary restrictions
        Document veganRecipe = new Document()
            .append("title", "Vegan Pasta")
            .append("extendedIngredients", "pasta, tomato sauce, vegetables")
            .append("instructions", "Test instructions")
            .append("readyInMinutes", 20)
            .append("servings", 2)
            .append("vegan", true)
            .append("vegetarian", true)
            .append("glutenFree", false)
            .append("dairyFree", true)
            .append("lowFodmap", false);

        List<Document> veganRecipes = new ArrayList<>();
        veganRecipes.add(veganRecipe);
        
        // Update mock to return our vegan recipe
        when(findIterable.into(any())).thenReturn(veganRecipes);
        when(cursor.next()).thenReturn(veganRecipe);

        // Test searching with both ingredients and dietary restrictions
        when(request.queryParams("ingredients")).thenReturn("pasta,tomato");
        when(request.queryParams("dietaryRestrictions")).thenReturn("vegan,dairyfree");

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
            
            // Verify each recipe matches both ingredients and dietary restrictions
            for (Map<String, Object> recipe : recipes) {
                // Check ingredients
                String ingredients = (String) recipe.get("extendedIngredients");
                assertNotNull(ingredients, "Recipe should have ingredients");
                assertTrue(ingredients.toLowerCase().contains("pasta"), "Recipe should contain pasta");
                assertTrue(ingredients.toLowerCase().contains("tomato"), "Recipe should contain tomato");
                // Check dietary restrictions
                assertTrue((Boolean) recipe.get("vegan"), "Recipe should be vegan");
                assertTrue((Boolean) recipe.get("dairyFree"), "Recipe should be dairy-free");
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

    @Test
    public void testRealDatabaseConnection() {
        // Create a real MongoDB client
        MongoClient realMongoClient = null;
        try {
            System.out.println("Starting test with real MongoDB connection...");
            
            // Set up MongoDB Atlas connection
            String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .serverApi(serverApi)
                    .build();
            
            // Connect to MongoDB Atlas
            realMongoClient = MongoClients.create(settings);
            System.out.println("Connected to MongoDB Atlas");
            
            RecipeHandler realHandler = new RecipeHandler(realMongoClient, "Recipes", "recipes");
            
            Request request = mock(Request.class);
            Response response = mock(Response.class);
            
            when(request.queryParams("ingredients")).thenReturn("chicken,rice");
            when(request.queryParams("dietaryRestrictions")).thenReturn(null);
            
            System.out.println("Executing handler with ingredients: chicken,rice");
            
            String result = (String) realHandler.handle(request, response);
            System.out.println("Raw response: " + result);
            
            Moshi moshi = new Moshi.Builder().build();
            Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(type);
            Map<String, Object> jsonResponse = adapter.fromJson(result);
            
            System.out.println("Response type: " + jsonResponse.get("response"));
            if (jsonResponse.get("message") != null) {
                System.out.println("Error message: " + jsonResponse.get("message"));
            }
            
            assertNotNull(jsonResponse, "Response should not be null");
            
            if ("error_server".equals(jsonResponse.get("response"))) {
                String errorMsg = (String) jsonResponse.get("message");
                fail("Server returned error: " + errorMsg);
            }
            
            assertEquals("success", jsonResponse.get("response"), "Response should indicate success");
            
            // Get and verify recipes
            List<Map<String, Object>> recipes = (List<Map<String, Object>>) jsonResponse.get("recipes");
            assertNotNull(recipes, "Recipes list should not be null");
            System.out.println("Found " + recipes.size() + " recipes");
            
            if (!recipes.isEmpty()) {
                Map<String, Object> firstRecipe = recipes.get(0);
                System.out.println("First recipe title: " + firstRecipe.get("title"));
                System.out.println("First recipe ingredients: " + firstRecipe.get("extendedIngredients"));
            }
            
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        } finally {
            if (realMongoClient != null) {
                System.out.println("Closing MongoDB connection");
                realMongoClient.close();
            }
        }
    }
} 