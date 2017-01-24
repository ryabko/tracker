package ru.kalcho.sql2o;

import java.util.UUID;

/**
 *
 */
public class UUIDConverter extends org.sql2o.converters.UUIDConverter {

    @Override
    public Object toDatabaseParam(UUID val) {
        if (val == null) {
            return null;
        }
        return val.toString();
    }

}
