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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserGamePlayerCollectionDb {
    public static final String collection_name = "user_game_player";
    public static List<UserGamePlayerCollectionData> cache_data;

    public static void MemoryCacheUserGamePlayerCollectionData(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(collection_name);
        FindIterable<Document> results = collection.find();
        cache_data = new ArrayList<>();
        for (Document doc : results) {
            cache_data.add(new UserGamePlayerCollectionData(doc));
        }
    }

    public static void createIndexes(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(collection_name);
        collection.createIndex(Indexes.ascending("player_id"), new IndexOptions().unique(true));
    }

    public static boolean bulkInsertUserGamePlayerCollectionData(MongoDatabase db, List<UserGamePlayerCollectionData> dataList) {
        try {
            MongoCollection<Document> collection = db.getCollection(collection_name);
            List<Document> documents = new ArrayList<>();
            for (UserGamePlayerCollectionData data : dataList) {
                documents.add(data.toDocument());
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


    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> findPlayerGameDataOne(MongoDatabase db, String where_player_id) {
        try {
            if (cache_data != null) {

                List<UserGamePlayerCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getPlayerId(), where_player_id);
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            FindIterable<Document> results = collection.find(Filters.eq("player_id", where_player_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new UserGamePlayerCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> findPlayerGameDataOne(MongoDatabase db, ClientSession session, String where_player_id) {
        try {
            if (cache_data != null) {

                List<UserGamePlayerCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getPlayerId(), where_player_id);
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            FindIterable<Document> results = collection.find(session, Filters.eq("player_id", where_player_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new UserGamePlayerCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> findPlayerGameDataOne(MongoDatabase db, UserGamePlayerCollectionData set_data) {
        try {
            if (cache_data != null) {

                List<UserGamePlayerCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getPlayerId(), set_data.getPlayerId());
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            FindIterable<Document> results = collection.find(Filters.eq("player_id", set_data.getPlayerId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new UserGamePlayerCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> findPlayerGameDataOne(MongoDatabase db, ClientSession session, UserGamePlayerCollectionData set_data) {
        try {
            if (cache_data != null) {

                List<UserGamePlayerCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getPlayerId(), set_data.getPlayerId());
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            FindIterable<Document> results = collection.find(session, Filters.eq("player_id", set_data.getPlayerId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new UserGamePlayerCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> addBalance(MongoDatabase db, String where_player_id, Double set_balance, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(Filters.eq("player_id", where_player_id), Updates.inc("balance", set_balance));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(Filters.eq("player_id", where_player_id)).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> addBalance(MongoDatabase db, ClientSession session, String where_player_id, Double set_balance, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(session, Filters.eq("player_id", where_player_id), Updates.inc("balance", set_balance));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(session, Filters.eq("player_id", where_player_id)).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> addBalance(MongoDatabase db, UserGamePlayerCollectionData set_data, Double set_balance, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(Filters.eq("player_id", set_data.getPlayerId()), Updates.inc("balance", set_balance));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(Filters.eq("player_id", set_data.getPlayerId())).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> addBalance(MongoDatabase db, ClientSession session, UserGamePlayerCollectionData set_data, Double set_balance, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(session, Filters.eq("player_id", set_data.getPlayerId()), Updates.inc("balance", set_balance));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(session, Filters.eq("player_id", set_data.getPlayerId())).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> changeJobID(MongoDatabase db, String where_player_id, Integer set_job_id, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(Filters.eq("player_id", where_player_id), Updates.set("job_id", set_job_id));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(Filters.eq("player_id", where_player_id)).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> changeJobID(MongoDatabase db, ClientSession session, String where_player_id, Integer set_job_id, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(session, Filters.eq("player_id", where_player_id), Updates.set("job_id", set_job_id));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(session, Filters.eq("player_id", where_player_id)).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> changeJobID(MongoDatabase db, UserGamePlayerCollectionData set_data, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(Filters.eq("player_id", set_data.getPlayerId()), Updates.set("job_id", set_data.getJobId()));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(Filters.eq("player_id", set_data.getPlayerId())).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> changeJobID(MongoDatabase db, ClientSession session, UserGamePlayerCollectionData set_data, boolean memory_update) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            UpdateResult result = collection.updateOne(session, Filters.eq("player_id", set_data.getPlayerId()), Updates.set("job_id", set_data.getJobId()));
            if (memory_update && result.getModifiedCount() > 0 && cache_data != null && "player_id" != null) {
                Document updatedDoc = collection.find(session, Filters.eq("player_id", set_data.getPlayerId())).first();
                if (updatedDoc != null) {
                    UserGamePlayerCollectionData updatedData = new UserGamePlayerCollectionData(updatedDoc);
                    cache_data.removeIf(item -> Objects.equals(item.getPlayerId(), updatedData.getPlayerId()));
                    cache_data.add(updatedData);
                }
            }
            return DataBaseResultPair.of(result.getModifiedCount() > 0, null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
}
