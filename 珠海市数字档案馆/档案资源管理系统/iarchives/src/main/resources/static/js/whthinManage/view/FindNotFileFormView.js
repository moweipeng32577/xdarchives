var textTpl = ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'];
Ext.define('WhthinManage.view.FindNotFileFormView', {
    extend: 'Ext.window.Window',
    xtype: 'FindNotFileFormView',
    itemId: 'FindNotFileFormView',
    title: '登记查无此档证明',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: 610,
    minWidth: 610,
    items: [
        {
            xtype: 'form',
            layout: 'fit',
            region: 'center',
            autoScroll : true,
            trackResetOnLoad:true,
            bodyStyle:'overflow-x:hidden;overflow-y:scroll',
            items: [
                {
                    layout:'form',
                    columnWidth:.97,
                    items: [{
                            xtype: 'textfield',
                            fieldLabel: '表单ID',
                            name: 'filenoneid',
                            hidden: true
                        }, {
                            xtype: 'textfield',
                            fieldLabel: '查档单ID',
                            name: 'docid',
                            hidden: true
                        }, {
                            xtype: 'textfield',
                            fieldLabel: '字号',
                            name: 'filenum',
                            readOnly: true
                        }, {
                        xtype: 'combobox',
                        fieldLabel: '档案类型',
                        editable: false,
                        allowBlank: true,
                        displayField: 'code',
                        valueField: 'code',
                        store: Ext.create('Ext.data.Store', {
                            proxy: {
                                type: 'ajax',
                                url: '/systemconfig/enums',
                                extraParams: {
                                    value: 'RecodeType'
                                },
                                reader: {
                                    type: 'json'
                                }
                            },
                            autoLoad: true
                        }),
                        name: 'recodetype',
                        itemId: 'recodeTypeitemId',
                        afterLabelTextTpl: textTpl,
                        listeners: {
                            afterrender: function (combo) {
                                var store = combo.getStore();
                                store.load(function () {
                                    if (this.getCount() > 0) {
                                        combo.select(this.getAt(0));
                                    }
                                });
                            }
                        }
                    },  {
                        xtype: 'textfield',
                        fieldLabel: '单位名称',
                        name: 'organname',
                        allowBlank: true,
                    },  {
                        xtype: 'datefield',
                        fieldLabel: '时间',
                        name: 'time',
                        allowBlank: false,
                        format: 'Ymd',
                        afterLabelTextTpl: textTpl
                    }, {
                        xtype: 'textfield',
                        fieldLabel: '查档人',
                        name: 'personname',
                        allowBlank: false,
                        afterLabelTextTpl: textTpl
                    },  {
                        xtype: 'datefield',
                        fieldLabel: '开始时间',
                        name: 'starttime',
                        allowBlank: false,
                        format: 'Ymd',
                        afterLabelTextTpl: textTpl
                    }, {
                        xtype: 'datefield',
                        fieldLabel: '结束时间',
                        name: 'endtime',
                        allowBlank: false,
                        format: 'Ymd',
                        afterLabelTextTpl: textTpl
                    }
                    ]},
                {
                    columnWidth:.97,
                    layout: 'form',
                    xtype: 'fieldset',
                    collapsible: true,// 收缩/展开
                    title: '男性',
                    autoHeight: true,
                    style: 'margin-left: 20px;',
                    defaultType: 'textfield', //表单默认属性
                    items: [
                        {
                            fieldLabel: '男方姓名',
                            name: 'manname'
                        },
                        {
                            itemId:'manradioId',
                            xtype: 'radiogroup',
                            items: [{
                                boxLabel: '身份证',
                                name: 'manradio',
                                inputValue: '0',
                                checked: 'true'
                            }, {
                                xtype: 'displayfield'
                            }, {
                                boxLabel: '已故',
                                name: 'manradio',
                                inputValue: '1'
                            }],
                            listeners:{
                                'change':function(field,newvalue,oldvalue,eopts){
                                    var view = this.up('FindNotFileFormView');
                                    var womancard = view.down('[itemId = mancard]');
                                    if(newvalue.manradio == 1){
                                        womancard.disable(false);
                                    }else{
                                        womancard.enable(true);
                                    }
                                }

                            }
                        },
                        {
                            fieldLabel: '身份证',
                            itemId:'mancard',
                            name: 'mancard'
                        }
                    ]
                }, {
                    xtype: 'form',
                    columnWidth:.97,
                    layout: 'form',
                    xtype: 'fieldset',
                    collapsible: true,// 收缩/展开
                    title: '女性',
                    autoHeight: true,
                    style: 'margin-left: 20px;',
                    defaultType: 'textfield',
                    items: [
                        {
                            fieldLabel: '女方姓名',
                            name: 'womanname'
                        },
                        {
                            itemId:'womanradioId',
                            xtype: 'radiogroup',
                            items: [{
                                boxLabel: '身份证',
                                name: 'womanradio',
                                inputValue: '0',
                                checked: 'true'
                            }, {
                                xtype: 'displayfield'
                            }, {
                                boxLabel: '已故',
                                name: 'womanradio',
                                inputValue: '1'
                            }],
                            listeners:{
                                'change':function(field,newvalue,oldvalue,eopts){
                                    var view = this.up('FindNotFileFormView');
                                    var womancard = view.down('[itemId = womancard]');
                                    if(newvalue.womanradio == 1){
                                        womancard.disable(false);
                                    }else{
                                        womancard.enable(true);
                                    }
                                }

                            }
                        },
                        {
                            fieldLabel: '身份证',
                            itemId:'womancard',
                            name: 'womancard'
                        }
                    ]
                }
            ]
        }],
    buttons: [
        { text: '保存',itemId:'findNoneAddSubmit'},
        { text: '保存并打印',itemId:'findNoneAddSubmitAndPrint'},
        { text: '打印',itemId:'print'},
        { text: '关闭',itemId:'findNoneClose',
            handler: function (btn) {
                //关闭窗口
                btn.findParentByType('FindNotFileFormView').close();
            }
        }
    ]
});