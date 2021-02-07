package rip.vapor.hcf.util.database.handler.data;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import rip.vapor.hcf.data.DataController;
import rip.vapor.hcf.data.Loadable;
import rip.vapor.hcf.util.database.DatabaseModule;
import rip.vapor.hcf.util.database.handler.DataHandler;
import rip.vapor.hcf.util.database.options.impl.MongoDatabaseOption;
import rip.vapor.hcf.util.database.type.mongo.MongoDataType;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

@Getter
@Setter
public class MongoDataHandler implements DataHandler {

    private final DatabaseModule controller;
    private final MongoDatabase database;

    /**
     * Constructor for setting up the MongoDataHandler
     */
    public MongoDataHandler(DatabaseModule controller) {
        this.controller = controller;

        final MongoDatabaseOption option = (MongoDatabaseOption) this.controller.getOption();

        final MongoClient client = !option.isAuthenticate() ?
                new MongoClient(option.getHostname(), option.getPort()) :
                new MongoClient(new ServerAddress(option.getHostname(), option.getPort()), Collections.singletonList(MongoCredential.createCredential(option.getUsername(), option.getAuthenticateDatabase(), option.getPassword().toCharArray())));

        this.database = client.getDatabase(option.getAuthenticateDatabase());
    }

    @Override
    public void delete(Loadable<?> loadable, String $collection) {
        final MongoCollection<Document> collection = database.getCollection($collection);

        collection.deleteOne(Filters.eq("uuid", loadable.getUniqueId().toString()));
    }

    @Override
    public void save(Loadable<?> loadable, String $collection) {
        final MongoDataType type = (MongoDataType) this.controller.getType();
        final MongoCollection<Document> collection = database.getCollection($collection);
        final Document document = collection.find(Filters.eq("uuid", loadable.getUniqueId().toString())).first();

        if(document != null) {
            collection.deleteOne(document);
        }

        type.save(collection, new Document(), loadable);
    }

    @Override
    public void load(DataController<?, ?> controller, Loadable<?> loadable, String collectionName) {
        final MongoDataType type = (MongoDataType) this.controller.getType();
        final MongoCollection<Document> collection = database.getCollection(collectionName);
        final Document document = collection.find(Filters.eq("uuid", loadable.getUniqueId().toString())).first();

        if(document != null) {
            type.load(document, controller, loadable);
        }
    }

    @Override
    public void loadAll(DataController<?, ?> controller, String collectionName, Class<? extends Loadable<?>> loadableType) {
        final MongoDataType type = (MongoDataType) this.controller.getType();
        final MongoCollection<Document> collection = database.getCollection(collectionName);

        collection.find().forEach((Block<? super Document>) document -> {
            Loadable<?> loadable = null;

            try {
                loadable = loadableType
                        .getConstructor(UUID.class)
                        .newInstance(UUID.fromString(document.getString("uuid")));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            } finally {
                type.load(document, controller, loadable);
            }

        });
    }
}