package ru.kalcho.sql2o;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * TODO: Make universal for LocalDate and LocalTime
 * TODO: add tests
 */
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public LocalDateTime convert(Object val) throws ConverterException {
        if (val == null) {
            return null;
        }

        if (val instanceof LocalDateTime) {
            return (LocalDateTime) val;
        }

        if (val instanceof java.util.Date) {
            return LocalDateTime.ofInstant(((Date) val).toInstant(), ZoneId.systemDefault());
        }

        if (val instanceof Number) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(((Number) val).longValue()), ZoneId.systemDefault());
        }

        throw new ConverterException("Cannot convert type " + val.getClass().toString() + " to java.time.LocalDateTime");
    }

    @Override
    public Object toDatabaseParam(LocalDateTime val) {
        if (val == null) {
            return null;
        }
        return new Timestamp(val.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

}
