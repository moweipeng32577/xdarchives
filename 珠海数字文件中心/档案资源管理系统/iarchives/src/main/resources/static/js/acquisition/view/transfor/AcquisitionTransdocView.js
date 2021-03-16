/**
 * Created by RonJiang on 2018/4/19 0019.
 */
Ext.define('Acquisition.view.transfor.AcquisitionTransdocView', {
    extend:'Ext.panel.Panel',
    xtype:'acquisitionTransdocView',
    layout:'card',
    activeItem:0,
    items:[{
        xtype:'acquisitionShowTransdocGridView'
    },{
        xtype:'acquisitionTransdocEntryGridView'
    }, {
        itemId: 'formview',
        xtype: 'formView'//指向表单视图
    }]
});