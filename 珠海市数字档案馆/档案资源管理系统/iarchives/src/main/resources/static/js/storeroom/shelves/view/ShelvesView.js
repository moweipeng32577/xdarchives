/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Shelves.view.ShelvesView',{
    extend:'Ext.panel.Panel',
    xtype:'shelves',
    layout:'border',
    items:[{
        region:'west',
        flex:1,
        xtype:'basicgrid',
        itemId:'shelvesgrid',
        store:'ShelvesStore',
        hasSearchBar:false,
        //tbar:functionButton,
        tbar:[{text:'新增',itemId:'add'}, {text:'修改',itemId:'modify'}, {text:'删除',itemId:'del'}],
        margin:'10 5 10 10',
        columns: [
            {text: '城区', dataIndex: 'citydisplay', flex: 1, menuDisabled: true},
            {text: '单位', dataIndex: 'unitdisplay', flex: 2, menuDisabled: true},
            {text: '楼层', dataIndex: 'floordisplay', flex: 1, menuDisabled: true},
            {text: '库房', dataIndex: 'roomdisplay', flex: 1, menuDisabled: true},
            {text: '密集架区', dataIndex: 'zonedisplay', flex: 2, menuDisabled: true},
            {text: '列数', dataIndex: 'countcol', flex: 1, menuDisabled: true},
            {text: '固定列', dataIndex: 'fixed', flex: 1, menuDisabled: true}
        ]
    },{
        region:'center',
        flex:1,
        xtype:'basicgrid',
        itemId:'detailgrid',
        store:'DetailStore',
        hasSearchBar:false,
        selType : 'rowmodel',
        margin:'10 10 10 5',
        columns: [
            {text: '列', dataIndex: 'coldisplay', flex: 1, menuDisabled: true},
            {text: '节', dataIndex: 'sectiondisplay', flex: 1, menuDisabled: true},
            {text: '层', dataIndex: 'layerdisplay', flex: 1, menuDisabled: true},
            {text: '面', dataIndex: 'sidedisplay', flex: 1, menuDisabled: true}
        ]
    }]




});