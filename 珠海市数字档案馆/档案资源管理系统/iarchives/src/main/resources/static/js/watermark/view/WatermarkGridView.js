/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Watermark.view.WatermarkGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'WatermarkGridView',
    title: '当前位置：水印管理',
    region: 'center',
    itemId: 'WatermarkGridViewID',
    searchstore:[{item: "title", name: "说明"}, {item: "isdefault", name: "是否默认"}],
    tbar: [{
        xtype: 'button',
        text: '增加',
        itemid: 'addWatermarkBtn'
    }, '-', {
        xtype: 'button',
        text: '修改',
        itemid: 'editWatermarkBtn'
    }, '-', {
        xtype: 'button',
        text: '删除',
        itemid: 'delWatermarkBtn'
    }],
    store: 'WatermarkGridStore',
    columns: [
        {text: '说明', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '水印具体位置', dataIndex: 'location', flex: 2, menuDisabled: true},
        {text: '水印坐标位置', dataIndex: 'coordinates', flex: 2, menuDisabled: true},
        {text: '角度', dataIndex: 'degree', flex: 2, menuDisabled: true},
        {text: '透明度', dataIndex: 'transparency', flex: 1, menuDisabled: true},
        {text: '是否默认', dataIndex: 'isdefault', flex: 2, menuDisabled: true},
        {text: '是否平铺', dataIndex: 'isrepeat', flex: 2, menuDisabled: true}
    ],
    allowDrag:true
});