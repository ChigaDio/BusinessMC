package com.businessmc.businessmcmod.util.collection.transaction;

public class TransactionResult {
    private final boolean success;
    private final String message;

    public TransactionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    public static TransactionResult success(String message) {
        return new TransactionResult(true, message);
    }

    public static TransactionResult failure(String message) {
        return new TransactionResult(false, message);
    }

    public  static TransactionResult flagReturn(Boolean flag,String message)
    {
        return flag == true ? success(message) : failure(message);
    }


    @Override
    public String toString() {
        return "TransactionResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
