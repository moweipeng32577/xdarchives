/**
 * Created by zengdw on 2019/10/31 0001.
 */

Ext.define('Template.view.TemplateDescView', {
    extend: 'Ext.window.Window',
    xtype: 'templateDescView',
    itemId:'templateDescViewid',
    width:'100%',
    height:'100%',
    header:false,
    modal: true,
    layout:'fit',
    items:[{
        xtype: 'templateDescGridView'
    }]
});
