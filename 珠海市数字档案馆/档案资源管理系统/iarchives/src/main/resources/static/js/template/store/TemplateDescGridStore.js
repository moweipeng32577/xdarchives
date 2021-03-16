/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.store.TemplateDescGridStore',{
    extend:'Ext.data.Store',
    model:'Template.model.TemplateDescGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/template/templateDescs',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});