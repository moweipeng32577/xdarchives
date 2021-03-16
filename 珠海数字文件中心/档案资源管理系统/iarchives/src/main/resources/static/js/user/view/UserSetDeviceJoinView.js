/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.view.UserSetDeviceJoinView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userSetDeviceJoinView',
    itemId:"userSetDeviceJoinView",
    store: 'UserSetDeviceJoinStore',
    // checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    bufferedRenderer: false,
    animate: true
});
