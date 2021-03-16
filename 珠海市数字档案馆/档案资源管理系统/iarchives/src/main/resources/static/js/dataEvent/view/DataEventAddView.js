Ext.define('DataEvent.view.DataEventAddView', {
	extend: 'Ext.window.Window',
    itemId: 'dataEventAddViewID',
    xtype: 'dataEventAddView',
    title: '档案关联',
    width:600,
    height:300,
    modal:true,
    closeToolText:'关闭',
    layout: 'fit',
    items: [{
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        xtype: 'form',
        itemId: 'formitemid',
        margin: '22',
        items: [{
            xtype:'textfield',
            fieldLabel: '事件描述',
            name: 'transdesc',
            itemId: 'refiditemid',
            allowBlank: false,
            margin: '10 0 15 0',
            afterLabelTextTpl: [
	            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
	        ]
        }, {
            xtype:'textfield',
            fieldLabel: '事件编号',
            name: 'transunit',
            itemId: 'transunitid',
            allowBlank: false,
            margin: '10 0 15 0',
            afterLabelTextTpl: [
	            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
	        ]
        }]
    }],
    buttons: [
    	{ 
	    	xtype: "label",
	        text:'温馨提示：红色外框表示输入非法数据！',
	        itemId:'tips',
	        style:{color:'red'}
	    },
        { text: '确认', itemId:'sure'},
        { text: '关闭', itemId:'close'}
    ]
});