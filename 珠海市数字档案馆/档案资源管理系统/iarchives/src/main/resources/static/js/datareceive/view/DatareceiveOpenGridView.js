/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('Datareceive.view.DatareceiveOpenGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'datareceiveOpenGridView',
    itemId: 'datareceiveOpenGridViewID',
    bodyBorder: false,
    store: 'DatareceiveOpenGridStore',
    hasCloseButton: false,
    hasSearchBar: false,
    head: false,
    searchstore: [
        {item: "filename", name: "文件名"}
    ],
    tbar: [
        {
            itemId: 'receive',
            xtype: 'button',
            iconCls: 'fa fa-plus-circle',
            text: '接收'
        },'-',
        {
            itemId: 'received',
            xtype: 'button',
            iconCls: 'fa fa-indent',
            text: '已接收'
        }, '-', {
            text: '执行验证',
            itemId: 'implement',
            iconCls: 'fa fa-tripadvisor'
        }, '-', {
            text: '查看验证',
            itemId: 'lookdetail',
            iconCls: 'fa fa-eye'
        }, '-', {
            itemId: 'upload',
            text: '上传',
            hidden:true
        }, '-', {
            itemId: 'download',
            text: '下载',
            iconCls:'fa fa-download',
            hidden:true
        }, '-', {
            itemId:'print',
            xtype: 'button',
            iconCls:'fa fa-print',
            text: '打印单据'
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