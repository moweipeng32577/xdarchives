Ext.define('DigitalProcess.view.EntryFormView',{
    extend:'Ext.panel.Panel',
    xtype:'EntryFormView',
    //标签页靠左配置--end
    //  bodyStyle :'overflow-x:hidden;overflow-y:scroll',
    layout:'border',
    items:[{
        region: 'east',
        width:'30%',
        xtype:'Dpdynamicform',
        calurl:'/acquisition/getCalValue',
        items:[{
            xtype:'hidden',
            name:'entryid'
        }]
    },{
            region: 'center',
            entrytype:'capture',
            xtype:'electronic'
    }
    ],

    buttons: [{
        xtype: 'label'  ,
        itemId: 'totalTextnew',
        text: '',
        style: {color: 'red'}
    },{
        xtype: 'label',
        itemId: 'nowTextnew',
        text: '',
        style: {color: 'red'}
    },{
        xtype:'button',
        itemId:'preButton',
        text:'上一条(ctrl+↑)'
    },{
        xtype:'button',
        itemId:'nextButton',
        text:'下一条(ctrl+↓)'
    }, {
        text: '保存并完成',
        itemId: 'saveAndfinish'
    }, {
        text: '保存',
        itemId: 'save'
    }, {
        text: '返回',
        itemId: 'back'
    }
    ]
});

Ext.define('DigitalProcess.view.DpDynamicFormView',{
    extend:'Ext.form.Panel',
    xtype:'Dpdynamicform',
    inited:false,//标记当前表单组件加载状态，控制表单在第一次显示的时候构建组件，完成后值为true

    layout:'column',
    scrollable:true,
    bodyPadding:15,
    trackResetOnLoad:true,
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelSeparator:'：'
    },
    listeners:{
        activate:function(form){
            //表单激活且未构建组件时，初始化表单组件
            if(!form.inited){
                form.initField();
            }
        }
    },

    //动态渲染表单控件
    initField:function(obj,operate){
        this.add({
            layout:'column',
            columnWidth:.98,
            xtype:'panel',
            itemId:'preNextPanel',
            items:[{
                layout:'hbox',
                items:[{
                    xtype:'button',
                    itemId:'preBtn',
                    text:'上一条',
                    margin:'-2 2 5 0'
                },{
                    xtype:'button',
                    itemId:'nextBtn',
                    text:'下一条',
                    margin:'-2 2 5 2'
                },{
                    xtype:'label',
                    itemId:'totalText',
                    text:'',
                    style:{color:'red'},
                    margin:'6 2 5 4'
                },{
                    xtype:'label',
                    itemId:'nowText',
                    text:'',
                    style:{color:'red'},
                    margin:'6 2 5 6'
                }
                ]
            }]
        });

        if(typeof obj=='undefined'){
            obj = this.getFormField();
        }
        var activeFormField = [];//常用表单字段
        var inactiveFormField = [];//非常用表单字段
        for(var i=0; i < obj.length; i++){
            if(!obj[i].inactiveformfield){
                activeFormField.push(obj[i]);
            }else{
                inactiveFormField.push(obj[i]);
            }
        }

        for(var i=0; i < activeFormField.length; i++){
            var convertedField = this.getConvertedField(operate,activeFormField[i]);
            this.add(convertedField);
        }
        var inactiveFormFieldArr = [];
        for(var i=0; i < inactiveFormField.length; i++){//遍历非常用字段
            inactiveFormFieldArr.push(this.getConvertedField(operate,inactiveFormField[i]));//将非常用字段控件加入至数组存储
        }
        if(inactiveFormFieldArr.length>0){
            this.add({
                layout : 'column',
                xtype:'fieldset',
                style:'background:#fff;padding-top:0px',
                columnWidth:.98,
                title: '其它字段',
                collapsible: true,
                collapsed:false,
                autoScroll: true,
                items:inactiveFormFieldArr
            });
        }
        this.inited = true;
    },

    getConvertedField:function (operate,template) {
        var field = {
            fieldLabel:template.fieldname+(template.frequired ? '<label style="color:#ff0b23;!important;font-size: 1.2em !important;">*</label>' : ''),
            name:template.fieldcode,
            value:template.fdefault,
            readOnly:template.freadonly,
            freadOnly:template.freadonly,
            allowBlank:template.frequired ? false : true,
            columnWidth: .98,
            xtype:'textfield',
            margin:'1 1 1 1'
        };

        return this.convertField(operate,template,field);
    },

    convertField:function (operate,template,field) {//根据不同的类型，返回不同的控件
        var fieldArr = [];
        var displayfieldField = {//预留空间(*或'')
            xtype:'displayfield',
            margin:'5 1 1 1',
            value: '',
            columnWidth:0
        };
        var container = {//容纳两个、三个或四个对象的容器，items属性根据控件类型不同另外定义
            xtype:'container',
            columnWidth:1,
            layout:'column',
            baseCls:'x-plain'
        };
        switch (template.ftype) {
            case 'keyword' :
                //主题词标引，先添加文本框
                // field.columnWidth = template.frows >= 1 ? (1-0.08-0.08-0.02) : (1-0.16-0.16-0.02);
                field.columnWidth =  .98;
                field.margin = '1 0 1 1';
                this.setTips(field,template);
                this.setValidate(field,template);
                fieldArr.push(field);
                //添加下拉框控件
                // fieldArr.push({
                //     name:'code',
                //     xtype:'combobox',
                //     queryMode:'local',
                //     forceSelection:true,
                //     displayField:'code',
                //     valueField:'code',
                //     editable:false,
                //     scope:this,
                //     columnWidth:template.frows >= 1 ? 0.08 : 0.16,
                //     store: {
                //         proxy: {
                //             type: 'ajax',
                //             extraParams:{
                //                 value:template.fenums
                //             },
                //             url: '/systemconfig/enums',
                //             reader: {
                //                 type: 'json'
                //             }
                //         },
                //         autoLoad: true
                //     },
                //     listeners: {
                //         select: function(){
                //             handler: {
                //                 var form = this.up("dynamicform");//指向当前表单
                //                 var keywordField = form.getForm().findField('keyword');//获取到表单中主题词框的值
                //                 var keyword = keywordField.value;
                //                 var code = this.value;// 下拉框中主题词的值
                //                 if(typeof(code) != 'undefined' && code != null && code != ''){//如果获取到的下拉框选择的值不为null
                //                     //如果主题词框中已经有值
                //                     if(typeof(keyword) != 'undefined' && keyword != null && keyword != ''){
                //                         keywordField.setValue(keyword + "/" + code);//那么用"/"将两个词进行分隔显示
                //                     } else {
                //                         keywordField.setValue(code);
                //                     }
                //                 }
                //             }
                //         }
                //     }
                // });
                // if (operate == 'show') {
                //     //添加按钮
                //     fieldArr.push({
                //         xtype:'button',
                //         text:'主题词标引',
                //         itemId:template.fieldcode + 'indexBtn',
                //         columnWidth:template.frows >= 1 ? 0.08 : 0.16,
                //         scope:this,
                //         disabled: false,
                //         handler:this.setKeyWord
                //     });
                //     fieldArr.push(displayfieldField);
                //     container.items = fieldArr;
                //     return container;
                // }
                // fieldArr.push({
                //     xtype:'button',
                //     text:'主题词标引',
                //     itemId:template.fieldcode + 'indexBtn',
                //     columnWidth:template.frows >= 1 ? 0.08 : 0.16,
                //     scope:this,
                //     disabled: true
                // });
                fieldArr.push(displayfieldField);
                container.items = fieldArr;
                return container;
            case 'string' :
                if(template.fieldcode == 'pages') {
                    // field.columnWidth = template.frows >= 1 ? (1-0.08-0.02) : (1-0.16-0.02);
                    field.columnWidth =  .98;
                    field.margin = '1 0 1 1';
                    field.regex = /^\d+$/;
                    field.regexText = '请输入正整型数字';
                    this.setTips(field,template);
                    this.setValidate(field,template);
                    fieldArr.push(field);
                    // if (operate == 'show') {
                    //     //添加按钮
                    //     fieldArr.push({
                    //         xtype:'button',
                    //         text:'获取',
                    //         itemId:template.fieldcode + 'pageBtn',
                    //         columnWidth:template.frows >= 1 ? 0.08 : 0.16,
                    //         margin:'1 1 1 0',
                    //         scope:this,
                    //         disabled: false,
                    //         handler:this.getPages
                    //     });
                    //     fieldArr.push(displayfieldField);
                    //     container.items = fieldArr;
                    //     return container;
                    // }
                    //添加按钮
                    // fieldArr.push({
                    //     xtype:'button',
                    //     text:'获取',
                    //     itemId:template.fieldcode + 'pageBtn',
                    //     columnWidth:template.frows >= 1 ? 0.08 : 0.16,
                    //     margin:'1 1 1 0',
                    //     scope:this,
                    //     disabled: true
                    // });
                    fieldArr.push(displayfieldField);
                    container.items = fieldArr;
                    return container;
                }
            case '':
                //文本块及文本框
                if (template.frows > 1) {
                    field.xtype = 'textarea';
                    field.margin = '1 1 10 1';
                }
                field.itemId = template.fieldcode;
                this.setTips(field,template);
                this.setValidate(field,template);
                fieldArr.push(field);
                fieldArr.push(displayfieldField);
                container.items = fieldArr;
                return container;
            case 'date' :
                //日期控件
                field.xtype = 'datefield';
                field.format = 'Ymd';
                field.altFormats='Ymd|Y.m.d|Y年m月d日|Y年n月j日|Y/n/j/Y|Y-n-j|Y/n/j/Y H:i:s|Ynj|Y-m-d H:i:s';//适配多种格式显示，只是显示，保存还是以Ymd格式保存
                this.setTips(field,template);
                this.setValidate(field,template);
                field.formatText = false;
                field.maxValue = new Date();
                fieldArr.push(field);
                fieldArr.push(displayfieldField);
                container.items = fieldArr;
                return container;
            case 'enum' :
                //下拉框控件
                field.xtype = 'combobox';
                field.queryMode = 'local';
                field.forceSelection = true;
                if(template.fenumsedit){
                    field.forceSelection = false;
                }
                field.displayField='code';
                field.valueField='code';
                this.setTips(field,template);
                this.setValidate(field,template);
                field.store = Ext.create('Ext.data.Store',{
                    proxy: {
                        type: 'ajax',
                        extraParams:{
                            value:template.fenums
                        },
                        url: '/systemconfig/enums',
                        reader: {
                            type: 'json'
                        }
                    },
                    autoLoad: true
                });
                fieldArr.push(field);
                fieldArr.push(displayfieldField);
                container.items = fieldArr;
                return container;
            case 'calculation' :
                //计算项，先添加一个文本框，然后添加一个按钮
                // field.columnWidth = template.frows >= 1 ? (1-0.08-0.02) : (1-0.16-0.02);
                field.columnWidth =  .98;
                field.margin = '1 0 1 1';
                field.regex = /^\d+$/;
                field.regexText = '请输入正整型数字';
                this.setTips(field,template);
                this.setValidate(field,template);
                fieldArr.push(field);
                // if (operate == 'show') {
                //     //添加按钮
                //     fieldArr.push({
                //         xtype:'button',
                //         text:'获取',
                //         itemId:template.fieldcode + 'calBtn',
                //         columnWidth:template.frows >= 1 ? 0.08 : 0.16,
                //         margin:'1 1 1 0',
                //         scope:this,
                //         disabled: false,
                //         handler:this.setCalValue
                //     });
                //     fieldArr.push(displayfieldField);
                //     container.items = fieldArr;
                //     return container;
                // }
                // //添加按钮
                // fieldArr.push({
                //     xtype:'button',
                //     text:'获取',
                //     itemId:template.fieldcode + 'calBtn',
                //     columnWidth:template.frows >= 1 ? 0.08 : 0.16,
                //     margin:'1 1 1 0',
                //     scope:this,
                //     disabled: true
                // });
                fieldArr.push(displayfieldField);
                container.items = fieldArr;
                return container;
            case 'daterange' :
                return this.initDateRange(template,displayfieldField);
            default :
                fieldArr.push(field);
                fieldArr.push(displayfieldField);
                container.items = fieldArr;
                return container;
        }
    },

    setTips:function(field,template){
        //field.blankText = '';
        if(template.ftip&&template.ftip!=''){
            field.listeners = {
                render: function(sender) {
                    new Ext.ToolTip({
                        target: sender.el,
                        trackMouse: true,
                        dismissDelay: 0,
                        anchor: 'buttom',
                        html: ' <i class="fa fa-info-circle"></i> '+template.ftip
                    });
                }
            };
        }
    },

    setValidate:function(field,template){
        switch (template.fvalidate) {
            case 'YearValidation' :
                field.regex = /^(1949|19[5-9]\d|20\d{2}|2100)$/;
                field.regexText = '请输入正确年份';
                break;
            case 'IDValidation':
                field.regex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
                field.regexText = '请输入正确身份证号码';
                break;
            case 'PhoneValidation':
                field.regex = /^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$|0?1[3|4|5|8][0-9]\d{8}/;
                field.regexText = '请输入正确电话号码';
                break;
            default:
                break;
        }
    },

    //初始化时间段控件
    initDateRange:function(template,displayfieldField){
        var fieldArr = [];
        var enddayItemid = template.fieldcode + 'endday';
        fieldArr.push({
            xtype: 'datefield',
            fieldLabel: template.fieldname+(template.frequired ? '<label style="color:#ff0b23;!important;font-size: 1.2em !important;">*</label>' : ''),
            itemId: template.fieldcode + 'startday',
            format: 'Ymd',
            altFormats:'Ymd|Y.m.d|Y年m月d日|Y年n月j日|Y/n/j/Y|Y-n-j|Y/n/j/Y H:i:s|Ynj|Y-m-d H:i:s',//适配多种格式显示，只是显示，保存还是以Ymd格式保存
            name: template.fieldcode + 'startday',
            formatText: template.ftip,
            maxValue: new Date(),
            columnWidth: template.frows >= 1 ? (0.5-0.02) : ((1-0.04-0.04)/2),
            listeners:{
                //展开开始日期窗口，关闭结束日期窗口
                expand: function(field) {
                    var endday = this.findParentByType('dynamicform').down('[itemId='+enddayItemid+']');
                    endday.collapse();
                },
                //选中开始日期，设置结束时期最小值，并弹出结束日期窗口
                select: function(field,value){
                    var endday = this.findParentByType('dynamicform').down('[itemId='+enddayItemid+']');
                    endday.setMinValue(value);
                    Ext.defer(function(){
                        endday.expand();
                    },10);
                }
            },
            readOnly:template.freadonly,
            allowBlank:template.frequired ? false : true
        });
        fieldArr.push(displayfieldField);
        fieldArr.push({
            xtype: 'datefield',
            fieldLabel: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至',
            labelSeparator: '',
            itemId: template.fieldcode + 'endday',
            format: 'Ymd',
            altFormats:'Ymd|Y.m.d|Y年m月d日|Y年n月j日|Y/n/j/Y|Y-n-j|Y/n/j/Y H:i:s|Ynj|Y-m-d H:i:s',//适配多种格式显示，只是显示，保存还是以Ymd格式保存
            name: template.fieldcode + 'endday',
            formatText: template.ftip,
            maxValue: new Date(),
            columnWidth: template.frows >= 1 ? (0.5-0.02) : (0.5-0.02),
            readOnly: template.freadonly,
            allowBlank: template.frequired ? false : true
        });
        fieldArr.push(displayfieldField);
        return {
            xtype:'container',
            columnWidth:template.frows >= 1 ? 1 : 1,
            layout:'column',
            baseCls:'x-plain',
            items:fieldArr
        };
    },

    //动态解析数据库日期范围数据并加载至两个datefield中
    initDaterangeContent:function(entry){//entry为异步请求返回的实体对象
        var form = this;
        var fields = form.getForm().getFields();//所有Field对象的集合
        var startdayName;//日期控件中开始日期框的name,与其itemId内容一致
        var enddayName;//日期控件中结束日期框的name,与其itemId内容一致
        for(var i = 0; i < fields.length; i++ ){
            var itemid = fields.get(i).getItemId();
            if(Ext.String.endsWith(itemid,'startday',true)){
                startdayName = itemid;
            }else if(Ext.String.endsWith(itemid,'endday',true)){
                enddayName = itemid;
            }
        }
        var daterangeFieldcode = startdayName.split('startday')[0];//日期范围控件的字段编码
        var daterangeValue = entry[daterangeFieldcode];//日期范围控件的数据库存值（XXXXXXXX-XXXXXXXX）
        if(daterangeValue==null){
            return;
        }
        var startfield = form.getForm().findField(startdayName);
        startfield.setValue(daterangeValue.split('-',2)[0]);
        startfield.wasDirty = false;
        var endfield = form.getForm().findField(enddayName);
        endfield.setValue(daterangeValue.split('-',2)[1]);
        endfield.wasDirty = false;
    },

    //拼接开始日期与结束日期
    getDaterangeValue:function(){
        var form = this;
        // var form = this.findFormView(btn).down('dynamicform');
        var fields = form.getForm().getFields();//所有Field对象的集合
        var startdayName;//日期控件中开始日期框的name,与其itemId内容一致
        var enddayName;//日期控件中结束日期框的name,与其itemId内容一致
        for(var i = 0; i < fields.length; i++ ){
            var itemid = fields.get(i).getItemId();
            if(Ext.String.endsWith(itemid,'startday',true)){
                startdayName = itemid;
            }else if(Ext.String.endsWith(itemid,'endday',true)){
                enddayName = itemid;
            }
        }
        var startdayValue = form.getForm().findField(startdayName).getValue();//若不修改日期，则获得string，若修改，则获得日期object
        var enddayValue = form.getForm().findField(enddayName).getValue();//若不修改日期，则获得string，若修改，则获得日期object
        if(startdayValue==null && enddayValue==null){//null类型为object，若为null，直接返回null
            return null;
        }
        if(startdayValue==null){
            startdayValue='';
        }
        if(enddayValue==null){
            enddayValue='';
        }
        if(typeof startdayValue=='object'){
            startdayValue = startdayValue.format('yyyyMMdd');
        }
        if(typeof enddayValue=='object'){
            enddayValue = enddayValue.format('yyyyMMdd');
        }
        return startdayValue+'-'+enddayValue;
    },

    //日期范围字段的字段编码获得
    getRangeDateForCode:function () {
        var form = this;
        var code = null;
        var fields = form.getForm().getFields();//所有Field对象的集合
        for (var i = 0; i < fields.length; i++) {
            var itemid = fields.get(i).getItemId();
            if (Ext.String.endsWith(itemid,'startday',true)) {
                code = itemid.split('startday')[0];
                break;
            }
        }
        return code;//返回字段编码为字符串
    },

    //设置主题词的值
    setKeyWord:function(){
        var form = this;//指向当前表单
        var titleField = form.getForm().findField('title');//获取到题名的值
        var keywordField = form.getForm().findField('keyword');//获取到主题词的值
        var title = titleField.value;
        var keyword = keywordField.value;
        if(typeof(title) != 'undefined' && title != null && title != ''){//如果题名的值不为null
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: "/systemconfig/findConfigValue",//判断题名是否为主题词名称
                params:{
                    value:title
                },
                success:function(response){
                    var data = Ext.decode(response.responseText).data;
                    if (data.length == 0) {
                        XD.msg('当前题名中无可标引的主题词!');
                    }else if (data.length == 1) {//如果文本框题名只有一个主题词时
                        keywordField.setValue(data[0]);//那么直接填充
                    } else {//如果有多个主题词
                        var str = "";
                        for (var i = 0; i < data.length; i++) {
                            if (i == data.length - 1) {
                                str = str + data[i];
                                break;
                            }
                            str = str + data[i] + "/";
                        }
                        keywordField.setValue(str);
                    }
                }
            });
        } else {
            XD.msg('当前题名中无可标引的主题词!');
            return;
        }
    },
    //获取电子文件页数
    getPages:function() {
        var eleFrom = this.ownerCt.down('electronic');
        var entryid = eleFrom.entryid;
        var entrytype = eleFrom.entrytype;
        var treeNodes = eleFrom.down('treepanel').getStore().getRoot().childNodes;
        var filefnids = new Array();
        for (var i = 0; i < treeNodes.length; i++) {
            filefnids.push(treeNodes[i].data.fnid);
        }
        var pagesfield = this.down('[name=pages]'); //获取条目表单中的pages字段
        if(treeNodes.length == 0){
            XD.confirm('当前条目没有上传电子原文，'+ '是否设置页数为 0', function () {
                pagesfield.setValue('0');  //将电子文件页数显示到表单中
                if(entryid != null) {
                    Ext.Ajax.request({
                        method: 'POST',
                        scope: this,
                        url: "/electronic/setPages/",
                        params: {
                            entrytype: entrytype,
                            entryid: entryid,
                            pages: 0
                        }
                    });
                }
            });
        } else {
            Ext.Msg.wait('正在读取页数，请耐心等待……', '正在读取');
            Ext.Ajax.request({
                method: 'POST',
                scope: this,
                url: "/electronic/getPages/",
                params: {
                    filefnids: filefnids,
                    entryid: entryid,
                    entrytype: entrytype
                },
                success: function (response) {
                    if (Ext.decode(response.responseText).success == false) {
                        if (Ext.decode(response.responseText).data != pagesfield.getValue()) {
                            XD.confirm(Ext.decode(response.responseText).msg + '其余电子文件总页数：' + Ext.decode(response.responseText).data + '！ '+ '与该条目页数不一致，是否更改？', function () {
                                pagesfield.setValue(Ext.decode(response.responseText).data);  //将电子文件页数显示到表单中
                                if(entryid != null) {
                                    Ext.Ajax.request({
                                        method: 'POST',
                                        scope: this,
                                        url: "/electronic/setPages/",
                                        params: {
                                            entrytype: entrytype,
                                            entryid: entryid,
                                            pages: Ext.decode(response.responseText).data
                                        }
                                    });
                                }
                            });
                        }
                        else{
                            XD.msg('电子文件总页数：' + Ext.decode(response.responseText).data + '，与该条目页数一致');
                            Ext.Msg.hide();
                        }
                    }
                    else {
                        if (Ext.decode(response.responseText).data != pagesfield.getValue()) {
                            XD.confirm('电子文件总页数：' + Ext.decode(response.responseText).data + '，'+ ' 与该条目页数不一致！是否更改？', function () {
                                pagesfield.setValue(Ext.decode(response.responseText).data);  //将电子文件页数显示到表单中
                                if(entryid != null) {
                                    Ext.Ajax.request({
                                        method: 'POST',
                                        scope: this,
                                        url: "/electronic/setPages/",
                                        params: {
                                            entrytype: entrytype,
                                            entryid: entryid,
                                            pages: Ext.decode(response.responseText).data
                                        }
                                    });
                                }
                            });
                        }
                        else{
                            XD.msg('电子文件总页数：' + Ext.decode(response.responseText).data + '，与该条目页数一致');
                            Ext.Msg.hide();
                        }
                    }
                }
            })
        }
    },
    getNodename: function (nodeid) {
        var nodename;
        Ext.Ajax.request({
            async:false,
            url: '/nodesetting/getFirstLevelNode/' + nodeid,
            success:function (response) {
                nodename = Ext.decode(response.responseText);
            }
        });
        return nodename;
    },

    //设置计算项的值
    setCalValue:function(){
        var form = this;
        var formValues = form.getValues();
        var formParams = {};
        for(var name in formValues){//遍历表单中的所有值
            formParams[name] = formValues[name];
        }
        formParams.nodeid = form.nodeid;
        formParams.nodename = this.getNodename(form.nodeid);
        formParams.docid = form.docid;
        formParams.type = form.type;
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
            },
            failure: function (form, action) {
                XD.msg(action.result.msg);
            }
        });
    },

    //设置档号值
    setArchivecodeValue:function () {
        var form = this;
        var nodename;
        Ext.Ajax.request({
            async:false,
            url: '/nodesetting/getFirstLevelNode/' + form.nodeid,
            success:function (response) {
                nodename = Ext.decode(response.responseText);
            }
        });
        return this.setArchivecodeValueWithNode(nodename);
    },

    setArchivecodeValueWithNode:function(nodename){
        var form = this;
        var archivecodeValue;
        var archivecodeWidget = form.getForm().findField('archivecode');

        if(!form.isValid()){
            var fieldObject = form.getForm().getValues();
            for(var key in fieldObject){
                if(fieldObject.hasOwnProperty(key)){
                    if(form.getForm().findField(key).allowBlank==false&&fieldObject[key]==''){
                        form.getForm().findField(key).focus(true, 100);
                        break;
                    }
                }
            }
            XD.msg('有必填项未填写');
            return;
        }
        var formfield = this.getFormField();
        var count = 0;
        for(var i=0;i<formfield.length;i++){
            var fdlength = formfield[i].fieldlength;
            if(fdlength!=null&&fdlength!=''){
                var fieldlength = parseInt(fdlength);
                var formtextfield = form.down('[name=' + formfield[i].fieldcode + ']');
                var valueslength = formtextfield.getValue().length;
                if (valueslength > fieldlength) {
                    formtextfield.setFieldStyle({'border': '0.8px solid red'});
                    this.render( formtextfield,fdlength);
                    count++;
                }
            }
        }
        if(count>0){
            XD.msg('有输入框输入超过有效长度');
            return;
        }
        if(!archivecodeWidget){
            return '无档号节点';
        }
        var formValues = form.getValues();
        var formParams = {};
        for(var name in formValues){//遍历表单中的所有值
            if (name == 'kccount' || name == 'fscount') {
                if (formValues[name] == '' || formValues[name] == null) {
                    formValues[name] = 0;
                }
            } else {
                formParams[name] = formValues[name];
            }
        }
        formParams.dataNodeid = form.nodeid;
        formParams.nodename = form.nodename;
        var archivecode = "";
        Ext.Ajax.request({//获取动态生成的档号
            url:'/codesetting/getArchivecodeValue',
            async:false,
            params:formParams,
            success:function(response){
                var responseText = Ext.decode(response.responseText);
                if(responseText.success==false){
                    XD.msg(responseText.msg);
                    return;
                }else{
                    archivecode = responseText.data;
                    archivecodeWidget.setValue(archivecode);
                    archivecodeValue = archivecodeWidget.getValue();
                    var descriptiondateField = form.getForm().findField('descriptiondate');
                    if(descriptiondateField){
                        descriptiondateField.setValue(Ext.util.Format.date(new Date(),'Y-m-d H:i:s'));
                    }
                }
            }
        });
        return archivecodeValue;
    },


    //点击查看、著录、修改时设置电子文件顶部按钮显示状态
    fileLabelStateChange:function (view,operate) {
        if(operate == 'look'||operate == 'lookfile'){
            for(var i=0;i<view.down('toolbar').query('button').length;i++){
                view.down('toolbar').query('button')[i].hide();//查看电子文件顶部按钮隐藏
            }
        }else{
            for(var i=0;i<view.down('toolbar').query('button').length;i++){
                view.down('toolbar').query('button')[i].show();//查看电子文件顶部按钮显示
            }
        }
    },

    //获取表单字段
    getFormField:function () {
        var formField;
        Ext.Ajax.request({
            url: '/template/form',
            async:false,
            params:{
                nodeid:this.nodeid
            },
            success: function (response) {
                formField = Ext.decode(response.responseText);
            }
        });
        return formField;
    },
    render : function(textfield, fdlength) {
        Ext.QuickTips.init();
        Ext.QuickTips.register({
            target : textfield.el,
            text : ' <i class="fa fa-info-circle"></i>  输入最大长度为'+fdlength+''
        })
    }

});

