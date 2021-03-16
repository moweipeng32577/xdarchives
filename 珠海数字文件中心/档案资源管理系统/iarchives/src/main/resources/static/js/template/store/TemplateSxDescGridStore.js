/**
 * Created by yl on 2021-01-20.
 */
/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.store.TemplateSxDescGridStore',{
    extend:'Ext.data.Store',
    model:'Template.model.TemplateSxDescGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/template/templateSxDescs',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});