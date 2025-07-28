package com.businessmc.businessmcmod.util.collection;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Date;

public class BusinessShopCollectionDb {
    public static final String collection_name = "business_shop";
    public static Map<Integer, BusinessShopCollectionData> cache_data;

    public static void MemoryCacheBusinessShopCollectionData(MongoDatabase db) {
        cache_data = new HashMap<>();
        MongoCollection<Document> collection = db.getCollection(collection_name);
        FindIterable<Document> results = collection.find();
        for (Document doc : results) {
            BusinessShopCollectionData data = new BusinessShopCollectionData(doc);
            cache_data.put(data.getId(), data);
        }

    }

    public static void createIndexes(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(collection_name);
        collection.createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
    }

    public static boolean bulkInsertBusinessShopCollectionData(MongoDatabase db, List<BusinessShopCollectionData> dataList) {
        try {
            if(cache_data == null) cache_data = new HashMap<>();
            MongoCollection<Document> collection = db.getCollection(collection_name);
            List<Document> documents = new ArrayList<>();
            for (BusinessShopCollectionData data : dataList) {
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

    public static DataBaseResultPair<Boolean, BusinessShopCollectionData> findIdOne(MongoDatabase db, Integer where_id) {
        try {

            if (cache_data != null) {
                BusinessShopCollectionData result = cache_data.get(where_id);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("business_shop");
            FindIterable<Document> results = collection.find(Filters.eq("id", where_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new BusinessShopCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, BusinessShopCollectionData> findIdOne(MongoDatabase db, ClientSession session, Integer where_id) {
        try {

            if (cache_data != null) {
                BusinessShopCollectionData result = cache_data.get(where_id);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("business_shop");
            FindIterable<Document> results = collection.find(session, Filters.eq("id", where_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new BusinessShopCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, BusinessShopCollectionData> findIdOne(MongoDatabase db, BusinessShopCollectionData set_data) {
        try {

            if (cache_data != null) {
                BusinessShopCollectionData result = cache_data.get(set_data.getId());
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("business_shop");
            FindIterable<Document> results = collection.find(Filters.eq("id", set_data.getId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new BusinessShopCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, BusinessShopCollectionData> findIdOne(MongoDatabase db, ClientSession session, BusinessShopCollectionData set_data) {
        try {

            if (cache_data != null) {
                BusinessShopCollectionData result = cache_data.get(set_data.getId());
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("business_shop");
            FindIterable<Document> results = collection.find(session, Filters.eq("id", set_data.getId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new BusinessShopCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
}
