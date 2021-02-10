package rip.vapor.hcf.util.database.handler;

import rip.vapor.hcf.data.DataController;
import rip.vapor.hcf.data.Loadable;

import java.util.UUID;

public interface DataHandler {
    /**
     * Delete a Loadable from a Collection
     *
     * @param loadable   the loadable
     * @param collection the collection
     */
    void delete(Loadable<?> loadable, String collection);

    /**
     * Save a Loadable inside of a Collection
     *
     * @param loadable   the loadable
     * @param collection the collection
     */
    void save(Loadable<?> loadable, String collection);

    /**
     * Load a DataType from an object
     *
     * @param controller     the controller
     * @param loadableType   the loadable
     * @param uuid           the unique identifier
     * @param collectionName the collection
     */
    void load(DataController<?, ?> controller, Class<? extends Loadable<?>> loadableType, UUID uuid, String collectionName);

    /**
     * Load all documents from a collection
     *
     * @param controller     the controller
     * @param collectionName the collection
     */
    void loadAll(DataController<?, ?> controller, String collectionName, Class<? extends Loadable<?>> loadableType);
}