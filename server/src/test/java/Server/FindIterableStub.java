package Server;

import com.mongodb.CursorType;
import com.mongodb.ExplainVerbosity;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Collation;
import java.util.Collection;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public class FindIterableStub implements FindIterable<Document> {
  private final Document doc;

  public FindIterableStub(Document doc) {
    this.doc = doc;
  }

  @Override
  public Document first() {
    return doc;
  }

  // --- Stubbed chainable methods ---
  @Override public FindIterable<Document> filter(Bson filter) { return this; }
  @Override public FindIterable<Document> limit(int limit) { return this; }
  @Override public FindIterable<Document> skip(int skip) { return this; }
  @Override public FindIterable<Document> sort(Bson sort) { return this; }
  @Override public FindIterable<Document> projection(Bson projection) { return this; }
  @Override public FindIterable<Document> maxTime(long maxTime, TimeUnit timeUnit) { return this; }
  @Override public FindIterable<Document> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) { return this; }
  @Override public FindIterable<Document> noCursorTimeout(boolean noCursorTimeout) { return this; }

  @Override
  public FindIterable<Document> oplogReplay(boolean b) {
    return null;
  }

  @Override
  public FindIterable<Document> partial(boolean b) {
    return null;
  }

  @Override
  public FindIterable<Document> cursorType(CursorType cursorType) {
    return null;
  }

  @Override
  public FindIterable<Document> batchSize(int i) {
    return null;
  }

  @Override
  public FindIterable<Document> collation(Collation collation) {
    return null;
  }

  @Override
  public FindIterable<Document> comment(String s) {
    return null;
  }

  @Override
  public FindIterable<Document> comment(BsonValue bsonValue) {
    return null;
  }

  @Override
  public FindIterable<Document> hint(Bson bson) {
    return null;
  }

  @Override
  public FindIterable<Document> hintString(String s) {
    return null;
  }

  @Override
  public FindIterable<Document> let(Bson bson) {
    return null;
  }

  @Override
  public FindIterable<Document> max(Bson bson) {
    return null;
  }

  @Override
  public FindIterable<Document> min(Bson bson) {
    return null;
  }

  @Override
  public FindIterable<Document> returnKey(boolean b) {
    return null;
  }

  @Override
  public FindIterable<Document> showRecordId(boolean b) {
    return null;
  }

  @Override
  public FindIterable<Document> allowDiskUse(Boolean aBoolean) {
    return null;
  }

  // --- Optional methods ---
  @Override public MongoCursor<Document> iterator() { return null; }
  @Override public MongoCursor<Document> cursor() { return null; }
  @Override public <U> com.mongodb.client.MongoIterable<U> map(Function<Document, U> mapper) { return null; }

  @Override
  public <A extends Collection<? super Document>> A into(A objects) {
    return null;
  }

  @Override public Document explain() { return null; }

  @Override
  public Document explain(ExplainVerbosity explainVerbosity) {
    return null;
  }

  @Override
  public <E> E explain(Class<E> aClass) {
    return null;
  }

  @Override
  public <E> E explain(Class<E> aClass, ExplainVerbosity explainVerbosity) {
    return null;
  }

  // If you're not using this in your test, omit this method
  // @Override public <E> com.mongodb.client.Explainable<E> asExplainable() { return null; }
}
