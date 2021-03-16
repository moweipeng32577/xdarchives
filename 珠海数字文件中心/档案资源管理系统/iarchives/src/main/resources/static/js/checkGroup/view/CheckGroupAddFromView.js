/**
 * Created by Administrator on 2018/11/30.
 */

Ext.define('CheckGroup.view.CheckGroupAddFromView', {
    extend: 'Ext.window.Window',
    xtype: 'checkGroupAddFormView',
    itemId:'checkGroupAddFormViewid',
    title: '增加质检组',
    frame: true,
    resizable: true,
    width: 410,
    minWidth: 410,
    minHeight: 150,
    modal:true,
    closeToolText:'关闭',
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

    items: [{
        xtype: 'form',
        modelValidation: true,
        margin: '15',
        items: [
            { fieldLabel: '',name:'checkgroupid',hidden:true},
            { fieldLabel: '质检组名',name:'groupname',allowBlank: false,afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]},
            { fieldLabel: '描述',name:'desci'},
            {
                xtype: 'radiogroup',
                fieldLabel: '类型',
                hidden:false,
                items:[{
                    boxLabel: '质检',
                    name: 'type',
                    inputValue: '质检',
                    checked:'true'
                }
                    ,{
                        xtype:'displayfield'
                    },{
                        boxLabel: '验收',
                        name: 'type',
                        inputValue: '验收'
                    }
                ]
            }
        ]
    }],

    buttons: [
        { text: '提交',itemId:'checkGroupAddSubmit'},
        { text: '关闭',itemId:'checkGroupAddClose'}
    ]
});
