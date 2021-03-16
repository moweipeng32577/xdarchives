/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('UserGroup.view.UserGroupSetSjView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userGroupSetSjView',
    itemId:"userGroupSetSjView",
    store: 'UserGroupSetSjStore',
    // checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    animate: true
});
