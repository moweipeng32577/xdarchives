package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"agentId1", "agentId2", "relation"})
@XmlRootElement(name="agent_relation")
public class AgentRelation
{

    @XmlElement(name="agent_id1", required=true)
    protected String agentId1;

    @XmlElement(name="agent_id2", required=true)
    protected String agentId2;

    @XmlElement(required=true)
    protected String relation;

    public String getAgentId1()
    {
        return this.agentId1;
    }

    public void setAgentId1(String value)
    {
        this.agentId1 = value;
    }

    public String getAgentId2()
    {
        return this.agentId2;
    }

    public void setAgentId2(String value)
    {
        this.agentId2 = value;
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