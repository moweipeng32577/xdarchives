/**
 * Created by yl on 2017/11/20.
 */
Ext.define('Borrow.view.BorrowSXView',{
    extend:'Ext.panel.Panel',
    xtype:'borrowSXView',
    layout:'border',
    items:[{
        region:'center',
        xtype:'borrowgrid'
    }]
});