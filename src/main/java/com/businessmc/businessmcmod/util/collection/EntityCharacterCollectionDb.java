package com.businessmc.businessmcmod.util.collection;


import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;

import org.bson.Document;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntityCharacterCollectionDb {
    public static final String collection_name = "entity_character";
    public static Map<Integer, EntityCharacterCollectionData> cache_data;

    public static void MemoryCacheEntityCharacterCollectionData(MongoDatabase db) {
        cache_data = new HashMap<>();
        MongoCollection<Document> collection = db.getCollection(collection_name);
        FindIterable<Document> results = collection.find();
        for (Document doc : results) {
            EntityCharacterCollectionData data = new EntityCharacterCollectionData(doc);
            cache_data.put(data.getId(), data);
        }

    }

    public static void createIndexes(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(collection_name);
        collection.createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
    }

    public static boolean bulkInsertEntityCharacterCollectionData(MongoDatabase db, List<EntityCharacterCollectionData> dataList) {
        try {
            if(cache_data == null) cache_data = new HashMap<>();
            MongoCollection<Document> collection = db.getCollection(collection_name);
            List<Document> documents = new ArrayList<>();
            for (EntityCharacterCollectionData data : dataList) {
                documents.add(data.toDocument());
                cache_data.put(data.getId(), data);
            }
            collection.insertMany(documents);
            return true;
        } catch (com.mongodb.MongoWriteException e) {
            if (e.getCode() == 11000) {
                return false;
            }
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    public static DataBaseResultPair<Boolean, EntityCharacterCollectionData> findIdOne(MongoDatabase db, Integer where_id) {
        try {

            if (cache_data != null) {
                EntityCharacterCollectionData result = cache_data.get(where_id);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("entity_character");
            FindIterable<Document> results = collection.find(Filters.eq("id", where_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new EntityCharacterCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, EntityCharacterCollectionData> findIdOne(MongoDatabase db, ClientSession session, Integer where_id) {
        try {

            if (cache_data != null) {
                EntityCharacterCollectionData result = cache_data.get(where_id);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("entity_character");
            FindIterable<Document> results = collection.find(session, Filters.eq("id", where_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new EntityCharacterCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, EntityCharacterCollectionData> findIdOne(MongoDatabase db, EntityCharacterCollectionData set_data) {
        try {

            if (cache_data != null) {
                EntityCharacterCollectionData result = cache_data.get(set_data.getId());
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("entity_character");
            FindIterable<Document> results = collection.find(Filters.eq("id", set_data.getId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new EntityCharacterCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, EntityCharacterCollectionData> findIdOne(MongoDatabase db, ClientSession session, EntityCharacterCollectionData set_data) {
        try {

            if (cache_data != null) {
                EntityCharacterCollectionData result = cache_data.get(set_data.getId());
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("entity_character");
            FindIterable<Document> results = collection.find(session, Filters.eq("id", set_data.getId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new EntityCharacterCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
}
