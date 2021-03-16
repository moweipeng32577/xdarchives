/**
 * Created by RonJiang on 2018/4/18 0018.
 */
Ext.define('Audit.view.SendbackreasonFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'sendbackreasonFormView',
    layout:'column',
    items:[{
        columnWidth: .98,
        xtype: 'textarea',
        name:'sendbackreason',
        labelWidth:90,
        margin:'15 5 5 15',
        fieldLabel:'退回原因',
        onlyRead:false
    }],
    buttons:[{
        text:'返回',
        itemId:'back'
    }]
});