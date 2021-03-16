/**
 * Created by Administrator on 2019/3/15.
 */


Ext.define('Acquisition.view.AcquisitionLookMediaView', {
    extend: 'Ext.window.Window',
    xtype:'acquisitionLookMediaView',
    width:'100%',
    height:'100%',
    modal: true,
    header: false,
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    closeToolText: '关闭',
    layout: 'fit',
    items: [{
        xtype:'missPageElectronicView'
    }
    ]
});
