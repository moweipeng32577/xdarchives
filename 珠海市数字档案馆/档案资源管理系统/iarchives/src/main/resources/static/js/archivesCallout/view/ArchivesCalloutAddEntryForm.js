var textTpl = ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'];
Ext.define('ArchivesCallout.view.ArchivesCalloutAddEntryForm', {
    extend: 'Ext.window.Window',
    xtype: 'ArchivesCalloutAddEntryForm',
    itemId:'ArchivesCalloutAddEntryForm',
    title: '调档',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    minHeight: 250,
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

    items: [
        {
            xtype: 'form',
            modelValidation: true,
            margin: '15',
            items: [
                        { fieldLabel: 'id',name:'id',hidden:true},
                        { fieldLabel: 'a0',name:'a0',hidden:true},
                        { fieldLabel: 'id',name:'a1',hidden:true},
                        { fieldLabel: 'id',name:'a2',hidden:true},
                        { fieldLabel: 'id',name:'a3',hidden:true},
                        { fieldLabel: 'id',name:'a4',hidden:true},
                        { fieldLabel: 'za4',name:'za4',hidden:true},
                        { fieldLabel: 'tidy',name:'tidy',hidden:true},
                        { fieldLabel: 'scan',name:'scan',hidden:true},
                        { fieldLabel: 'audit',name:'audit',hidden:true},
                        { fieldLabel: 'record',name:'record',hidden:true},
                        { fieldLabel: 'pages',name:'pages',hidden:true},
                        { fieldLabel: 'copies',name:'copies',hidden:true},
                        { fieldLabel: 'filecount',name:'filecount',hidden:true},
                        { fieldLabel: 'pictureprocess',name:'pictureprocess',hidden:true},
                        { fieldLabel: 'workstate',name:'workstate',hidden:true},
                        { fieldLabel: 'lendstate',name:'lendstate',hidden:true},
                        { fieldLabel: 'checkstate',name:'checkstate',hidden:true},
                        { fieldLabel: 'scanstate',name:'scanstate',hidden:true},
                        { fieldLabel: 'checkcount',name:'checkcount',hidden:true},
                        { fieldLabel: 'picturestate',name:'picturestate',hidden:true},
                        { fieldLabel: 'businesssigner',name:'businesssigner',hidden:true},
                        { fieldLabel: 'businesssigncode',name:'businesssigncode',hidden:true},
                        { fieldLabel: 'signtime',name:'signtime',hidden:true},
                        { fieldLabel: 'entrysigner',name:'entrysigner',hidden:true},
                        { fieldLabel: 'entrysigncode',name:'entrysigncode',hidden:true},
                        { fieldLabel: 'entrysigntime',name:'entrysigntime',hidden:true},
                        { fieldLabel: 'entrysignorgan',name:'entrysignorgan',hidden:true},
                        { fieldLabel: '批次号',name:'batchcode',readOnly:true},
                        { fieldLabel: '归档号',name:'archivecode',allowBlank: false, afterLabelTextTpl: textTpl},
                {
                    columnWidth:1,
                    xtype: 'TreeComboboxView',
                    //fieldLabel: '拆件到分类',
                    fieldLabel: '节点',
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
        }
    ],

    buttons: [
        { text: '提交',itemId:'entryAddSubmit'},
        { text: '关闭',itemId:'entryAddClose'}
    ]
});