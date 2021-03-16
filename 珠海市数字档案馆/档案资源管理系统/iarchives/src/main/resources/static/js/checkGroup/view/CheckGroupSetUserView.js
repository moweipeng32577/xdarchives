/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('CheckGroup.view.CheckGroupSetUserView',{
    extend: 'Ext.window.Window',
    xtype:'checkGroupSetUserView',
    itemId:'checkGroupSetUserViewId',
    width:'100%',
    height:'100%',
    header:false,
    modal: true,
    layout:'fit',
    items:[{
        xtype: 'checkGroupUserGridView'
    }]
});
