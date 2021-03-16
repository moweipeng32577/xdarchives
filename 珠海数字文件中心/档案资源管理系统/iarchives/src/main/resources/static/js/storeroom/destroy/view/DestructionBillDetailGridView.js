/**
 * Created by yl on 2017/11/29.
 */
Ext.define('Destroy.view.DestructionBillDetailGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'destructionBillDetailGridView',
    title: '当前位置：',
    region: 'center',
    height: '72%',
    itemId:'destructionBillDetailGridViewID',
    // hasSearchBar:false,
    searchstore: [
        {item: "title", name: "题名"},
        {item: "entrystorage", name: "存储位置"},
        {item: "filedate", name: "文件时间"}
    ],
    tbar: [],
    store: 'DestructionBillDetailGridStore',
    columns: [
        {text: '存储位置', dataIndex: 'entrystorage', flex: 2, menuDisabled: true,
            renderer: function (value) {
                if(typeof(value) == 'undefined'){return '未入库';}else{return value;}
            }},
        {text: '保管期限', dataIndex: 'entryretention', flex: 1, menuDisabled: true},
        {text: '题名',dataIndex: 'title',flex: 1, menuDisabled: true},
        {text: '档号',dataIndex: 'archivecode', flex: 1, menuDisabled: true},
        {text: '文件时间', dataIndex: 'filedate', flex: 1, menuDisabled: true},
        {text: '鉴定类型',dataIndex:'state',flex:1, menuDisabled:true}
    ]
});