package net.vault7;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

class MongoDbService {

  private MongoTemplate mongoTemplate;

  MongoDbService() {
    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();

    builder.writeConcern(WriteConcern.ACKNOWLEDGED).maxConnectionIdleTime(600_000);
    List<ServerAddress> serverAddressList = Collections
        .singletonList(new ServerAddress("localhost:27017"));

    MongoClient mongoClient = new MongoClient(serverAddressList, builder.build());
    this.mongoTemplate = new MongoTemplate(mongoClient, "my-database");
  }

  void insertAdresses(Collection<? extends Address> batchToSave) {
    mongoTemplate.insert(batchToSave, Address.class);
  }
}
