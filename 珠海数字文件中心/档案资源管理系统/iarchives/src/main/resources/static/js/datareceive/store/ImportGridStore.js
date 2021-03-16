/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Datareceive.store.ImportGridStore', {
    extend:'Ext.data.Store',
    model:'Datareceive.model.ImportGridModel',
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