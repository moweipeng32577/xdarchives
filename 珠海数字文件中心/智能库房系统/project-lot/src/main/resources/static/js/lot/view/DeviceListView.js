/**
 * Created by Rong on 2019-01-21.
 */
Ext.define('Lot.view.DeviceListView',{
    extend:'Ext.grid.GridPanel',
    xtype:'devicelist',
    store:'DeviceByFloorCodeStore',
    frame:false,
    margin:5,
    columns:[
        {xtype: 'rownumberer', align: 'center', width:40},
        {text: '名称', dataIndex: 'name', flex: 2},
        {text: '类型', dataIndex: 'type', flex: 1, renderer:function(value){
            var types = Ext.getStore('DeviceTypeStore').queryRecords('code',value);
            if(types.length > 0){
                return types[0].get('name');
            }
            return value.typeName;
        }},
        // {text: '状态', dataIndex: 'statusStr', flex : 1}
    ]
});