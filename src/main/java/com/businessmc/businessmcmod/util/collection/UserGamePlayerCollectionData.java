package com.businessmc.businessmcmod.util.collection;


import org.bson.Document;

public class UserGamePlayerCollectionData {
    private String player_id;
    private boolean player_id_flag;
    private String player_name;
    private boolean player_name_flag;
    private Double balance;
    private boolean balance_flag;
    public UserGamePlayerCollectionData() {

        this.player_id_flag = false;

        this.player_name_flag = false;

        this.balance_flag = false;
    }
    public UserGamePlayerCollectionData(Document doc) {
        if (doc.containsKey("player_id")) {
            this.player_id = doc.getString("player_id");
            this.player_id_flag = true;
        } else {
            this.player_id_flag = false;
        }
        if (doc.containsKey("player_name")) {
            this.player_name = doc.getString("player_name");
            this.player_name_flag = true;
        } else {
            this.player_name_flag = false;
        }
        if (doc.containsKey("balance")) {
            this.balance = doc.getDouble("balance");
            this.balance_flag = true;
        } else {
            this.balance_flag = false;
        }
    }
    public String getPlayer_id() {
        return player_id;
    }
    public boolean isPlayer_idFlag() {
        return player_id_flag;
    }
    public void setPlayer_id(String value) {
        this.player_id = value;
        this.player_id_flag = true;
    }
    public String getPlayer_name() {
        return player_name;
    }
    public boolean isPlayer_nameFlag() {
        return player_name_flag;
    }
    public void setPlayer_name(String value) {
        this.player_name = value;
        this.player_name_flag = true;
    }
    public Double getBalance() {
        return balance;
    }
    public boolean isBalanceFlag() {
        return balance_flag;
    }
    public void setBalance(Double value) {
        this.balance = value;
        this.balance_flag = true;
    }
    public Document toDocument() {
        Document doc = new Document();
        if (player_id_flag) {
            doc.append("player_id", player_id);
        }
        if (player_name_flag) {
            doc.append("player_name", player_name);
        }
        if (balance_flag) {
            doc.append("balance", balance);
        }
        return doc;
    }
}
