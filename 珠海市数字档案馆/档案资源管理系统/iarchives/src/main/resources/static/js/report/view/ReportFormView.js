/**
 * Created by Rong on 2017/10/31.
 */
Ext.define('Report.view.ReportFormView',{
    extend:'Ext.form.Panel',
    xtype:'reportform',
    layout:'column',
    defaults:{
        layout:'form',
        // xtype:'textfield',
        labelWidth: 220,
        labelSeparator:'：'
    },
    items:[{
        columnWidth:.98,
        xtype : 'textfield',
        fieldLabel:'报表名称',
        name:'reportname',
        margin:'30 1 10 20',
        allowBlank:false
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'-33 25 1 3'
    },
    //     {
    //     columnWidth:.98,
    //     xtype : 'textfield',
    //     itemId:'reportParamCounts',
    //     fieldLabel:'报表参数个数',
    //     name:'reportparamcounts',
    //     margin:'1 1 10 20',
    //     allowBlank:false
    // },{
    //     columnWidth: .02,
    //     xtype : 'displayfield',
    //     value: '<label style="color:#ff0b23;!important;">*</label>',
    //     margin:'1 25 1 3'
    // },{
    //     columnWidth:.98,
    //     xtype : 'textfield',
    //     fieldLabel:'报表参数值(用逗号分隔且按顺序绑定)',
    //     name:'reportparamvalue',
    //     margin:'1 1 10 20',
    //     allowBlank:false
    // },{
    //     columnWidth: .02,
    //     xtype : 'displayfield',
    //     value: '<label style="color:#ff0b23;!important;">*</label>',
    //     margin:'1 25 1 3'
    // },{
    //     columnWidth:.48,
    //     xtype : 'textfield',
    //     fieldLabel:'打印表名(主从表用分号分隔开)',
    //     name:'tables',
    //     margin:'1 1 10 20',
    //     allowBlank:false
    // },{
    //     columnWidth: .02,
    //     xtype : 'displayfield',
    //     value: '<label style="color:#ff0b23;!important;">*</label>',
    //     margin:'1 25 1 3'
    // },{
    //     columnWidth:.48,
    //     xtype : 'textfield',
    //     fieldLabel:'打印主键名(请输入主键ID)',
    //     name:'mainkey',
    //     margin:'1 1 10 20',
    //     allowBlank:false
    // },{
    //     columnWidth: .02,
    //     xtype : 'displayfield',
    //     value: '<label style="color:#ff0b23;!important;">*</label>',
    //     margin:'1 25 1 3'
    // },{
    //     columnWidth:.98,
    //     xtype : 'textfield',
    //     fieldLabel:'打印描述字段',
    //     name:'printfieldnamelist',
    //     margin:'1 1 10 20',
    //     allowBlank:false
    // },{
    //     columnWidth: .02,
    //     xtype : 'displayfield',
    //     value: '<label style="color:#ff0b23;!important;">*</label>',
    //     margin:'1 25 1 3'
    // },{
    //     columnWidth:.98,
    //     xtype : 'textfield',
    //     fieldLabel:'打印字段',
    //     name:'printfieldcodelist',
    //     margin:'1 1 10 20',
    //     allowBlank:false
    // },{
    //     columnWidth: .02,
    //     xtype : 'displayfield',
    //     value: '<label style="color:#ff0b23;!important;">*</label>',
    //     margin:'1 25 1 3'
    // },
        {
        columnWidth:.5,
        xtype : 'radio',
        itemId:'publicReport',
        fieldLabel:'访问类型',
        inputValue: '公有报表',
        name:'reporttype',
        margin:'1 1 10 20',
        boxLabel:'公有报表',
        // listeners: {
        //     'change':function(group,checked){
        //         var node = this.up('form').down('[itemId=nodeName]');
        //         if(checked){
        //             node.setRawValue('');
        //             node.disable(true);
        //         }
        //     }
        // }
    },{
        columnWidth:.5,
        xtype : 'radio',
        itemId:'privateReport',
        inputValue: '私有报表',
        name:'reporttype',
        margin:'1 1 10 1',
        boxLabel:'私有报表',
        // listeners: {
        //     'render':function(){
        //         var node = this.up('form').down('[itemId=nodeName]');
        //         node.disable(true);
        //     },
        //     'change':function(group,checked){
        //         var node = this.up('form').down('[itemId=nodeName]');
        //         if(checked){
        //             node.enable(true);
        //         }else{
        //             node.disable(true);
        //         }
        //     }
        // }
    },{
        columnWidth:.98,
        xtype: 'reportTreeComboboxView',
        itemId: 'nodeName',
        fieldLabel: '节点',
        name: 'nodename',
        margin:'1 1 10 20',
        editable: false,
        url: '/nodesetting/getNodeByParentIdReport',
        extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
        hidden:true,
        allowBlank: false
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        // value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'1 20 1 3'
    },{
        columnWidth: 1,
        xtype: 'hidden',
        name: 'reportid'
    },{
        columnWidth: .32,
        xtype: 'textfield',
        itemId:'media',
        fieldLabel: '报表样式',
        labelWidth: 85,
        editable: false,
        name: 'filename',
        margin: '10 0 0 -6'
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth: .07,
        margin: '-1 0 0 0',
        items: [
            {
                xtype: 'button',
                itemId:'electronUpId',
                text: '上传'
            }
        ]
    }],

    buttons:[{
        text:'保存(Ctrl+S)',
        itemId:'save'
    },'-',{
        text:'连续录入(Ctrl+Shift+S)',
        itemId:'continuesave'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});