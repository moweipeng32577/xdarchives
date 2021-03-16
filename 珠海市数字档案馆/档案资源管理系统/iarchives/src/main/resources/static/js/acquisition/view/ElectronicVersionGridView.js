/**
 * Created by Administrator on 2019/2/26.
 */

Ext.define('Acquisition.view.ElectronicVersionGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'electronicVersionGridView',
    itemId:'electronicVersionGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    tbar: [{
        itemId:'lookeleVersion',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-',{
        itemId:'deleleVersion',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
    }, '-',{
        itemId:'rebackVersion',
        xtype: 'button',
        iconCls:'fa fa-backward',
        text: '回滚到该版本'
    }, '-',{
        itemId:'loadVersion',
        xtype: 'button',
        iconCls:'fa fa-download',
        text: '下载'
    }, '-',{
        xtype: 'button',
        itemId:'backVersion',
        iconCls:'fa fa-undo',
        text: '返回'
    }],
    store: 'ElectronicVersionGridStore',
    columns: [
        {text: '版本号', dataIndex: 'version', flex: 2, menuDisabled: true},
        {text: '创建时间', dataIndex: 'createtime', flex: 3, menuDisabled: true},
        {text: '文件大小', dataIndex: 'filesize', flex: 3, menuDisabled: true},
        {text: '创建者', dataIndex: 'createname', flex: 3, menuDisabled: true},
        {text: '备注', dataIndex: 'remark', flex: 3, menuDisabled: true}
    ]
});
