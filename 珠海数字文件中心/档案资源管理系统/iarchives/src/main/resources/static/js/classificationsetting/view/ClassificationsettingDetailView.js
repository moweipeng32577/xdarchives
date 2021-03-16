/**
 * Created by tanly on 2017/11/2 0002.
 */

Ext.define('Classificationsetting.view.ClassificationsettingDetailView', {
    extend: 'Ext.window.Window',
    xtype: 'classificationsettingDetailView',
    itemId:'classificationsettingDetailViewid',
    title: '',
    width: 750,
    height: 270,
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
    items:[{
        xtype: 'form',
        margin:'25',
        modelValidation: true,
        trackResetOnLoad:true,
        items: [{
            fieldLabel: '',
            name:'classid',
            hidden:true,
            itemId:'classiditemid'
        },{
            fieldLabel: '',
            name:'parentclassid',
            hidden:true,
            itemId:'parentclassiditemid'
        },{
            fieldLabel: '',
            name:'codelevel',
            hidden:true,
            itemId:'codelevelitemid'
        },{
            fieldLabel: '',
            name:'sortsequence',
            hidden:true,
            itemId:'ordersitemid'
        },{
            xtype: 'textfield',
            fieldLabel: '分类名称',
            allowBlank: false,
            name:'classname',
            itemId:'classnameitemid',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]
        },{
            xtype: 'textfield',
            fieldLabel: '分类编码',
            name:'code',
            itemId:'classcodeitemid',
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]
        },{
            xtype: 'combo',
            fieldLabel: '分类类型',
            name: 'classlevel',
            itemId: 'classlevelItem',
            editable: false,
            store: new Ext.data.ArrayStore({
                fields: ['value', 'text'],
                data: [['0', '无'],['3', '未归管理'], ['4', '已归管理'], ['2', '案卷管理'], ['1', '卷内文件'], ['5', '资料管理'], ['6', '文件管理'],['7', '全宗卷管理'],['8','编研采集']]
            }),
            valueField: 'value',
            displayField: 'text',
            listeners:{
                afterrender:function(combo){
                    if(combo.getValue()===null){
                        var store = combo.getStore();
                        if(store.getCount() > 0){
                            combo.select(store.getAt(0));
                        }
                    }
                }
            }
        }
        ]
    }]
    ,
    buttons: [{
        text: '预览数据节点',
        itemId:'classPreviewBtnID'
    },{
        text: '取消',
        itemId:'classCancelBtnID'
    }
    ]
});
