/**
 * Created by yl on 2019/1/10.
 */
Ext.define('OfflineAccession.view.OfflineAccessionFourSexView', {
    extend: 'Ext.window.Window',
    xtype:'offlineAccessionFourSexView',
    title:'四性验证',
    width: '85%',
    height: '80%',
    resizable: false,//禁止缩放
    modal: true,
    layout: 'fit',
    closeToolText: '关闭',
    closeAction: 'hide',
    items: {
        xtype:'offlineAccessionResultGridView'
    }
});
