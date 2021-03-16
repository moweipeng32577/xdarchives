/**
 * 数据关联窗口
 */
Ext.define('Management.view.ManagementAssociationView', {
	extend: 'Ext.window.Window',
    itemId: 'managementAssociationViewID',
    xtype: 'managementAssociationView',
    title: '数据关联',
    width:600,
    height:350,
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
            columnWidth: .5,
            items: [{
	            xtype: 'radio',
	            checked: true,
	            itemId:'newAssociationId',
	            inputValue: 'newSession',
	            name:'association',
	            boxLabel:'创建新关联',
	            // 数据关联 - 选择创建方式
	            listeners:{
	            	'render':function(){
                        //选择事件按钮
                        var selectEvent = this.up('managementAssociationView').down('[itemId=selectEvent]');
                        selectEvent.disable(false);
                    },
				    'change':function(group, checked){
				    	//事件描述
                        var refiditemid = this.up('managementAssociationView').down('[itemId=refiditemid]');
                        //事件编写
                        var transunitid = this.up('managementAssociationView').down('[itemId=transunitid]');
                        //选择事件按钮
                        var selectEvent = this.up('managementAssociationView').down('[itemId=selectEvent]');
                        if(checked){
                            refiditemid.enable(true);
                            transunitid.enable(true);
                            selectEvent.disable(false);
                        }else{
                            refiditemid.disable(false);
                            transunitid.disable(false);
                            selectEvent.enable(true);
                        }
                    }
				}
            }]
        }, {
            columnWidth: .5,
            items: [{
	            xtype: 'radio',
	            itemId:'associationId',
	            inputValue: 'session',
	            name:'association',
	            boxLabel:'关联已有事件'
            }]
        }, {
            xtype:'textfield',
            fieldLabel: '事件描述',
            name: 'transdesc',
            itemId: 'refiditemid',
            allowBlank: false,
            afterLabelTextTpl: [
	            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
	        ]
        }, {
        	xtype:'textfield',
            fieldLabel: '事件编号',
            name: 'transunit',
            itemId: 'transunitid',
            allowBlank: false,
            afterLabelTextTpl: [
	            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
	        ]
        }, {
        	region: 'east',
        	xtype:'button',
        	text: '选择事件',
        	disabled: false,
        	itemId: 'selectEvent',
        	width: 35
        }]
    }],
    buttons: [
    	{
	    	xtype: "label",
	        text:'温馨提示：红色外框表示输入非法数据！',
	        itemId:'tips',
	        style:{color:'red'}
	    },
        { text: '确认',itemId:'sure'},
        { text: '返回',itemId:'close'}
    ]
});