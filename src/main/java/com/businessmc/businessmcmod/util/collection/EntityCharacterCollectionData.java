package com.businessmc.businessmcmod.util.collection;

import com.businessmc.businessmcmod.util.collection.details.RangePos;
import com.businessmc.businessmcmod.util.collection.details.Vector3;
import org.bson.Document;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class EntityCharacterCollectionData {
    private Integer id; // ID
    private String name; // ���O
    private Integer type; // �^�C�v(0:���Q 1:�G)
    private Boolean is_fixed; // �Œ�t���O
    private Boolean is_invincible; // ���G�t���O
    private Double health; // �̗�
    private RangePos range_move = new RangePos(); // �͈͈ړ�(�Ȃ���Ύ����ړ�)
    private Vector3 spawn_pos = new Vector3(); // �o�����W(�Ȃ���΂ǂ��ł��o��)

    public EntityCharacterCollectionData() {
    }

    public EntityCharacterCollectionData(Document doc) {        this.id = doc.getInteger("id");
        this.name = doc.getString("name");
        this.type = doc.getInteger("type");
        this.is_fixed = doc.getBoolean("is_fixed");
        this.is_invincible = doc.getBoolean("is_invincible");
        this.health = doc.getDouble("health");

        Object obj = doc.get("range_move");
        if (obj instanceof Document docnest) {
            this.range_move = new RangePos(docnest);
        } else {
            this.range_move = new RangePos();
        }
        obj = doc.get("start_range");
        if (obj instanceof Document docnest) {
            this.spawn_pos = new Vector3(docnest);
        } else {
            this.spawn_pos = new Vector3();
        }



    }

    public Document toDocument() {        Document doc = new Document();
        doc.append("id", this.id);
        doc.append("name", this.name);
        doc.append("type", this.type);
        doc.append("is_fixed", this.is_fixed);
        doc.append("is_invincible", this.is_invincible);
        doc.append("health", this.health);
        doc.append("range_move", this.range_move.toDocument());
        doc.append("spawn_pos", this.spawn_pos.toDocument());
        return doc;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public Boolean getIsFixed() {
        return is_fixed;
    }

    public Boolean getIsInvincible() {
        return is_invincible;
    }

    public Double getHealth() {
        return health;
    }

    public RangePos getRangeMove() {
        return range_move;
    }

    public Vector3 getSpawnPos() {
        return spawn_pos;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setIsFixed(Boolean is_fixed) {
        this.is_fixed = is_fixed;
    }

    public void setIsInvincible(Boolean is_invincible) {
        this.is_invincible = is_invincible;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

    public void setRangeMove(RangePos range_move) {
        this.range_move = range_move;
    }

    public void setSpawnPos(Vector3 spawn_pos) {
        this.spawn_pos = spawn_pos;
    }
}
