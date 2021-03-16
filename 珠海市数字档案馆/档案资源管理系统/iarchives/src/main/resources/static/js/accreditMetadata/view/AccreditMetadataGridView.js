/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('AccreditMetadata.view.AccreditMetadataGridView', {
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
        {text: 'id', dataIndex: 'aid', flex: 2, menuDisabled: true,hidden:true},
        {text: '授权标识符', dataIndex: 'shortname', flex: 2, menuDisabled: true},
        {text: '授权名称', dataIndex: 'fullname', flex: 2, menuDisabled: true},
        {text: '授权类型', dataIndex: 'atype', flex: 2, menuDisabled: true},
        // {text: '参数值', dataIndex: 'text', flex: 2, menuDisabled: true},
        {text: '发布时间', dataIndex: 'publishtime', flex: 2, menuDisabled: true},
        {text: '排序', dataIndex: 'sortsequence', flex: 2, menuDisabled: true}
    ]
});