/**
 * Created by RonJiang on 2018/01/23.
 */
Ext.define('Comps.view.AdvancedSearchDynamicFormView',{
    extend:'Ext.form.Panel',
    xtype:'advancedSearchDynamicForm',
    inited:false,//标记当前表单组件加载状态，控制表单在第一次显示的时候构建组件，完成后值为true
    isFill:false,//记录表单是否初始化了默认值

    layout:'column',
    scrollable:true,
    bodyPadding:15,
    defaults:{
        layout:'form',
        xtype:'combobox',
        labelSeparator:'：'
    },
    listeners:{
        activate:function(form){
            //表单激活且未构建组件时，初始化表单组件
            if(!form.inited){
                form.initSearchConditionField();
            }
        }
    },
    
    //动态渲染表单控件
    initSearchConditionField:function(obj,flag){
        if(typeof obj=='undefined'){
            obj = this.getFormField();
        }
        var field = [];
        var activeFormField = [];//常用表单字段
        var inactiveFormField = [];//非常用表单字段
        for(var i = 0; i < obj.length; i++){
            if(obj[i].fieldcode=='fscount' || obj[i].fieldcode=='kccount'){
                continue;//高级检索表单不加载“份数”和“库存份数”字段
            }
            if(!obj[i].inactiveformfield){
                activeFormField.push(obj[i]);
                field.push(obj[i].fieldcode);
            }else{
                inactiveFormField.push(obj[i]);
            }
        }
        if(flag){    //目录高级检索
            for(var i=0; i < activeFormField.length; i++){
                var convertedField = this.getConvertedSearchField(activeFormField[i]);
                this.add(convertedField);
            }
            var inactiveFormFieldArr = [];
            for(var i=0; i < inactiveFormField.length; i++){//遍历非常用字段
                inactiveFormFieldArr.push(this.getConvertedSearchField(inactiveFormField[i]));//将非常用字段控件加入至数组存储
            }
            if(inactiveFormFieldArr.length>0){
                this.add({
                    layout : 'column',
                    xtype:'fieldset',
                    style:'background:#fff;padding-top:0px',
                    columnWidth:.98,
                    title: '其它字段',
                    collapsible: true,
                    collapsed:true,
                    autoScroll: true,
                    items:inactiveFormFieldArr
                });
            }
        }else {
        var inactiveField = [];
        var inactiveFormFieldArr = [];
        for(var i = 0; i < inactiveFormField.length; i++){//遍历非常用字段
        	inactiveField.push(inactiveFormField[i].fieldcode);
        }
        var value = '';
        Ext.Ajax.request({
			method: 'post',
			url:'/classifySearch/getLastSearchInfo',
			timeout:XD.timeout,
			scope: this,
			async: true,
			params: {
				nodeid: obj[0].nodeid,
				type: obj.type,
				field: field,
				inactiveField: inactiveField
			},
			success:function(res){
				value = Ext.decode(res.responseText).data;
				for(var i = 0; i < inactiveFormField.length; i++){
					var info = this.getConvertedSearchField(inactiveFormField[i]);
					if (value != '' && typeof(value) != 'undefined') {
						var fieldValue = value.split('∪')[1].split('∩')[i];
						if (fieldValue != 'null') {
							info.items[1].value = fieldValue;
                            this.isFill=true;
						}
					}
					inactiveFormFieldArr.push(info);//将非常用字段控件加入至数组存储
				}
				if(inactiveFormFieldArr.length > 0){
		            this.add({
		                layout : 'column',
		                xtype:'fieldset',
		                style:'background:#fff;padding-top:0px',
		                columnWidth:.98,
		                title: '其它字段',
		                collapsible: true,
		                collapsed:true,
		                autoScroll: true,
		                items:inactiveFormFieldArr
		            });
		        }
				for(var i = 0; i < activeFormField.length; i++){
					var convertedField = this.getConvertedSearchField(activeFormField[i]);
					if (value != '' && typeof(value) != 'undefined') {
						var fieldValue = value.split('∪')[0].split('∩')[i];
						if (fieldValue != 'null') {
							convertedField.items[1].value = fieldValue;
                            this.isFill=true;
						}
					}
					this.add(convertedField);
				}
			},
			failure:function(){
				Ext.MessageBox.hide();
				XD.msg('操作失败！');
			}
		});
        }
        this.inited = true;
    },

    getConvertedSearchField:function (template) {
        var operatorField = {//操作符下拉框
            xtype:'combobox',
            fieldLabel:template.fieldname,
            name:template.fieldcode+'OperatorCombo',
            store:[['like','类似于'],['notLike','不类似于'],['equal','等于'],['notEqual','不等于'],
                ['greaterThan','大于'],['greatAndEqual','大或等于'],['lessThan','小于'],
                ['lessAndEqual','小或等于'],['isNull','为空'],['isNotNull','不为空']],
            value:'like',
            editable:false,
            margin:'5 1 1 1',
            columnWidth:template.frows >= 1 ? 0.30 : 0.60
        };
        var contentField = {//检索内容输入框
            name:template.fieldcode,
            margin:'5 1 1 1',
            columnWidth:template.frows >= 1 ? (0.90-0.2-0.02) : (0.60-0.2-0.02)
        };
        var displayfieldField = {//预留空间
            xtype:'displayfield',
            margin:'5 1 1 1',
            columnWidth:template.frows >= 1 ? 0.01 : 0.02
        };
        return this.convertSearchField(template,operatorField,contentField,displayfieldField);
    },

    convertSearchField:function (template,operatorField,contentField,displayfieldField) {//根据不同的类型，返回不同的控件
        var container = {//容纳三个对象的容器，items属性根据控件类型不同另外定义
            xtype:'container',
            columnWidth:template.frows >= 1 ? 1 : 0.5,
            layout:'column',
            baseCls:'x-plain'
        };
        switch (template.ftype) {
            case 'string' :
            case '':
                //文本块及文本框
                if (template.frows > 1) {
                    contentField.xtype = 'textarea';
                    contentField.margin = '5 1 10 1';
                }else{
                    contentField.xtype = 'textfield';
                }
                container.items = [operatorField,contentField,displayfieldField];
                return container;
            case 'calculation':
            case 'keyword':
                contentField.xtype = 'textfield';
                container.items = [operatorField,contentField,displayfieldField];
                return container;
            case 'date' :
                //日期控件
                contentField.xtype = 'datefield';
                contentField.format = 'Ymd';
                contentField.formatText = template.ftip;
                contentField.maxValue = new Date();
                container.items = [operatorField,contentField,displayfieldField];
                return container;
            case 'enum' :
                //下拉框控件
                contentField.xtype = 'combobox';
                contentField.queryMode = 'local';
                contentField.forceSelection = true;
                contentField.displayField='code';
                contentField.valueField='code';
                contentField.store = Ext.create('Ext.data.Store',{
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
                container.items = [operatorField,contentField,displayfieldField];
                return container;
            case 'daterange' :
                return this.initDateRange(template,displayfieldField);
            default :
                container.items = [operatorField,contentField,displayfieldField];
                return container;
        }
    },

    //初始化时间段控件
    initDateRange:function(template,displayfieldField){
        var fieldArr = [];
        var enddayItemid = template.fieldcode + 'endday';
        fieldArr.push({
            xtype : 'datefield',
            margin:'5 1 1 1',
            fieldLabel:template.fieldname,
            itemId:template.fieldcode + 'startday',
            format : 'Ymd',
            maxValue : new Date(),
            name:template.fieldcode + 'startday',
            formatText : template.ftip,
            columnWidth:template.frows >= 1 ? (0.5-0.02) : ((1-0.04-0.04)/2),
            listeners:{
                //展开开始日期窗口，关闭结束日期窗口
                expand : function(field) {
                    var endday = this.findParentByType('advancedSearchDynamicForm').down('[itemId='+enddayItemid+']');
                    endday.collapse();
                },
                //选中开始日期，设置结束时期最小值，并弹出结束日期窗口
                select : function(field,value){
                    var endday = this.findParentByType('advancedSearchDynamicForm').down('[itemId='+enddayItemid+']');
                    endday.setMinValue(value);
                    Ext.defer(function(){
                        endday.expand();
                    },10);
                }
            }
        });
        fieldArr.push(displayfieldField);
        fieldArr.push({
            xtype : 'datefield',
            margin:'5 1 1 1',
            fieldLabel:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至',
            labelSeparator:'',
            itemId:template.fieldcode + 'endday',
            format : 'Ymd',
            maxValue : new Date(),
            name:template.fieldcode + 'endday',
            formatText : template.ftip,
            columnWidth:template.frows >= 1 ? (0.5-0.02) : ((1-0.04-0.04)/2)
        });
        fieldArr.push(displayfieldField);
        return {
            xtype:'container',
            columnWidth:template.frows >= 1 ? 1 : 0.5,
            layout:'column',
            baseCls:'x-plain',
            items:fieldArr
        };
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

    //获取声像表单字段
    getSxFormField:function () {
        var formField;
        Ext.Ajax.request({
            url: '/template/sxform',
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

    //清除后重新加载，解决不刷新问题
    _reset:function (form) {
        form.removeAll();//移除form中的所有表单控件
        var formField = form.getFormField();//根据节点id查询表单字段
        formField.type = '高级检索';
        form.templates = formField;
        form.initSearchConditionField(formField);//重新动态添加表单控件
        form.isFill=false;
    }
});