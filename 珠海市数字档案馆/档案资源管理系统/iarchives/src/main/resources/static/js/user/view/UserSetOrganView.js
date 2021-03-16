/**
 * Created by tanly on 2018/04/21 0025.
 */
Ext.define('User.view.UserSetOrganView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userSetOrganView',
    itemId:"userSetOrganView",
    store: 'UserSetOrganStore',
    // checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    animate: true
});
