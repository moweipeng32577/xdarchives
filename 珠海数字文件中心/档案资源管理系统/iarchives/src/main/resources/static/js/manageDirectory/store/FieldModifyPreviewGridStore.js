/**
 * Created by Administrator on 2019/6/26.
 */

Ext.define('ManageDirectory.store.FieldModifyPreviewGridStore', {
    extend: 'Ext.data.Store',
    model: 'ManageDirectory.model.FieldModifyPreviewGridModel',
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