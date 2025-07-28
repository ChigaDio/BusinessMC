package com.businessmc.businessmcmod.util.collection.details;

import org.bson.Document;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class ShopItem {
    private String item_id_name; // ItemBlockGame��ID��
    private Double price; // ���i
    private Double price_rate; // ���i���[�g(�{��)
    private Boolean is_base_price_calculation; // ���̃A�C�e���̉��i���v�Z�ɂ���邩

    public ShopItem() {
    }

    public ShopItem(Document doc) {        this.item_id_name = doc.getString("item_id_name");
        this.price = doc.getDouble("price");
        this.price_rate = doc.getDouble("price_rate");
        this.is_base_price_calculation = doc.getBoolean("is_base_price_calculation");
    }

    public Document toDocument() {        Document doc = new Document();
        doc.append("item_id_name", this.item_id_name);
        doc.append("price", this.price);
        doc.append("price_rate", this.price_rate);
        doc.append("is_base_price_calculation", this.is_base_price_calculation);
        return doc;
    }


    public String getItemIdName() {
        return item_id_name;
    }

    public Double getPrice() {
        return price;
    }

    public Double getPriceRate() {
        return price_rate;
    }

    public Boolean getIsBasePriceCalculation() {
        return is_base_price_calculation;
    }

    public void setItemIdName(String item_id_name) {
        this.item_id_name = item_id_name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setPriceRate(Double price_rate) {
        this.price_rate = price_rate;
    }

    public void setIsBasePriceCalculation(Boolean is_base_price_calculation) {
        this.is_base_price_calculation = is_base_price_calculation;
    }
}
