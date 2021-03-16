Ext.define('Workflow.view.NodeDlSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'nodeDlSelectView',
    itemId:'nodeDlSelectViewId',
    title: '设置环节用户',
    width:1500,
    height:750,
    bodyPadding: 20,
    layout:'hbox',
    modal:true,
    closeToolText:'关闭',
    items:[{
        flex:3,
        height:'100%',
        xtype:'workflowDlGridView'//当前节点所有环节用户列表
    },{
        flex:2,
        height:'100%',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'WorkflowDlGridStore',
        displayField: 'realname',
        valueField: 'userid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选用户(按Ctrl+F查找)',
        toTitle: '已选用户'
    }],
    buttons: [
	    {
	    	margin:'15',
	        xtype:'checkbox',
	        boxLabel:'同步至所有审批流程',
	        itemId:'synchronization'
    	},
        { text: '提交',itemId:'nodeDlSelectSubmit'},
        { text: '关闭',itemId:'nodeDlSelectClose'}
    ]
});