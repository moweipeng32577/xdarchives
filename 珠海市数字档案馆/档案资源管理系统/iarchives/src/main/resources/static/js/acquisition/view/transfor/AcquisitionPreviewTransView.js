/**
 * Created by Leo on 2021/2/5 0005.
 */
Ext.define('Acquisition.view.transfor.AcquisitionPreviewTransView', {
    extend:'Ext.panel.Panel',
    xtype:'acquisitionPreviewTransView',
    layout:'card',
    activeItem:0,
    items:[{
        xtype:'acquisitionPreviewTransEntryGridView'
    }, {
        itemId: 'formview',
        xtype: 'formView'//指向表单视图
    }]
});