/**
 * Created by Administrator on 2019/6/18.
 */
var textTpl = ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'];
Ext.define('Accept.view.AcceptdocBatchForm',{
    extend: 'Ext.window.Window',
    xtype: 'AcceptdocBatchForm',
    itemId:'AcceptdocBatchFormId',
    title: '新建批次',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    modal:true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items:[
        {
            xtype: 'form',
            layout:'column',
            bodyPadding:10,
            defaults:{
                xtype:'textfield',
                labelAlign:'right',
                labelWidth:70,
                margin:'10 10 5 10'
            },
            modelValidation: true,
            items:[
                {
                    fieldLabel: '表单ID',
                    name:'batchid',
                    hidden:true
                },
                { fieldLabel: '单据ID',
                    name:'acceptdocid',
                    hidden:true
                },
                {
                    fieldLabel: '批次号',
                    name:'batchcode',
                    readOnly:true,
                    afterLabelTextTpl: textTpl,
                    columnWidth:1
                },
                {
                    fieldLabel: '消毒员',
                    name:'disinfector',
                    afterLabelTextTpl: textTpl,
                    columnWidth:1,
                    allowBlank:false
                },{
                    columnWidth: 1,
                    xtype: 'datefield',
                    fieldLabel: '消毒时间',
                    itemId:'submitdateId',
                    allowBlank: false,
                    name: 'disinfectiontime',
                    afterLabelTextTpl: textTpl,
                    format: 'Y-m-d H:i:s',
                    value: new Date(),
                },{
                    columnWidth:.53,
                    fieldLabel: '档案范围',
                    itemId:'startScope',
                    margin:'10 0 5 10',
                    afterLabelTextTpl: textTpl,
                    allowBlank:false
                },{
                    columnWidth:.47,
                    fieldLabel: '至',
                    margin:'10 10 5 0',
                    labelWidth:25,
                    itemId:'endScope',
                    afterLabelTextTpl: textTpl,
                    allowBlank:false
                },{
                    columnWidth:1,
                    fieldLabel: '备注',
                    name:'batchremark',
                    xtype:'textarea'
                }
            ]
        }],
    buttons: [{
        xtype: "label",
        itemId:'tips',
        style:{color:'red'},
        text:'温馨提示：红色外框表示输入非法数据！',
        margin:'6 2 5 4'
    },
        { text: '提交',itemId:'batchAddSubmit'},
        { text: '关闭',itemId:'batchAddClose',
            handler:function (btn) {
                btn.findParentByType('AcceptdocBatchForm').close();
            }
        }
    ]
});