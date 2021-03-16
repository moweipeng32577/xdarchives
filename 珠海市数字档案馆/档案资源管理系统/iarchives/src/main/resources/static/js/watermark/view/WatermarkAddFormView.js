var colorStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "黑色", Value: 'black' },
        { Name: "红色", Value: 'red' },
        { Name: "蓝色", Value: 'blue' },
        { Name: "绿色", Value: 'green'},
        { Name: "黄色", Value: 'yellow' }
    ]
});

var ztStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "上居左", Value: '1' },
        { Name: "上居中", Value: '2'},
        { Name: "上居右", Value: '3' },
        { Name: "中居左", Value: '4'},
        { Name: "中居中", Value: '5' },
        { Name: "中居右", Value: '6'},
        { Name: "下居左", Value: '7'},
        { Name: "下居中", Value: '8' },
        { Name: "下居右", Value: '9'}
    ]
});

Ext.define('Watermark.view.WatermarkAddFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'WatermarkAddFormView',
    itemId: 'WatermarkAddFormViewid',
    nodeid:'',
    title: '',
    modal: true,
    layout:'card',
    activeItem:0,
    isDigital:false,
    initComponent: function() {
        var me = this;
        me.initItemsX();//初始化items
        this.callParent();
    },
    initItemsX: function(){
        var me = this;
        me.items = [{
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            xtype: 'form',
            itemId: 'lsformitemid',
            margin: '22',
            items: [{
                xtype:'textfield',
                fieldLabel: '',
                name: 'docid',
                hidden: true,
                // itemId: 'nodeiditemid'
            },
                { xtype:'textfield',itemId: 'id',fieldLabel: '',name:'id',hidden:true},
                { xtype:'textfield',itemId: 'watermark_picture_path',fieldLabel: '',name:'watermark_picture_path',hidden:true},
                {
                xtype:'textarea',
                fieldLabel: '水印说明',
                name: 'title',
                itemId: 'refiditemid',
                emptyText: "",
                regexText   : "不能为空",
                allowBlank  : false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填选项">*</span>'
                ]
            }, {
                layout: 'column',
                itemId:'multcolumnId',
                items: [{
                    columnWidth: .47,
                    items: [
                        {
                            xtype:'numberfield',
                            itemId:'coordinates1',
                            name: "coordinates",
                            fieldLabel: "X轴坐标",
                            style: 'width: 100%',
                            value:0,
                            allowDecimals : false,    //是否允许小数
                            regexText : '请输入正确的数字'
                        }
                    ]
                },{
                    columnWidth:.06,
                    xtype:'displayfield'
                },{
                    columnWidth: .47,
                    items: [
                        {
                            xtype:'numberfield',
                            name: "coordinates",
                            itemId:'coordinates2',
                            fieldLabel: "Y轴坐标",
                            style: 'width: 100%',
                            value:0,
                            allowDecimals : false,    //是否允许小数
                            //regex: /^(0|[1-9][0-9]*)$/,
                            regexText : '请输入正确的数字'
                        }
                    ]
                },
                    {

                        columnWidth: .47,
                        items: [{
                            width:'100%',
                            xtype: "combobox",
                            name: "location",
                            itemId:'location',
                            fieldLabel: "水印位置",
                            store: ztStore,
                            editable: false,
                            allowBlank: false,
                            displayField: "Name",
                            valueField: "Value",
                            emptyText: "--请选择--",
                            queryMode: "local",
                            listeners: {
                                //搜索条件默认选择第一项
                                afterrender: function (combo) {
                                    var store = combo.getStore();
                                    if (store.getCount() > 0) {
                                        combo.select(store.getAt(0));
                                    }
                                }
                            }
                        }]
                    },{
                    columnWidth:.06,
                    xtype:'displayfield'
                },{
                    columnWidth: .47,
                    items: [
                        {
                            xtype:'numberfield',
                            name: "degree",
                            fieldLabel: "水印角度",
                            style: 'width: 100%',
                            allowDecimals : false,    //是否允许小数
                            value:0,
                            regexText : '请输入正确的数字'
                        }
                    ]

                },
                    {
                        columnWidth: .47,
                        items:{
                            xtype:'numberfield',
                            name: "transparency",
                            fieldLabel: "透明度",
                            decimalPrecision:1,
                            minValue:0,
                            maxValue:1,
                            value:0.5,
                            step:0.05,
                            allowDecimals : true,
                            decimalPrecision: 2,
                            style: 'width: 100%',
                            value:1,
                            regexText : '请输入正确的数字'
                        }

                    },
                    {
                        columnWidth:.06,
                        xtype:'displayfield'
                    },
                    {

                        columnWidth:.47,
                        items: [{
                            width:'100%',
                            xtype: "combobox",
                            name: "color",
                            itemId:'watermarkTextColor',
                            fieldLabel: "水印颜色",
                            store: colorStore,
                            editable: false,
                            allowBlank: false,
                            displayField: "Name",
                            valueField: "Value",
                            emptyText: "--请选择--",
                            queryMode: "local",
                            listeners: {
                                //搜索条件默认选择第一项
                                afterrender: function (combo) {
                                    var store = combo.getStore();
                                    if (store.getCount() > 0) {
                                        combo.select(store.getAt(0));
                                    }
                                }
                            }
                        }]
                    },
                    {
                        columnWidth: 1,
                        items:{
                            width:'100%',
                            itemId:'watermarktext',
                            xtype: "textfield",
                            name: "watermark_picture_text",
                            fieldLabel: "水印文本",
                            allowBlank  : false,
                            afterLabelTextTpl: [
                                '<span style="color:red;font-weight:bold" data-qtip="必填选项">*</span>'
                            ]
                        }
                    },
                    {
                        columnWidth: .47,
                        items:{
                            xtype:'numberfield',
                            name: "fontsize",
                            fieldLabel: "字体大小",
                            decimalPrecision:1,
                            minValue:0,
                            maxValue:999,
                            style: 'width: 100%',
                            value:1,
                            regexText : '请输入正确的数字'
                        }

                    },
                    {
                        columnWidth:.06,
                        xtype:'displayfield'
                    },
                    {
                        columnWidth: .47,
                        items:{
                            xtype:'numberfield',
                            name: "linewidth",
                            fieldLabel: "字体加粗大小",
                            decimalPrecision:1,
                            minValue:0,
                            maxValue:999,
                            style: 'width: 100%',
                            value:1,
                            regexText : '请输入正确的数字'
                        }

                    },
                    {
                        columnWidth: 1,
                        items:{
                            xtype:'numberfield',
                            name: "spacing",
                            fieldLabel: "文字间距",
                            decimalPrecision:1,
                            minValue:0,
                            maxValue:999,
                            style: 'width: 100%',
                            value:1,
                            regexText : '请输入正确的数字'
                        }

                    },
                    {
                        columnWidth: .35,
                        itemId:'ispicture',
                        xtype: 'radiogroup',
                        fieldLabel: '图片水印',
                        items:[{
                            boxLabel: '是',
                            name: 'ispicture',
                            inputValue: '1',
                            checked:'true',
                            listeners: {
                                'render':function(view){
                                    var watermarktext = this.up('form').down('[itemId=watermarktext]');
                                    var watermarkTextColor = this.up('form').down('[itemId=watermarkTextColor]');
                                    watermarktext.disable(true);
                                    watermarkTextColor.disable(true);
                                },
                                'change':function(group,checked){
                                    //coordinates1
                                    var watermarktext = this.up('form').down('[itemId=watermarktext]');
                                    var watermarkTextColor = this.up('form').down('[itemId=watermarkTextColor]');
                                    var afterBtn = this.up('WatermarkAddFormView').down('[itemId=afterId]');
                                    var nextBtn = this.up('WatermarkAddFormView').down('[itemId=nextId]');
                                    if(checked){
                                        watermarktext.disable(true);
                                        watermarkTextColor.disable(true);
                                        // afterBtn.show();
                                        nextBtn.show();
                                    }else{
                                        watermarktext.enable(true);
                                        watermarkTextColor.enable(true);
                                        // afterBtn.hide();
                                        nextBtn.hide();
                                    }
                                }
                            }
                        },{
                            boxLabel: '否',
                            name: 'ispicture',
                            inputValue: '0'
                        }],

                    }, {
                        columnWidth: .35,
                        xtype: 'radiogroup',
                        fieldLabel: '使用利用者姓名',
                        itemId:'namedefault',
                        items:[{
                            boxLabel: '是',
                            name: 'namedefault',
                            itemId:'namedefault1',
                            inputValue: '1',
                            listeners: {
                                'change':function(group,checked){
                                    var watermarktext = this.up('form').down('[itemId=watermarktext]');
                                    if(checked){
                                        //Ext.getCmp('organCode').allowBlank = true;
                                        watermarktext.allowBlank = true;
                                    }else{
                                        watermarktext.allowBlank = false;
                                    }
                                }
                            }
                        },{
                            boxLabel: '否',
                            name: 'namedefault',
                            itemId:'namedefault2',
                            inputValue: '0',
                            checked:'true'
                        }]
                    },
                    {
                        columnWidth: .35,
                        xtype: 'radiogroup',
                        fieldLabel: '默认水印',
                        items:[{
                            boxLabel: '是',
                            name: 'isdefault',
                            inputValue: '1',
                            checked:'true'
                        },{
                            boxLabel: '否',
                            name: 'isdefault',
                            inputValue: '0'
                        }]
                    },

                    {
                        columnWidth: .35,
                        xtype: 'radiogroup',
                        fieldLabel: '使用坐标',
                        items:[{
                            boxLabel: '是',
                            name: 'iscoordinates',
                            inputValue: '1',
                            listeners: {
                                'render':function(view){
                                    var coordinates1 = this.up('form').down('[itemId=coordinates1]');
                                    var coordinates2 = this.up('form').down('[itemId=coordinates2]');
                                    coordinates1.disable(true);
                                    coordinates2.disable(true);
                                },
                                'change':function(group,checked){
                                    var coordinates1 = this.up('form').down('[itemId=coordinates1]');
                                    var coordinates2 = this.up('form').down('[itemId=coordinates2]');
                                    var location = this.up('form').down('[itemId=location]');
                                    if(checked){
                                        coordinates1.enable(true);
                                        coordinates2.enable(true);
                                        location.disable(true);
                                        location.value = '-1';
                                    }else{
                                        coordinates1.disable(true);
                                        coordinates2.disable(true);
                                        location.enable(true);
                                        location.value = '1';
                                    }
                                }
                            }
                        },{
                            boxLabel: '否',
                            name: 'iscoordinates',
                            inputValue: '0',
                            checked:'true'
                        }]
                    },
                    {
                        columnWidth: .35,
                        xtype: 'radiogroup',
                        fieldLabel: '默认平铺',
                        items:[{
                            boxLabel: '是',
                            name: 'isrepeat',
                            inputValue: '1'
                        },{
                            boxLabel: '否',
                            name: 'isrepeat',
                            inputValue: '0',
                            checked:'true'
                        }]
                    },{
                        columnWidth: .35,
                        xtype: 'radiogroup',
                        fieldLabel: '使用利用者ip',
                        itemId:'useip',
                        items:[{
                            boxLabel: '是',
                            name: 'useip',
                            itemId:'useipYes',
                            inputValue: '1',
                            listeners: {
                                'change':function(group,checked){
                                    var watermarktext = this.up('form').down('[itemId=watermarktext]');
                                    if(checked){
                                        watermarktext.allowBlank = true;
                                    }else{
                                        watermarktext.allowBlank = false;
                                    }
                                }
                            }
                        },{
                            boxLabel: '否',
                            name: 'useip',
                            itemId:'useipNo',
                            inputValue: '0',
                            checked:'true'
                        }]
                    },{
                        columnWidth: .35,
                        xtype: 'radiogroup',
                        fieldLabel: '管理平台',
                        itemId:'ismanage',
                        items:[{
                            boxLabel: '是',
                            name: 'ismanage',
                            itemId:'ismanageYes',
                            inputValue: '1'
                        },{
                            boxLabel: '否',
                            name: 'ismanage',
                            itemId:'ismanageNo',
                            inputValue: '0',
                            checked:'true'
                        }]
                    },{
                        columnWidth: .35,
                        xtype: 'radiogroup',
                        fieldLabel: '利用平台',
                        itemId:'isuse',
                        items:[{
                            boxLabel: '是',
                            name: 'isuse',
                            itemId:'isuseYes',
                            inputValue: '1'
                        },{
                            boxLabel: '否',
                            name: 'isuse',
                            itemId:'isuseNo',
                            inputValue: '0',
                            checked:'true'
                        }]
                    }
                ]
            }
            ]
        },{
            xtype:'WatermarkMediaView',
            uploadUrl:me.uploadUrl,
            mediaUrl:me.mediaUrl,
            mediaType:me.mediaType,
            selTitle:me.selTitle,
            selUploadMediaUrl:me.selUploadMediaUrl,
            isAdd:me.isAdd,
            isTop:true,
            title:''
        }];
    }
    ,
    buttons: [
        {
            text: '上一步',
            hidden:true,
            itemId: 'afterId'
        },{
            text: '下一步',
            itemId: 'nextId'
        },{
            text: '预览',
            itemId: 'previewId'
        },{
            text: '完成',
            itemId: 'finishId'
        }
    ]
});
