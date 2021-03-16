/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('AcceptDirectory.model.AcceptDetailGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: "id", type: "string"},
        {name: "impuser", type: "string"},
        {name: "imptime", type: "string"},
        {name: "successcount", type: "string"},
        {name: "defeatedcount", type: "string"}
        ]
});
