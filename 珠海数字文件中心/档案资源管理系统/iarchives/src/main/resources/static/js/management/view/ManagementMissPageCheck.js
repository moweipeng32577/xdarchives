/**
 * Created by Administrator on 2019/3/15.
 */


Ext.define('Management.view.ManagementMissPageCheck', {
    extend: 'Ext.window.Window',
    xtype: 'managementMissPageCheck',
    itemId: 'managementMissPageCheckId',
    title: '漏页检测',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: '70%',
    height: '80%',
    modal: true,
    layout: 'border',
    items: [
        {
            region: 'north',
            xtype: 'fieldset',
            height: 100,
            title: '合计',
            items: [{
                xtype: 'label',
                html: ''
            }]
        },
        {
            region: 'center',
            xtype: 'managementMissPageDetailView'
        }
    ]
});
