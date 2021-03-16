/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('Borrow.view.BorrowView', {
    extend: 'Ext.panel.Panel',
    xtype: 'borrowView',
    layout: 'card',
    activeItem: 0,
    title: '权限档案',
    items: [{
        layout: 'border',
        xtype: 'panel',
        itemId: 'gridview',
        items: [{
            region: 'west',
            width: XD.treeWidth,
            xtype: 'treepanel',
            itemId: 'treepanelId',
            rootVisible: false,
            store: 'BorrowStore',
            collapsible: true,
            split: 1,
            hideHeaders: true,
            header: false
        }, {
            region: 'center',
            xtype: 'panel',
            layout: 'border',
            items: [{
                region: 'center',
                xtype: 'borrowSXView'
            }]
        }]
    }]
});