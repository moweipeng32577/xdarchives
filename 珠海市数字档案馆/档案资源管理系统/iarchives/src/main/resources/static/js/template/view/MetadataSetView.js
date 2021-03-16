/**
 * Created by Administrator on 2020/7/2.
 */


Ext.define('Template.view.MetadataSetView', {
    extend: 'Ext.window.Window',
    xtype: 'metadataSetView',
    height: '70%',
    width: '80%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '设置元数据',
    closeAction: 'hide',
    layout: 'fit',
    items: [
        {
            xtype: 'metadataGridView'
        }
    ],
    buttons: [{
        text: '保存',
        itemId: 'saveMetadataId'
    },{
        text:'返回',
        itemId:'back'
    }]
});
