/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.view.UserSetAreaView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userSetAreaView',
    itemId:"userSetAreaId",
    store: 'UserSetAreaStore',
    // checkPropagation: 'both',
    useArrows: true,
    rootVisible: false,
    frame: true,
    scrollable:true,
    bufferedRenderer: false,
    animate: true
});
