/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('ServiceMetadata.view.AccreditMetadataGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'accreditMetadataGridView',
    region: 'center',
    itemId: 'accreditMetadataGridViewID',
    allowDrag: false, //允许拖拉
    searchstore: [{item: "shortname", name: "授权标识符"}, {item: "text", name: "参数值"}],
    tbar: [{
        xtype: 'button',
        text: '增加',
        iconCls:'fa fa-plus-circle',
        itemId: 'add'
    }, '-', {
        xtype: 'button',
        text: '修改',
        iconCls:'fa fa-pencil-square-o',
        itemId: 'update'
    }, '-', {
        xtype: 'button',
        text: '删除',
        iconCls:'fa fa-trash-o',
        itemId: 'delete'
    }],
    store: 'AccreditMetadataGridStore',
    columns: [
        {text: 'id', dataIndex: 'cid', flex: 2, menuDisabled: true,hidden:true},
        {text: '业务行为', dataIndex: 'operation', flex: 2, menuDisabled: true},
        {text: '业务状态', dataIndex: 'mstatus', flex: 2, menuDisabled: true},
        {text: '行为描述', dataIndex: 'operationmsg', flex: 2, menuDisabled: true},
        {text: '授权标识', dataIndex: 'shortname', flex: 2, menuDisabled: true},
        {text: '排序', dataIndex: 'sortsequence', flex: 2, menuDisabled: true}
    ]
});