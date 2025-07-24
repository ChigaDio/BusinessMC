package com.businessmc.businessmcmod.util.collection;


import org.bson.Document;
import java.util.List;

public class UserGamePlayerCollectionData {
    private String player_id; // �v���C���[ID
    private String player_name; // �v���C���[��
    private Double balance; // �c��
    private Integer job_id; // �E��ID

    public UserGamePlayerCollectionData() {
    }

    public UserGamePlayerCollectionData(Document doc) {
        this.player_id = doc.getString("player_id");
        this.player_name = doc.getString("player_name");
        this.balance = doc.getDouble("balance");
        this.job_id = doc.getInteger("job_id");
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.append("player_id", this.player_id);
        doc.append("player_name", this.player_name);
        doc.append("balance", this.balance);
        doc.append("job_id", this.job_id);
        return doc;
    }


    public String getPlayerId() {
        return player_id;
    }

    public String getPlayerName() {
        return player_name;
    }

    public Double getBalance() {
        return balance;
    }

    public Integer getJobId() {
        return job_id;
    }

    public void setPlayerId(String player_id) {
        this.player_id = player_id;
    }

    public void setPlayerName(String player_name) {
        this.player_name = player_name;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setJobId(Integer job_id) {
        this.job_id = job_id;
    }
}
