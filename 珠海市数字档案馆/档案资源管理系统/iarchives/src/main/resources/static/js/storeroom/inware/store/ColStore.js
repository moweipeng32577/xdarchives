/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inware.store.ColStore',{
    extend:'Ext.data.Store',
    model:'Inware.model.ColModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/cols',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});