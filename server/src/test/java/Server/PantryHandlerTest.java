package Server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.util.List;

public class PantryHandlerTest {

  private MongoClient mockClient;
  private MongoDatabase mockDatabase;
  private MongoCollection<Document> mockCollection;
  private PantryHandler handler;

  @BeforeEach
  public void setup() {
    // Mocks
    mockClient = mock(MongoClient.class);
    mockDatabase = mock(MongoDatabase.class);
    mockCollection = mock(MongoCollection.class);

    // Behavior
    when(mockClient.getDatabase("UserPantries")).thenReturn(mockDatabase);
    when(mockDatabase.getCollection("pantries")).thenReturn(mockCollection);

    // Replace the real MongoClient in PantryHandler with our mocked one using a subclass or helper if needed
    handler = new PantryHandler() {
      @Override
      public Object handle(Request request, Response response) {
        // Inject the mock client into this version of the handler
        try (MongoClient mongoClient = mockClient) {
          return super.handle(request, response);
        }
      }
    };
  }

  // good pantry fetch call for a user with an existing pantry
  @Test
  public void testFetchPantry_WithExistingUser_ShouldReturnPantry() {
    String userId = "test-user-id";

    // Setup MongoDB connection
    String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
    ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    try (MongoClient client = MongoClients.create(settings)) {
      MongoDatabase db = client.getDatabase("UserPantries");
      MongoCollection<Document> col = db.getCollection("pantries");

      // Clean up existing test user if needed
      col.deleteOne(Filters.eq("userId", userId));

      // Insert test document
      Document testUser = new Document("userId", userId)
          .append("pantry", List.of(
              new Document("name", "apple").append("quantity", 3)
                  .append("expirationDate", "01/01/30")
          ));
      col.insertOne(testUser);

      // Now simulate request
      PantryHandler handler = new PantryHandler();
      Request request = mock(Request.class);
      Response response = mock(Response.class);

      when(request.queryParams("userid")).thenReturn(userId);
      when(request.queryParams("fetch")).thenReturn("true");

      Object result = handler.handle(request, response);
      String resultJson = result.toString();

      System.out.println("Response: " + resultJson);

      assertTrue(resultJson.contains("success"));
      assertTrue(resultJson.contains("apple"));
    } catch (Exception e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  // bad fetch call for user with no existing pantry - should return an error
  @Test
  public void testFetchPantry_FirstTimeUser_ShouldReturnError() {
    String userId = "new-user-id-123"; // Make this unique for test isolation

    // Setup MongoDB connection
    String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
    ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    try (MongoClient client = MongoClients.create(settings)) {
      MongoDatabase db = client.getDatabase("UserPantries");
      MongoCollection<Document> col = db.getCollection("pantries");

      // Ensure the user doesn't exist
      col.deleteOne(Filters.eq("userId", userId));

      // Set up handler and mocked request/response
      PantryHandler handler = new PantryHandler();
      Request request = mock(Request.class);
      Response response = mock(Response.class);

      when(request.queryParams("userid")).thenReturn(userId);
      when(request.queryParams("fetch")).thenReturn("true");

      // Execute handler
      Object result = handler.handle(request, response);
      String resultJson = result.toString();

      System.out.println("Response: " + resultJson);

      // Assertions
      assertTrue(resultJson.contains("error"));
      assertTrue(resultJson.contains("Pantry not found for user"));
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  // test fetching pantry, then adding an ingredient, then fetching again
  @Test
  public void testFetchAddFetchIngredientFlow() {
    String userId = "test-user-flow-123";
    String name = "test-apple";
    String expirationDate = "12/31/99";
    int quantity = 5;

    String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
    ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    try (MongoClient client = MongoClients.create(settings)) {
      MongoDatabase db = client.getDatabase("UserPantries");
      MongoCollection<Document> col = db.getCollection("pantries");

      // Clean slate
      col.deleteOne(Filters.eq("userId", userId));

      PantryHandler handler = new PantryHandler();

      // === STEP 1: Initial Fetch ===
      Request fetch1 = mock(Request.class);
      Response resp1 = mock(Response.class);

      when(fetch1.queryParams("userid")).thenReturn(userId);
      when(fetch1.queryParams("fetch")).thenReturn("true");

      String result1 = (String) handler.handle(fetch1, resp1);
      System.out.println("Initial Fetch Result: " + result1);

      assertTrue(result1.contains("error") || result1.contains("Pantry not found"));

      // === STEP 2: Add Ingredient ===
      Request add = mock(Request.class);
      Response resp2 = mock(Response.class);

      when(add.queryParams("userid")).thenReturn(userId);
      when(add.queryParams("name")).thenReturn(name);
      when(add.queryParams("expiration")).thenReturn(expirationDate);
      when(add.queryParams("quantity")).thenReturn(String.valueOf(quantity));
      when(add.queryParams("delete")).thenReturn(null);
      when(add.queryParams("fetch")).thenReturn(null);
      when(add.queryParams("update")).thenReturn(null);

      String addResult = (String) handler.handle(add, resp2);
      System.out.println("Add Result: " + addResult);

      assertTrue(addResult.contains("success"));
      assertTrue(addResult.contains("Ingredient added"));

      // === STEP 3: Second Fetch ===
      Request fetch2 = mock(Request.class);
      Response resp3 = mock(Response.class);

      when(fetch2.queryParams("userid")).thenReturn(userId);
      when(fetch2.queryParams("fetch")).thenReturn("true");

      String result2 = (String) handler.handle(fetch2, resp3);
      System.out.println("Second Fetch Result: " + result2);

      assertTrue(result2.contains("success"));
      assertTrue(result2.contains(name));
      assertTrue(result2.contains(expirationDate));
      assertTrue(result2.contains(String.valueOf(quantity)));

    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  // test add an ingredient then change the quantity
  @Test
  public void testPantryUpdateAfterAddingNewIngredient() {
    String userId = "test-user-updatecheck-456";
    String initialIngredientName = "rice";
    String initialExpiration = "01/01/30";
    int initialQuantity = 2;

    String newIngredientName = "beans";
    String newExpiration = "02/02/30";
    int newQuantity = 3;

    String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
    ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    try (MongoClient client = MongoClients.create(settings)) {
      MongoDatabase db = client.getDatabase("UserPantries");
      MongoCollection<Document> col = db.getCollection("pantries");

      col.deleteOne(Filters.eq("userId", userId));

      PantryHandler handler = new PantryHandler();

      // === STEP 1: Add Initial Ingredient ===
      Request addInitial = mock(Request.class);
      Response resp1 = mock(Response.class);

      when(addInitial.queryParams("userid")).thenReturn(userId);
      when(addInitial.queryParams("name")).thenReturn(initialIngredientName);
      when(addInitial.queryParams("expiration")).thenReturn(initialExpiration);
      when(addInitial.queryParams("quantity")).thenReturn(String.valueOf(initialQuantity));
      when(addInitial.queryParams("delete")).thenReturn(null);
      when(addInitial.queryParams("fetch")).thenReturn(null);
      when(addInitial.queryParams("update")).thenReturn(null);

      handler.handle(addInitial, resp1);

      // === STEP 2: Fetch Pantry (before adding new ingredient) ===
      Request fetch1 = mock(Request.class);
      Response resp2 = mock(Response.class);

      when(fetch1.queryParams("userid")).thenReturn(userId);
      when(fetch1.queryParams("fetch")).thenReturn("true");

      String pantryBefore = (String) handler.handle(fetch1, resp2);
      System.out.println("Pantry before adding new ingredient: " + pantryBefore);

      assertTrue(pantryBefore.contains(initialIngredientName));
      assertFalse(pantryBefore.contains(newIngredientName));

      // === STEP 3: Add New Ingredient ===
      Request addNew = mock(Request.class);
      Response resp3 = mock(Response.class);

      when(addNew.queryParams("userid")).thenReturn(userId);
      when(addNew.queryParams("name")).thenReturn(newIngredientName);
      when(addNew.queryParams("expiration")).thenReturn(newExpiration);
      when(addNew.queryParams("quantity")).thenReturn(String.valueOf(newQuantity));
      when(addNew.queryParams("delete")).thenReturn(null);
      when(addNew.queryParams("fetch")).thenReturn(null);
      when(addNew.queryParams("update")).thenReturn(null);

      handler.handle(addNew, resp3);

      // === STEP 4: Fetch Pantry Again ===
      Request fetch2 = mock(Request.class);
      Response resp4 = mock(Response.class);

      when(fetch2.queryParams("userid")).thenReturn(userId);
      when(fetch2.queryParams("fetch")).thenReturn("true");

      String pantryAfter = (String) handler.handle(fetch2, resp4);
      System.out.println("Pantry after adding new ingredient: " + pantryAfter);

      // === Assertions ===
      assertTrue(pantryAfter.contains(initialIngredientName), "Original ingredient should still be there");
      assertTrue(pantryAfter.contains(newIngredientName), "Newly added ingredient should now be in pantry");
      assertFalse(pantryBefore.contains(newIngredientName), "New ingredient should not be in old pantry snapshot");

    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  // test fetch pantry, then change the quantity for an ingredient, then fetch again
  @Test
  public void testUpdateIngredientQuantityBetweenFetches() {
    String userId = "test-user-update-quantity-789";
    String ingredientName = "rice";
    String expiration = "03/03/30";
    int initialQuantity = 2;
    int updatedQuantity = 5;

    String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
    ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    try (MongoClient client = MongoClients.create(settings)) {
      MongoDatabase db = client.getDatabase("UserPantries");
      MongoCollection<Document> col = db.getCollection("pantries");

      // 🔄 Clean slate
      col.deleteOne(Filters.eq("userId", userId));

      PantryHandler handler = new PantryHandler();

      // === STEP 1: Add Ingredient with Initial Quantity ===
      Request add = mock(Request.class);
      Response resp1 = mock(Response.class);

      when(add.queryParams("userid")).thenReturn(userId);
      when(add.queryParams("name")).thenReturn(ingredientName);
      when(add.queryParams("expiration")).thenReturn(expiration);
      when(add.queryParams("quantity")).thenReturn(String.valueOf(initialQuantity));
      when(add.queryParams("delete")).thenReturn(null);
      when(add.queryParams("fetch")).thenReturn(null);
      when(add.queryParams("update")).thenReturn(null);

      handler.handle(add, resp1);

      // === STEP 2: Fetch and Confirm Initial Quantity ===
      Request fetch1 = mock(Request.class);
      Response resp2 = mock(Response.class);

      when(fetch1.queryParams("userid")).thenReturn(userId);
      when(fetch1.queryParams("fetch")).thenReturn("true");

      String pantryBefore = (String) handler.handle(fetch1, resp2);
      System.out.println("Pantry before update: " + pantryBefore);

      assertTrue(pantryBefore.contains("rice"));
      assertTrue(pantryBefore.contains(String.valueOf(initialQuantity)));

      // === STEP 3: Update the Ingredient Quantity ===
      Request update = mock(Request.class);
      Response resp3 = mock(Response.class);

      when(update.queryParams("userid")).thenReturn(userId);
      when(update.queryParams("name")).thenReturn(ingredientName);
      when(update.queryParams("expiration")).thenReturn(expiration);
      when(update.queryParams("quantity")).thenReturn(String.valueOf(updatedQuantity));
      when(update.queryParams("update")).thenReturn("true");
      when(update.queryParams("fetch")).thenReturn(null);
      when(update.queryParams("delete")).thenReturn(null);

      String updateResult = (String) handler.handle(update, resp3);
      System.out.println("Update result: " + updateResult);
      assertTrue(updateResult.contains("Ingredient quantity updated"));

      // === STEP 4: Fetch Again and Confirm Updated Quantity ===
      Request fetch2 = mock(Request.class);
      Response resp4 = mock(Response.class);

      when(fetch2.queryParams("userid")).thenReturn(userId);
      when(fetch2.queryParams("fetch")).thenReturn("true");

      String pantryAfter = (String) handler.handle(fetch2, resp4);
      System.out.println("Pantry after update: " + pantryAfter);

      assertTrue(pantryAfter.contains("rice"));
      assertTrue(pantryAfter.contains(String.valueOf(updatedQuantity)));
      assertFalse(pantryAfter.contains(String.valueOf(initialQuantity)));

    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  // test delete an ingredient and make sure it remains deleted across reload
  @Test
  public void testDeleteIngredientAndVerifyRemoval() {
    String userId = "test-user-delete-001";
    String name = "carrot";
    String expirationDate = "04/04/30";
    int quantity = 4;

    String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
    ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    try (MongoClient client = MongoClients.create(settings)) {
      MongoDatabase db = client.getDatabase("UserPantries");
      MongoCollection<Document> col = db.getCollection("pantries");

      // Clean up any previous test data
      col.deleteOne(Filters.eq("userId", userId));

      PantryHandler handler = new PantryHandler();

      // === STEP 1: Add the Ingredient ===
      Request add = mock(Request.class);
      Response resp1 = mock(Response.class);

      when(add.queryParams("userid")).thenReturn(userId);
      when(add.queryParams("name")).thenReturn(name);
      when(add.queryParams("expiration")).thenReturn(expirationDate);
      when(add.queryParams("quantity")).thenReturn(String.valueOf(quantity));
      when(add.queryParams("delete")).thenReturn(null);
      when(add.queryParams("fetch")).thenReturn(null);
      when(add.queryParams("update")).thenReturn(null);

      handler.handle(add, resp1);

      // === STEP 2: Fetch and Confirm Ingredient Exists ===
      Request fetch1 = mock(Request.class);
      Response resp2 = mock(Response.class);

      when(fetch1.queryParams("userid")).thenReturn(userId);
      when(fetch1.queryParams("fetch")).thenReturn("true");

      String pantryBeforeDelete = (String) handler.handle(fetch1, resp2);
      System.out.println("Pantry before deletion: " + pantryBeforeDelete);

      assertTrue(pantryBeforeDelete.contains("name"));
      assertTrue(pantryBeforeDelete.contains(String.valueOf(quantity)));

      // === STEP 3: Delete the Ingredient ===
      Request delete = mock(Request.class);
      Response resp3 = mock(Response.class);

      when(delete.queryParams("userid")).thenReturn(userId);
      when(delete.queryParams("name")).thenReturn(name);
      when(delete.queryParams("expiration")).thenReturn(expirationDate);
      when(delete.queryParams("delete")).thenReturn("true");
      when(delete.queryParams("fetch")).thenReturn(null);
      when(delete.queryParams("update")).thenReturn(null);
      when(delete.queryParams("quantity")).thenReturn(null);

      String deleteResult = (String) handler.handle(delete, resp3);
      System.out.println("Delete result: " + deleteResult);

      assertTrue(deleteResult.contains("success"));
      assertTrue(deleteResult.contains("removed"));

      // === STEP 4: Fetch and Confirm Ingredient Is Gone ===
      Request fetch2 = mock(Request.class);
      Response resp4 = mock(Response.class);

      when(fetch2.queryParams("userid")).thenReturn(userId);
      when(fetch2.queryParams("fetch")).thenReturn("true");

      String pantryAfterDelete = (String) handler.handle(fetch2, resp4);
      System.out.println("Pantry after deletion: " + pantryAfterDelete);

      assertFalse(pantryAfterDelete.contains(name));
      assertFalse(pantryAfterDelete.contains(expirationDate));

    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

}