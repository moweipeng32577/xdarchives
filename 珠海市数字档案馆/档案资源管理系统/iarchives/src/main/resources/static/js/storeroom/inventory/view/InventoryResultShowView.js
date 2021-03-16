/**
 * Created by tanly on 2017/12/6 0006.
 */

var store1 = Ext.create("Ext.data.Store", {
    fields: [],
    data: []
});
Ext.define('Inventory.view.InventoryResultShowView', {

    extend:'Ext.window.Window',
    xtype:'inventoryResultShowView',
    layout:'border',
    items:[{
        region:'north',
        buttons:[{text:'修改',itemId:'modi'},{text:'移库',itemId:'shelmove'}]
    },{
        region:'center',
        xtype:'basicgrid',
        itemId:'inventoryResultShowGrid',
        //selType : 'rowmodel',//默认checkboxmodel 是选择框
        store:'InventoryResultStore',
        hasSearchBar:false,
        columns: [
            {text: '存档条码', dataIndex: 'chipcode', flex: 2, menuDisabled: true},
            /*{text: '异常类型', dataIndex: 'resulttype', flex: 2, menuDisabled: true},*/
            {
                text: '异常类型',
                dataIndex: 'resulttype',
                flex: 1,
                menuDisabled: true,
                renderer: function (value) {
                    var str=value;
                    if(str=='1'){
                        str='盘点多出';
                    }else if(str=='2'){
                        str='未盘点到';
                    }else if(str=='3'){
                        str='状态异常';
                    }else if(str=='4'){
                        str='位置异常';
                    }
                    return str;
                }
            },
            {
                text: '盘点编号',
                dataIndex: 'resultid',
                flex: 2,
                menuDisabled: true
            }/*,
            {
                text: '库存编号',
                dataIndex: 'storage',
                flex: 1,
                menuDisabled: true,
                renderer: function (value) {
                    return value['stid'];
                }
            }*/
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