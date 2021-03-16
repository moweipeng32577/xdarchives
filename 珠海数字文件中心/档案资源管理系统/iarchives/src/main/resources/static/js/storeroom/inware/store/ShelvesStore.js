/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inware.store.ShelvesStore',{
    extend:'Ext.data.Store',
    model:'Inware.model.StateModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/inware/inwares'
    },
    autoload:true,
    remoteSort:true
});