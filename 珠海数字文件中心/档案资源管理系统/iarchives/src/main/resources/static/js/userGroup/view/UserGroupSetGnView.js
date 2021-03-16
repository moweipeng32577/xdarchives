/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('UserGroup.view.UserGroupSetGnView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userGroupSetGnView',
    itemId:"userGroupSetGnView",
    store: 'UserGroupSetGnStore',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    bufferedRenderer: false,
    animate: true
});
