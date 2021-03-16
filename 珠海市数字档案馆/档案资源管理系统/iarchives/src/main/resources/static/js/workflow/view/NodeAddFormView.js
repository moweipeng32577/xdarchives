/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('Workflow.view.NodeAddFormView', {
    extend: 'Ext.window.Window',
    xtype: 'nodeAddFormView',
    itemId:'nodeAddFormViewId',
    title: '增加节点',
    frame: true,
    resizable: true,
    width: 510,
    minWidth: 510,
    minHeight: 200,
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
        margin:'20',
        items: [
            { fieldLabel: '',name:'id',hidden:true},
            //{ fieldLabel: '',name:'orders',hidden:true,value:0},
            { fieldLabel: '',name:'workid',hidden:true},
            {
                fieldLabel: '节点名称',
                name:'text',
                allowBlank: false,
                minLength :1,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]
            },
            {
                fieldLabel: '节点描述',
                name:'desci',
                allowBlank: false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]
            },{
                fieldLabel: '节点顺序',
                name:'orders',
                allowBlank: false,
                itemId:'nodeSort',
                //hidden:this.title=='修改节点'?true:false,
                editable:true,//只能从下拉菜单中选择，不可手动编辑
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ]
            }
        ]
    }],

    buttons: [
        { text: '提交',itemId:'nodeAddSubmit'},
        { text: '关闭',itemId:'nodeAddClose'}
    ]
});