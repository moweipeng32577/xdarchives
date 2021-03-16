/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.view.SendbackreasonFormView', {
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
        margin:'15 5 5 15',
        fieldLabel:'退回原因',
        height:170,//文本框默认高度为30
        allowBlank:false
    },{
        columnWidth:0.02,
        xtype:'displayfield',
        value:'<label style="color:#ff0b23;!important;">*</label>',
        margin:'15 0 5 0'
    }],
    buttons:[{
        text:'确定退回',
        itemId:'affirmSendback'
    }]
});
