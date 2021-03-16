/**
 * Created by RonJiang on 2018/04/08
 */
Ext.define('Funds.view.FundsView', {
    extend:'Ext.panel.Panel',
    xtype:'funds',
    layout:'card',
    activeItem:0,
    items:[{
        xtype:'fundsgrid'
    },{
        xtype:'fundsform'
    }]
});