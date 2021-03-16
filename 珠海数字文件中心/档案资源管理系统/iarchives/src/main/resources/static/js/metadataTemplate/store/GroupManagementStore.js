Ext.define('MetadataTemplate.store.GroupManagementStore',{
    extend:'Ext.data.Store',
    model:'MetadataTemplate.model.GroupManagementModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/metadataTemplate/getGroupField',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});