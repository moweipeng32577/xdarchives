/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('ProjectAdd.view.ProjectManageAddView', {
    extend: 'Ext.window.Window',
    xtype: 'projectManageAddView',
    itemId: 'projectManageAddViewId',
    title: '新增项目',
    frame: true,
    resizable: true,
    width: 600,
    height: 450,
    autoScroll: true,
    modal: true,
    closeToolText: '关闭',
    closeAction: 'hide',
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
    items: [{
        xtype: 'form',
        modelValidation: true,
        margin: '15',
        items: [
            {
                xtype: 'textfield',
                fieldLabel: 'id',
                name: 'id',
                hidden:true
            }, {
                itemId: 'titleId',
                xtype: 'textfield',
                fieldLabel: '标题',
                allowBlank: false,
                name: 'title'
            }, {
                itemId: 'workprojectId',
                xtype: 'textarea',
                fieldLabel: '工作项目',
                allowBlank: false,
                name: 'workproject'
            },{
                itemId: 'workcontentId',
                xtype: 'textarea',
                fieldLabel: '工作内容',
                allowBlank: false,
                name: 'workcontent'
            },{
                itemId: 'leaderresponId',
                xtype: 'textfield',
                fieldLabel: '责任领导',
                allowBlank: false,
                name: 'leaderrespon'
            },{
                itemId: 'undertakedepartId',
                xtype: 'textfield',
                fieldLabel: '承办科室',
                allowBlank: false,
                name: 'undertakedepart'
            },{
                itemId: 'undertakerId',
                xtype: 'textfield',
                fieldLabel: '承办人',
                allowBlank: false,
                name: 'undertaker'
            },{
                itemId: 'cooperatedepartId',
                xtype: 'textfield',
                fieldLabel: '配合科室',
                allowBlank: false,
                name: 'cooperatedepart'
            },{
                itemId: 'finishtimeId',
                xtype: 'textfield',
                fieldLabel: '完成时间',
                allowBlank: false,
                name: 'finishtime'
            },{
                itemId: 'opinionId',
                xtype: 'textfield',
                fieldLabel: '督导意见',
                allowBlank: false,
                name: 'opinion'
            },{
                itemId: 'desciId',
                xtype: 'textarea',
                fieldLabel: '备注',
                allowBlank: false,
                name: 'desci'
            }
        ]
    }],
    buttons: [
        {text: '确定', itemId: 'projectCheck'},
        {text: '关闭', itemId: 'projectClose'}
    ]
});