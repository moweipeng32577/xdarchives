/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Inventory.model.InventoryResultModel',{
    extend:'Ext.data.Model',
    fields: [
        /*{name: 'id', type: 'string',mapping:'checkid'},*/
        {name: 'chipcode', type: 'string'},
        {name: 'resulttype', type: 'string'},
        {name: 'checkid', type: 'string'},
        {name: 'storageid', type: 'string'}
    ]
});