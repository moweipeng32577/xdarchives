/**
 * Created by tanly on 2017/11/9 0009.
 */
Ext.define('Template.view.TemplateCopyFormView', {
    extend: 'Ext.window.Window',
    xtype: 'templateCopyFormView',
    itemId:'templateCopyFormViewid',
    title: '复制模板',
    width: 700,
    height: 260,
    modal:true,
    closeToolText:'关闭',
    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 90%'
    },
    items:[{
        xtype: 'form',
        margin:'22',
        modelValidation: true,
        items: [{
            fieldLabel: '',
            name:'sourceid',
            hidden:true,
            itemId:'sourceItemID'
        },{
            fieldLabel: '',
            name:'targetid',
            hidden:true,
            itemId:'targetItemID'
        },{
            xtype: 'templateTreeComboboxView',
            fieldLabel: '源模板',
            editable:false,
            url: '/nodesetting/getExpandNodeById',
            extraParams:{pcid:''},
            allowBlank: false,
            name:'sourceSelectItem',
            itemId:'sourceSelectItemID',
            readOnly : true
        },{
            xtype : 'tbtext'
        },{
            xtype: 'templateTreeComboboxView',
            fieldLabel: '目标模板',
            editable:false,
            emptyText: '请选择',
            url: '/nodesetting/getCheckExpandNodeByIds',
            extraParams:{pcid:''},
            allowBlank: false,
            name:'targetSelectItem',
            itemId:'targetSelectItemID'
        },{
            xtype: 'checkbox',
            boxLabel: '包含档号设置',
            itemId: 'withCodeID'
        }
        ]
    }],
    buttons: [{
        text: '保存',
        itemId:'templateSaveBtnID'
    },{
        text: '取消',
        itemId:'templateCancelBtnID'
    }
    ]
});