/**
 * Created by Administrator on 2019/3/15.
 */


Ext.define('Management.view.ManagementLookMediaView', {
    extend: 'Ext.window.Window',
    xtype:'managementLookMediaView',
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
