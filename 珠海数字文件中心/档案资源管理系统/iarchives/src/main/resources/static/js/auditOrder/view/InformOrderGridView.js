/**
 * Created by Administrator on 2020/6/16.
 */


Ext.define('AuditOrder.view.InformOrderGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'informOrderGridView',
    itemId:'informOrderGridViewID',
    hasSearchBar:false,
    hasCheckColumn:false,
    tbar:{
        overflowHandler:'scroller',
        items:[{
            text:'返回',
            itemId:'backId'
        }]
    },
    store: 'InformOrderGridStore',
    columns: [
        {text: '标题', dataIndex: 'title', flex: 1, menuDisabled: true},
        {text: '内容', dataIndex: 'text', flex: 2, menuDisabled: true}
    ]
});
