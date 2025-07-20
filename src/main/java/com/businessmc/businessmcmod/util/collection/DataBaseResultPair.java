package com.businessmc.businessmcmod.util.collection;

import java.util.Objects;


/// データテーブル用
public class DataBaseResultPair<L, R> {
    private final L success;
    private final R result;

    // プライベートコンストラクタ
    private DataBaseResultPair(L left, R result) {
        this.success = left;
        this.result = result;
    }

    // 静的ファクトリメソッド
    public static <L, R> DataBaseResultPair<L, R> of(L left, R right) {
        return new DataBaseResultPair<>(left, right);
    }

    // ゲッター
    public L isSuccess() {
        return success;
    }

    public R getResult() {
        return result;
    }

    // 必要に応じてtoString, equals, hashCodeをオーバーライド
    @Override
    public String toString() {
        return "(" + success + ", " + result + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataBaseResultPair<?, ?> that = (DataBaseResultPair<?, ?>) o;
        return Objects.equals(success, that.success) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, result);
    }
}