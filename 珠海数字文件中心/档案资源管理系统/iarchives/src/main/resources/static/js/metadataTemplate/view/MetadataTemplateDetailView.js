/**
 * Created by tanly on 2017/11/8 0002.
 */

Ext.define('MetadataTemplate.view.MetadataTemplateDetailView', {
    extend: 'Ext.window.Window',
    xtype: 'templateDetailView',
    itemId:'templateDetailViewid',
    width:'100%',
    height:'100%',
    header:false,
    autoScroll : true,
    modal:true,
    resizable: false,
    closeToolText:'关闭',
    items:[{
        xtype:'form',
        margin: '40',
        modelValidation: true,
        items:[{
            layout: 'column',
            items:[{
                columnWidth: .5,
                margin: '15 40 0 0',
                layout:{
                    type:'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype:'textfield',
                    fieldLabel: '',
                    name:'templateid',
                    hidden:true
                },{
                    xtype:'textfield',
                    fieldLabel: '',
                    name:'classify',
                    hidden:true
                },{
                    xtype:'textfield',
                    fieldLabel:'字段编码',
                    name:'fieldcode',
                    labelWidth:110,
                    editable:false
                },{
                    xtype:'textfield',
                    fieldLabel:'字段长度',
                    name:'fdlength',
                    labelWidth:110
                },{
                    xtype:'textfield',
                    fieldLabel:'元数据名称',
                    name:'fieldname',
                    labelWidth:110
                },{
                    xtype:'textfield',
                    fieldLabel:'元数据编码',
                    name:'metadatacode',
                    labelWidth:110
                },{
                    xtype:'textfield',
                    fieldLabel:'字段所属表',
                    name:'fieldtable',
                    labelWidth:110
                },{
                    xtype:'fieldset',
                    style:'background:#fff;padding-top:0px',
                    title: '列表字段',
                    autoHeight:true,
                    labelWidth:60,
                    labelAlign:'right',
                    animCollapse :true,
                    items:[{
                        xtype: "checkbox",
                        name: 'gfield',
                        inputValue: "true",
                        itemId:'isGridID',
                        fieldLabel: '是否为列表字段',
                        labelWidth:140,
                        handler: fieldsetHanlder
                    },{
                        layout: "form",
                        style: "margin-left:20px",
                        itemId:'isgridsetting',
                        items: [{
                            xtype:'textfield',
                            name: "gwidth",
                            fieldLabel: "字段宽度",
                            regex: /^(0|[1-9][0-9]*)$/,
                            regexText : '请输入正确的数字'
                        },{
                            xtype:'numberfield',
                            name: "gsequence",
                            fieldLabel: "排序",
                            regex: /^(0|[1-9][0-9]*)$/,
                            regexText : '请输入正确的数字'
                        },{
                            xtype: "checkbox",
                            name: "ghidden",
                            fieldLabel: "是否为隐藏字段"
                        }]
                    }]
                },{
                    xtype:'fieldset',
                    title: '检索字段',
                    style:'background:#fff;padding-top:0px',
                    autoHeight:true,
                    labelWidth:60,
                    labelAlign:'right',
                    items:[{
                        xtype: "checkbox",
                        name: 'qfield',
                        inputValue: "true",
                        fieldLabel: '是否为检索字段',
                        labelWidth:140,
                        handler: fieldsetHanlder
                    },{
                        layout: "form",
                        style: "margin-left:20px",
                        itemId:'isquerysetting',
                        items: [{
                                xtype:'numberfield',
                                name: "qsequence",
                                fieldLabel: "排序",
                                regex: /^(0|[1-9][0-9]*)$/,
                                regexText : '请输入正确的数字'
                            }
                        ]
                    }]
                }]
            },{
                columnWidth: .5,
                margin:'0 0 0 40',
                items: [{
                    xtype:'fieldset',
                    title: '表单字段',
                    style:'background:#fff;',
                    autoHeight:true,
                    labelWidth:60,
                    labelAlign:'right',
                    items:[{
                        xtype: "checkbox",
                        name: 'ffield',
                        inputValue: "true",
                        itemId:'Isform',
                        fieldLabel: '是否为表单字段',
                        labelWidth:140,
                        handler: fieldsetHanlder
                    },{
                        layout: "form",
                        style:"margin-left:20px;margin-bottom:43px",
                        itemId:'isformsetting',
                        items: [{
                            xtype:'textfield',
                            name: "fdefault",
                            itemId:'defaultValue',
                            fieldLabel: "默认值"
                        },{
                            xtype:'textfield',
                            name: "fieldlength",
                            itemId:'fieldlengthValue',
                            fieldLabel: "字段位数"
                        },
                        initComboItem({
                            name: "ftype",
                            fieldLabel: "字段类型",
                            store: [
                                ["string", "字符型"],
                                ["calculation", "统计型"],
                                ["enum","枚举型"],
                                ["date", "日期型"],
                                ["daterange", "日期范围型"],
                                ["keyword","主题词型"]
                            ],
                            listeners:{
                                render:function(combo){
                                    var enums = this.findParentByType('form').getForm().findField('fenums');
                                    if('enum' == combo.getValue() || 'keyword' == combo.getValue()){
                                        enums.enable();
                                    }else{
                                        enums.disable();
                                    }
                                },
                                afterrender:function(combo){
                                    var store = combo.getStore();
                                    var value = combo.getValue();
                                    store.load(function(){
                                        if(this.getCount() > 0 && !value){
                                            combo.select(this.getAt(0));
                                        }
                                    });
                                },
                                select:function(combo){
                                    var enums = this.findParentByType('form').getForm().findField('fenums');
                                    if('keyword' == combo.getValue()){
                                        enums.enable();
                                        for(var i=0;i<enums.getStore().totalCount;i++){
                                            if(enums.getStore().getAt(i).data.code=='主题词'){
                                                enums.select(enums.getStore().getAt(i));
                                                break;
                                            }
                                        }
                                    }else if('enum' == combo.getValue()){
                                        enums.enable();
                                    }else{
                                        enums.disable();
                                    }
                                }
                            }
                        }),
                        initComboItem({
                            name: "fvalidate",
                            fieldLabel: "检验规则",
                            store: [
                                ["NoValidation", "不校验"],
                                ["YearValidation", "年份校验"],
                                ["IDValidation", "身份证校验"],
                                ["PhoneValidation", "电话号码校验"]
                            ]
                        }),
                        initComboItem({
                            disabled:true,
                            name: "fenums",
                            fieldLabel: "枚举值",
                            valueField:'value',
                            displayField:'code',
                            store: {
                                proxy: {
                                    type: 'ajax',
                                    url: '/systemconfig/configs',
                                    reader: {
                                        type: 'json'
                                    }
                                },
                                autoLoad: true
                            }
                        }),{
                            xtype:'textfield',
                            name: "ftip",
                            fieldLabel: "字段提醒"
                        },{
                            xtype:'numberfield',
                            name: "fsequence",
                            fieldLabel: "排序",
                            regex: /^(0|[1-9][0-9]*)$/,
                            regexText : '请输入正确的数字'
                        },{
                            xtype:'textfield',
                            name: "frows",
                            fieldLabel: "文本框行数",
                            regex: /^(0|[1-9][0-9]*)$/,
                            regexText : '请输入正确的数字'
                        },{
                            xtype: "checkbox",
                            name: "frequired",
                            fieldLabel: "是否为必填项"
                        },{
                            xtype: "checkbox",
                            name: "fenumsedit",
                            fieldLabel: "枚举项是否可输入"
                        },{
                            xtype: "checkbox",
                            name: "freadonly",
                            fieldLabel: "是否只读"
                        },{
                            xtype: "checkbox",
                            name: "inactiveformfield",
                            fieldLabel: "是否为非常用字段"
                        },initComboItem(
                                {
                                    disabled:false,
                                    name: "fieldgid",
                                    fieldLabel: "字段分组",
                                    valueField:'groupid',
                                    displayField:'groupname',
                                    store: {
                                        proxy: {
                                            type: 'ajax',
                                            url: '/metadataTemplate/groupField',
                                            reader: {
                                                type: 'json'
                                            }
                                        },
                                        autoLoad: true
                                    }
                                }
                            )]
                    }]
                }]
            }]
        }]
    }],
    buttons: [{
        text: '保存',
        itemId: 'templateSaveBtnID'
    }, {
        text: '取消',
        itemId: 'templateCancelBtnID'
    }]
});

function fieldsetHanlder(sender, checked) {
    var panel = sender.nextSibling();
    var templateDetailView = panel.up('templateDetailView');
    var form = templateDetailView.down('form');
    var formValues = form.getForm().getValues();
    var fieldcode = formValues['fieldcode'];
    if(panel.isVisible()){
        if (checked) {
            panel.el.slideIn();
            if(fieldcode=='fscount' || fieldcode=='kccount'){
                //设置默认份数及库存份数为1
                form.down('[itemId=defaultValue]').setValue('1');
            }
        } else {
            panel.el.slideOut();
        }
    }
}

function initComboItem(config) {
    return Ext.apply({
        xtype: "combo",
        hiddenName: config.name,
        mode: "local",
        triggerAction: "all",
        forceSelection: true,
        editable:false
    }, config);
}