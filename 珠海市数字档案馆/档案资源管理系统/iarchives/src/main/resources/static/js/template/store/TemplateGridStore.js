/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.store.TemplateGridStore',{
    extend:'Ext.data.Store',
    model:'Template.model.TemplateGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/template/templates',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});