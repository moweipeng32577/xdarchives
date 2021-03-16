/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('AcceptDirectory.view.AcceptDetailGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'acceptDetailGridView',
    itemId:'acceptDetailGridViewId',
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:true,
    hasCheckColumn:false,
    store: 'AcceptDetailGridStore',
    tbar:[{
        text: '打印',
        iconCls: 'fa fa-print',
        itemId: 'printAcceptDetail'
    }],
    columns: [
        {text: '导入用户', dataIndex: 'impuser', flex: 2, menuDisabled: true},
        {text: '导入时间', dataIndex: 'imptime', flex: 3, menuDisabled: true},
        {text: '成功接收记录数', dataIndex: 'successcount', flex: 3, menuDisabled: true},
        {text: '失败记录数', dataIndex: 'defeatedcount', flex: 3, menuDisabled: true}
    ]
});

