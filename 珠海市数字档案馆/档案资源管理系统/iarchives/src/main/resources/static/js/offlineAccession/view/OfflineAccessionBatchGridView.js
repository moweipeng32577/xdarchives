/**
 * Created by yl on 2017/11/2.
 */
Ext.define('OfflineAccession.view.OfflineAccessionBatchGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'offlineAccessionBatchGridView',
    region: 'center',
    searchstore: [{item: "batchname", name: "批次名"}],
    tbar: [{
        itemId: 'addBatch',
        text: '新增批次',
        iconCls:'fa fa-trash-o'
    }, '-', {
        itemId: 'showBatch',
        text: '查看接收批次',
        iconCls:'fa fa-trash-o'
    }, '-', {
        itemId: 'offAccession',
        text: '离线接收',
        iconCls:'fa fa-trash-o'
    }, '-', {
        itemId: 'deleteBtnID',
        text: '删除批次',
        iconCls:'fa fa-trash-o'
    }, '-', {
        itemId: 'print',
        text: '打印单据',
        iconCls:'fa fa-trash-o'
    }],
    store: 'OfflineAccessionBatchGridStore',
    columns: [
        {text: '批次名', dataIndex: 'batchname', width: 200, menuDisabled: true},
        {text: '交接工作名称', dataIndex: 'workname', width: 200, menuDisabled: true},
        {text: '内容描述', dataIndex: 'workvalue', width: 200, menuDisabled: true},
        {text: '移交电子档案数量', dataIndex: 'elenum', width: 200, menuDisabled: true},
        {text: '移交数据量', dataIndex: 'datanum', width: 200, menuDisabled: true},
        {text: '载体起止顺序号', dataIndex: 'innercode', width: 200, menuDisabled: true},
        {text: '移交载体类型规格', dataIndex: 'datatype', width: 200, menuDisabled: true},
        {text: '检验内容', dataIndex: 'checkvalue', width: 200, menuDisabled: true},
        {text: '单位名称', dataIndex: 'unitname', width: 200, menuDisabled: true},
        {text: '移交单位', dataIndex: 'tfterunit', width: 200, menuDisabled: true},


    ]
});