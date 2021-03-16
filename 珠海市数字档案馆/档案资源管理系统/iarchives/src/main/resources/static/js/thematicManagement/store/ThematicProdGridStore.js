/**
 * Created by yl on 2017/11/1.
 */
Ext.define('ThematicProd.store.ThematicProdGridStore',{
    extend:'Ext.data.Store',
    model:'ThematicProd.model.ThematicProdGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/thematicMake/getThematic',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});