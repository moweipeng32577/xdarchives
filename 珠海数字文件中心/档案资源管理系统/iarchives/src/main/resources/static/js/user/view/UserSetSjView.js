/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.view.UserSetSjView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userSetSjView',
    itemId:"userSetSjView",
    store: 'UserSetSjStore',
    // checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    animate: true
});
