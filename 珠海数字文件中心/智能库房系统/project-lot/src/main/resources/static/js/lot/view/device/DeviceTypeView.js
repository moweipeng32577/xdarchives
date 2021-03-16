Ext.define('Lot.view.device.DeviceTypeView',{
    extend:'Ext.grid.Panel',
    xtype:'DeviceTypeView',
    store:'DeviceTypeStore',
    tbar:[{text:'增加',itemId:'deviceTypeAddBtn'},{text:'修改',itemId:'deviceTypemodifyBtn'},{text:'删除',itemId:'deviceTypedelBtn'}],
    selType:'checkboxmodel',
    columns:[
        {xtype: 'rownumberer', align: 'center', width:40},
        {text: '设备类型名称', dataIndex: 'typeName', flex: 1},
        {text: '设备类型编码', dataIndex: 'typeCode', flex: 1},
        {text: '设备平面图', dataIndex: 'typeMap', flex: 2}
    ],
    listeners : {
        'render' : function(view) {
            var store = view.getStore();
            if(store.getCount()>0){
                var record = view.getStore().getAt(0);
                view.getSelectionModel().select(record);
            }
        },
        'select':function (model,record) {
            var devicePanel = model.view.up('[itemId = devicePanel]');
            var DeviceGridView = devicePanel.down('DeviceGridView');
            var DeviceGridViewStore = DeviceGridView.getStore().load({
                params:{typeCode:record.get('typeCode')},
            });

        }
    }
});