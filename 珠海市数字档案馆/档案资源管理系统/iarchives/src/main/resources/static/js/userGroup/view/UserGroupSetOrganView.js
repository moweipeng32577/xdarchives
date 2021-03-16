/**
 * Created by tanly on 2018/4/23 0023.
 */
Ext.define('UserGroup.view.UserGroupSetOrganView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userGroupSetOrganView',
    itemId: "userGroupSetOrganView",
    store: 'UserGroupSetOrganStore',
    checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable: true,
    animate: true
});
