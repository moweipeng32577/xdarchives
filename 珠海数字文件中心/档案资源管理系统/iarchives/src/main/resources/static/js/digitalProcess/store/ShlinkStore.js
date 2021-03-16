/**
 * Created by Administrator on 2019/9/18.
 */


Ext.define('DigitalProcess.store.ShlinkStore',{
    extend:'Ext.data.Store',
    xtype:'ShlinkStore',
    fields: ['id', 'title'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getLinkByassembly',
        extraParams: {assemblyid:null},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
