/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.view.UserSetDeviceView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userSetDeviceView',
    itemId:"userSetDeviceView",
    store: 'UserSetDeviceStore',
    // checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    bufferedRenderer: false,
    animate: true
});
