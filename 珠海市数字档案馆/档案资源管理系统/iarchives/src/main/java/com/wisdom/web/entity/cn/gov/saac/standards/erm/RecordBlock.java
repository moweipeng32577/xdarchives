package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import com.wisdom.web.entity.sip.Document;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"document", "documentRelation"})
@XmlRootElement(name="record_block")
public class RecordBlock
{

    @XmlElement(required=true)
    protected List<Document> document;

    @XmlElement(name="document_relation")
    protected List<DocumentRelation> documentRelation;

    public List<Document> getDocument()
    {
        if (this.document == null) {
            this.document = new ArrayList();
        }
        return this.document;
    }

    public List<DocumentRelation> getDocumentRelation()
    {
        if (this.documentRelation == null) {
            this.documentRelation = new ArrayList();
        }
        return this.documentRelation;
    }
}
