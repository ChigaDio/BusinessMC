package com.businessmc.businessmcmod.util.collection;

import com.businessmc.businessmcmod.util.collection.details.ShopItem;
import com.businessmc.businessmcmod.util.collection.details.Vector3;
import org.bson.Document;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class BusinessShopCollectionData {
    private Integer id; // ID
    private String shop_name; // �V���b�v��
    private String trader_name; // �X�喼
    private Double base_rate; // ��b���[�g
    private Vector3 fixed_pos; // �Œ�ʒu
    private List<Integer> access_job_list; // �A�N�Z�X�ł���Job
    private List<ShopItem> buy_list; // �w�����X�g
    private List<ShopItem> sale_list; // ���p���X�g

    public BusinessShopCollectionData() {
    }

    public BusinessShopCollectionData(Document doc) {        this.id = doc.getInteger("id");
        this.shop_name = doc.getString("shop_name");
        this.trader_name = doc.getString("trader_name");
        this.base_rate = doc.getDouble("base_rate");
        this.fixed_pos = new Vector3(doc);
        this.access_job_list = doc.getList("access_job_list", Integer.class);
        List<Document> buyDocs = doc.getList("buy_list", Document.class);
        this.buy_list = buyDocs.stream().map(ShopItem::new).collect(Collectors.toList());

        List<Document> saleDocs = doc.getList("sale_list", Document.class);
        this.sale_list = saleDocs.stream().map(ShopItem::new).collect(Collectors.toList());
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.append("id", this.id);
        doc.append("shop_name", this.shop_name);
        doc.append("trader_name", this.trader_name);
        doc.append("base_rate", this.base_rate);
        doc.append("fixed_pos", this.fixed_pos.toDocument());
        doc.append("access_job_list", this.access_job_list);
        List<Document> buyDocs = buy_list.stream().map(ShopItem::toDocument).collect(Collectors.toList());
        doc.append("buy_list", buyDocs);

        List<Document> saleDocs = sale_list.stream().map(ShopItem::toDocument).collect(Collectors.toList());
        doc.append("sale_list", saleDocs);
        return doc;
    }


    public Integer getId() {
        return id;
    }

    public String getShopName() {
        return shop_name;
    }

    public String getTraderName() {
        return trader_name;
    }

    public Double getBaseRate() {
        return base_rate;
    }

    public Vector3 getFixedPos() {
        return fixed_pos;
    }

    public List<Integer> getAccessJobList() {
        return access_job_list;
    }

    public List<ShopItem> getBuyList() {
        return buy_list;
    }

    public List<ShopItem> getSaleList() {
        return sale_list;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    public void setTraderName(String trader_name) {
        this.trader_name = trader_name;
    }

    public void setBaseRate(Double base_rate) {
        this.base_rate = base_rate;
    }

    public void setFixedPos(Vector3 fixed_pos) {
        this.fixed_pos = fixed_pos;
    }

    public void setAccessJobList(List<Integer> access_job_list) {
        this.access_job_list = access_job_list;
    }

    public void setBuyList(List<ShopItem> buy_list) {
        this.buy_list = buy_list;
    }

    public void setSaleList(List<ShopItem> sale_list) {
        this.sale_list = sale_list;
    }
}
