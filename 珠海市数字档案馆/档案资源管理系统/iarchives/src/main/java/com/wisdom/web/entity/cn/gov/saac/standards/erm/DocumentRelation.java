package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"fileId1", "fileId2", "relation"})
@XmlRootElement(name="document_relation")
public class DocumentRelation
{

    @XmlElement(name="file_id1", required=true)
    protected String fileId1;

    @XmlElement(name="file_id2", required=true)
    protected String fileId2;

    @XmlElement(required=true)
    protected String relation;

    public String getFileId1()
    {
        return this.fileId1;
    }

    public void setFileId1(String value)
    {
        this.fileId1 = value;
    }

    public String getFileId2()
    {
        return this.fileId2;
    }

    public void setFileId2(String value)
    {
        this.fileId2 = value;
    }

    public String getRelation()
    {
        return this.relation;
    }

    public void setRelation(String value)
    {
        this.relation = value;
    }
}