/**
 * Created by Administrator on 2019/6/26.
 */

Ext.define('AcceptDirectory.store.FieldModifyPreviewGridStore', {
    extend: 'Ext.data.Store',
    model: 'AcceptDirectory.model.FieldModifyPreviewGridModel',
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