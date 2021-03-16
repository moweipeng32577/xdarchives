/**
 * Created by RonJiang on 2018/1/26 0026.
 */
Ext.define('Management.store.FieldModifyPreviewGridStore', {
    extend: 'Ext.data.Store',
    model: 'Management.model.FieldModifyPreviewGridModel',
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