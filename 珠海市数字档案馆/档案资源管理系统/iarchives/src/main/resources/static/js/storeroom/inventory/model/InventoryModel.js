/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Inventory.model.InventoryModel',{
    extend:'Ext.data.Model',
    fields: [
        /*{name: 'id', type: 'string',mapping:'checkid'},*/
        {name: 'checknum', type: 'string'},
        {name: 'checktime', type: 'string'},
        {name: 'checkuser', type: 'string'},
        {name: 'rangetype', type: 'string'},
        {name: 'description', type: 'string'}
    ]
});