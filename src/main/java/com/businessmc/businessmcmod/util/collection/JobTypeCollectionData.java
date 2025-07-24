package com.businessmc.businessmcmod.util.collection;

import org.bson.Document;
import java.util.List;

public class JobTypeCollectionData {
    private Integer job_id; // �E��ID
    private Double sell_ratio; // �����{��
    private String job_name; // �E�Ɩ�

    public JobTypeCollectionData() {
    }

    public JobTypeCollectionData(Document doc) {
        this.job_id = doc.getInteger("job_id");
        this.sell_ratio = doc.getDouble("sell_ratio");
        this.job_name = doc.getString("job_name");
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.append("job_id", this.job_id);
        doc.append("sell_ratio", this.sell_ratio);
        doc.append("job_name", this.job_name);
        return doc;
    }


    public Integer getJobId() {
        return job_id;
    }

    public Double getSellRatio() {
        return sell_ratio;
    }

    public String getJobName() {
        return job_name;
    }

    public void setJobId(Integer job_id) {
        this.job_id = job_id;
    }

    public void setSellRatio(Double sell_ratio) {
        this.sell_ratio = sell_ratio;
    }

    public void setJobName(String job_name) {
        this.job_name = job_name;
    }
}



