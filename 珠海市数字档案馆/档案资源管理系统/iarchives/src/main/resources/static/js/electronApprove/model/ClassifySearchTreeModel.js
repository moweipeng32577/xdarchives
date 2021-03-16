/**
 * Created by Administrator on 2020/7/17.
 */


Ext.define('ElectronApprove.model.ClassifySearchTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});
