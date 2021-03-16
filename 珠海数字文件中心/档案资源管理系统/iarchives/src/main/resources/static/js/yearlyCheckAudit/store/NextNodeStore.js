/**
 * Created by yl on 2017/10/26.
 */
Ext.define('YearlyCheckAudit.store.NextNodeStore',{
    extend:'Ext.data.Store',
    xtype:'nextNodeStore',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/yearlyCheckAudit/getNextNode',
        extraParams: {
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});