/**
 * Created by tanly on 2017/11/7 0007.
 */
Ext.define('Codesetting.model.CodesettingJsonModel', {
    extend: 'Ext.data.Model',
    xtype: 'codesettingJsonModel',
    fields: [{name: "fieldname", type: "string"},
        {name: "fieldcode", type: "string"}]
});
