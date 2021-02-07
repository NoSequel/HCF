package rip.vapor.hcf.util.database;

import rip.vapor.hcf.module.Module;
import rip.vapor.hcf.util.database.handler.DataHandler;
import rip.vapor.hcf.util.database.options.DatabaseOption;
import rip.vapor.hcf.util.database.type.DataType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseModule implements Module {

    private final DataType<?, ?> type;
    private final DatabaseOption option;

    private DataHandler dataHandler;

    /**
     * Constructor for creating a new DatabaseController
     *
     * @param option      the options of the database
     * @param type        the type of the data
     */
    public DatabaseModule(DatabaseOption option, DataType<?, ?> type) {
        this.type = type;
        this.option = option;
    }
}