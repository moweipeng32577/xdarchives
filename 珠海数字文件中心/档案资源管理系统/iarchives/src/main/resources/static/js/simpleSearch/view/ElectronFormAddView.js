/**
 * Created by Administrator on 2019/2/19.
 */

Ext.define('SimpleSearch.view.ElectronFormAddView', {
    extend: 'Ext.panel.Panel',
    xtype: 'electronFormAddView',
    itemId: 'electronFormAddViewId',
    region: 'center',
    autoScroll: true,
    items:[{
        xtype:'electronAddFormView'
    }]
});
