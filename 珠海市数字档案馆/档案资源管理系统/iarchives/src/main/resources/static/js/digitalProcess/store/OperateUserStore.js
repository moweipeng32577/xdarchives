Ext.define('DigitalProcess.store.OperateUserStore',{
    extend:'Ext.data.Store',
    xtype:'OperateUserStore',
    fields: ['userid', 'username'],
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getOperateUsers',
        extraParams: {calloutId:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});