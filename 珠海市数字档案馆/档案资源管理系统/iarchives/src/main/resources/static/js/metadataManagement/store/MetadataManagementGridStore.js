/**
 * Created by SunK on 2020/5/25 0025.
 */
Ext.define('MetadataManagement.store.MetadataManagementGridStore',{
    extend:'Ext.data.Store',
    xtype:'metadataManagementGridStore',
    model:'MetadataManagement.model.MetadataManagementGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/metadataManagement/entries',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});