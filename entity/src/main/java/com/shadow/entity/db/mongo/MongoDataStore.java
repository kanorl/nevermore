package com.shadow.entity.db.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mongodb.morphia.Datastore;

/**
 * Created by nevermore on 2015/5/4.
 */
public interface MongoDataStore extends Datastore {

    MongoCollection<Document> getMongoCollection(Class<?> clazz);
}
