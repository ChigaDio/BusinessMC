package com.businessmc.businessmcmod.util.collection;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserGamePlayerCollectionDb {
    @SuppressWarnings({"java:S3776", "unused"})
    public static boolean bulkInsertUserGamePlayer(MongoDatabase db, List<UserGamePlayerCollectionData> dataList) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            List<Document> documents = new ArrayList<>();
            for (UserGamePlayerCollectionData data : dataList){
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
    @SuppressWarnings({"java:S3776", "unused"})
    public static boolean bulkUpdateUserGamePlayer(MongoDatabase db, List<UserGamePlayerCollectionData> dataList) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            List<WriteModel<Document>> updates = new ArrayList<>();
            for (UserGamePlayerCollectionData data : dataList){
                List<Bson> filters = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    filters.add(Filters.eq("player_id", data.getPlayer_id()));
                }
                if (data.isPlayer_nameFlag()) {
                    filters.add(Filters.eq("player_name", data.getPlayer_name()));
                }
                if (data.isBalanceFlag()) {
                    filters.add(Filters.eq("balance", data.getBalance()));
                }
                Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                List<Bson> updateOps = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    updateOps.add(Updates.set("player_id", data.getPlayer_id()));
                }
                if (data.isPlayer_nameFlag()) {
                    updateOps.add(Updates.set("player_name", data.getPlayer_name()));
                }
                if (data.isBalanceFlag()) {
                    updateOps.add(Updates.set("balance", data.getBalance()));
                }
                if (!updateOps.isEmpty()) {
                    updates.add(new UpdateManyModel<>(filter, Updates.combine(updateOps)));
                }
            }
            if (!updates.isEmpty()) {
                BulkWriteResult result = collection.bulkWrite(updates);
                return result.getModifiedCount() > 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Asynchronous Bulk Operations
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<Boolean> bulkInsertUserGamePlayerAsync(MongoDatabase db, List<UserGamePlayerCollectionData> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
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
        });
    }
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<Boolean> bulkUpdateUserGamePlayerAsync(MongoDatabase db, List<UserGamePlayerCollectionData> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
                List<WriteModel<Document>> updates = new ArrayList<>();
                for (UserGamePlayerCollectionData data : dataList) {
                    List<Bson> filters = new ArrayList<>();
                    if (data.isPlayer_idFlag()) {
                        filters.add(Filters.eq("player_id", data.getPlayer_id()));
                    }
                    if (data.isPlayer_nameFlag()) {
                        filters.add(Filters.eq("player_name", data.getPlayer_name()));
                    }
                    if (data.isBalanceFlag()) {
                        filters.add(Filters.eq("balance", data.getBalance()));
                    }
                    Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                    List<Bson> updateOps = new ArrayList<>();
                    if (data.isPlayer_idFlag()) {
                        updateOps.add(Updates.set("player_id", data.getPlayer_id()));
                    }
                    if (data.isPlayer_nameFlag()) {
                        updateOps.add(Updates.set("player_name", data.getPlayer_name()));
                    }
                    if (data.isBalanceFlag()) {
                        updateOps.add(Updates.set("balance", data.getBalance()));
                    }
                    if (!updateOps.isEmpty()) {
                        updates.add(new UpdateManyModel<>(filter, Updates.combine(updateOps)));
                    }
                }
                if (!updates.isEmpty()) {
                    BulkWriteResult result = collection.bulkWrite(updates);
                    return result.getModifiedCount() > 0;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        });
    }

    @SuppressWarnings({"java:S3776", "unused"})
    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> findPlayerGameDataOne(MongoDatabase db, String arg1) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            Bson filter = Filters.eq("player_id", arg1);
            Document doc = collection.find(filter).first();
            if (doc == null) {
                return DataBaseResultPair.of(false, null);
            }
            return DataBaseResultPair.of(true, new UserGamePlayerCollectionData(doc));
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    // SQL: SELECT * FROM user_game_player WHERE player_id = arg1 AND
// Generated Java MongoDB Code for method: findPlayerGameData (Single Arguments)
    @SuppressWarnings({"java:S3776", "unused"})
    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> findPlayerGameDataTransactionOne(MongoDatabase db, ClientSession session,String arg1) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            Bson filter = Filters.eq("player_id", arg1);
            Document doc = collection.find(session,filter).first();
            if (doc == null) {
                return DataBaseResultPair.of(false, null);
            }
            return DataBaseResultPair.of(true, new UserGamePlayerCollectionData(doc));
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    @SuppressWarnings({"java:S3776", "unused"})
    public static DataBaseResultPair<Boolean, List<UserGamePlayerCollectionData>> findPlayerGameDataTransactionMany(MongoDatabase db, ClientSession session,String arg1) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            Bson filter = Filters.eq("player_id", arg1);
            FindIterable<Document> results = collection.find(session,filter);
            List<UserGamePlayerCollectionData> resultList = new ArrayList<>();
            for (Document doc : results) {
                resultList.add(new UserGamePlayerCollectionData(doc));
            }
            return resultList.isEmpty() ? DataBaseResultPair.of(false, Collections.emptyList()) : DataBaseResultPair.of(true, resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }

    // Generated Java MongoDB Code for method: findPlayerGameDataNoAutoIndex (Single Arguments)
    @SuppressWarnings({"java:S3776", "unused"})
    public static DataBaseResultPair<Boolean, UserGamePlayerCollectionData> findPlayerGameDataNoAutoIndexTransactionOne(MongoDatabase db, ClientSession session,String arg1) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            Bson filter = Filters.eq("player_id", arg1);
            Document doc = collection.find(session,filter).first();
            if (doc == null) {
                return DataBaseResultPair.of(false, null);
            }
            return DataBaseResultPair.of(true, new UserGamePlayerCollectionData(doc));
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
    @SuppressWarnings({"java:S3776", "unused"})
    public static DataBaseResultPair<Boolean, List<UserGamePlayerCollectionData>> findPlayerGameDataNoAutoIndexTransactionMany(MongoDatabase db, ClientSession session,String arg1) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            Bson filter = Filters.eq("player_id", arg1);
            FindIterable<Document> results = collection.find(session,filter);
            List<UserGamePlayerCollectionData> resultList = new ArrayList<>();
            for (Document doc : results) {
                resultList.add(new UserGamePlayerCollectionData(doc));
            }
            return resultList.isEmpty() ? DataBaseResultPair.of(false, Collections.emptyList()) : DataBaseResultPair.of(true, resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }

    // Generated Java MongoDB Code for method: findPlayerGameDataAsyncWithData (UsersCollectionData Argument, Async)
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<DataBaseResultPair<Boolean, UserGamePlayerCollectionData>> findPlayerGameDataTransactionOneAsyncWithData(MongoDatabase db,ClientSession session, UserGamePlayerCollectionData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
                List<Bson> filters = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    filters.add(Filters.eq("player_id", data.getPlayer_id()));
                }
                Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                Bson whereFilter = Filters.and(Filters.eq("player_id", data.getPlayer_id()));
                Bson combinedFilter = Filters.and(filter, whereFilter);
                Document doc = collection.find(combinedFilter).first();
                if (doc == null) {
                    return DataBaseResultPair.of(false, null);
                }
                return DataBaseResultPair.of(true, new UserGamePlayerCollectionData(doc));
            } catch (Exception e) {
                return DataBaseResultPair.of(false, null);
            }
        });
    }
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<DataBaseResultPair<Boolean, List<UserGamePlayerCollectionData>>> findPlayerGameDataTransactionManyAsyncWithData(MongoDatabase db,ClientSession session, UserGamePlayerCollectionData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
                List<Bson> filters = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    filters.add(Filters.eq("player_id", data.getPlayer_id()));
                }
                Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                Bson whereFilter = Filters.and(Filters.eq("player_id", data.getPlayer_id()));
                Bson combinedFilter = Filters.and(filter, whereFilter);
                FindIterable<Document> results = collection.find(session,combinedFilter);
                List<UserGamePlayerCollectionData> resultList = new ArrayList<>();
                for (Document doc : results) {
                    resultList.add(new UserGamePlayerCollectionData(doc));
                }
                return resultList.isEmpty() ? DataBaseResultPair.of(false, Collections.emptyList()) : DataBaseResultPair.of(true, resultList);
            } catch (Exception e) {
                return DataBaseResultPair.of(false, Collections.emptyList());
            }
        });
    }

    // Generated Java MongoDB Code for method: findPlayerGameDataNoAutoIndexAsyncWithData (UsersCollectionData Argument, Async)
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<DataBaseResultPair<Boolean, UserGamePlayerCollectionData>> findPlayerGameDataNoAutoIndexTransactionOneAsyncWithData(MongoDatabase db,ClientSession session, UserGamePlayerCollectionData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
                List<Bson> filters = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    filters.add(Filters.eq("player_id", data.getPlayer_id()));
                }
                Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                Bson whereFilter = Filters.and(Filters.eq("player_id", data.getPlayer_id()));
                Bson combinedFilter = Filters.and(filter, whereFilter);
                Document doc = collection.find(combinedFilter).first();
                if (doc == null) {
                    return DataBaseResultPair.of(false, null);
                }
                return DataBaseResultPair.of(true, new UserGamePlayerCollectionData(doc));
            } catch (Exception e) {
                return DataBaseResultPair.of(false, null);
            }
        });
    }
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<DataBaseResultPair<Boolean, List<UserGamePlayerCollectionData>>> findPlayerGameDataNoAutoIndexTransactionManyAsyncWithData(MongoDatabase db,ClientSession session, UserGamePlayerCollectionData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
                List<Bson> filters = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    filters.add(Filters.eq("player_id", data.getPlayer_id()));
                }
                Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                Bson whereFilter = Filters.and(Filters.eq("player_id", data.getPlayer_id()));
                Bson combinedFilter = Filters.and(filter, whereFilter);
                FindIterable<Document> results = collection.find(session,combinedFilter);
                List<UserGamePlayerCollectionData> resultList = new ArrayList<>();
                for (Document doc : results) {
                    resultList.add(new UserGamePlayerCollectionData(doc));
                }
                return resultList.isEmpty() ? DataBaseResultPair.of(false, Collections.emptyList()) : DataBaseResultPair.of(true, resultList);
            } catch (Exception e) {
                return DataBaseResultPair.of(false, Collections.emptyList());
            }
        });
    }

    // SQL: UPDATE user_game_player SET balance = balance + arg1? WHERE player_id = arg1 AND
// Generated Java MongoDB Code for method: addBalance (Single Arguments)
    @SuppressWarnings({"java:S3776", "unused"})
    public static boolean addBalanceTransaction(MongoDatabase db, ClientSession session,Double arg1) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            Bson update = Updates.combine(Updates.inc("balance", arg1));
            Bson filter = Filters.eq("player_id", arg1);
            UpdateResult result = collection.updateOne(session,filter, update);
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Generated Java MongoDB Code for method: addBalanceNoAutoIndex (Single Arguments)
    @SuppressWarnings({"java:S3776", "unused"})
    public static boolean addBalanceNoAutoIndexTransaction(MongoDatabase db, ClientSession session,Double arg1) {
        try {
            MongoCollection<Document> collection = db.getCollection("user_game_player");
            Bson update = Updates.combine(Updates.inc("balance", arg1));
            Bson filter = Filters.eq("player_id", arg1);
            UpdateResult result = collection.updateOne(session,filter, update);
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Generated Java MongoDB Code for method: addBalanceAsyncWithData (UsersCollectionData Argument, Async)
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<Boolean> addBalanceTransactionAsyncWithData(MongoDatabase db,ClientSession session, UserGamePlayerCollectionData data, Double arg1) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
                List<Bson> filters = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    filters.add(Filters.eq("player_id", data.getPlayer_id()));
                }
                Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                Bson updateOps = Updates.combine(Updates.set("balance", data.getBalance()));
                Bson whereFilter = Filters.and(Filters.eq("player_id", arg1));
                Bson combinedFilter = Filters.and(filter, whereFilter);
                UpdateResult result = collection.updateOne(session,combinedFilter, updateOps);
                return result.getMatchedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        });
    }

    // Generated Java MongoDB Code for method: addBalanceNoAutoIndexAsyncWithData (UsersCollectionData Argument, Async)
    @SuppressWarnings({"java:S3776", "unused"})
    public static CompletableFuture<Boolean> addBalanceNoAutoIndexTransactionAsyncWithData(MongoDatabase db, ClientSession session, UserGamePlayerCollectionData data, Double arg1) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> collection = db.getCollection("user_game_player");
                List<Bson> filters = new ArrayList<>();
                if (data.isPlayer_idFlag()) {
                    filters.add(Filters.eq("player_id", data.getPlayer_id()));
                }
                Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);
                Bson updateOps = Updates.combine(Updates.set("balance", data.getBalance()));
                Bson whereFilter = Filters.and(Filters.eq("player_id", arg1));
                Bson combinedFilter = Filters.and(filter, whereFilter);
                UpdateResult result = collection.updateOne(session,combinedFilter, updateOps);
                return result.getMatchedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        });
    }
}
