/**
 * Created by Administrator on 2019/6/14.
 */
var textTpl = ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'];
Ext.define('Accept.view.AcceptAddDocForm',{
    extend: 'Ext.window.Window',
    xtype: 'AcceptAddDocForm',
    itemId:'AcceptAddDocFormId',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    height:420,
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
            modelValidation: true,
            margin: '15',
            items:[
                {
                    fieldLabel: '表单ID',
                    itemId:'acceptItemId',
                    name:'acceptdocid',
                    hidden:true
                },
                {
                    fieldLabel: '提交人',
                    itemId:'submitterId',
                    name:'submitter',
                    afterLabelTextTpl: textTpl,
                    allowBlank:false
                },
/*                {
                    xtype: 'AcceptdocTreeComboboxView',
                    fieldLabel: '提交单位',
                    editable: false,
                    url: '/nodesetting/getKfNodeByParentId',
                    extraParams: {pcid: ''},
                    allowBlank: false,
                    afterLabelTextTpl: textTpl,
                    name: 'organ',
                    emptyText: '请选择节点',
                    itemId: 'parentSelectItemID'},*/
                {
                    columnWidth: .98,
                    xtype: 'datefield',
                    fieldLabel: '提交时间',
                    itemId:'submitdateId',
                    allowBlank: false,
                    name: 'submitdate',
                    afterLabelTextTpl: textTpl,
                    format: 'Y-m-d H:i:s',
                    value: new Date(),
                    margin: '5 10 5 0'
                },{
                    fieldLabel: '提交单位',
                    itemId:'submitorganId',
                    name: 'submitorgan',
                    afterLabelTextTpl: textTpl,
                    allowBlank:false
                },
                {
                    fieldLabel: '档案数量',
                    itemId:'archiveNumId',
                    name: 'archivenum',
                    afterLabelTextTpl: textTpl,
                    allowBlank:false
                },
                {
                    xtype:'textarea',
                    itemId:'docremarkId',
                    fieldLabel: '备注',
                    name:'docremark'
                }
            ]
        }],
    buttons: [
        {
            xtype: "label",
            itemId:'tips',
            style:{color:'red'},
            text:'温馨提示：红色外框表示输入非法数据！',
            margin:'6 2 5 4'
        },
        { text: '提交',itemId:'docAddSubmit'},
        { text: '关闭',itemId:'docAddClose',
            handler:function (btn) {
                btn.findParentByType('AcceptAddDocForm').close();
            }
        }
    ]
});