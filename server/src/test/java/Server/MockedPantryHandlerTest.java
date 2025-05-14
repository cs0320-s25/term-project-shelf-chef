package Server;

import static javax.swing.UIManager.getString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mongodb.client.MongoClient;
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

public class MockedPantryHandlerTest {

  private MongoClient mockClient;
  private MongoDatabase mockDatabase;
  private MongoCollection<Document> mockCollection;
  private PantryHandler handler;

  @BeforeEach
  public void setup() {
    mockClient = mock(MongoClient.class);
    mockDatabase = mock(MongoDatabase.class);
    mockCollection = mock(MongoCollection.class);

    when(mockClient.getDatabase("UserPantries")).thenReturn(mockDatabase);
    when(mockDatabase.getCollection("pantries")).thenReturn(mockCollection);

    handler = new PantryHandler() {
      @Override
      public Object handle(Request request, Response response) {
        try (MongoClient ignored = mockClient) {
          return super.handle(request, response);
        }
      }
    };
  }

  // test fetching an existing pantry (should return the correct pantry)
  @Test
  public void testFetchPantry_WithExistingUser_ShouldReturnPantry() {
    String userId = "test-user-id";

    Document testUser = new Document("userId", userId)
        .append("pantry", List.of(
            new Document("name", "apple").append("quantity", 3).append("expirationDate", "01/01/30")
        ));

    when(mockCollection.find(Filters.eq("userId", userId))).thenReturn(new FindIterableStub(List.of(testUser)));

    Request request = mock(Request.class);
    Response response = mock(Response.class);

    when(request.queryParams("userid")) .thenReturn(userId);
    when(request.queryParams("fetch"))  .thenReturn("true");

    Object result = handler.handle(request, response);
    String resultJson = result.toString();

    assertTrue(resultJson.contains("success"));
    assertTrue(resultJson.contains("apple"));
  }

  // test fetching for a pantry that doesn't exist -- should return an error
  @Test
  public void testFetchPantry_FirstTimeUser_ShouldReturnError() {
    String userId = "new-user-id-123";

    when(mockCollection.find(Filters.eq("userId", userId))).thenReturn(new FindIterableStub(List.of()));

    Request request = mock(Request.class);
    Response response = mock(Response.class);

    when(request.queryParams("userid")) .thenReturn(userId);
    when(request.queryParams("fetch"))  .thenReturn("true");

    Object result = handler.handle(request, response);
    String resultJson = result.toString();

    assertTrue(resultJson.contains("error"));
    assertTrue(resultJson.contains("Pantry not found for user"));
  }

  // test adding an ingredient
  @Test
  public void testFetchAddFetchIngredientFlow() {
    String userId = "test-user-flow-123";
    String name = "test-apple";
    String expirationDate = "31/12/99";
    int quantity = 5;

    when(mockCollection.find(Filters.eq("userId", userId))).thenReturn(new FindIterableStub(List.of()));

    Request fetch1 = mock(Request.class);
    when(fetch1.queryParams("userid")).thenReturn(userId);
    when(fetch1.queryParams("fetch")) .thenReturn("true");
    Request add = mock(Request.class);
    Response resp2 = mock(Response.class);
    when(add.queryParams("userid"))     .thenReturn(userId);
    when(add.queryParams("name"))       .thenReturn(name);
    when(add.queryParams("expiration")) .thenReturn(expirationDate);
    when(add.queryParams("quantity"))   .thenReturn(String.valueOf(quantity));
    when(add.queryParams("delete"))     .thenReturn(null);
    when(add.queryParams("fetch"))      .thenReturn(null);
    when(add.queryParams("update"))     .thenReturn(null);

    when(mockCollection.find(Filters.eq("userId", userId))).thenReturn(new FindIterableStub(List.of(
        new Document("userId", userId).append("pantry", List.of(new Document("name", name).append("quantity", quantity).append("expirationDate", expirationDate)))
    )));

    String addResult = (String) handler.handle(add, resp2);
    assertTrue(addResult.contains("success"));
    assertTrue(addResult.contains("Ingredient added"));

    Request fetch2 = mock(Request.class);
    Response resp3 = mock(Response.class);
    when(fetch2.queryParams("userid")).thenReturn(userId);
    when(fetch2.queryParams("fetch")) .thenReturn("true");

    String result2 = (String) handler.handle(fetch2, resp3);
    assertTrue(result2.contains("success"));
    assertTrue(result2.contains(name));
    assertTrue(result2.contains(expirationDate));
    assertTrue(result2.contains(String.valueOf(quantity)));
  }

  // test adding and removing an ingredient
  @Test
  public void testUpdateIncrementsQuantity() {
    String userId = "test-user-incr";
    String expiration = "01/01/30";

    // Initial pantry state with quantity 5
    List<Document> fakeDB = new java.util.ArrayList<>(List.of(
        new Document("userId", userId).append("pantry", List.of(
            new Document("name", "rice").append("quantity", 5).append("expirationDate", expiration)
        ))
    ));

    // When .find() is called, return current fakeDB state
    when(mockCollection.find(Filters.eq("userId", userId)))
        .thenAnswer(invocation -> new FindIterableStub(fakeDB));

    // Intercept updateOne to simulate increment
    doAnswer(invocation -> {
      // Simulate incrementing the quantity in-place
      Document userDoc = fakeDB.get(0);
      List<Document> pantry = (List<Document>) userDoc.get("pantry");
      for (Document ingredient : pantry) {
        if (ingredient.getString("name").equals("rice")) {
          int oldQty = ingredient.getInteger("quantity");
          ingredient.put("quantity", oldQty + 1); // simulate .inc()
        }
      }
      return null;
    }).when(mockCollection).updateOne((Bson) any(), (Bson) any());

    // Simulate "add 1" request
    Request add = mock(Request.class);
    Response resp = mock(Response.class);
    when(add.queryParams("userid")).thenReturn(userId);
    when(add.queryParams("expiration")).thenReturn(expiration);
    when(add.queryParams("quantity")).thenReturn("1");
    when(add.queryParams("delete")).thenReturn(null);
    when(add.queryParams("fetch")).thenReturn(null);
    when(add.queryParams("update")).thenReturn(null);

    Object result = handler.handle(add, resp);
    String json = result.toString();
    assertTrue(json.contains("success"));

    // Fetch and assert new quantity = 6
    Request fetch = mock(Request.class);
    Response resp2 = mock(Response.class);
    when(fetch.queryParams("userid")).thenReturn(userId);
    when(fetch.queryParams("fetch")).thenReturn("true");

    Object fetchResult = handler.handle(fetch, resp2);
    String finalJson = fetchResult.toString();
    System.out.println(finalJson);
    assertTrue(finalJson.contains("17"), "Quantity should increase to 17");

    Document userDoc = fakeDB.get(0);
    List<Document> pantry = (List<Document>) userDoc.get("pantry");
    for (Document item : pantry) {
      if (item.getString("name").equals("rice")) {
        System.out.println(item.getString("im here"));
        int current = item.getInteger("quantity");
        item.put("quantity", Math.max(0, current + 1));
      }
    }

  }

}

