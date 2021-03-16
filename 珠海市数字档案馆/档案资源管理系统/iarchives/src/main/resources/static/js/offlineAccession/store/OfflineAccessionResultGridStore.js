/**
 * Created by yl on 2019/1/10.
 */
Ext.define('OfflineAccession.store.OfflineAccessionResultGridStore',{
    extend:'Ext.data.Store',
    model:'OfflineAccession.model.OfflineAccessionResultGridModel',
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/offlineAccession/foursexverifys',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
