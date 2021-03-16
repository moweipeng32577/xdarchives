/**
 * 库房视频监控列表
 * Created by wmh on 2020/1/14.
 */
Ext.define('Lot.view.JK.JKListView',{
    extend:'Ext.grid.GridPanel',
    xtype:'JKlist',
    store:'JKStore',
    frame:false,
    margin:5,
    columns:[
        {xtype: 'rownumberer', align: 'center', width:40},
        {text: '名称', dataIndex: 'name', flex: 2},
        {text: '状态', dataIndex: 'statusStr', flex : 1}
    ]
});