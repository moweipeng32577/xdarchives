/**
 * Created by Administrator on 2019/10/31.
 */


Ext.define('Management.view.LookBackCaptureDocView', {
    extend:'Ext.panel.Panel',
    xtype:'lookBackCaptureDocView',
    layout:'card',
    activeItem:0,
    items:[{
        xtype:'lookBackCaptureDocGridView'
    },{
        xtype:'lookBackCaptureDocEntryView'
    }]
});
