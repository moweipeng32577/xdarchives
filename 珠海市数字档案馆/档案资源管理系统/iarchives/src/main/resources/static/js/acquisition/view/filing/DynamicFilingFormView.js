/**
 * Created by RonJiang on 2017/12/1 0001.
 */
Ext.define('Acquisition.view.filing.DynamicFilingFormView',{
    extend:'Ext.form.Panel',
    xtype:'dynamicfilingform',
    inited:false,//标记当前表单组件加载状态，控制表单在第一次显示的时候构建组件，完成后值为true

    layout:'column',
    scrollable:true,
    bodyPadding:15,
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelSeparator:'：'
    },
    listeners:{
        activate:function(form){
            //表单激活且未构建组件时，初始化表单组件
            if(!form.inited){
                form.initFilingField();
            }
        }
    },

    //动态渲染表单控件
    initFilingField:function(){
        var fieldArr = [];
        Ext.Ajax.request({
            url: '/template/codesettingTemplate',
            async:false,
            params:{
                nodeid:this.nodeid
            },
            scope:this,
            success: function(response) {
                var responseText = Ext.decode(response.responseText);
                if(responseText.success==false){
                    XD.msg(responseText.msg);
                }else{
                    var obj = responseText.data;
                    if(obj.length==0){
                        XD.msg('请检查对应分类的模板及档号设置信息是否正确');
                        this.initedstate = false;//用于判断是否允许进入归档第二步操作
                        return;
                    }else{
                        this.initedstate = true;
                    }
                    for(var i=0; i < obj.length; i++){
                        var field = {
                            fieldLabel:obj[i].fieldname,
                            name:obj[i].fieldcode,
                            allowBlank:obj[i].frequired ? false:true,
                            columnWidth:0.49
                        };
                        //根据不同的类型，渲染不同的控件
                        switch (obj[i].ftype) {
                            case 'string' :
                            case 'keyword':
                            case '':
                                //文本框
                                if(obj[i].fieldcode=='organ' || obj[i].fieldcode=='funds' || obj[i].fieldcode=='filingyear'){
                                    field.value = this.getDefaultOrganValue(obj[i].nodeid, obj[i].ftype, obj[i].fieldcode);
                                } else {
                                    field.value = obj[i].fdefault;
                                }
                                if (obj[i].freadonly) {
                                    field.editable = false;
                                } else {
                                    field.editable = true;
                                }
                                field.margin = '0 0 0 10';
                                this.add(field);
                                this.add({
                                    columnWidth: 0.01,
                                    xtype: 'displayfield',
                                    value: field.allowBlank ? '':'<label style="color:#ff0b23;!important;">*</label>'
                                });
                                break;
                            case 'enum' :
                                //下拉框控件
                                field.xtype = 'combobox';
                                field.queryMode = 'local';
                                field.forceSelection = obj[i].fenumsedit?false:true;
                                field.displayField='code';
                                field.valueField='code';
                                field.editable = obj[i].fenumsedit?true:false;
                                field.margin = '0 0 0 10';
                                field.store = Ext.create('Ext.data.Store',{
                                    proxy: {
                                        type: 'ajax',
                                        extraParams:{
                                            value:obj[i].fenums
                                        },
                                        url: '/systemconfig/enums',
                                        reader: {
                                            type: 'json'
                                        }
                                    },
                                    autoLoad: true
                                });
                                field.listeners = {
                                    afterrender:function(combo){
                                        var store = combo.getStore();
                                        store.load(function(){
                                            if(this.getCount() > 0){
                                                combo.select(this.getAt(0));
                                            }
                                        });
                                    }
                                };
                                if(obj[i].fieldcode=='entryretention'){
                                    var forceselect = obj[i].fenumsedit ? false : true;
                                    field.fieldLabel='保管期限：';
                                    field.columnWidth = 0.23;
                                    field.margin = '0 0 0 10';
                                    field.disabled = true;
                                    field.itemId = 'appraisaltype';
                                    field.name = 'appraisaltype';
                                    field.displayField = 'name';
                                    field.valueField = 'item';
                                    field.store = Ext.create('Ext.data.Store',{
                                        proxy: {
                                            type: 'ajax',
                                            url: '/appraisalStandard/enums',
                                            reader: {
                                                type: 'json'
                                            }
                                        },
                                        autoLoad: true
                                    });
                                    this.add(field);
                                    this.add({
                                        xtype: 'checkbox',
                                        itemId:'autoAppraisal',
                                        name:'autoAppraisal',
                                        inputValue: true,
                                        boxLabel: '自动鉴定',
                                        checked: false,
                                        margin:'0 0 0 10',
                                        columnWidth: 0.11
                                    });
                                    this.add({
                                        xtype: 'combobox',
                                        itemId:obj[i].fieldcode,
                                        name:obj[i].fieldcode,
                                        queryMode: 'local',
                                        forceSelection: forceselect,
                                        displayField: 'code',
                                        valueField: 'code',
                                        editable: true,
                                        columnWidth: 0.15,
                                        margin:'0 0 0 10',
                                        store: Ext.create('Ext.data.Store',{
                                            proxy: {
                                                type: 'ajax',
                                                extraParams:{
                                                    value:obj[i].fenums
                                                },
                                                url: '/systemconfig/enums',
                                                reader: {
                                                    type: 'json'
                                                }
                                            },
                                            autoLoad: true
                                        }),
                                        listeners:{
                                            afterrender:function(combo){
                                                var store = combo.getStore();
                                                store.load(function(){
                                                    if(this.getCount() > 0){
                                                        combo.select(this.getAt(0));
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    this.add({
                                        columnWidth: 0.01,
                                        xtype: 'displayfield',
                                        value: field.allowBlank ? '':'<label style="color:#ff0b23;!important;">*</label>'
                                    });
                                } else {
                                    this.add(field);
                                    this.add({
                                        columnWidth: 0.01,
                                        xtype: 'displayfield',
                                        value: field.allowBlank ? '' : '<label' +
                                            ' style="color:#ff0b23;!important;">*</label>'
                                    });
                                }
                                break;
                            case 'calculation' ://计算项字段不在表单中显示
                                field.columnWidth=0.39;
                                if (obj[i].freadonly) {
                                    field.editable = false;
                                } else {
                                    field.editable = true;
                                }
                                field.margin = '0 0 0 10';
                                field.itemId = obj[i].fieldcode;
                                field.name = obj[i].fieldcode;
                                field.displayField = obj[i].fieldcode;
                                field.valueField = obj[i].fieldcode;
                                field.hidden = false;
                                this.add(field);
                                //添加按钮
                                this.add({
                                    xtype:'button',
                                    text:'获取',
                                    itemId:obj[i].fieldcode + 'calBtn',
                                    columnWidth:0.1,
                                    margin:'0 0 0 10',
                                    scope:this,
                                    hidden:false,
                                    handler:this.setCalValue
                                });
                                this.add({
                                    columnWidth: 0.01,
                                    xtype: 'displayfield',
                                    hidden: false,
                                    itemId: obj[i].fieldcode + 'Field',
                                    value: field.allowBlank ? '':'<label style="color:#ff0b23;!important;">*</label>'
                                });
                                break;
                            default :
                                field.margin = '0 0 0 10';
                                this.add(field);
                                this.add({
                                    columnWidth: 0.01,
                                    xtype: 'displayfield',
                                    value: field.allowBlank ? '':'<label style="color:#ff0b23;!important;">*</label>'
                                });
                                break;
                        }
                        // this.initRequire(obj[i]);
                        this.reset();
                    }
                    this.initGenArchivecodeBtn();
                }
            }
        });
        this.inited = true;
    },

    getEntryretention: function (obj) {
        var is = false;
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].fieldcode == 'entryretention') {
                is = true;
            }
        }
        return is;
    },

    initGenArchivecodeBtn: function () {
        /*this.add({
            xtype:'displayfield',
            columnWidth:0.85
        });*/
        this.add({
            xtype:'label',
            columnWidth:0.85,
            text: '温馨提示：生成档号，是根据下面列表中当前显示的先后顺序进行生成档号的。',
            itemId:'gdtiId',
            style: {
                color: 'red',
                'font-size': '24px',
                'font-weight': 'bold'
            }
        });
        this.add({
            xtype:'button',
            text:'生成档号',
            itemId:'generateArchivecode',
            columnWidth:0.15,
            margin:'10 20 0 0'
        });
        this.add({
            xtype:'label',
            columnWidth:1,
            text: '操作提示：先确定归档范围，可到"未归"中加入预归记录；再整理归档顺序，通过设置归档排序、上下移调序实现；最后生成档号，修改确定后进行归档。',
            itemId:'gdtiIdTwo',
            style: {
                color: 'blue',
                'font-size': '24px',
                'font-weight': 'bold'
            }
        });
    },

    //初始化必填项标识
    // initRequire:function(template){
    //     //必填项在控件后面添加符号“*”，非必填项添加空格保持控件对齐
    //     this.add({
    //         xtype:'displayfield',
    //         value:template.frequired ? '<label style="color:#ff0b23;!important;">*</label>' : '',
    //         columnWidth:0.02,
    //         margin:'1 5 1 3'
    //     });
    // },

    //设置计算项的值
    setCalValue:function(){
        var form = this;
        var formValues = form.getValues();
        var formParams = {};
        for(var name in formValues){//遍历表单中的所有值
            formParams[name] = formValues[name];
        }
        formParams.nodeid = form.nodeid;
        var archive = '';
        var calFieldName = '';
        var calValue = '';
        Ext.Ajax.request({//计算项的数值获取并设置
            url:form.calurl,//动态URL
            async:true,
            params:formParams,
            success:function(response){
                var result = Ext.decode(response.responseText).data;
                if(!result){
                    XD.msg(Ext.decode(response.responseText).msg);
                }else{
                    calFieldName = result.calFieldName;
                    calValue = result.calValueStr;
                    archive = result.archive;
                }
                var calField = form.getForm().findField(calFieldName);
                if(calField==null){
                    return;
                }
                calField.setValue(calValue);//设置档号最后一个构成字段的值，填充至文本框中
                var archiveCode = form.getForm().findField('archivecode');
                if(archiveCode==null){
                    return;
                }
                archiveCode.setValue(archive);
            }
        });
    },
    getDefaultOrganValue: function (nodeid, type, field) {
        var fundsValue;
        Ext.Ajax.request({
        	params: {
        		nodeid: nodeid,
        		type: type,
        		field: field
        	},
            method:'POST',
            async:false,
            url: '/template/getDefault',
            success: function (response) {
                fundsValue = Ext.decode(response.responseText).data;
            }
        });
        return fundsValue;
    }
});