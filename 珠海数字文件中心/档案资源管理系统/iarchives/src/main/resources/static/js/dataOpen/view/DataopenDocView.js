/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Dataopen.view.DataopenDocView', {
    extend:'Ext.panel.Panel',
    xtype:'dataopenDocView',
    layout:'card',
    activeItem:0,
    items:[{
        xtype:'dataopenShowDocGridView'
    },{
        xtype:'dataopenDocEntryGridView'
    }]
});