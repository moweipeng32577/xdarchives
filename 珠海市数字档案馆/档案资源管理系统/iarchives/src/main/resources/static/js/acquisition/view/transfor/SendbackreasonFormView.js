/**
 * Created by RonJiang on 2018/4/18 0018.
 */
Ext.define('Acquisition.view.transfor.SendbackreasonFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'sendbackreasonFormView',
    layout:'column',
    height:'80%',
    autoScroll: true,
    items:[{
        columnWidth: .98,
        xtype: 'textarea',
        name:'sendbackreason',
        labelWidth:90,
        margin:'30 5 5 15',
        fieldLabel:'退回原因',
        height:170,//文本框默认高度为30
        readOnly: true
    }]
    // buttons:[{
    //     text:'返回',
    //     itemId:'back'
    // }]
});