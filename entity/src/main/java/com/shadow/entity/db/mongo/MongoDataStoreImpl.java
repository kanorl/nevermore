package com.shadow.entity.db.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mongodb.morphia.DatastoreImpl;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;

/**
 * Created by nevermore on 2015/5/4.
 */
public class MongoDataStoreImpl extends DatastoreImpl implements MongoDataStore {

    public MongoDataStoreImpl(Morphia morphia, Mapper mapper, MongoClient mongoClient, String dbName) {
        super(morphia, mapper, mongoClient, dbName);
    }

    public MongoDataStoreImpl(Morphia morphia, MongoClient mongoClient, String dbName) {
        super(morphia, mongoClient, dbName);
    }

    @Override
    public MongoCollection<Document> getMongoCollection(Class<?> clazz) {
        return this.getMongo().getDatabase(getDB().getName()).getCollection(getCollection(clazz).getName());
    }
}
