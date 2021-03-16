/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('Curator.view.ProjectAddLookFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'projectAddLookFormView',
    itemId: 'projectAddLookFormViewId',
    height: '55%',
    layout: 'fit',
    region: 'center',
    autoScroll: true,
    items: [{
        xtype: 'form',
        layout: 'column',
        bodyPadding: 16,
        autoScroll: true,
        fieldDefaults: {
            labelWidth: 70
        },
        items: [
            {
                columnWidth: .3,
                xtype: 'label',
                text: '温馨提示：红色外框表示输入非法数据！',
                style: {
                    color: 'red',
                    'font-size': '16px'
                },
                margin: '10 0 0 0'
            }, {
                columnWidth: .7,
                xtype: 'displayfield'
            }, {
                columnWidth: 1,
                fieldLabel: '标题',
                itemId: 'titleId',
                xtype: 'textfield',
                name: 'title',
                labelWidth: 85,
                margin: '10 0 0 0',
                editable:false
            },{
                columnWidth: .47,
                fieldLabel: '工作项目',
                editable: false,
                xtype: 'textarea',
                name: 'workproject',
                labelWidth: 85,
                margin: '10 0 0 0'
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                fieldLabel: '工作内容',
                editable: false,
                xtype: 'textarea',
                name: 'workcontent',
                labelWidth: 85,
                margin: '10 0 0 0'
            },{
                columnWidth: .47,
                xtype: 'textfield',
                itemId: 'leaderresponId',
                name: 'leaderrespon',
                fieldLabel: '责任领导',
                editable: false,
                labelWidth: 85,
                margin: '10 0 0 0'
            }, {
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                fieldLabel: '承办科室',
                name: 'undertakedepart',
                xtype: 'textfield',
                labelWidth: 85,
                editable: false,
                margin:'10 0 0 0'
            },{
                columnWidth: .23,
                fieldLabel: '承办人',
                xtype: 'textfield',
                name: 'undertaker',
                labelWidth: 85,
                editable: false,
                margin:'10 0 0 0'
            }, {
                columnWidth:.01,
                xtype:'displayfield'
            },{
                columnWidth: .23,
                fieldLabel: '配合科室',
                xtype: 'textfield',
                name: 'cooperatedepart',
                labelWidth: 85,
                editable: false,
                margin:'10 0 0 0'
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth:.47,
                itemId: 'finishtimeId',
                xtype: 'textfield',
                fieldLabel: '完成时间',
                labelWidth: 85,
                margin: '10 0 0 0',
                editable: false,
                name: 'finishtime'
            },{
                columnWidth:.47,
                itemId: 'opinionId',
                xtype: 'textfield',
                fieldLabel: '督导意见',
                editable: false,
                labelWidth: 85,
                margin: '10 0 0 0',
                name: 'opinion'
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth:.47,
                itemId: 'projectstatusId',
                xtype: 'textfield',
                fieldLabel: '状态',
                editable: false,
                margin: '10 0 0 0',
                labelWidth: 85,
                name: 'projectstatus'
            },{
                columnWidth:0.47,
                itemId: 'desciId',
                xtype: 'textarea',
                fieldLabel: '备注',
                editable: false,
                margin: '10 0 0 0',
                labelWidth: 85,
                name: 'desci'
            }
        ]
    }],
    buttons: [
        {text: '关闭', itemId: 'close'}
    ]
});
