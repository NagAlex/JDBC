package org.nag.storage;

//import org.nag.objects.Cat;
import org.nag.objects.Entity;
import org.nag.objects.Ignore;
//import org.nag.objects.User;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
//import java.lang.annotation.*;


/**
 * Implementation of {@link org.nag.storage.Storage} that uses database as a storage for objects.
 * It uses simple object type names to define target table to save the object.
 * It uses reflection to access objects fields and retrieve data to map to database tables.
 * As an identifier it uses field id of {@link org.nag.objects.Entity} class.
 * Could be created only with {@link java.sql.Connection} specified.
 */
public class DatabaseStorage implements Storage {
    private Connection connection;

    public DatabaseStorage(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T extends Entity> T get(Class<T> clazz, Integer id) throws Exception {
        String sql = "SELECT * FROM " + clazz.getSimpleName() + " WHERE id = " + id;
        try(Statement statement = connection.createStatement()) {
            List<T> result = extractResult(clazz, statement.executeQuery(sql));
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public <T extends Entity> List<T> list(Class<T> clazz) throws Exception {
        String sql = "SELECT * FROM " + clazz.getSimpleName();
        try(Statement statement = connection.createStatement()) {
            return extractResult(clazz, statement.executeQuery(sql));
        }
    }

    @Override
    public <T extends Entity> boolean delete(T entity) throws Exception {
        String sql = "DELETE FROM " + entity.getClass().getSimpleName() + " WHERE id = " + entity.getId();
        try(Statement statement = connection.createStatement()) {
            int rowsProcessed = statement.executeUpdate(sql);
            return rowsProcessed != 0;
        }
    }

    @Override
    //save/update object and update it with new id if it's a creation
    public <T extends Entity> void save(T entity) throws Exception {
        Map<String, Object> data = prepareEntity(entity);
        Set<String> columnNames = data.keySet();
        String sql;
        if (entity.isNew()) {
            int entitiesNum = list(entity.getClass()).size();
            entity.setId(entitiesNum + 1);
            //SQL query to create object
            sql = "INSERT INTO " + entity.getClass().getSimpleName() + " SET Id = " + entity.getId();
            for(String columnName: columnNames) {
                if(!columnName.equalsIgnoreCase("id"))
                    sql += ", " + columnName + " = " + data.get(columnName);
            }
            
        } else {
            int id = entity.getId();
            //SQL query to update object
            sql = "UPDATE " + entity.getClass().getSimpleName() + " SET ";
            for(String columnName: columnNames) {
                sql += columnName + " = " + data.get(columnName) + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + " WHERE id = " + id;
            
        }
        //System.out.println(sql);
        try(Statement statement = connection.createStatement()) {
            int rowsProcessed = statement.executeUpdate(sql);
        }

        
    }

    //converts object to map (helpful in save method)
    private <T extends Entity> Map<String, Object> prepareEntity(T entity) throws Exception {
        Map<String, Object> mapEntity = new HashMap<>();
        mapEntity.put("Id", entity.getId());
        Class<? extends Entity> clazz = entity.getClass();
        Field[] entityFields = clazz.getDeclaredFields();
        for(Field f: entityFields) {
            Ignore ann = f.getAnnotation(Ignore.class);
            if (ann != null) continue;
            try {
                boolean access = f.isAccessible();
                f.setAccessible(true);
                mapEntity.put(f.getName(), f.getType() == String.class ? "'" + f.get(entity) + "'": f.get(entity));
                f.setAccessible(access);
            } catch (Exception e) {
                System.out.println("An error occures while getting field's value");
            }
        }
        return mapEntity;
    }

    //creates list of new instances of clazz by using data from resultset
    private <T extends Entity> List<T> extractResult(Class<T> clazz, ResultSet resultset) throws Exception {
        List<T> entities = new ArrayList<>();
        while(resultset.next()) {
            T inst = clazz.newInstance();

            Integer entityId = resultset.getInt(1);
            inst.setId(entityId);

            Field[] instFields = clazz.getDeclaredFields();

            for(Field f: instFields) {
                Ignore ann = f.getAnnotation(Ignore.class);
                if (ann != null) continue;
                boolean access = f.isAccessible();
                f.setAccessible(true);
                f.set(inst, resultset.getObject(f.getName()));
                f.setAccessible(access);
            }
            entities.add(inst);
        }
        return entities;
    }
}
