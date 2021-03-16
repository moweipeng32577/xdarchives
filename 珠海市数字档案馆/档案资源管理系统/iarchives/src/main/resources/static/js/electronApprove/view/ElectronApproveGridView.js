/**
 * Created by xd on 2017/10/21.
 */
Ext.define('ElectronApprove.view.ElectronApproveGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'electronApproveGridView',
    region: 'north',
    height:'40%',
    itemId:'electronApproveGridViewID',
    hasSearchBar:false,
    tbar: [{
        xtype: 'button',
        itemId:'addId',
        iconCls:'fa fa-plus',
        hidden: true,
        text: '添加'
    }, '-', {
        xtype: 'button',
        itemId:'deleteBtnID',
        iconCls:' fa fa-trash-o',
        hidden: true,
        text: '删除'
    }, '-', {
        itemId:'look',
        xtype: 'button',
        text: '查看'
    }, '-', {
        xtype: 'button',
        itemId:'setType',
        text: '设置查档类型'
    },'-',{
        itemId:'setlyqx',
        xtype: 'button',
        text: '设置利用权限'
    },'-',{
        itemId:'setelectronic',
        xtype: 'button',
        text: '设置文件权限'
    },'-',{
        itemId:'print',
        xtype: 'button',
        text: '打印'
    }],
    store: 'ElectronApproveGridStore',
    columns: [
        {
            xtype:'actioncolumn',
            resizable:false,//不可拉伸
            hideable:false,
            header: '原文',
            dataIndex: 'eleid',
            sortable:true,
            width:60,
            align:'center',
            items:['@file']
        },
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
        {text: '卷内顺序号', dataIndex: 'innerfile', flex: 2, menuDisabled: true},
        {text: '件号', dataIndex: 'recordcode', flex: 2, menuDisabled: true},
        {text: '保管期限', dataIndex: 'entryretention', flex: 2, menuDisabled: true},
        {text: '归档年度', dataIndex: 'filingyear', flex: 2, menuDisabled: true},
        {text: '页数', dataIndex: 'pages', flex: 2, menuDisabled: true},
        {text: '门类', dataIndex: 'nodefullname', flex: 4, menuDisabled: true},
        {text: '利用权限', dataIndex: 'lyqx', flex: 2, menuDisabled: true},
        {text: '查档类型', dataIndex: 'type', flex: 2, menuDisabled: true}
    ]
});
