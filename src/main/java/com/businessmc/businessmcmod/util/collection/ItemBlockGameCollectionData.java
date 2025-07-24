package com.businessmc.businessmcmod.util.collection;

import org.bson.Document;
import java.util.List;

public class ItemBlockGameCollectionData {
    private String id_name; // ID��
    private String name_lang; // ���{�ꖼ
    private Double rate; // ���[�g
    private Double price; // ���i
    private List<Integer> recipe_create_job_list; // ����E��job���X�g
    private List<Integer> action_price_job; // �A�N�V���������Ƃ�������W���u
    private List<String> tags_list; // �^�O���X�g

    public ItemBlockGameCollectionData() {
    }

    public ItemBlockGameCollectionData(Document doc) {        this.id_name = doc.getString("id_name");
        this.name_lang = doc.getString("name_lang");
        this.rate = doc.getDouble("rate");
        this.price = doc.getDouble("price");
        this.recipe_create_job_list = (List<Integer>) doc.get("recipe_create_job_list");
        this.action_price_job = (List<Integer>) doc.get("action_price_job");
        this.tags_list = (List<String>) doc.get("tags_list");
    }

    public Document toDocument() {        Document doc = new Document();
        doc.append("id_name", this.id_name);
        doc.append("name_lang", this.name_lang);
        doc.append("rate", this.rate);
        doc.append("price", this.price);
        doc.append("recipe_create_job_list", this.recipe_create_job_list);
        doc.append("action_price_job", this.action_price_job);
        doc.append("tags_list", this.tags_list);
        return doc;
    }


    public String getIdName() {
        return id_name;
    }

    public String getNameLang() {
        return name_lang;
    }

    public Double getRate() {
        return rate;
    }

    public Double getPrice() {
        return price;
    }

    public List<Integer> getRecipeCreateJobList() {
        return recipe_create_job_list;
    }

    public List<Integer> getActionPriceJob() {
        return action_price_job;
    }

    public List<String> getTagsList() {
        return tags_list;
    }

    public void setIdName(String id_name) {
        this.id_name = id_name;
    }

    public void setNameLang(String name_lang) {
        this.name_lang = name_lang;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setRecipeCreateJobList(List<Integer> recipe_create_job_list) {
        this.recipe_create_job_list = recipe_create_job_list;
    }

    public void setActionPriceJob(List<Integer> action_price_job) {
        this.action_price_job = action_price_job;
    }

    public void setTagsList(List<String> tags_list) {
        this.tags_list = tags_list;
    }
}
