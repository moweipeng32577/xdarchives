/**
 * Created by Administrator on 2019/3/15.
 */


Ext.define('Acquisition.view.AcquisitionMissPageCheck', {
    extend: 'Ext.window.Window',
    xtype: 'acquisitionMissPageCheck',
    itemId: 'acquisitionMissPageCheckId',
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
            xtype: 'acquisitionMissPageDetailView'
        }
    ]
});
