package com.businessmc.businessmcmod.util.collection.transaction;

import com.mongodb.client.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class TransactionExecutor {

    private final MongoClient client;
    private final ExecutorService executorService;

    // コンストラクタ
    public TransactionExecutor(MongoClient client) {
        this(client, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    public TransactionExecutor(MongoClient client, ExecutorService executorService) {
        this.client = client;
        this.executorService = executorService;
    }

    @FunctionalInterface
    public interface TransactionLogic {
        TransactionResult execute(MongoDatabase db, ClientSession session) throws Exception;
    }

    // ✅ 同期処理（リトライ対応）
    public TransactionResult runTransaction(String dbName, TransactionLogic logic) {
        int maxRetries = 3;
        int attempt = 0;

        while (true) {
            attempt++;
            try (ClientSession session = client.startSession()) {
                session.startTransaction();
                MongoDatabase db = client.getDatabase(dbName);
                TransactionResult result;

                try {
                    result = logic.execute(db, session);
                    if (result.isSuccess()) {
                        session.commitTransaction();
                    } else {
                        session.abortTransaction();
                    }
                    return result;

                } catch (Exception e) {
                    session.abortTransaction();
                    if (attempt >= maxRetries) {
                        return TransactionResult.failure("Transaction failed after " + attempt + " attempts: " + e.getMessage());
                    }
                    try { Thread.sleep(100 * attempt); } catch (InterruptedException ignored) {}
                }
            }
        }
    }

    // ✅ 非同期処理（タイムアウト付き、リトライ付き）
    public CompletableFuture<TransactionResult> runTransactionAsync(
            String dbName,
            TransactionLogic logic,
            long timeoutMillis
    ) {
        Supplier<TransactionResult> task = () -> runTransaction(dbName, logic);
        return CompletableFuture.supplyAsync(task, executorService)
                .orTimeout(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    // 簡易版: timeout指定なし
    public CompletableFuture<TransactionResult> runTransactionAsync(String dbName, TransactionLogic logic) {
        return runTransactionAsync(dbName, logic, 10_000); // デフォルト10秒タイムアウト
    }

    public void shutdownExecutor() {
        executorService.shutdown();
    }
}
