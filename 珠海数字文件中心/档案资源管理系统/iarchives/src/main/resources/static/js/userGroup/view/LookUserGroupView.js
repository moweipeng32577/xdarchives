/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.view.LookUserGroupView', {
    extend: 'Ext.window.Window',
    xtype: 'lookUserGroupView',
    itemId:'lookUserGroupViewId',
    header:false,
    frame: true,
    width: '100%',
    height: '100%',
    modal:true,
    closeToolText:'关闭',
    closeAction:'hide',
    layout: 'fit',
    items: [{
        xtype: 'lookUserGroupGridView'
    }]
});
