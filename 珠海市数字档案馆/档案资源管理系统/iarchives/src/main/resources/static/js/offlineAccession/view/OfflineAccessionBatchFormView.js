/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('OfflineAccession.view.OfflineAccessionBatchFormView', {
    extend: 'Ext.window.Window',
    xtype: 'offlineAccessionBatchFormView',
    itemId:'offlineAccessionBatchFormView',
    title: '增加批次',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    height: '80%',
    autoScroll: true,
    modal:true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%',
        layout:'form'
    },
    items: [{
        bodyPadding: 15,
        xtype: 'form',
        modelValidation: true,
        margin: '15',
        items: [
            { fieldLabel: '批次号',name:'batchcode',allowBlank: false},
            { fieldLabel: '批次名',name:'batchname', allowBlank: false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ],
                listeners: {
                    render: function(sender) {
                        new Ext.ToolTip({
                            target: sender.el,
                            trackMouse: true,
                            dismissDelay: 0,
                            anchor: 'buttom',
                            html: '请输入批次名'
                        });
                    }
                }
            },
            { fieldLabel: '交接工作名称',name:'workname',allowBlank: false ,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]},
            { fieldLabel: '内容描述',name:'workvalue'},
            { fieldLabel: '移交电子档案数量',name:'elenum',allowBlank: false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]},
            { fieldLabel: '移交数据量',name:'datanum',allowBlank: false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]},
            { fieldLabel: '载体起止顺序号',name:'innercode'},
            { fieldLabel: '移交载体类型规格',name:'datatype'},
            { fieldLabel: '检验内容',name:'checkvalue'},
            { fieldLabel: '单位名称',name:'unitname',allowBlank: false,
                afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]},
            { fieldLabel: '移交单位',name:'tfterunit',allowBlank: false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]},
           {
                xtype: 'TreeComboboxView',
                //fieldLabel: '拆件到分类',
                fieldLabel: '接收单位',
                editable: false,
                url: '/nodesetting/getSzhWCLNodeByParentId',
                extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
                allowBlank: false,
                name: 'nodeid',
                itemId: 'dismantleNode',
                margin:'20 20 5 10',
                allowBlack:false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]
            }
        ]
    }],

    buttons: [
        { text: '保存',itemId:'batchsubmit'},
        { text: '关闭',itemId:'batchclose'}
    ]
});