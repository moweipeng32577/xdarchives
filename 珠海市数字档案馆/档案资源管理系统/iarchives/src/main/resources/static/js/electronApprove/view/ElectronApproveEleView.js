/**
 * Created by Administrator on 2018/10/30.
 */

Ext.define('ElectronApprove.view.ElectronApproveEleView', {
    extend: 'Ext.window.Window',
    xtype: 'electronApproveEleView',
    width: '100%',
    height: '100%',
    title: '设置文件权限',
    closable: false,
    modal: true,
    layout: 'fit',
    items: [{
        entrytype:'electronic',
        xtype:'electronApproveEleFormView'
    }]
});


