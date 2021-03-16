/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.view.UserSetGnView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userSetGnView',
    itemId:"userSetGnView",
    store: 'UserSetGnStore',
    // checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    bufferedRenderer: false,
    animate: true
});
