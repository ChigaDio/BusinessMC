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

public class ItemBlockGameCollectionDb {
    public static final String collection_name = "item_block_game";
    public static Map<String, ItemBlockGameCollectionData> cache_data;

    public static void MemoryCacheItemBlockGameCollectionData(MongoDatabase db) {
        cache_data = new HashMap<>();
        MongoCollection<Document> collection = db.getCollection(collection_name);
        FindIterable<Document> results = collection.find();
        for (Document doc : results) {
            ItemBlockGameCollectionData data = new ItemBlockGameCollectionData(doc);
            cache_data.put(data.getIdName(), data);
        }

    }

    public static void createIndexes(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(collection_name);
        collection.createIndex(Indexes.ascending("id_name"), new IndexOptions().unique(true));
    }

    public static boolean bulkInsertItemBlockGameCollectionData(MongoDatabase db, List<ItemBlockGameCollectionData> dataList) {
        try {
            if(cache_data == null) cache_data = new HashMap<>();
            MongoCollection<Document> collection = db.getCollection(collection_name);
            List<Document> documents = new ArrayList<>();
            for (ItemBlockGameCollectionData data : dataList) {
                documents.add(data.toDocument());
                cache_data.put(data.getIdName(), data);
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

    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findAllItemBlockMany(MongoDatabase db) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = new ArrayList<>(cache_data.values());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(new Document());
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findAllItemBlockMany(MongoDatabase db, ClientSession session) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = new ArrayList<>(cache_data.values());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, new Document());
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findAllItemBlockMany(MongoDatabase db, ItemBlockGameCollectionData set_data) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = new ArrayList<>(cache_data.values());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(new Document());
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findAllItemBlockMany(MongoDatabase db, ClientSession session, ItemBlockGameCollectionData set_data) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = new ArrayList<>(cache_data.values());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, new Document());
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findIdNameOne(MongoDatabase db, String where_id_name) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(where_id_name);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.eq("id_name", where_id_name)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findIdNameOne(MongoDatabase db, ClientSession session, String where_id_name) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(where_id_name);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.eq("id_name", where_id_name)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findIdNameOne(MongoDatabase db, ItemBlockGameCollectionData set_data) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(set_data.getIdName());
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.eq("id_name", set_data.getIdName())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findIdNameOne(MongoDatabase db, ClientSession session, ItemBlockGameCollectionData set_data) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(set_data.getIdName());
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.eq("id_name", set_data.getIdName())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> AddRecipeJob(MongoDatabase db, String where_id_name, List<Integer> set_recipe_create_job_list, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            if (memory_update && cache_data != null) {
                Document updatedDoc = collection.findOneAndUpdate(Filters.eq("id_name", where_id_name), Updates.addToSet("recipe_create_job_list", set_recipe_create_job_list), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

                if (memory_update && updatedDoc != null && cache_data != null) {
                    ItemBlockGameCollectionData updatedData = new ItemBlockGameCollectionData(updatedDoc);
                    cache_data.put(updatedData.getIdName(), updatedData);
                }
                return DataBaseResultPair.of(updatedDoc != null, null);
            } else {
                UpdateResult result = collection.updateOne(Filters.eq("id_name", where_id_name), Updates.addToSet("recipe_create_job_list", set_recipe_create_job_list));
                return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
            }
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> AddRecipeJob(MongoDatabase db, ClientSession session, String where_id_name, List<Integer> set_recipe_create_job_list, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            if (memory_update && cache_data != null) {
                Document updatedDoc = collection.findOneAndUpdate(session, Filters.eq("id_name", where_id_name), Updates.addToSet("recipe_create_job_list", set_recipe_create_job_list), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

                if (memory_update && updatedDoc != null && cache_data != null) {
                    ItemBlockGameCollectionData updatedData = new ItemBlockGameCollectionData(updatedDoc);
                    cache_data.put(updatedData.getIdName(), updatedData);
                }
                return DataBaseResultPair.of(updatedDoc != null, null);
            } else {
                UpdateResult result = collection.updateOne(session, Filters.eq("id_name", where_id_name), Updates.addToSet("recipe_create_job_list", set_recipe_create_job_list));
                return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
            }
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> AddRecipeJob(MongoDatabase db, ItemBlockGameCollectionData set_data, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            if (memory_update && cache_data != null) {
                Document updatedDoc = collection.findOneAndUpdate(Filters.eq("id_name", set_data.getIdName()), Updates.addToSet("recipe_create_job_list", set_data.getRecipeCreateJobList()), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

                if (memory_update && updatedDoc != null && cache_data != null) {
                    ItemBlockGameCollectionData updatedData = new ItemBlockGameCollectionData(updatedDoc);
                    cache_data.put(updatedData.getIdName(), updatedData);
                }
                return DataBaseResultPair.of(updatedDoc != null, null);
            } else {
                UpdateResult result = collection.updateOne(Filters.eq("id_name", set_data.getIdName()), Updates.addToSet("recipe_create_job_list", set_data.getRecipeCreateJobList()));
                return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
            }
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> AddRecipeJob(MongoDatabase db, ClientSession session, ItemBlockGameCollectionData set_data, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            if (memory_update && cache_data != null) {
                Document updatedDoc = collection.findOneAndUpdate(session, Filters.eq("id_name", set_data.getIdName()), Updates.addToSet("recipe_create_job_list", set_data.getRecipeCreateJobList()), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

                if (memory_update && updatedDoc != null && cache_data != null) {
                    ItemBlockGameCollectionData updatedData = new ItemBlockGameCollectionData(updatedDoc);
                    cache_data.put(updatedData.getIdName(), updatedData);
                }
                return DataBaseResultPair.of(updatedDoc != null, null);
            } else {
                UpdateResult result = collection.updateOne(session, Filters.eq("id_name", set_data.getIdName()), Updates.addToSet("recipe_create_job_list", set_data.getRecipeCreateJobList()));
                return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
            }
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findJobRecipeMany(MongoDatabase db, Integer where_recipe_create_job_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getRecipeCreateJobList() != null && item.getRecipeCreateJobList().contains(where_recipe_create_job_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.in("recipe_create_job_list", where_recipe_create_job_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findJobRecipeMany(MongoDatabase db, ClientSession session, Integer where_recipe_create_job_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getRecipeCreateJobList() != null && item.getRecipeCreateJobList().contains(where_recipe_create_job_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.in("recipe_create_job_list", where_recipe_create_job_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findJobRecipeMany(MongoDatabase db, ItemBlockGameCollectionData set_data, Integer where_recipe_create_job_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getRecipeCreateJobList() != null && item.getRecipeCreateJobList().contains(where_recipe_create_job_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.in("recipe_create_job_list", where_recipe_create_job_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findJobRecipeMany(MongoDatabase db, ClientSession session, ItemBlockGameCollectionData set_data, Integer where_recipe_create_job_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getRecipeCreateJobList() != null && item.getRecipeCreateJobList().contains(where_recipe_create_job_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.in("recipe_create_job_list", where_recipe_create_job_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionJobOne(MongoDatabase db, Integer where_action_price_job) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.values().stream()
                        .filter(item -> item.getActionPriceJob() != null && item.getActionPriceJob().contains(where_action_price_job))
                        .findFirst().orElse(null);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.in("action_price_job", where_action_price_job)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionJobOne(MongoDatabase db, ClientSession session, Integer where_action_price_job) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.values().stream()
                        .filter(item -> item.getActionPriceJob() != null && item.getActionPriceJob().contains(where_action_price_job))
                        .findFirst().orElse(null);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.in("action_price_job", where_action_price_job)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionJobOne(MongoDatabase db, ItemBlockGameCollectionData set_data, Integer where_action_price_job) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.values().stream()
                        .filter(item -> item.getActionPriceJob() != null && item.getActionPriceJob().contains(where_action_price_job))
                        .findFirst().orElse(null);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.in("action_price_job", where_action_price_job)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionJobOne(MongoDatabase db, ClientSession session, ItemBlockGameCollectionData set_data, Integer where_action_price_job) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.values().stream()
                        .filter(item -> item.getActionPriceJob() != null && item.getActionPriceJob().contains(where_action_price_job))
                        .findFirst().orElse(null);
                return DataBaseResultPair.of(result != null, result);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.in("action_price_job", where_action_price_job)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionBlockJobOne(MongoDatabase db, Integer where_action_price_job, String where_id_name) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(where_id_name);
                if (result != null && result.getActionPriceJob() != null && result.getActionPriceJob().contains(where_action_price_job)) {
                    return DataBaseResultPair.of(true, result);
                }
                return DataBaseResultPair.of(false, null);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.and(Filters.in("action_price_job", where_action_price_job), Filters.eq("id_name", where_id_name))).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionBlockJobOne(MongoDatabase db, ClientSession session, Integer where_action_price_job, String where_id_name) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(where_id_name);
                if (result != null && result.getActionPriceJob() != null && result.getActionPriceJob().contains(where_action_price_job)) {
                    return DataBaseResultPair.of(true, result);
                }
                return DataBaseResultPair.of(false, null);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.and(Filters.in("action_price_job", where_action_price_job), Filters.eq("id_name", where_id_name))).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionBlockJobOne(MongoDatabase db, ItemBlockGameCollectionData set_data, Integer where_action_price_job, String where_id_name) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(set_data.getIdName());
                if (result != null && result.getActionPriceJob() != null && result.getActionPriceJob().contains(where_action_price_job)) {
                    return DataBaseResultPair.of(true, result);
                }
                return DataBaseResultPair.of(false, null);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.and(Filters.in("action_price_job", where_action_price_job), Filters.eq("id_name", where_id_name))).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, ItemBlockGameCollectionData> findActionBlockJobOne(MongoDatabase db, ClientSession session, ItemBlockGameCollectionData set_data, Integer where_action_price_job, String where_id_name) {
        try {

            if (cache_data != null) {
                ItemBlockGameCollectionData result = cache_data.get(set_data.getIdName());
                if (result != null && result.getActionPriceJob() != null && result.getActionPriceJob().contains(where_action_price_job)) {
                    return DataBaseResultPair.of(true, result);
                }
                return DataBaseResultPair.of(false, null);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.and(Filters.in("action_price_job", where_action_price_job), Filters.eq("id_name", where_id_name))).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new ItemBlockGameCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findTagsMany(MongoDatabase db, List<String> where_tags_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getTagsList() != null && item.getTagsList().contains(where_tags_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.in("tags_list", where_tags_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findTagsMany(MongoDatabase db, ClientSession session, List<String> where_tags_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getTagsList() != null && item.getTagsList().contains(where_tags_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.in("tags_list", where_tags_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findTagsMany(MongoDatabase db, ItemBlockGameCollectionData set_data, List<String> where_tags_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getTagsList() != null && item.getTagsList().contains(where_tags_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(Filters.in("tags_list", where_tags_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
    public static DataBaseResultPair<Boolean, List<ItemBlockGameCollectionData>> findTagsMany(MongoDatabase db, ClientSession session, ItemBlockGameCollectionData set_data, List<String> where_tags_list) {
        try {

            if (cache_data != null) {
                List<ItemBlockGameCollectionData> resultList = cache_data.values().stream()
                        .filter(item -> item.getTagsList() != null && item.getTagsList().contains(where_tags_list))
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
            }
            MongoCollection<Document> collection = db.getCollection("item_block_game");
            FindIterable<Document> results = collection.find(session, Filters.in("tags_list", where_tags_list));
            List<ItemBlockGameCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new ItemBlockGameCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }
}
