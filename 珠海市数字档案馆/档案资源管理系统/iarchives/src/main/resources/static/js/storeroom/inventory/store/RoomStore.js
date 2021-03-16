/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inventory.store.RoomStore',{
    extend:'Ext.data.Store',
    model:'Inventory.model.RoomModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/rooms',
        extraParams:{
            //typeId: combo.value
            citydisplay:'珠海',
            unitdisplay:'欣档'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});