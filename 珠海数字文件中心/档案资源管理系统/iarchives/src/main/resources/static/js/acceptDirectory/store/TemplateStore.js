/**
 * Created by Administrator on 2019/6/24.
 */


Ext.define('AcceptDirectory.store.TemplateStore',{
    extend:'Ext.data.Store',
    proxy: {
        type: 'ajax',
        url: '/import/template',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
