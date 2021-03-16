/**
 * Created by zdw on 2020/03/20
 */
Ext.define('Showroom.view.ShowroomView',{
    extend: 'Ext.panel.Panel',
    xtype:'showroom',
    layout:'card',
    activeItem:0,
    items:[{
        itemId:'gridview',
        xtype:'showroomGridView'
    }]
});