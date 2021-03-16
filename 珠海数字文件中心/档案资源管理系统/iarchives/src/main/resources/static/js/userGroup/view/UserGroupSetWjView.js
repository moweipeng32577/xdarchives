/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('UserGroup.view.UserGroupSetWjView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userGroupSetWjView',
    itemId:"userGroupSetWjView",
    store: 'UserGroupSetWjStore',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    bufferedRenderer: false,
    animate: true
});
