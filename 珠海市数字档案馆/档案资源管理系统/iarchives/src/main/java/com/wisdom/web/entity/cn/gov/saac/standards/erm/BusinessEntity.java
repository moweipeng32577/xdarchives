package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"businessId", "agentId", "activity", "actionTime"})
@XmlRootElement(name="business_entity")
public class BusinessEntity
{

    @XmlElement(name="business_id", required=true)
    protected String businessId;

    @XmlElement(name="agent_id", required=true)
    protected String agentId;

    @XmlElement(required=true)
    protected String activity;

    @XmlElement(name="action_time", required=true)
    @XmlSchemaType(name="dateTime")
    protected XMLGregorianCalendar actionTime;

    public String getBusinessId()
    {
        return this.businessId;
    }

    public void setBusinessId(String value)
    {
        this.businessId = value;
    }

    public String getAgentId()
    {
        return this.agentId;
    }

    public void setAgentId(String value)
    {
        this.agentId = value;
    }

    public String getActivity()
    {
        return this.activity;
    }

    public void setActivity(String value)
    {
        this.activity = value;
    }

    public XMLGregorianCalendar getActionTime()
    {
        return this.actionTime;
    }

    public void setActionTime(XMLGregorianCalendar value)
    {
        this.actionTime = value;
    }
}
