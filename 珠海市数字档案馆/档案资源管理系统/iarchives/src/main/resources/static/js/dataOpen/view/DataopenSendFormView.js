/**
 * Created by tanly on 2017/12/2 0002.
 */
var lyModeStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "条目开放", value: "条目开放"},
        { text: "原文开放", value: "原文开放" }

    ]
});
Ext.define('Dataopen.view.DataopenSendFormView',{
    extend: 'Ext.form.Panel',
    xtype: 'dataopenSendFormView',
    itemId:'dataopenSendFormViewId',
    region: 'center',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 110
    },
    layout:'column',
    bodyPadding: '10 30 5 30',
    items:[{ xtype: 'textfield',name:'id',hidden:true},
        {
            columnWidth:.96,
            xtype: 'textfield',
            fieldLabel: '单据题名',
            name:'doctitle',
            itemId:'doctitleItem',
            allowBlank:false,
            margin:'5 0 0 0'
        },{
	        columnWidth: .02,
	        xtype: 'displayfield',
	        value: '<label style="color:#ff0b23;!important;">*</label>',
	        margin:'10 0 0 5'
	    },{
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '送审人',
            itemId:'submitterItem',
            name:'submitter',
            allowBlank:false,
            margin:'10 0 0 0'
        },{
	        columnWidth: .02,
	        xtype: 'displayfield',
	        value: '<label style="color:#ff0b23;!important;">*</label>',
	        margin:'10 0 0 5'
	    },{
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '开放批次号',
            name:'batchnum',
            allowBlank:false,
            margin:'10 0 0 0'
        },{
	        columnWidth: .02,
	        xtype: 'displayfield',
	        value: '<label style="color:#ff0b23;!important;">*</label>',
	        margin:'10 0 0 5'
	    },{
            columnWidth: .47,
            fieldLabel: '送审时间',
            xtype: 'datefield',
            name: 'submitdate',
            format: 'Ymd',
            margin:'10 0 0 0',
            allowBlank:false,
            value:new Date()
        },{
	        columnWidth: .02,
	        xtype: 'displayfield',
	        value: '<label style="color:#ff0b23;!important;">*</label>',
	        margin:'10 0 0 5'
	    },{
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '条目总数',
            allowBlank:false,
            name:'entrycount',
            margin:'10 0 0 0'
        },{
	        columnWidth: .02,
	        xtype: 'displayfield',
	        value: '<label style="color:#ff0b23;!important;">*</label>',
	        margin:'10 0 0 5'
	    },
        // {
        //     columnWidth: .47,
        //     xtype : 'combo',
        //     store : new Ext.data.ArrayStore({
        //         fields: ['text', 'value'],
        //         data: [['是',0], ['否', 1]]
        //     }) ,
        //     name:'opened',
        //     fieldLabel: '是否开放',
        //     displayField : 'text',
        //     valueField : 'value',
        //     editable:false,
        //     margin:'10 0 0 0',
        //     listeners:{
        //         afterrender:function(combo){
        //             var store = combo.getStore();
        //             if(store.getCount() > 0){
        //                 combo.select(store.getAt(0));
        //             }
        //         }
        //     }
        // },
        // {
        //     columnWidth:.06,
        //     xtype:'displayfield'
        // },
        {
            columnWidth: .47,
            xtype : 'combo',
            store : lyModeStore ,
            name:'opentype',
            fieldLabel: '开放类型',
            displayField : 'text',
            allowBlank:false,
            valueField : 'value',
            hidden:true,
            editable:false,
            margin:'10 0 0 0',
            listeners:{
                afterrender:function(combo){
                    var store = combo.getStore();
                    if(store.getCount() > 0){
                        combo.select(store.getAt(0));
                    }
                }
            }
        },{
	        columnWidth: .02,
	        xtype: 'displayfield',
	        value: '<label style="color:#ff0b23;!important;">*</label>',
	        margin:'10 0 0 5'
	    },{
            columnWidth: 1,
            xtype: 'textarea',
            fieldLabel: '备注信息',
            name:'remarks',
            margin:'10 0 0 0'
        }
    ],
    buttons: [
    	{
	        xtype: "label",
	        itemId:'tips',
	        style:{color:'red'},
	        text:'温馨提示：红色外框表示输入非法数据！',
	        margin:'6 2 5 4'
	    },
	    { text: '提交',itemId:'submit'},
        { text: '下一步',itemId:'next'},
        { text: '返回',itemId:'back'}
    ]
});