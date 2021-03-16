package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"agentId", "agentType", "agentName", "organizationCode", "positionName"})
@XmlRootElement(name="agent")
public class Agent
{

    @XmlElement(name="agent_id", required=true)
    protected String agentId;

    @XmlElement(name="agent_type")
    protected String agentType;

    @XmlElement(name="agent_name", required=true)
    protected String agentName;

    @XmlElement(name="organization_code")
    protected String organizationCode;

    @XmlElement(name="position_name")
    protected String positionName;

    public String getAgentId()
    {
        return this.agentId;
    }

    public void setAgentId(String value)
    {
        this.agentId = value;
    }

    public String getAgentType()
    {
        return this.agentType;
    }

    public void setAgentType(String value)
    {
        this.agentType = value;
    }

    public String getAgentName()
    {
        return this.agentName;
    }

    public void setAgentName(String value)
    {
        this.agentName = value;
    }

    public String getOrganizationCode()
    {
        return this.organizationCode;
    }

    public void setOrganizationCode(String value)
    {
        this.organizationCode = value;
    }

    public String getPositionName()
    {
        return this.positionName;
    }

    public void setPositionName(String value)
    {
        this.positionName = value;
    }
}
