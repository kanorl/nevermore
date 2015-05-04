package com.shadow.entity.db.mongo;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;

/**
 * Created by nevermore on 2015/5/4.
 */
public class Morphia2 extends Morphia {

    public MongoDataStore createMongoDataStore(MongoClient mongoClient, String dbName) {
        return new MongoDataStoreImpl(this, mongoClient, dbName);
    }

    public MongoDataStore createMongoDataStore(MongoClient mongoClient, Mapper mapper, String dbName) {
        return new MongoDataStoreImpl(this, mapper, mongoClient, dbName);
    }
}
