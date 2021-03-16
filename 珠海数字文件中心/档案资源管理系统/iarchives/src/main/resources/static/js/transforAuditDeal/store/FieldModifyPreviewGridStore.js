/**
 * Created by Administrator on 2019/10/26.
 */

Ext.define('TransforAuditDeal.store.FieldModifyPreviewGridStore', {
    extend: 'Ext.data.Store',
    model: 'TransforAuditDeal.model.FieldModifyPreviewGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/batchModify/getModifyFieldList',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
