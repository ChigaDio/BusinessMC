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

public class JobTypeCollectionDb {
    public static final String collection_name = "job_type";
    public static List<JobTypeCollectionData> cache_data;

    public static void MemoryCacheJobTypeCollectionData(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(collection_name);
        FindIterable<Document> results = collection.find();
        cache_data = new ArrayList<>();
        for (Document doc : results) {
            cache_data.add(new JobTypeCollectionData(doc));
        }
    }

    public static void createIndexes(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(collection_name);
        collection.createIndex(Indexes.ascending("job_id"), new IndexOptions().unique(true));
    }

    public static boolean bulkInsertJobTypeCollectionData(MongoDatabase db, List<JobTypeCollectionData> dataList) {
        try {
            MongoCollection<Document> collection = db.getCollection(collection_name);
            List<Document> documents = new ArrayList<>();
            for (JobTypeCollectionData data : dataList) {
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


    public static DataBaseResultPair<Boolean, List<JobTypeCollectionData>> findAllJobTypeMany(MongoDatabase db) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;

                            return match;
                        })
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(false, null);

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(new Document());
            List<JobTypeCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new JobTypeCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }

    public static DataBaseResultPair<Boolean, List<JobTypeCollectionData>> findAllJobTypeMany(MongoDatabase db, ClientSession session) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;

                            return match;
                        })
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(false, null);

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(session, new Document());
            List<JobTypeCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new JobTypeCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }

    public static DataBaseResultPair<Boolean, List<JobTypeCollectionData>> findAllJobTypeMany(MongoDatabase db, JobTypeCollectionData set_data) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;

                            return match;
                        })
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(false, null);

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(new Document());
            List<JobTypeCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new JobTypeCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }

    public static DataBaseResultPair<Boolean, List<JobTypeCollectionData>> findAllJobTypeMany(MongoDatabase db, ClientSession session, JobTypeCollectionData set_data) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;

                            return match;
                        })
                        .collect(Collectors.toList());
                return DataBaseResultPair.of(false, null);

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(session, new Document());
            List<JobTypeCollectionData> resultList = new ArrayList<>(); for (Document doc : results) { resultList.add(new JobTypeCollectionData(doc)); } return DataBaseResultPair.of(!resultList.isEmpty(), resultList);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, Collections.emptyList());
        }
    }

    public static DataBaseResultPair<Boolean, JobTypeCollectionData> findJobIdOne(MongoDatabase db, Integer where_job_id) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getJobId(), where_job_id);
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(Filters.eq("job_id", where_job_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new JobTypeCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, JobTypeCollectionData> findJobIdOne(MongoDatabase db, ClientSession session, Integer where_job_id) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getJobId(), where_job_id);
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(session, Filters.eq("job_id", where_job_id)).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new JobTypeCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, JobTypeCollectionData> findJobIdOne(MongoDatabase db, JobTypeCollectionData set_data) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getJobId(), set_data.getJobId());
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(Filters.eq("job_id", set_data.getJobId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new JobTypeCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }

    public static DataBaseResultPair<Boolean, JobTypeCollectionData> findJobIdOne(MongoDatabase db, ClientSession session, JobTypeCollectionData set_data) {
        try {
            if (cache_data != null) {

                List<JobTypeCollectionData> resultList = cache_data.stream()
                        .filter(item -> {
                            boolean match = true;
                            match &= Objects.equals(item.getJobId(), set_data.getJobId());
                            return match;
                        })
                        .collect(Collectors.toList());
                return resultList.isEmpty() ? DataBaseResultPair.of(false, null) : DataBaseResultPair.of(true, resultList.getFirst());

            }
            MongoCollection<Document> collection = db.getCollection("job_type");
            FindIterable<Document> results = collection.find(session, Filters.eq("job_id", set_data.getJobId())).limit(1);
            Document doc = results.first(); return DataBaseResultPair.of(doc != null, doc != null ? new JobTypeCollectionData(doc) : null);
        } catch (Exception e) {
            return DataBaseResultPair.of(false, null);
        }
    }
}
