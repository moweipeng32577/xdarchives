/**
 * Created by Rong on 2018/10/9.
 */
Ext.define('Inware.store.TemplateStore',{
    extend:'Ext.data.Store',
    proxy: {
        type: 'ajax',
        url: '/import/template',
        extraParams: {isEntryStorage:true},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
