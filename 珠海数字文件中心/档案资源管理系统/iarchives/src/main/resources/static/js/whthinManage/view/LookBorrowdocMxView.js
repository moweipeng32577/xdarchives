/**
 * Created by yl on 2017/11/3.
 */
Ext.define('WhthinManage.view.LookBorrowdocMxView', {
    extend: 'Ext.window.Window',
    xtype: 'lookBorrowdocMxView',
    height: '100%',
    width: '100%',
    header: false,
    closeAction: 'hide',//关闭按钮点击时默认为close，移除window并彻底销毁；hide为仅隐藏
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype: 'lookBorrowDetailFormView'},
        {xtype: 'lookBorrowdocMxGridView'}
    ]
});