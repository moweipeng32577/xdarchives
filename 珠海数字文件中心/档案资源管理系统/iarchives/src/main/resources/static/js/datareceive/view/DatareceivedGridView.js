/**
 * Created by yl on 2020/3/18.
 */
Ext.define('Datareceive.view.DatareceivedGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'datareceivedGridView',
    itemId: 'datareceivedGridViewID',
    bodyBorder: false,
    store: 'DatareceivedGridStore',
    hasCloseButton: false,
    hasSearchBar: false,
    head: false,
    tbar: [
        {
            itemId: 'delete',
            xtype: 'button',
            iconCls: 'fa fa-trash-o',
            text: '删除'
        }
    ],
    columns: [
        {text: '文件名', dataIndex: 'filename', flex: 2, menuDisabled: true},
        {text: '交接工作名称', dataIndex: 'transfertitle', flex: 2, menuDisabled: true},
        {text: '内容描述', dataIndex: 'transdesc', flex: 2, menuDisabled: true},
        {text: '移交人', dataIndex: 'transuser', flex: 1, menuDisabled: true},
        {text: '载体起止顺序号', dataIndex: 'sequencecode', flex: 1, menuDisabled: true},
        {text: '移交电子档案数', dataIndex: 'transcount', flex: 1, menuDisabled: true},
        {text: '移交数据量', dataIndex: 'transfersize', flex: 1, menuDisabled: true},
        {text: '移交部门', dataIndex: 'transorgan', flex: 2, menuDisabled: true},
        {text: '实体移交时间', dataIndex: 'transdate', flex: 2, menuDisabled: true},
        {text: '移交载体数量', dataIndex: 'transferstcount', flex: 1, menuDisabled: true}
    ]
});