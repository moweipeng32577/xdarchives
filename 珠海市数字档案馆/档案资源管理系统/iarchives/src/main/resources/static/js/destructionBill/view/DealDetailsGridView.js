/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('DestructionBill.view.DealDetailsGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'DealDetailsGridView',
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:false,
    hasCheckColumn:false,
    store: 'DealDetailsGridStore',
    columns: [
        {text: '环节', dataIndex: 'node', flex: 2, menuDisabled: true},
        {text: '办理人', dataIndex: 'spman', flex: 1, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 1, menuDisabled: true},
        {text: '办理时间', dataIndex: 'spdate', flex: 2.5, menuDisabled: true},
        {text: '批示', dataIndex: 'approve', flex: 4, menuDisabled: true}
    ]
});
