/**
 * Created by tanly on 2017/12/6 0006.
 */

var store1 = Ext.create("Ext.data.Store", {
    fields: [],
    data: []
});
Ext.define('Inventory.view.InventoryShowView', {

   extend:'Ext.panel.Panel',
    xtype:'inventoryShowView',
    layout:'border',
    items:[{
        region:'north',
        buttons:[{text:'状态异常',itemId:'staErr'},{text:'位置异常',itemId:'shelErr'},{text:'未盘点到',itemId:'lessErr'},{text:'盘点多出',itemId:'moreErr'}]
    },{
        region:'center',
        xtype:'basicgrid',
        itemId:'inventorygrid',
        //selType : 'rowmodel',//默认checkboxmodel 是选择框
        store:'InventoryStore',
        hasSearchBar:false,
        columns: [
            {text: '编号', dataIndex: 'checknum', flex: 1, menuDisabled: true},
            {text: '时间', dataIndex: 'checktime', flex: 1, menuDisabled: true},
            {text: '盘点人', dataIndex: 'checkuser', flex: 1, menuDisabled: true},
            /*{text: '盘点范围', dataIndex: 'rangetype', flex: 1, menuDisabled: true},*/
            {
                text: '盘点范围',
                dataIndex: 'rangetype',
                flex: 1,
                menuDisabled: true,
                renderer: function (value) {
                    var str=value;
                    if(str=='room'){
                        str='整库'
                    }else if(str=='zone'){
                        str='整区'
                    }else if(str=='col'){
                        str='整列'
                    }
                    return str;
                }
            },
            {text: '备注', dataIndex: 'description', flex: 3, menuDisabled: true}
        ]
    }]

   /* extend:'Ext.panel.Panel',
    layout:'fit',
    itemId:'inventoryGrid',
    xtype:'inventoryShowView',
    selType : 'rowmodel',//默认checkboxmodel 是选择框
    //store:store1,
    store:'inventoryStore',
    hasSearchBar:false,
    columns: [
        {text: '编号', dataIndex: 'checknum', flex: 1, menuDisabled: true},
        {text: '时间', dataIndex: 'checktime', flex: 2, menuDisabled: true},
        {text: '盘点人', dataIndex: 'checkuser', flex: 1, menuDisabled: true},
        {text: '范围', dataIndex: 'rangetype', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'description', flex: 2, menuDisabled: true}

    ]*/
});