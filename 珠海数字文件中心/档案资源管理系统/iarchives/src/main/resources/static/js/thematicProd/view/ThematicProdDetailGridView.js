/**
 * Created by yl on 2017/12/22.
 */
Ext.define('ThematicProd.view.ThematicProdDetailGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'thematicProdDetailGridView',
    title: '信息编研',
    hasSearchBar:false,
    tbar: [{
        xtype: 'button',
        itemId:'leadInID',
        iconCls:'fa fa-upload',
        text: '导入'
    }, '-', {
        xtype: 'button',
        itemId:'addBtnID',
        iconCls:'fa fa-plus',
        text: '增加'
    }, '-', {
        xtype: 'button',
        itemId:'updateBtnID',
        iconCls:'fa fa-pencil-square-o',
        text: '修改'
    }, '-', {
        xtype: 'button',
        itemId:'deleteBtnID',
        iconCls:' fa fa-trash-o',
        text: '删除'
    }, '-', {
        xtype: 'button',
        itemId:'seeBtnID',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        xtype: 'button',
        itemId:'back',
        iconCls:'fa fa-undo',
        text: '返回'
    }],
    store: 'ThematicProdDetailGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 0.4, menuDisabled: true},
        // {text: '文件夹', dataIndex: 'chapter', flex: 0.1, menuDisabled: true},
        // {text: '下级文件夹', dataIndex: 'section', flex: 0.1, menuDisabled: true},
        {text: '时间', dataIndex: 'filedate', flex: 0.2, menuDisabled: true},
        {text: '责任者', dataIndex: 'responsibleperson',flex: 0.2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filecode', flex: 0.2, menuDisabled: true},
        {text: '主题词', dataIndex: 'subheadings', flex: 0.3, menuDisabled: true},
        {text: '电子文件', dataIndex: 'mediatext',flex: 0.3, menuDisabled: true}
    ]
});