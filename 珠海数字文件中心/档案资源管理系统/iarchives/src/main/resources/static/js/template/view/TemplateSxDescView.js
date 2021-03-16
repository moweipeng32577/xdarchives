/**
 * Created by yl on 2021-01-20.
 */
/**
 * Created by zengdw on 2019/10/31 0001.
 */

Ext.define('Template.view.TemplateSxDescView', {
    extend: 'Ext.window.Window',
    xtype: 'templateSxDescView',
    itemId:'templateSxDescViewid',
    width:'100%',
    height:'100%',
    header:false,
    modal: true,
    layout:'fit',
    items:[{
        xtype: 'templateSxDescGridView'
    }]
});
