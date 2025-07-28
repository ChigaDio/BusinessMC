package com.businessmc.businessmcmod.util.collection.details;

import org.bson.Document;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class Vector3 {
    private Double x  =0.0; // x
    private Double y  = 0.0; // y
    private Double z = 0.0; // z

    public Vector3() {
    }

    public Vector3(Document doc) {        this.x = doc.getDouble("x");
        this.y = doc.getDouble("y");
        this.z = doc.getDouble("z");
    }

    public Document toDocument() {        Document doc = new Document();
        doc.append("x", this.x);
        doc.append("y", this.y);
        doc.append("z", this.z);
        return doc;
    }


    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public void setZ(Double z) {
        this.z = z;
    }
}
