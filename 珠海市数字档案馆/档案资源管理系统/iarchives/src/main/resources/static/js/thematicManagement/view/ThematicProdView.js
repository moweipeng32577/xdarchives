/**
 * Created by yl on 2017/10/27.
 */
Ext.define('ThematicProd.view.ThematicProdView', {
    extend: 'Ext.Panel',
    xtype: 'thematicProdView',
    layout:'card',
    activeItem:0,
    items: [{
        itemId:'thematicProdGridViewID',
        xtype:'thematicProdGridView'
    },{
        xtype:'thematicProdDetailGridView'
    }]
});