package Server;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class TestMongo {
  MongoClientSettings settings = MongoClientSettings.builder()
      .applyConnectionString(new ConnectionString("your full Atlas URI"))
      .build();

  try (
    MongoClient client = MongoClients.create(settings)) {
      MongoDatabase db = client.getDatabase("test");
      System.out.println("Connected to MongoDB Atlas");
    }



}
