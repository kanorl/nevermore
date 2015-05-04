package com.shadow.entity.db.mongo;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * @author nevermore on 2015/5/2.
 */
public class MongoDataStoreFactoryBean extends AbstractFactoryBean<MongoDataStore> {

    private String dbName;
    private MongoClient mongoClient;

    @Override
    public Class<?> getObjectType() {
        return MongoDataStore.class;
    }

    @Override
    protected MongoDataStore createInstance() throws Exception {
        Morphia2 morphia = new Morphia2();
        return morphia.createMongoDataStore(mongoClient, dbName);
    }

    @Override
    protected void destroyInstance(MongoDataStore instance) throws Exception {
        mongoClient.close();
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
}
