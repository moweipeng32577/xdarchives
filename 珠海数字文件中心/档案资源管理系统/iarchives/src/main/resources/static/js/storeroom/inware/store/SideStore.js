/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inware.store.SideStore',{
    extend:'Ext.data.Store',
    model:'Inware.model.SideModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/sides',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});