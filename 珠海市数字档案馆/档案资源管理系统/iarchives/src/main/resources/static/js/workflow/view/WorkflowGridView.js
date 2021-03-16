/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.view.WorkflowGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'workflowGridView',
    region: 'center',
    itemId:'workflowGridViewID',
    hasSearchBar:false,
    tbar: [{
        itemId:'nodeAdd',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '增加节点'
    }, '-', {
        itemId:'nodeEdit',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '修改节点'
    }, '-', {
        itemId:'nodeDel',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除节点'
    }, '-', {
        itemId:'nodeUser',
        xtype: 'button',
        iconCls:'fa fa-user',
        text: '设置环节用户'
    }, '-', {
        itemId:'nodeQx',
        xtype: 'button',
        iconCls:'fa fa-cog',
        text: '设置节点权限'
    }, '-', {
        itemId:'nodeSortq',
        xtype: 'button',
        iconCls:'fa fa-bars',
        text: '调序'
    }, '-', {
        itemId:'approveScope',
        xtype: 'button',
        iconCls:'fa fa-cog',
        text: '审批范围'
    }, '-',{
        xtype: "checkbox",
        boxLabel : '是否可催办',
        itemId:'urging',
        listeners: {
            change:function(obj,ischecked){
                urgingChange(obj,ischecked);
            }
        }
    }, '-',{
        xtype: "checkbox",
        boxLabel : '是否短信通知',
        itemId:'sendmsgId',
        listeners: {
            change:function(obj,ischecked){
                sendmsgChange(obj,ischecked);
            }
        }
    }],
    store: 'WorkflowGridStore',
    columns: [
        {text: '节点名称', dataIndex: 'text', flex: 2, menuDisabled: true},
        {text: '节点描述', dataIndex: 'desci', flex: 2, menuDisabled: true},
        {text: '下一节点', dataIndex: 'nexttext', flex: 2, menuDisabled: true},
        {text: '节点顺序', dataIndex: 'orders', flex: 2, menuDisabled: true}
    ]
});

//设置催办
function urgingChange(obj,ischecked) {
    var workid=window.wtreeView.getSelectionModel().getSelected().items[0].get('id');
    Ext.Ajax.request({
        url: '/workflow/updateWorkflowUrging',
        method: 'POST',
        params: {
            workid:workid,
            state:ischecked==true?"1":"2",
            type:'urging'
        },
        success: function (resp) {
            var respText = Ext.decode(resp.responseText);
            if (respText.success != true) {//后台设置失败则改变勾选框
                XD.msg(respText.msg);
                obj.setValue(ischecked);
            }
        },
        failure: function() {
            XD.msg('操作失败');
        }
    });
}

//设置短信通知
function sendmsgChange(obj,ischecked) {
    var workid=window.wtreeView.getSelectionModel().getSelected().items[0].get('id');
    Ext.Ajax.request({
        url: '/workflow/updateWorkflowUrging',
        method: 'POST',
        params: {
            workid:workid,
            state:ischecked==true?"1":"0",
            type:'sendmsg'
        },
        success: function (resp) {
            var respText = Ext.decode(resp.responseText);
            if (respText.success != true) {//后台设置失败则改变勾选框
                XD.msg(respText.msg);
                obj.setValue(ischecked);
            }
        },
        failure: function() {
            XD.msg('操作失败');
        }
    });
}
