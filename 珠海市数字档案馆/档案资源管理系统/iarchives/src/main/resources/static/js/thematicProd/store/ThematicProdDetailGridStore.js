/**
 * Created by yl on 2017/11/1.
 */
Ext.define('ThematicProd.store.ThematicProdDetailGridStore',{
    extend:'Ext.data.Store',
    model:'ThematicProd.model.ThematicProdDetailGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/infoCompilation/getThematicDetail',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});