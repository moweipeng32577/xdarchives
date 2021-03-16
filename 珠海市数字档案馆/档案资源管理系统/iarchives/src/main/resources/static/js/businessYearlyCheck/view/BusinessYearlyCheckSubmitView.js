/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('BusinessYearlyCheck.view.BusinessYearlyCheckSubmitView', {
    extend: 'Ext.window.Window',
    xtype: 'businessYearlyCheckSubmitView',
    itemId: 'businessYearlyCheckSubmitViewId',
    title: '提交单',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    closeAction:'hide',
    width: '70%',
    height: '60%',
    modal: true,
    layout: 'border',
    items: [
        {xtype: 'businessYearlyCheckSubmitFormView'},
        {xtype: 'businessYearlyCheckSubmitGridView'}]
});
