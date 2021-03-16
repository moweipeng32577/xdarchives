Ext.define('Lot.view.deviceArea.DeviceFloorGridView',{
    extend:'Ext.grid.Panel',
    xtype:'DeviceFloorGridView',
    itemId:'floorId',
    store:'FloorStore',
    // selType:'checkboxmodel',
    columns: [
        {xtype: 'rownumberer', align: 'center', width: 30},
        {text:'id',dataIndex:'id',flex:1,hidden:true},
        {text: '楼层名称', dataIndex: 'floorName', flex: 1},
        {text: '楼层图', dataIndex: 'floorMap', flex: 1}
    ],
    tbar: [{text:'增加',  itemId:'floorAddBtn'},{text:'修改', itemId:'floorModifyBtn'},{text:'删除', itemId:'floorDelBtn'}],
    listeners : {
        'render' : function(view) {
            var store = view.getStore();
            if(store.getCount()>0){
                var record = view.getStore().getAt(0);
                view.getSelectionModel().select(record);
            }
        },
        'select':function (model,record) {
            var areaPanel = model.view.up('[itemId = areaPanel]');
            var DeviceAreaGridView = areaPanel.down('DeviceAreaGridView');
            var DeviceAreaGridViewStore = DeviceAreaGridView.getStore().load({
                params:{floorid:record.data.floorid},
                callback: function () {
                    if(DeviceAreaGridViewStore.getCount()>0){
                        var record = DeviceAreaGridViewStore.getAt(0);
                        DeviceAreaGridView.getSelectionModel().select(record);
                    }
                    else{
                        //没有该区域时，设备清空
                        var itemselector = model.view.up('[itemId=areaPanel]').down('itemselector');
                        itemselector.setValue();
                    }
                }
            });
        }
    }
});