/**
 * Created by Administrator on 2020/5/9.
 */


Ext.define('ProjectRate.view.ProjectRateView', {
    extend: 'Ext.panel.Panel',
    xtype: 'projectRateView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype:'projectRateGridView'
    },{
        xtype:'EntryFormView'
    }]
});
