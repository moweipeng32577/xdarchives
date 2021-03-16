/**
 * Created by tanly on 2017/11/8 0024.
 */
var filecount = 0;//计算文件数
Ext.define('MetadataTemplate.view.GroupingManagement', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'GroupingManagement',
    region: 'center',
    itemId: 'templateGridViewID',
    hasSearchBar:false,
    preview:'',
    tbar: {
        items:[{
            xtype: 'button',
            text: '增加',
            iconCls:'fa fa-columns',
            itemId: 'add'
        },'-',{
            xtype: 'button',
            text: '修改',
            iconCls:'fa fa-columns',
            itemId: 'modify'
        },'-',{
            xtype: 'button',
            text: '删除',
            iconCls:'fa fa-columns',
            itemId: 'del'
        },'-',{
            xtype: 'button',
            text: '返回',
            iconCls:'fa fa-columns',
            itemId: 'back'
        }],
        overflowHandler:'scroller'
    },
    store: 'GroupManagementStore',
    columns: [
        {text: '组名', dataIndex: 'groupname', flex: 2, menuDisabled: false},
        {text: '描述', dataIndex: 'grouptext', flex: 2, menuDisabled: true}
    ]
});
