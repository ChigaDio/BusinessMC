package com.businessmc.businessmcmod.util.collection.details;

import org.bson.Document;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class RangePos {
    private Vector3 start_range = new Vector3(); // �n�_
    private Vector3 end_range= new Vector3(); // �I�_

    public RangePos() {
    }

    public RangePos(Document doc) {
        Object startObj = doc.get("start_range");
        if (startObj instanceof Document startDoc) {
            this.start_range = new Vector3(startDoc);
        } else {
            this.start_range = new Vector3(); // または null を許容するなら null
        }
        Object endObj = doc.get("end_range");
        if (endObj instanceof Document startDoc) {
            this.end_range = new Vector3(startDoc);
        } else {
            this.end_range = new Vector3(); // または null を許容するなら null
        }
    }

    public Document toDocument() {        Document doc = new Document();
        start_range = new Vector3();
        end_range = new Vector3();
        doc.append("start_range", this.start_range.toDocument());
        doc.append("end_range", this.end_range.toDocument());
        return doc;
    }


    public Vector3 getStartRange() {
        return start_range;
    }

    public Vector3 getEndRange() {
        return end_range;
    }

    public void setStartRange(Vector3 start_range) {
        this.start_range = start_range;
    }

    public void setEndRange(Vector3 end_range) {
        this.end_range = end_range;
    }
}
