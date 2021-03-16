package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"businessEntity"})
@XmlRootElement(name="business_block")
public class BusinessBlock
{

    @XmlElement(name="business_entity", required=true)
    protected List<BusinessEntity> businessEntity;

    public List<BusinessEntity> getBusinessEntity()
    {
        if (this.businessEntity == null) {
            this.businessEntity = new ArrayList();
        }
        return this.businessEntity;
    }
}