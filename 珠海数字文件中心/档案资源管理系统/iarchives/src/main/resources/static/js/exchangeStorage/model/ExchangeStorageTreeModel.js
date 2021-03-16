/**
 * Created by yl on 2018/3/19.
 */
Ext.define('ExchangeStorage.model.ExchangeStorageTreeModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});