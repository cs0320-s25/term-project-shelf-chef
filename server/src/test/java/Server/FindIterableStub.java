package Server;

import com.mongodb.CursorType;
import com.mongodb.ExplainVerbosity;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FindIterableStub implements FindIterable<Document> {
  private final List<Document> documents;

  public FindIterableStub(List<Document> documents) {
    this.documents = documents;
  }

  @Override
  public Document first() {
    return documents.isEmpty() ? null : documents.get(0);
  }

  @Override
  public <U> MongoIterable<U> map(Function<Document, U> function) {
    return null;
  }

  @Override
  public <A extends Collection<? super Document>> A into(A objects) {
    return null;
  }

  @Override
  public MongoCursor<Document> iterator() {
    return null;
  }

  @Override
  public MongoCursor<Document> cursor() {
    return null;
  }

  @Override
  public void forEach(Consumer<? super Document> action) {
    documents.forEach(action);
  }

  @Override
  public FindIterable<Document> filter(Bson filter) {
    return this;
  }

  @Override
  public FindIterable<Document> limit(int limit) {
    return this;
  }

  @Override
  public FindIterable<Document> skip(int i) {
    return null;
  }

  @Override
  public FindIterable<Document> maxTime(long l, TimeUnit timeUnit) {
    return null;
  }

  @Override
  public FindIterable<Document> maxAwaitTime(long l, TimeUnit timeUnit) {
    return null;
  }

  @Override
  public FindIterable<Document> sort(Bson sort) {
    return this;
  }

  @Override
  public FindIterable<Document> noCursorTimeout(boolean b) {
    return null;
  }

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

  @Override
  public Document explain() {
    return null;
  }

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

  @Override
  public FindIterable<Document> projection(Bson projection) {
    return this;
  }

  // Ignore other methods unless your handler explicitly calls them
}
