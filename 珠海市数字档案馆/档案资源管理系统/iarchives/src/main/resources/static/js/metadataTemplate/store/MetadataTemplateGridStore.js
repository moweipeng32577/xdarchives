/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('MetadataTemplate.store.MetadataTemplateGridStore',{
    extend:'Ext.data.Store',
    model:'MetadataTemplate.model.MetadataTemplateGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/metadataTemplate/templates',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});