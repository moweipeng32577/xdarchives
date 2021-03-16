/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.view.UserSetWjView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userSetWjView',
    itemId:"userSetWjView",
    store: 'UserSetWjStore',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    bufferedRenderer: false,
    animate: true
});
