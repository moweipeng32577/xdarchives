Ext.define('Template.view.EditOtherOptionFormView',{
    extend: 'Ext.window.Window',
    xtype: 'editOtherOptionFormView',
    itemId:'editOtherOptionFormViewId',
    closeToolText:'关闭',
    closeAction:'hide',
    width:'100%',
    height:'100%',
    layout:'border',
    items:[{
        xtype:'panel',
        margin:'15 15 15 40',
        region:'west',
        layout:'fit',
        width:'60%',
        height:'75%',
        // flex:1,
        items:[{
            margin:'5 5 5 5',
            xtype:'templateTableFieldGridView'
        }]
    },
    {
        xtype:'panel',
        margin: '20',

    items:[{
        width:'50%',
        height:'100%',
        region:'east',
        layout:'fit',
        xtype:'fieldset',
        title: '表单字段',
        style:'background:#fff;',
        autoHeight:true,
        labelWidth:60,
        labelAlign:'right',
        items:[{
            xtype: 'form',
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
                },{
                    xtype: "checkbox",
                    name: "archivecodeedit",
                    fieldLabel: "归档修改字段"
                }]
        }]
        }]
    }],
    buttons: [{
        text: '保存',
        itemId: 'codesettingSaveBtnId'
    },{
        text:'返回',
        itemId:'back'
    }]

})
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