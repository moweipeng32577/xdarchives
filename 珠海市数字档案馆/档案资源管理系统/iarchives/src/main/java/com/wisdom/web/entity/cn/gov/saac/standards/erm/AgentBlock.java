package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"agent", "agentRelation"})
@XmlRootElement(name="agent_block")
public class AgentBlock
{

    @XmlElement(required=true)
    protected List<Agent> agent;

    @XmlElement(name="agent_relation")
    protected List<AgentRelation> agentRelation;

    public List<Agent> getAgent()
    {
        if (this.agent == null) {
            this.agent = new ArrayList();
        }
        return this.agent;
    }

    public List<AgentRelation> getAgentRelation()
    {
        if (this.agentRelation == null) {
            this.agentRelation = new ArrayList();
        }
        return this.agentRelation;
    }
}
