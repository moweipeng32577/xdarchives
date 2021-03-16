/**
 * Created by RonJiang on 2018/4/8 0008.
 */
Ext.define('Funds.store.FundsGridStore',{
    extend:'Ext.data.Store',
    model:'Funds.model.FundsGridModel',
    //autoLoad: true,
    pageSize: XD.pageSize,
    retmoeSort:true,
    proxy: {
        type: 'ajax',
        url: '/funds/getFunds',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});