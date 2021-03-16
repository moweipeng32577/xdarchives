/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Import.store.ImportGridStore', {
    extend:'Ext.data.Store',
    model:'Import.model.ImportGridModel',
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/previews',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});