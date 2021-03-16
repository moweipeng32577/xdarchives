/**
 * Created by yl on 2017/11/29.
 */
Ext.define('Destroy.model.DestructionBillDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'ugid'},
        {name: 'retention', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'filedate', type: 'string'}
    ]
});