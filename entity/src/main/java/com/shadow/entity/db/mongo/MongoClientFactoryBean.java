package com.shadow.entity.db.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * @author nevermore on 2015/5/2.
 */
public class MongoClientFactoryBean extends AbstractFactoryBean<MongoClient> {

    private String host;
    private int port;
    private MongoClientOptions mongoClientOptions;

    @Override
    public Class<?> getObjectType() {
        return MongoClient.class;
    }

    @Override
    protected MongoClient createInstance() throws Exception {
        return new MongoClient(new ServerAddress(host, port), mongoClientOptions);
    }

    @Override
    protected void destroyInstance(MongoClient instance) throws Exception {
        instance.close();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMongoClientOptions(MongoClientOptions mongoClientOptions) {
        this.mongoClientOptions = mongoClientOptions;
    }
}
