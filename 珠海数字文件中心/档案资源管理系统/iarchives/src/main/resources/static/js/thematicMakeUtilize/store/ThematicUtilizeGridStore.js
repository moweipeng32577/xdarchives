/**
 * Created by yl on 2017/11/2.
 */
Ext.define('ThematicUtilize.store.ThematicUtilizeGridStore',{
    extend:'Ext.data.Store',
    autoLoad: true,
    sortOnLoad: true,
    model:'ThematicUtilize.model.ThematicUtilizeGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/thematicMakeUtilize/getThematicDetailFb',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});