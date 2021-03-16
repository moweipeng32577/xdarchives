/**
 * Created by Administrator on 2019/10/25.
 */


Ext.define('TransforAuditDeal.model.AuditDocModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping:"docid"},
        {name: "transdesc", type: "string"},
        {name: "transuser", type: "string"},
        {name: "transorgan", type: "string"},
        {name: "transcount", type: "string"},
        {name: "transdate", type: "string"},
        {name: "nodefullname", type: "string"}
        ]
});