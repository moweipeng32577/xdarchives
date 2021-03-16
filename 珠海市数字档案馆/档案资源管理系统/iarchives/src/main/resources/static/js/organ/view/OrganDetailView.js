/**
 * Created by tanly on 2017/11/2 0002.
 */
var genderStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "单位", Value: 'unit' },
        { Name: "部门", Value: 'department'}
    ]
});
Ext.define('Organ.view.OrganDetailView', {
    extend: 'Ext.window.Window',
    xtype: 'organDetailView',
    itemId: 'organDetailViewid',
    title: '增加机构',
    width: 400,
    height: 500,
    modal: true,
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
        margin: '22',
        modelValidation: true,
        trackResetOnLoad:true,
        items: [{
            name: 'organid',
            hidden: true
        }, {
            name: 'servicesid',
            hidden: true
        }, {
            name: 'systemid',
            hidden: true
        }, {
            name: 'parentid',
            hidden: true
        }, {
            name: 'sortsequence',
            hidden: true
        }, {
            name: 'organlevel',
            hidden: true
        }, {
            fieldLabel: '机构名称',
            name: 'organname',
            allowBlank: false,
            itemId:'organitem',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]
        },{
            xtype: 'textfield',
            fieldLabel: '机构分类号',
            name:'code',
            itemId:'organcodeitemid'
        }, {
            xtype:'combo',
            fieldLabel: '机构类型',
            name: 'organtype',
            editable: false,
            allowBlank: false,
            store:genderStore,
            displayField: "Name",
            valueField: "Value",
            queryMode: "local",
            itemId:'typeItem',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]
        }, {
            fieldLabel: '服务名称',
            name: 'servicesname'
        }, {
            fieldLabel: '系统名称',
            name: 'systemname'
        },/* {
            xtype: 'textfield',
            fieldLabel: '层级',
            name: 'organlevel',
            allowBlank: false,
            regex: /^([1-9]\d{0,5})$/,
            regexText : '请输入小于6位的正整数',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]
        },*/ {
            fieldLabel: '机构编码',
            name: 'refid'
        }, {
            xtype: 'textfield',
            fieldLabel: '备注',
            name: 'desciption'
        }, {
            xtype: 'radiogroup',
            fieldLabel: '状态',
            items:[{
                boxLabel: '启用',
                name: 'usestatus',
                inputValue: '1',
                itemId:'useItemid'
            },{
                xtype:'displayfield'
            },{
                boxLabel: '停用',
                name: 'usestatus',
                inputValue: '0'
            }]
        }
        ]
    }],
    buttons: [{
        text: '预览数据节点',
        itemId: 'previewBtn'
    }, {
        text: '提交',
        itemId: 'submitBtn'
    }, {
        text: '取消',
        itemId: 'cancelBtn'
    }]
});