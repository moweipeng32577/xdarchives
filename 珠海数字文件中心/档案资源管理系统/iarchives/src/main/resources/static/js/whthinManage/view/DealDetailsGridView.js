/**
 * Created by Administrator on 2018/10/22.
 */

Ext.define('WhthinManage.view.DealDetailsGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'DealDetailsGridView',
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:false,
    hasCheckColumn:false,
    store: 'DealDetailsGridStore',
    tbar: [{
        itemId:'lookApproveId',
        xtype: 'button',
        iconCls:'fa fa-comment-o',
        text: '查看批示详情'
    }],
    columns: [
        {text: '环节', dataIndex: 'node', flex: 2, menuDisabled: true},
        {text: '办理人', dataIndex: 'spman', flex: 1, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 1, menuDisabled: true},
        {text: '办理时间', dataIndex: 'spdate', flex: 2, menuDisabled: true},
        {text: '批示', dataIndex: 'approve', flex: 4, menuDisabled: true}
    ]
});
