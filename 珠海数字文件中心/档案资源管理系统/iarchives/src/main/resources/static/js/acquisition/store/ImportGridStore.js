/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Acquisition.store.ImportGridStore', {
    extend:'Ext.data.Store',
    model:'Acquisition.model.ImportGridModel',
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