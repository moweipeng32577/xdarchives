/**
 * Created by RonJiang on 2018/04/23
 */
Ext.define('Recyclebin.view.RecyclebinView', {
    extend:'Ext.panel.Panel',
    xtype:'recyclebin',
    layout:'card',
    activeItem:0,
    items:[{
        xtype:'recyclebingrid'
    },{
        xtype:'recyclebinform'
    }]
});